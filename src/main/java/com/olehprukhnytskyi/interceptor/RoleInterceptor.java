package com.olehprukhnytskyi.interceptor;

import com.olehprukhnytskyi.annotation.RequireRole;
import com.olehprukhnytskyi.exception.BadRequestException;
import com.olehprukhnytskyi.exception.error.CommonErrorCode;
import com.olehprukhnytskyi.util.CustomHeaders;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import java.util.Arrays;

@Component
public class RoleInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) {
        if (handler instanceof HandlerMethod handlerMethod) {
            RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
            if (requireRole == null) {
                requireRole = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
            }
            if (requireRole != null) {
                String rolesHeader = request.getHeader(CustomHeaders.X_USER_ROLES);
                if (rolesHeader == null || rolesHeader.isBlank()) {
                    throw new BadRequestException(CommonErrorCode.BAD_REQUEST,
                            "You do not have permission to perform this action");
                }
                String[] requiredRoles = requireRole.value();
                boolean hasRole = Arrays.stream(rolesHeader.split(","))
                        .map(String::trim)
                        .anyMatch(userRole -> Arrays.stream(requiredRoles)
                                .anyMatch(reqRole -> reqRole.equalsIgnoreCase(userRole)));
                if (!hasRole) {
                    throw new BadRequestException(CommonErrorCode.BAD_REQUEST,
                            "You do not have permission to perform this action");
                }
            }
        }
        return true;
    }
}
