package com.entregaexpress.logiflow.fleetservice.model;

import com.entregaexpress.logiflow.common.enums.TipoVehiculo;

/**
 * Implementación concreta para motorizados (entregas urbanas)
 */
public class Motorizado extends VehiculoEntrega {
    
    private static final double COSTO_POR_KM = 0.15;
    private static final double CAPACIDAD_MAX_KG = 10.0;
    private static final double AUTONOMIA_MAX_KM = 80.0;
    
    private int cilindrada;
    private boolean tieneTopCase;
    
    public Motorizado() {
        super(TipoVehiculo.MOTORIZADO);
        this.capacidadCargaKg = CAPACIDAD_MAX_KG;
        this.autonomiaKm = AUTONOMIA_MAX_KM;
    }
    
    @Override
    public double calcularCostoPorKm() {
        // Motos tienen el costo operativo más bajo
        double costoBase = COSTO_POR_KM;
        
        // Reducción de costo si tiene menos cilindrada (más eficiente)
        if (cilindrada < 150) {
            costoBase *= 0.8;
        }
        
        return costoBase;
    }
    
    @Override
    public boolean validarEstadoOperativo() {
        // Validaciones específicas para motos
        return placa != null && !placa.isEmpty()
                && marca != null
                && cilindrada > 0
                && anio >= 2010; // Solo motos de 2010 en adelante
    }
    
    public void setCilindrada(int cilindrada) {
        this.cilindrada = cilindrada;
    }
    
    public void setTieneTopCase(boolean tieneTopCase) {
        this.tieneTopCase = tieneTopCase;
        // Si tiene top case, puede llevar un poco más de peso
        if (tieneTopCase) {
            this.capacidadCargaKg = CAPACIDAD_MAX_KG + 2.0;
        }
    }
}
