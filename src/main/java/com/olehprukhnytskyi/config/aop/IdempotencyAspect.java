package com.olehprukhnytskyi.config.aop;

import com.olehprukhnytskyi.config.annotation.Idempotent;
import com.olehprukhnytskyi.util.CustomHeaders;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class IdempotencyAspect {
    private final RedisTemplate<String, Object> redisTemplate;

    @Around("@annotation(idempotent)")
    public Object handleIdempotency(ProceedingJoinPoint joinPoint,
                                    Idempotent idempotent) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes()).getRequest();
        Class<?> returnType = ((MethodSignature) joinPoint.getSignature()).getReturnType();
        String clientKey = request.getHeader(CustomHeaders.X_REQUEST_ID);
        String finalKey;
        if (clientKey != null && !clientKey.isBlank()) {
            finalKey = "idempotency:client:" + clientKey;
            log.debug("Using client provided key: {}", finalKey);
        } else {
            if (!idempotent.allowFallback()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Header " + CustomHeaders.X_REQUEST_ID + " is required");
            }
            String fingerprint = generateFingerprint(joinPoint, request);
            finalKey = "idempotency:fingerprint:" + fingerprint;
            log.debug("Generated fallback fingerprint: {}", finalKey);
        }
        Object cachedBody = redisTemplate.opsForValue().get(finalKey);
        if (cachedBody != null) {
            log.info("Idempotency hit! Returning cached response for key: {}", finalKey);
            if (ResponseEntity.class.isAssignableFrom(returnType)) {
                return ResponseEntity.status(HttpStatus.CREATED).body(cachedBody);
            }
            return cachedBody;
        }
        String lockKey = finalKey + ":lock";
        Boolean isLocked = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "LOCKED", Duration.ofSeconds(10));
        if (Boolean.FALSE.equals(isLocked)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Request is already being processed");
        }
        try {
            Object result = joinPoint.proceed();
            Object objectToCache = result;
            if (objectToCache instanceof ResponseEntity<?> responseEntity) {
                objectToCache = responseEntity.getBody();
            }
            if (objectToCache != null) {
                redisTemplate.opsForValue().set(
                        finalKey,
                        objectToCache,
                        idempotent.ttl(),
                        idempotent.unit()
                );
            }
            return result;
        } finally {
            redisTemplate.delete(lockKey);
        }
    }

    private String generateFingerprint(ProceedingJoinPoint joinPoint, HttpServletRequest request) {
        StringBuilder signature = new StringBuilder();
        String userId = request.getHeader(CustomHeaders.X_USER_ID);
        signature.append("user:").append(userId).append("|");
        signature.append("path:").append(request.getRequestURI()).append("|");
        for (Object arg : joinPoint.getArgs()) {
            if (arg == null) {
                continue;
            }
            if (arg instanceof MultipartFile file) {
                signature.append("file:")
                        .append(file.getOriginalFilename())
                        .append(":")
                        .append(file.getSize())
                        .append("|");
            } else if (isPrimitiveOrString(arg)) {
                signature.append("param:").append(arg).append("|");
            } else {
                signature.append("obj:").append(arg).append("|");
            }
        }
        return DigestUtils.md5DigestAsHex(signature.toString().getBytes());
    }

    private boolean isPrimitiveOrString(Object object) {
        return object instanceof String || object instanceof Number || object instanceof Boolean;
    }
}
