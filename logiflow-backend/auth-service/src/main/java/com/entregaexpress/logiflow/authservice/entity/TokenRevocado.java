package com.entregaexpress.logiflow.authservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad para gestionar tokens revocados (blacklist)
 */
@Entity
@Table(name = "tokens_revocados")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenRevocado {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 500)
    private String token;
    
    @Column(nullable = false)
    private LocalDateTime fechaRevocacion;
    
    @Column(nullable = false)
    private LocalDateTime fechaExpiracion;
}
