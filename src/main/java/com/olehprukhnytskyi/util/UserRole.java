package com.olehprukhnytskyi.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User role")
public enum UserRole {
    @Schema(description = "User")
    USER,

    @Schema(description = "VIP")
    VIP,

    @Schema(description = "Admin")
    ADMIN;

    @JsonCreator
    public static UserRole fromString(String value) {
        return UserRole.valueOf(value.toUpperCase());
    }
}
