package com.entregaexpress.logiflow.pedidoservice.dto;

import com.entregaexpress.logiflow.common.enums.EstadoPedido;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para actualización parcial de pedido (PATCH)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarPedidoRequest {
    
    private EstadoPedido estado;
    
    private Long repartidorId;
    
    private String observaciones;
    
    private Double origenLatitud;
    private Double origenLongitud;
    private Double destinoLatitud;
    private Double destinoLongitud;
}
