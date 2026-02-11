package com.entregaexpress.logiflow.common.enums;

/**
 * Tipos de entrega según el alcance geográfico
 */
public enum TipoEntrega {
    /**
     * Entregas urbanas rápidas (última milla) mediante motorizados
     */
    URBANA,
    
    /**
     * Entregas intermunicipales dentro de la provincia con vehículos livianos
     */
    INTERMUNICIPAL,
    
    /**
     * Entregas nacionales mediante furgonetas o camiones medianos/grandes
     */
    NACIONAL
}
