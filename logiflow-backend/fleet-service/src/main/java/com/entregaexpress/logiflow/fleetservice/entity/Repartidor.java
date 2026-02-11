package com.entregaexpress.logiflow.fleetservice.entity;

import com.entregaexpress.logiflow.common.enums.EstadoRepartidor;
import com.entregaexpress.logiflow.common.enums.TipoVehiculo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "repartidores")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Repartidor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long usuarioId;
    
    @Column(nullable = false, length = 200)
    private String nombreCompleto;
    
    @Column(unique = true, length = 20)
    private String licencia;
    
    @Column(length = 20)
    private String telefono;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoVehiculo tipoVehiculo;
    
    @Column(length = 20)
    private String placaVehiculo;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoRepartidor estado;
    
    private Double ubicacionLatitud;
    private Double ubicacionLongitud;
    
    @Column
    private LocalDateTime ultimaActualizacionUbicacion;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    @UpdateTimestamp
    private LocalDateTime fechaActualizacion;
}
