package com.entregaexpress.logiflow.pedidoservice.entity;

import com.entregaexpress.logiflow.common.enums.EstadoPedido;
import com.entregaexpress.logiflow.common.enums.TipoEntrega;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad Pedido - representa un pedido de delivery en el sistema
 */
@Entity
@Table(name = "pedidos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String codigoPedido;
    
    @Column(nullable = false)
    private Long clienteId;
    
    @Column(nullable = false, length = 500)
    private String origenDireccion;
    
    @Column(nullable = false, length = 500)
    private String destinoDireccion;
    
    private Double origenLatitud;
    private Double origenLongitud;
    private Double destinoLatitud;
    private Double destinoLongitud;
    
    @Column(nullable = false)
    private Double distanciaKm;
    
    @Column(nullable = false)
    private Double pesoKg;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoEntrega tipoEntrega;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoPedido estado;
    
    @Column(length = 1000)
    private String descripcion;
    
    private Long repartidorId;
    
    @Column(nullable = false)
    private Double costoEstimado;
    
    @Column
    private LocalDateTime fechaAsignacion;
    
    @Column
    private LocalDateTime fechaEntrega;
    
    @Column(length = 1000)
    private String observaciones;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    @UpdateTimestamp
    private LocalDateTime fechaActualizacion;
}
