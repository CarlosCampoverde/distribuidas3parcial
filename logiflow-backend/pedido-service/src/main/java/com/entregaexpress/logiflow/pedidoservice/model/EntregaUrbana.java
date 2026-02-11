package com.entregaexpress.logiflow.pedidoservice.model;

import com.entregaexpress.logiflow.common.enums.TipoEntrega;

/**
 * Implementación concreta para entregas urbanas rápidas (última milla)
 * Realizada mediante motorizados
 */
public class EntregaUrbana extends EntregaBase implements IRegistrableGPS {
    
    private static final double MAX_DISTANCIA_KM = 15.0;
    private static final double MAX_PESO_KG = 10.0;
    
    private double ultimaLatitud;
    private double ultimaLongitud;
    private long ultimoTimestamp;
    
    public EntregaUrbana() {
        super(TipoEntrega.URBANA);
    }
    
    @Override
    protected String prepararEntrega() {
        return "Empacando para entrega urbana en motorizado";
    }
    
    @Override
    protected String asignarRecursos() {
        return "Motorizado asignado para zona urbana";
    }
    
    @Override
    public boolean validar() {
        if (!super.validar()) {
            return false;
        }
        return distanciaKm <= MAX_DISTANCIA_KM && pesoKg <= MAX_PESO_KG;
    }
    
    @Override
    public double calcularCosto() {
        double costoBase = TARIFA_BASE_URBANA;
        double costoDistancia = distanciaKm * TARIFA_POR_KM_URBANA;
        double costoPeso = pesoKg * TARIFA_POR_KG;
        
        return costoBase + costoDistancia + costoPeso;
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
