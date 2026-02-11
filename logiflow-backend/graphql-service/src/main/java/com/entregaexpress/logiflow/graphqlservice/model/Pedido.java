package com.entregaexpress.logiflow.graphqlservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {
    private Long id;
    private String codigoPedido;
    private Long clienteId;
    private Long repartidorId;
    private String estado;
    private String tipoEntrega;
    private String origenDireccion;
    private String destinoDireccion;
    private Double origenLatitud;
    private Double origenLongitud;
    private Double destinoLatitud;
    private Double destinoLongitud;
    private Double distanciaKm;
    private Double pesoKg;
    private Double costoEstimado;
    private String descripcion;
    private String fechaCreacion;
    private String fechaAsignacion;
    private String fechaEntrega;
}
