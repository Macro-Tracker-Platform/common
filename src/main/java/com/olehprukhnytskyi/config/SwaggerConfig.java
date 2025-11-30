package com.olehprukhnytskyi.config;

import com.olehprukhnytskyi.properties.SwaggerProperties;
import com.olehprukhnytskyi.util.CustomHeaders;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(SwaggerProperties.class)
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
    private final SwaggerProperties swaggerProperties;

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
    public GroupedOpenApi groupedServiceApi() {
        return GroupedOpenApi.builder()
                .group(swaggerProperties.getGroupName())
                .pathsToMatch(swaggerProperties.getPaths())
                .build();
    }

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .servers(List.of(new Server().url(swaggerProperties.getGatewayBaseUrl())))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
