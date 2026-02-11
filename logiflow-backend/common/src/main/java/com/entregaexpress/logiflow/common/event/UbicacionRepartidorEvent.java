package com.entregaexpress.logiflow.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UbicacionRepartidorEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long repartidorId;
    private String nombreCompleto;
    private Double latitud;
    private Double longitud;
    private LocalDateTime timestamp;
    private Long pedidoAsignado;
    private String estado;
}
