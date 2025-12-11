package com.olehprukhnytskyi.config;

import com.olehprukhnytskyi.properties.SwaggerProperties;
import com.olehprukhnytskyi.util.CustomHeaders;
import java.util.List;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(SwaggerProperties.class)
public class SwaggerConfig {
    private final SwaggerProperties swaggerProperties;

    @Bean
    public OpenApiCustomizer hideInternalHeaders() {
        return openApi -> openApi.getPaths().values().forEach(pathItem ->
                pathItem.readOperations().forEach(operation -> {
                    if (operation.getParameters() != null) {
                        operation.getParameters().removeIf(p ->
                                p.getName().equalsIgnoreCase(CustomHeaders.X_USER_ID)
                        );
                    }
                })
        );
    }

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .servers(List.of(new Server().url(swaggerProperties.getPublicUrl())))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
