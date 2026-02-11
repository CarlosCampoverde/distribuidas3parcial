package com.entregaexpress.logiflow.fleetservice.model;

import com.entregaexpress.logiflow.common.enums.TipoVehiculo;

/**
 * Implementación concreta para vehículos livianos (entregas intermunicipales)
 */
public class VehiculoLiviano extends VehiculoEntrega {
    
    private static final double COSTO_POR_KM = 0.40;
    private static final double CAPACIDAD_MAX_KG = 100.0;
    private static final double AUTONOMIA_MAX_KM = 350.0;
    
    private int numeroPuertas;
    private boolean tieneCajaCarga;
    
    public VehiculoLiviano() {
        super(TipoVehiculo.VEHICULO_LIVIANO);
        this.capacidadCargaKg = CAPACIDAD_MAX_KG;
        this.autonomiaKm = AUTONOMIA_MAX_KM;
    }
    
    @Override
    public double calcularCostoPorKm() {
        double costoBase = COSTO_POR_KM;
        
        // Camionetas con caja de carga son más eficientes para cargas
        if (tieneCajaCarga) {
            costoBase *= 0.95;
        }
        
        return costoBase;
    }
    
    @Override
    public boolean validarEstadoOperativo() {
        // Validaciones específicas para vehículos livianos
        return placa != null && !placa.isEmpty()
                && marca != null
                && anio >= 2015  // Vehículos más recientes
                && numeroPuertas >= 4; // Mínimo 4 puertas
    }
    
    public void setNumeroPuertas(int numeroPuertas) {
        this.numeroPuertas = numeroPuertas;
    }
    
    public void setTieneCajaCarga(boolean tieneCajaCarga) {
        this.tieneCajaCarga = tieneCajaCarga;
        // Con caja de carga aumenta la capacidad
        if (tieneCajaCarga) {
            this.capacidadCargaKg = CAPACIDAD_MAX_KG + 50.0;
        }
    }
}
