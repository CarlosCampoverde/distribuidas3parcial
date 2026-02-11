package com.entregaexpress.logiflow.authservice.dto;

import com.entregaexpress.logiflow.common.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para respuesta de información de usuario
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {
    
    private Long id;
    private String username;
    private String email;
    private String nombreCompleto;
    private String telefono;
    private Role role;
    private Boolean activo;
    private Long zonaId;
    private String tipoFlota;
    private LocalDateTime fechaCreacion;
}
