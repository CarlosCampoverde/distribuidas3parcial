package com.entregaexpress.logiflow.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO estándar para respuestas exitosas en toda la plataforma
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    
    private LocalDateTime timestamp;
    private boolean success;
    private String message;
    private T data;
    
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .success(true)
                .data(data)
                .build();
    }
    
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .success(true)
                .message(message)
                .data(data)
                .build();
    }
}
