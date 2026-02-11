package com.entregaexpress.logiflow.pedidoservice.model;

import com.entregaexpress.logiflow.common.enums.TipoEntrega;

/**
 * Implementación concreta para entregas intermunicipales
 * Realizada mediante vehículos livianos (autos o camionetas)
 */
public class EntregaIntermunicipal extends EntregaBase implements IRegistrableGPS {
    
    private static final double MAX_DISTANCIA_KM = 200.0;
    private static final double MAX_PESO_KG = 100.0;
    
    private double ultimaLatitud;
    private double ultimaLongitud;
    private long ultimoTimestamp;
    
    public EntregaIntermunicipal() {
        super(TipoEntrega.INTERMUNICIPAL);
    }
    
    @Override
    protected String prepararEntrega() {
        return "Preparando paquete para transporte intermunicipal";
    }
    
    @Override
    protected String asignarRecursos() {
        return "Vehículo liviano asignado para ruta intermunicipal";
    }
    
    @Override
    public boolean validar() {
        if (!super.validar()) {
            return false;
        }
        return distanciaKm > 15.0 && distanciaKm <= MAX_DISTANCIA_KM && pesoKg <= MAX_PESO_KG;
    }
    
    @Override
    public double calcularCosto() {
        double costoBase = TARIFA_BASE_INTERMUNICIPAL;
        double costoDistancia = distanciaKm * TARIFA_POR_KM_INTERMUNICIPAL;
        double costoPeso = pesoKg * TARIFA_POR_KG;
        
        // Recargo por distancia mayor a 100 km
        double recargoDistancia = distanciaKm > 100 ? 5.0 : 0.0;
        
        return costoBase + costoDistancia + costoPeso + recargoDistancia;
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
