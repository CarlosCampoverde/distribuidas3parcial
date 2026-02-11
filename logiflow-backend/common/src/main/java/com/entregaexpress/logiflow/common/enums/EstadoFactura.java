package com.entregaexpress.logiflow.common.enums;

/**
 * Estados de una factura
 */
public enum EstadoFactura {
    /**
     * Factura en proceso de creación, no finalizada
     */
    BORRADOR,
    
    /**
     * Factura emitida, pendiente de pago
     */
    EMITIDA,
    
    /**
     * Factura pagada
     */
    PAGADA,
    
    /**
     * Factura cancelada
     */
    CANCELADA
}
