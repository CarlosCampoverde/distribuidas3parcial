package com.entregaexpress.logiflow.pedidoservice.model;

import com.entregaexpress.logiflow.common.enums.TipoEntrega;
import lombok.Getter;
import lombok.Setter;

/**
 * Clase abstracta que modela comportamientos comunes de tipos de entrega
 * Patrón de diseño: Template Method - Define estructura común pero no es instanciable
 * 
 * Esta clase define operaciones comunes para todos los tipos de entrega,
 * pero delega la implementación específica a las subclases concretas
 */
@Getter
@Setter
public abstract class EntregaBase implements IProcesableEntrega {
    
    protected TipoEntrega tipoEntrega;
    protected String origen;
    protected String destino;
    protected double distanciaKm;
    protected double pesoKg;
    protected String descripcion;
    
    // Tarifas base por tipo (pueden ser sobrescritas)
    protected static final double TARIFA_BASE_URBANA = 3.0;
    protected static final double TARIFA_BASE_INTERMUNICIPAL = 10.0;
    protected static final double TARIFA_BASE_NACIONAL = 25.0;
    
    protected static final double TARIFA_POR_KM_URBANA = 0.5;
    protected static final double TARIFA_POR_KM_INTERMUNICIPAL = 1.2;
    protected static final double TARIFA_POR_KM_NACIONAL = 2.0;
    
    protected static final double TARIFA_POR_KG = 0.8;
    
    /**
     * Constructor protegido - solo accesible para subclases
     */
    protected EntregaBase(TipoEntrega tipoEntrega) {
        this.tipoEntrega = tipoEntrega;
    }
    
    /**
     * Template Method: Define el flujo de procesamiento, pero delega pasos específicos
     */
    @Override
    public String procesarEntrega() {
        if (!validar()) {
            return "Error: La entrega no cumple con los requisitos mínimos";
        }
        
        String preparacion = prepararEntrega();
        String asignacion = asignarRecursos();
        String confirmacion = confirmarEntrega();
        
        return String.format("Entrega procesada: %s | %s | %s", preparacion, asignacion, confirmacion);
    }
    
    /**
     * Método abstracto - debe ser implementado por cada tipo específico
     */
    protected abstract String prepararEntrega();
    
    /**
     * Método abstracto - cada tipo asigna recursos según sus necesidades
     */
    protected abstract String asignarRecursos();
    
    /**
     * Hook method - puede ser sobrescrito por subclases si necesitan lógica adicional
     */
    protected String confirmarEntrega() {
        return "Entrega confirmada y lista para despacho";
    }
    
    /**
     * Validación común para todas las entregas
     */
    @Override
    public boolean validar() {
        return origen != null && !origen.trim().isEmpty() 
                && destino != null && !destino.trim().isEmpty()
                && distanciaKm > 0
                && pesoKg > 0;
    }
    
    /**
     * Método abstracto para cálculo de costo - cada tipo tiene su fórmula
     */
    @Override
    public abstract double calcularCosto();
}
