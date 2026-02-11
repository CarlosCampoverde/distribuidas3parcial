package com.entregaexpress.logiflow.fleetservice.model;

import com.entregaexpress.logiflow.common.enums.TipoVehiculo;

/**
 * Implementación concreta para camiones (entregas nacionales)
 */
public class Camion extends VehiculoEntrega {
    
    private static final double COSTO_POR_KM = 1.20;
    private static final double CAPACIDAD_MAX_KG = 5000.0;
    private static final double AUTONOMIA_MAX_KM = 800.0;
    
    private int numeroEjes;
    private boolean tieneRefrigeracion;
    private double volumenM3;
    
    public Camion() {
        super(TipoVehiculo.CAMION);
        this.capacidadCargaKg = CAPACIDAD_MAX_KG;
        this.autonomiaKm = AUTONOMIA_MAX_KM;
    }
    
    @Override
    public double calcularCostoPorKm() {
        double costoBase = COSTO_POR_KM;
        
        // Camiones con refrigeración tienen mayor costo operativo
        if (tieneRefrigeracion) {
            costoBase *= 1.3;
        }
        
        // Más ejes = más peso = más consumo
        costoBase += (numeroEjes - 2) * 0.10;
        
        return costoBase;
    }
    
    @Override
    public boolean validarEstadoOperativo() {
        // Validaciones específicas para camiones
        return placa != null && !placa.isEmpty()
                && marca != null
                && anio >= 2012  // Camiones relativamente nuevos
                && numeroEjes >= 2
                && volumenM3 > 0;
    }
    
    @Override
    public boolean puedeCompletarRuta(double distanciaKm) {
        // Camiones pueden recargar en ruta, más flexible
        return distanciaKm <= (autonomiaKm * 1.5);
    }
    
    public void setNumeroEjes(int numeroEjes) {
        this.numeroEjes = numeroEjes;
        // Más ejes = mayor capacidad de carga
        if (numeroEjes > 2) {
            this.capacidadCargaKg = CAPACIDAD_MAX_KG + ((numeroEjes - 2) * 2000.0);
        }
    }
    
    public void setTieneRefrigeracion(boolean tieneRefrigeracion) {
        this.tieneRefrigeracion = tieneRefrigeracion;
    }
    
    public void setVolumenM3(double volumenM3) {
        this.volumenM3 = volumenM3;
    }
}
