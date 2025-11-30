package com.olehprukhnytskyi.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "swagger")
public class SwaggerProperties {
    @NotBlank
    private String groupName;

    @NotEmpty
    private List<String> paths;

    @NotBlank
    private String gatewayBaseUrl;
}
