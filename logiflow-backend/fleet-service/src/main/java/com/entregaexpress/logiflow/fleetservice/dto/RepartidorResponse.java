package com.entregaexpress.logiflow.fleetservice.dto;

import com.entregaexpress.logiflow.common.enums.EstadoRepartidor;
import com.entregaexpress.logiflow.common.enums.TipoVehiculo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepartidorResponse {
    private Long id;
    private Long usuarioId;
    private String nombreCompleto;
    private String licencia;
    private String telefono;
    private TipoVehiculo tipoVehiculo;
    private String placaVehiculo;
    private EstadoRepartidor estado;
    private Double ubicacionLatitud;
    private Double ubicacionLongitud;
    private LocalDateTime ultimaActualizacionUbicacion;
    private LocalDateTime fechaCreacion;
}
