package com.entregaexpress.logiflow.fleetservice.service;

import com.entregaexpress.logiflow.common.enums.EstadoRepartidor;
import com.entregaexpress.logiflow.common.event.UbicacionRepartidorEvent;
import com.entregaexpress.logiflow.common.exception.ResourceNotFoundException;
import com.entregaexpress.logiflow.fleetservice.dto.*;
import com.entregaexpress.logiflow.fleetservice.entity.Repartidor;
import com.entregaexpress.logiflow.fleetservice.event.FleetEventPublisher;
import com.entregaexpress.logiflow.fleetservice.repository.RepartidorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RepartidorService {
    
    private final RepartidorRepository repartidorRepository;
    private final FleetEventPublisher eventPublisher;
    
    @Transactional
    public RepartidorResponse crearRepartidor(CrearRepartidorRequest request) {
        log.info("Creando nuevo repartidor: {}", request.getNombreCompleto());
        
        Repartidor repartidor = Repartidor.builder()
                .usuarioId(request.getUsuarioId())
                .nombreCompleto(request.getNombreCompleto())
                .licencia(request.getLicencia())
                .telefono(request.getTelefono())
                .tipoVehiculo(request.getTipoVehiculo())
                .placaVehiculo(request.getPlacaVehiculo())
                .estado(EstadoRepartidor.DISPONIBLE)
                .build();
        
        repartidor = repartidorRepository.save(repartidor);
        log.info("Repartidor creado: {}", repartidor.getId());
        
        return mapToResponse(repartidor);
    }
    
    @Transactional(readOnly = true)
    public RepartidorResponse obtenerPorId(Long id) {
        Repartidor repartidor = repartidorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Repartidor", "id", id));
        return mapToResponse(repartidor);
    }
    
    @Transactional(readOnly = true)
    public List<RepartidorResponse> obtenerTodos() {
        return repartidorRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<RepartidorResponse> obtenerPorEstado(EstadoRepartidor estado) {
        return repartidorRepository.findByEstado(estado).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public RepartidorResponse actualizar(Long id, ActualizarRepartidorRequest request) {
        Repartidor repartidor = repartidorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Repartidor", "id", id));
        
        if (request.getEstado() != null) {
            repartidor.setEstado(request.getEstado());
        }
        
        if (request.getUbicacionLatitud() != null && request.getUbicacionLongitud() != null) {
            repartidor.setUbicacionLatitud(request.getUbicacionLatitud());
            repartidor.setUbicacionLongitud(request.getUbicacionLongitud());
            repartidor.setUltimaActualizacionUbicacion(LocalDateTime.now());
            
            // Guardar y publicar evento
            repartidor = repartidorRepository.save(repartidor);
            publishUbicacionEvent(repartidor);
            return mapToResponse(repartidor);
        }
        
        repartidor = repartidorRepository.save(repartidor);
        return mapToResponse(repartidor);
    }
    
    private void publishUbicacionEvent(Repartidor repartidor) {
        UbicacionRepartidorEvent event = UbicacionRepartidorEvent.builder()
                .repartidorId(repartidor.getId())
                .nombreCompleto(repartidor.getNombreCompleto())
                .latitud(repartidor.getUbicacionLatitud())
                .longitud(repartidor.getUbicacionLongitud())
                .timestamp(LocalDateTime.now())
                .estado(repartidor.getEstado().name())
                .build();
        
        eventPublisher.publishUbicacionActualizada(event);
    }
    
    private RepartidorResponse mapToResponse(Repartidor r) {
        return RepartidorResponse.builder()
                .id(r.getId())
                .usuarioId(r.getUsuarioId())
                .nombreCompleto(r.getNombreCompleto())
                .licencia(r.getLicencia())
                .telefono(r.getTelefono())
                .tipoVehiculo(r.getTipoVehiculo())
                .placaVehiculo(r.getPlacaVehiculo())
                .estado(r.getEstado())
                .ubicacionLatitud(r.getUbicacionLatitud())
                .ubicacionLongitud(r.getUbicacionLongitud())
                .ultimaActualizacionUbicacion(r.getUltimaActualizacionUbicacion())
                .fechaCreacion(r.getFechaCreacion())
                .build();
    }
}
