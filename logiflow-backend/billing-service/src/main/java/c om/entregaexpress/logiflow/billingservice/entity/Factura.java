package com.entregaexpress.logiflow.billingservice.entity;

import com.entregaexpress.logiflow.common.enums.EstadoFactura;
import com.entregaexpress.logiflow.common.enums.TipoEntrega;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "facturas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Factura {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 50)
    private String numeroFactura;
    
    @Column(nullable = false)
    private Long pedidoId;
    
    @Column(nullable = false)
    private Long clienteId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEntrega tipoEntrega;
    
    @Column(nullable = false)
    private Double distanciaKm;
    
    @Column(nullable = false)
    private Double pesoKg;
    
    @Column(nullable = false)
    private Double subtotal;
    
    @Column(nullable = false)
    private Double impuestos;
    
    @Column(nullable = false)
    private Double total;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoFactura estado;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
}
