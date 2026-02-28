package com.satyam.apiforge.common;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private String timestamp = LocalDateTime.now().toString();

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "Success", data,
                LocalDateTime.now().toString());
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, data,
                LocalDateTime.now().toString());
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null,
                LocalDateTime.now().toString());
    }
}