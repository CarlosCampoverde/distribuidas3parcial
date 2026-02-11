package com.entregaexpress.logiflow.authservice.dto;

import com.entregaexpress.logiflow.common.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitud de registro de usuario
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    
    @NotBlank(message = "El username es obligatorio")
    @Size(min = 3, max = 100, message = "El username debe tener entre 3 y 100 caracteres")
    private String username;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    @Size(max = 150)
    private String email;
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;
    
    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(max = 150)
    private String nombreCompleto;
    
    @Size(max = 20)
    private String telefono;
    
    @NotNull(message = "El rol es obligatorio")
    private Role role;
    
    private Long zonaId;
    
    private String tipoFlota;
}
