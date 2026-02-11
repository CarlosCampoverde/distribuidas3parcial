package com.entregaexpress.logiflow.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuesta de autenticación con token JWT
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    private String token;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private String username;
    private String role;
    private Long userId;
}
