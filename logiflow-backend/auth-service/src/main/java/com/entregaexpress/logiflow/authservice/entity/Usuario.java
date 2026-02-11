package com.entregaexpress.logiflow.authservice.entity;

import com.entregaexpress.logiflow.common.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad Usuario para autenticación y autorización
 */
@Entity
@Table(name = "usuarios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 100)
    private String username;
    
    @Column(unique = true, nullable = false, length = 150)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false, length = 150)
    private String nombreCompleto;
    
    @Column(length = 20)
    private String telefono;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    @Column(name = "zona_id")
    private Long zonaId;
    
    @Column(length = 50)
    private String tipoFlota;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    @UpdateTimestamp
    private LocalDateTime fechaActualizacion;
}
