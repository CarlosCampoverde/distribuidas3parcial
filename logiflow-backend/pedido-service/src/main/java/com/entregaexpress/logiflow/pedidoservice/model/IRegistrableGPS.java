package com.entregaexpress.logiflow.pedidoservice.model;

/**
 * Interfaz para entidades que pueden ser rastreadas mediante GPS
 */
public interface IRegistrableGPS {
    
    /**
     * Registra una nueva ubicación GPS
     * @param latitud coordenada de latitud
     * @param longitud coordenada de longitud
     * @param timestamp timestamp de la ubicación
     */
    void registrarUbicacion(double latitud, double longitud, long timestamp);
    
    /**
     * Obtiene la última ubicación registrada
     * @return array [latitud, longitud] o null si no hay ubicación
     */
    double[] obtenerUltimaUbicacion();
}
