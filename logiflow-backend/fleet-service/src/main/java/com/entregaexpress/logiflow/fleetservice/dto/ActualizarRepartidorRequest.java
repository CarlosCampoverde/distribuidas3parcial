package com.entregaexpress.logiflow.fleetservice.dto;

import com.entregaexpress.logiflow.common.enums.EstadoRepartidor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarRepartidorRequest {
    private EstadoRepartidor estado;
    private Double ubicacionLatitud;
    private Double ubicacionLongitud;
}
