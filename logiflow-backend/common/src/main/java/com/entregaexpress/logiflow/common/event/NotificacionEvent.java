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
public class NotificacionEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String tipo; // PEDIDO_ESTADO, UBICACION, FACTURA
    private Long destinatarioId;
    private String titulo;
    private String mensaje;
    private LocalDateTime timestamp;
    private Object data; // Datos adicionales
}
