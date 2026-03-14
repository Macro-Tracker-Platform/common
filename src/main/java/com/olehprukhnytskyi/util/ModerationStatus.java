package com.olehprukhnytskyi.util;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ModerationStatus {
    NONE,
    PENDING_REVIEW,
    APPROVED,
    REJECTED;

    @JsonCreator
    public static ModerationStatus fromString(String value) {
        return ModerationStatus.valueOf(value.toUpperCase());
    }
}
