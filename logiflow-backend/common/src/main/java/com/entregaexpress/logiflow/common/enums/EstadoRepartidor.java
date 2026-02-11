package com.entregaexpress.logiflow.common.enums;

/**
 * Estados posibles de un repartidor
 */
public enum EstadoRepartidor {
    /**
     * Disponible para recibir asignaciones
     */
    DISPONIBLE,
    
    /**
     * En ruta realizando una entrega
     */
    EN_RUTA,
    
    /**
     * Vehículo en mantenimiento o reparación
     */
    MANTENIMIENTO,
    
    /**
     * Fuera de servicio (descanso, fin de turno, etc.)
     */
    INACTIVO
}
