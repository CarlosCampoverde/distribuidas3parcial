package com.entregaexpress.logiflow.pedidoservice.dto;

import com.entregaexpress.logiflow.common.enums.EstadoPedido;
import com.entregaexpress.logiflow.common.enums.TipoEntrega;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para respuesta de información de pedido
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponse {
    
    private Long id;
    private String codigoPedido;
    private Long clienteId;
    private String origenDireccion;
    private String destinoDireccion;
    private Double origenLatitud;
    private Double origenLongitud;
    private Double destinoLatitud;
    private Double destinoLongitud;
    private Double distanciaKm;
    private Double pesoKg;
    private TipoEntrega tipoEntrega;
    private EstadoPedido estado;
    private String descripcion;
    private Long repartidorId;
    private Double costoEstimado;
    private LocalDateTime fechaAsignacion;
    private LocalDateTime fechaEntrega;
    private String observaciones;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
