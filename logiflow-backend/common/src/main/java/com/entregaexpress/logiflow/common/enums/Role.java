package com.entregaexpress.logiflow.common.enums;

/**
 * Roles del sistema LogiFlow
 * Define los diferentes niveles de acceso y permisos en la plataforma
 */
public enum Role {
    /**
     * Usuario final que realiza pedidos
     */
    CLIENTE,
    
    /**
     * Conductor que realiza entregas (motorizado, vehículo liviano o camión)
     */
    REPARTIDOR,
    
    /**
     * Supervisa operaciones en una zona específica
     */
    SUPERVISOR,
    
    /**
     * Acceso a KPIs, reportes y métricas generales
     */
    GERENTE,
    
    /**
     * Administrador del sistema con acceso total
     */
    ADMIN
}
