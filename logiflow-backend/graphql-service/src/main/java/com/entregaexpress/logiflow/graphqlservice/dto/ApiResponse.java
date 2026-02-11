package com.entregaexpress.logiflow.graphqlservice.dto;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private String timestamp;
    private Boolean success;
    private String message;
    private T data;
}
