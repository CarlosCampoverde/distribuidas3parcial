package com.entregaexpress.logiflow.graphqlservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Repartidor {
    private Long id;
    private Long usuarioId;
    private String nombreCompleto;
    private String licencia;
    private String telefono;
    private String tipoVehiculo;
    private String placaVehiculo;
    private String estado;
    private Double ubicacionLatitud;
    private Double ubicacionLongitud;
    private String ultimaActualizacionUbicacion;
}
