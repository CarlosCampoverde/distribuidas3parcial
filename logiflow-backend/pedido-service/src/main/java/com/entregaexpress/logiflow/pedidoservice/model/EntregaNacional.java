package com.entregaexpress.logiflow.pedidoservice.model;

import com.entregaexpress.logiflow.common.enums.TipoEntrega;

/**
 * Implementación concreta para entregas nacionales
 * Realizada mediante furgonetas o camiones medianos/grandes
 */
public class EntregaNacional extends EntregaBase implements IRegistrableGPS {
    
    private static final double MAX_PESO_KG = 5000.0;
    
    private double ultimaLatitud;
    private double ultimaLongitud;
    private long ultimoTimestamp;
    
    public EntregaNacional() {
        super(TipoEntrega.NACIONAL);
    }
    
    @Override
    protected String prepararEntrega() {
        return "Preparando carga para transporte nacional - requiere documentación adicional";
    }
    
    @Override
    protected String asignarRecursos() {
        return "Camión asignado para ruta nacional";
    }
    
    @Override
    protected String confirmarEntrega() {
        // Hook method sobrescrito - entregas nacionales requieren documentación
        return "Entrega confirmada con documentación completa y guía de remisión";
    }
    
    @Override
    public boolean validar() {
        if (!super.validar()) {
            return false;
        }
        return distanciaKm > 200.0 && pesoKg <= MAX_PESO_KG;
    }
    
    @Override
    public double calcularCosto() {
        double costoBase = TARIFA_BASE_NACIONAL;
        double costoDistancia = distanciaKm * TARIFA_POR_KM_NACIONAL;
        double costoPeso = pesoKg * TARIFA_POR_KG;
        
        // Recargo por peso mayor a 1000 kg
        double recargoPeso = pesoKg > 1000 ? (pesoKg - 1000) * 0.5 : 0.0;
        
        // Recargo por distancia mayor a 500 km
        double recargoDistancia = distanciaKm > 500 ? 20.0 : 0.0;
        
        return costoBase + costoDistancia + costoPeso + recargoPeso + recargoDistancia;
    }
    
    @Override
    public void registrarUbicacion(double latitud, double longitud, long timestamp) {
        this.ultimaLatitud = latitud;
        this.ultimaLongitud = longitud;
        this.ultimoTimestamp = timestamp;
    }
    
    @Override
    public double[] obtenerUltimaUbicacion() {
        if (ultimoTimestamp == 0) {
            return null;
        }
        return new double[]{ultimaLatitud, ultimaLongitud};
    }
}
