package com.entregaexpress.logiflow.common.enums;

/**
 * Estados posibles de un pedido en LogiFlow
 */
public enum EstadoPedido {
    /**
     * Pedido recibido, pendiente de asignación
     */
    RECIBIDO,
    
    /**
     * Pedido asignado a un repartidor
     */
    ASIGNADO,
    
    /**
     * Repartidor en camino al destino
     */
    EN_RUTA,
    
    /**
     * Entrega completada exitosamente
     */
    ENTREGADO,
    
    /**
     * Pedido cancelado
     */
    CANCELADO,
    
    /**
     * Hubo un problema en la entrega (dirección incorrecta, cliente ausente, etc.)
     */
    INCIDENTE
}
