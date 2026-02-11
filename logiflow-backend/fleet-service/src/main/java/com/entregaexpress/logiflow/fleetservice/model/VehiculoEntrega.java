package com.entregaexpress.logiflow.fleetservice.model;

import com.entregaexpress.logiflow.common.enums.TipoVehiculo;
import lombok.Getter;
import lombok.Setter;

/**
 * Clase abstracta que modela comportamientos comunes de vehículos de entrega
 * Patrón de diseño: Template Method - Define estructura común pero no es instanciable
 * 
 * Esta clase NO puede ser instanciada directamente, solo a través de sus subclases concretas
 */
@Getter
@Setter
public abstract class VehiculoEntrega {
    
    protected String placa;
    protected TipoVehiculo tipo;
    protected String marca;
    protected String modelo;
    protected int anio;
    protected double capacidadCargaKg;
    protected double autonomiaKm;
    protected boolean disponible;
    
    /**
     * Constructor protegido - solo accesible para subclases
     */
    protected VehiculoEntrega(TipoVehiculo tipo) {
        this.tipo = tipo;
        this.disponible = true;
    }
    
    /**
     * Método abstracto - cada tipo de vehículo tiene su propio cálculo
     * @return costo operativo por kilómetro
     */
    public abstract double calcularCostoPorKm();
    
    /**
     * Método abstracto - validación específica por tipo
     * @return true si el vehículo puede operar, false en caso contrario
     */
    public abstract boolean validarEstadoOperativo();
    
    /**
     * Hook method - puede ser sobrescrito por subclases
     * @param distanciaKm distancia a recorrer
     * @return true si puede completar la ruta con autonomía actual
     */
    public boolean puedeCompletarRuta(double distanciaKm) {
        return distanciaKm <= autonomiaKm;
    }
    
    /**
     * Template method - define el flujo de asignación
     * @param pesoKg peso de la carga
     * @return mensaje de resultado de asignación
     */
    public String asignarCarga(double pesoKg) {
        if (!disponible) {
            return "Error: Vehículo no disponible";
        }
        
        if (!validarEstadoOperativo()) {
            return "Error: Vehículo no está en condiciones operativas";
        }
        
        if (pesoKg > capacidadCargaKg) {
            return String.format("Error: Carga excede capacidad (%,.2f kg > %,.2f kg)", 
                    pesoKg, capacidadCargaKg);
        }
        
        disponible = false;
        return String.format("Carga asignada exitosamente. Vehículo %s - Placa %s", 
                tipo, placa);
    }
    
    /**
     * Libera el vehículo para nuevas asignaciones
     */
    public void liberarVehiculo() {
        this.disponible = true;
    }
    
    /**
     * Representación en texto del vehículo
     */
    @Override
    public String toString() {
        return String.format("%s [%s %s %d] - Placa: %s - %.0f kg - %.0f km", 
                tipo, marca, modelo, anio, placa, capacidadCargaKg, autonomiaKm);
    }
}
