package com.entregaexpress.logiflow.pedidoservice.factory;

import com.entregaexpress.logiflow.common.enums.TipoEntrega;
import com.entregaexpress.logiflow.pedidoservice.model.*;

/**
 * Factory para crear instancias de tipos de entrega
 * Patrón de diseño: Factory Method
 * 
 * Centraliza la creación de objetos EntregaBase según el tipo solicitado
 */
public class EntregaFactory {
    
    /**
     * Crea una instancia de entrega según el tipo especificado
     * @param tipoEntrega tipo de entrega a crear
     * @return instancia concreta de EntregaBase
     * @throws IllegalArgumentException si el tipo no es soportado
     */
    public static EntregaBase crearEntrega(TipoEntrega tipoEntrega) {
        if (tipoEntrega == null) {
            throw new IllegalArgumentException("El tipo de entrega no puede ser null");
        }
        
        return switch (tipoEntrega) {
            case URBANA -> new EntregaUrbana();
            case INTERMUNICIPAL -> new EntregaIntermunicipal();
            case NACIONAL -> new EntregaNacional();
        };
    }
    
    /**
     * Crea y configura una entrega completa
     * @param tipoEntrega tipo de entrega
     * @param origen dirección de origen
     * @param destino dirección de destino
     * @param distanciaKm distancia en kilómetros
     * @param pesoKg peso en kilogramos
     * @param descripcion descripción del pedido
     * @return instancia configurada de EntregaBase
     */
    public static EntregaBase crearEntregaCompleta(
            TipoEntrega tipoEntrega,
            String origen,
            String destino,
            double distanciaKm,
            double pesoKg,
            String descripcion) {
        
        EntregaBase entrega = crearEntrega(tipoEntrega);
        entrega.setOrigen(origen);
        entrega.setDestino(destino);
        entrega.setDistanciaKm(distanciaKm);
        entrega.setPesoKg(pesoKg);
        entrega.setDescripcion(descripcion);
        
        return entrega;
    }
}
