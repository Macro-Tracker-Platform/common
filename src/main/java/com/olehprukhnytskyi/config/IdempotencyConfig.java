package com.olehprukhnytskyi.config;

import com.olehprukhnytskyi.config.aop.IdempotencyAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@ConditionalOnClass(RedisTemplate.class)
@ConditionalOnProperty(
        prefix = "app.idempotency",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false
)
@Import(IdempotencyAspect.class)
public class IdempotencyConfig {
}
