package com.entregaexpress.logiflow.pedidoservice.model;

/**
 * Interfaz para entidades que pueden ser procesadas como entregas
 * Patrón de diseño: Strategy - define contrato de comportamiento interoperable
 */
public interface IProcesableEntrega {
    
    /**
     * Procesa la entrega según el tipo específico
     * @return mensaje de confirmación del procesamiento
     */
    String procesarEntrega();
    
    /**
     * Valida si la entrega cumple con los requisitos mínimos
     * @return true si es válida, false en caso contrario
     */
    boolean validar();
    
    /**.
     * Calcula el costo estimado de la entrega
     * @return costo en la moneda local
     */
    double calcularCosto();
}
