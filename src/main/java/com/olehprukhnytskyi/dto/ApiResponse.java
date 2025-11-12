package com.olehprukhnytskyi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Standard API response wrapper")
public class ApiResponse<T> {
    @Schema(description = "Indicates if request was successful")
    private boolean success;

    @Schema(description = "Response data payload")
    private T data;

    @Schema(description = "Error details if success is false")
    private List<ApiError> error;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> error(List<ApiError> error) {
        return new ApiResponse<>(false, null, error);
    }

    public static <T> ApiResponse<T> error(ApiError error) {
        return new ApiResponse<>(false, null, List.of(error));
    }
}
