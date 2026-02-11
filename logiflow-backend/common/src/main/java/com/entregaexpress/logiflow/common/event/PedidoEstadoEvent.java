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
public class PedidoEstadoEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long pedidoId;
    private String codigoPedido;
    private String estadoAnterior;
    private String estadoNuevo;
    private Long repartidorId;
    private Long clienteId;
    private LocalDateTime timestamp;
    private String tipoEntrega;
    private String descripcion;
}
