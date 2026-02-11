package com.entregaexpress.logiflow.billingservice.service;

import com.entregaexpress.logiflow.billingservice.dto.FacturaResponse;
import com.entregaexpress.logiflow.billingservice.dto.GenerarFacturaRequest;
import com.entregaexpress.logiflow.billingservice.entity.Factura;
import com.entregaexpress.logiflow.billingservice.repository.FacturaRepository;
import com.entregaexpress.logiflow.common.enums.EstadoFactura;
import com.entregaexpress.logiflow.common.enums.TipoEntrega;
import com.entregaexpress.logiflow.common.exception.BusinessException;
import com.entregaexpress.logiflow.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio de facturación (versión mínima Fase 1)
 * Cálculo de tarifa básica y generación de factura en BORRADOR
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FacturaService {
    
    private final FacturaRepository facturaRepository;
    
    private static final double TARIFA_BASE_URBANA = 3.0;
    private static final double TARIFA_BASE_INTERMUNICIPAL = 10.0;
    private static final double TARIFA_BASE_NACIONAL = 25.0;
    
    private static final double TARIFA_KM_URBANA = 0.5;
    private static final double TARIFA_KM_INTERMUNICIPAL = 1.2;
    private static final double TARIFA_KM_NACIONAL = 2.0;
    
    private static final double TARIFA_KG = 0.8;
    private static final double PORCENTAJE_IVA = 0.12;
    
    @Transactional
    public FacturaResponse generarFactura(GenerarFacturaRequest request) {
        log.info("Generando factura para pedido: {}", request.getPedidoId());
        
        // Validar que no exista factura para este pedido
        if (facturaRepository.findByPedidoId(request.getPedidoId()).isPresent()) {
            throw new BusinessException("Ya existe una factura para este pedido");
        }
        
        // Cálculo de tarifa básica
        double subtotal = calcularTarifaBasica(
                request.getTipoEntrega(), 
                request.getDistanciaKm(), 
                request.getPesoKg()
        );
        
        double impuestos = subtotal * PORCENTAJE_IVA;
        double total = subtotal + impuestos;
        
        Factura factura = Factura.builder()
                .numeroFactura(generarNumeroFactura())
                .pedidoId(request.getPedidoId())
                .clienteId(request.getClienteId())
                .tipoEntrega(request.getTipoEntrega())
                .distanciaKm(request.getDistanciaKm())
                .pesoKg(request.getPesoKg())
                .subtotal(subtotal)
                .impuestos(impuestos)
                .total(total)
                .estado(EstadoFactura.BORRADOR)
                .build();
        
        factura = facturaRepository.save(factura);
        log.info("Factura generada: {}", factura.getNumeroFactura());
        
        return mapToResponse(factura);
    }
    
    @Transactional(readOnly = true)
    public FacturaResponse obtenerPorId(Long id) {
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura", "id", id));
        return mapToResponse(factura);
    }
    
    @Transactional(readOnly = true)
    public List<FacturaResponse> obtenerPorCliente(Long clienteId) {
        return facturaRepository.findByClienteId(clienteId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    private double calcularTarifaBasica(TipoEntrega tipo, double distanciaKm, double pesoKg) {
        double tarifaBase;
        double tarifaKm;
        
        switch (tipo) {
            case URBANA:
                tarifaBase = TARIFA_BASE_URBANA;
                tarifaKm = TARIFA_KM_URBANA;
                break;
            case INTERMUNICIPAL:
                tarifaBase = TARIFA_BASE_INTERMUNICIPAL;
                tarifaKm = TARIFA_KM_INTERMUNICIPAL;
                break;
            case NACIONAL:
                tarifaBase = TARIFA_BASE_NACIONAL;
                tarifaKm = TARIFA_KM_NACIONAL;
                break;
            default:
                throw new BusinessException("Tipo de entrega no soportado");
        }
        
        return tarifaBase + (distanciaKm * tarifaKm) + (pesoKg * TARIFA_KG);
    }
    
    private String generarNumeroFactura() {
        return "F-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private FacturaResponse mapToResponse(Factura f) {
        return FacturaResponse.builder()
                .id(f.getId())
                .numeroFactura(f.getNumeroFactura())
                .pedidoId(f.getPedidoId())
                .clienteId(f.getClienteId())
                .tipoEntrega(f.getTipoEntrega())
                .distanciaKm(f.getDistanciaKm())
                .pesoKg(f.getPesoKg())
                .subtotal(f.getSubtotal())
                .impuestos(f.getImpuestos())
                .total(f.getTotal())
                .estado(f.getEstado())
                .fechaCreacion(f.getFechaCreacion())
                .build();
    }
}
