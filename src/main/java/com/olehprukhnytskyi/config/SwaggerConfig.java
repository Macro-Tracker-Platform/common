package com.olehprukhnytskyi.config;

import com.olehprukhnytskyi.util.CustomHeaders;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {
    private static final Set<String> INTERNAL_HEADERS = Arrays
            .stream(CustomHeaders.class.getDeclaredFields())
            .filter(f -> f.getType().equals(String.class))
            .map(f -> {
                try {
                    return (String) f.get(null);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            })
            .collect(Collectors.toSet());

    @Bean
    public OpenApiCustomizer hideInternalHeaders() {
        return openApi -> openApi.getPaths().values().forEach(pathItem ->
                pathItem.readOperations().forEach(operation -> {
                    if (operation.getParameters() != null) {
                        operation.getParameters().removeIf(p ->
                                INTERNAL_HEADERS.contains(p.getName())
                        );
                    }
                })
        );
    }

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
