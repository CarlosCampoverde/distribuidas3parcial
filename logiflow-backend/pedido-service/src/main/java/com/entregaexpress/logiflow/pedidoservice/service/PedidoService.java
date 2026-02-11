package com.entregaexpress.logiflow.pedidoservice.service;

import com.entregaexpress.logiflow.common.enums.EstadoPedido;
import com.entregaexpress.logiflow.common.event.PedidoEstadoEvent;
import com.entregaexpress.logiflow.common.exception.BusinessException;
import com.entregaexpress.logiflow.common.exception.ResourceNotFoundException;
import com.entregaexpress.logiflow.pedidoservice.dto.*;
import com.entregaexpress.logiflow.pedidoservice.entity.Pedido;
import com.entregaexpress.logiflow.pedidoservice.event.PedidoEventPublisher;
import com.entregaexpress.logiflow.pedidoservice.factory.EntregaFactory;
import com.entregaexpress.logiflow.pedidoservice.model.EntregaBase;
import com.entregaexpress.logiflow.pedidoservice.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio de gestión de pedidos
 * Implementa operaciones CRUD con transacciones ACID
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PedidoService {
    
    private final PedidoRepository pedidoRepository;
    private final PedidoEventPublisher eventPublisher;
    
    /**
     * Crea un nuevo pedido utilizando el Factory Pattern
     * @param request datos del pedido
     * @return pedido creado
     */
    @Transactional
    public PedidoResponse crearPedido(CrearPedidoRequest request) {
        log.info("Creando nuevo pedido para cliente: {}", request.getClienteId());
        
        // Usar Factory para crear tipo specific de entrega
        EntregaBase entrega = EntregaFactory.crearEntregaCompleta(
                request.getTipoEntrega(),
                request.getOrigenDireccion(),
                request.getDestinoDireccion(),
                request.getDistanciaKm(),
                request.getPesoKg(),
                request.getDescripcion()
        );
        
        // Validar usando polimorfismo
        if (!entrega.validar()) {
            throw new BusinessException(
                    "El pedido no cumple con los requisitos para " + request.getTipoEntrega());
        }
        
        // Calcular costo usando polimorfismo
        double costo = entrega.calcularCosto();
        
        // Crear entidad
        Pedido pedido = Pedido.builder()
                .codigoPedido(generarCodigoPedido())
                .clienteId(request.getClienteId())
                .origenDireccion(request.getOrigenDireccion())
                .destinoDireccion(request.getDestinoDireccion())
                .origenLatitud(request.getOrigenLatitud())
                .origenLongitud(request.getOrigenLongitud())
                .destinoLatitud(request.getDestinoLatitud())
                .destinoLongitud(request.getDestinoLongitud())
                .distanciaKm(request.getDistanciaKm())
                .pesoKg(request.getPesoKg())
                .tipoEntrega(request.getTipoEntrega())
                .estado(EstadoPedido.RECIBIDO)
                .descripcion(request.getDescripcion())
                .costoEstimado(costo)
                .build();
        
        pedido = pedidoRepository.save(pedido);
        log.info("Pedido creado exitosamente: {}", pedido.getCodigoPedido());
        
        return mapToResponse(pedido);
    }
    
    /**
     * Obtiene un pedido por su ID
     * @param id ID del pedido
     * @return información del pedido
     */
    @Transactional(readOnly = true)
    public PedidoResponse obtenerPedidoPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", id));
        return mapToResponse(pedido);
    }
    
    /**
     * Obtiene un pedido por su código
     * @param codigoPedido código del pedido
     * @return información del pedido
     */
    @Transactional(readOnly = true)
    public PedidoResponse obtenerPedidoPorCodigo(String codigoPedido) {
        Pedido pedido = pedidoRepository.findByCodigoPedido(codigoPedido)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "codigo", codigoPedido));
        return mapToResponse(pedido);
    }
    
    /**
     * Obtiene todos los pedidos de un cliente
     * @param clienteId ID del cliente
     * @return lista de pedidos
     */
    @Transactional(readOnly = true)
    public List<PedidoResponse> obtenerPedidosPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteId(clienteId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene todos los pedidos asignados a un repartidor
     * @param repartidorId ID del repartidor
     * @return lista de pedidos
     */
    @Transactional(readOnly = true)
    public List<PedidoResponse> obtenerPedidosPorRepartidor(Long repartidorId) {
        return pedidoRepository.findByRepartidorId(repartidorId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Actualiza parcialmente un pedido (PATCH)
     * @param id ID del pedido
     * @param request datos a actualizar
     * @return pedido actualizado
     */
    @Transactional
    public PedidoResponse actualizarPedido(Long id, ActualizarPedidoRequest request) {
        log.info("Actualizando pedido: {}", id);
        
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", id));
        
        // Actualizar solo campos no nulos
        if (request.getEstado() != null) {
            EstadoPedido estadoAnterior = pedido.getEstado();
            validarTransicionEstado(pedido.getEstado(), request.getEstado());
            pedido.setEstado(request.getEstado());
            
            if (request.getEstado() == EstadoPedido.ASIGNADO) {
                pedido.setFechaAsignacion(LocalDateTime.now());
            } else if (request.getEstado() == EstadoPedido.ENTREGADO) {
                pedido.setFechaEntrega(LocalDateTime.now());
            }
            
            // Guardar antes de publicar evento
            Pedido pedidoActualizado = pedidoRepository.save(pedido);
            
            // Publicar evento de cambio de estado
            publishEstadoEvent(pedidoActualizado, estadoAnterior, request.getEstado());
            
            return mapToResponse(pedidoActualizado);
        }
        
        if (request.getRepartidorId() != null) {
            pedido.setRepartidorId(request.getRepartidorId());
        }
        
        if (request.getObservaciones() != null) {
            pedido.setObservaciones(request.getObservaciones());
        }
        
        if (request.getOrigenLatitud() != null) {
            pedido.setOrigenLatitud(request.getOrigenLatitud());
        }
        
        if (request.getOrigenLongitud() != null) {
            pedido.setOrigenLongitud(request.getOrigenLongitud());
        }
        
        if (request.getDestinoLatitud() != null) {
            pedido.setDestinoLatitud(request.getDestinoLatitud());
        }
        
        if (request.getDestinoLongitud() != null) {
            pedido.setDestinoLongitud(request.getDestinoLongitud());
        }
        
        pedido = pedidoRepository.save(pedido);
        log.info("Pedido actualizado exitosamente: {}", id);
        
        return mapToResponse(pedido);
    }
    
    /**
     * Cancela un pedido (cancelación lógica)
     * @param id ID del pedido
     */
    @Transactional
    public void cancelarPedido(Long id) {
        log.info("Cancelando pedido: {}", id);
        
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", id));
        
        if (pedido.getEstado() == EstadoPedido.ENTREGADO) {
            throw new BusinessException("No se puede cancelar un pedido ya entregado");
        }
        
        pedido.setEstado(EstadoPedido.CANCELADO);
        pedidoRepository.save(pedido);
        
        log.info("Pedido cancelado exitosamente: {}", id);
    }
    
    /**
     * Obtiene todos los pedidos
     * @return lista de todos los pedidos
     */
    @Transactional(readOnly = true)
    public List<PedidoResponse> obtenerTodosPedidos() {
        return pedidoRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene pedidos por estado
     * @param estado estado del pedido
     * @return lista de pedidos
     */
    @Transactional(readOnly = true)
    public List<PedidoResponse> obtenerPedidosPorEstado(EstadoPedido estado) {
        return pedidoRepository.findByEstado(estado).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    private String generarCodigoPedido() {
        return "P-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private void validarTransicionEstado(EstadoPedido estadoActual, EstadoPedido estadoNuevo) {
        // Validaciones básicas de transición de estado
        if (estadoActual == EstadoPedido.ENTREGADO && estadoNuevo != EstadoPedido.ENTREGADO) {
            throw new BusinessException("No se puede cambiar el estado de un pedido ya entregado");
        }
        
        if (estadoActual == EstadoPedido.CANCELADO && estadoNuevo != EstadoPedido.CANCELADO) {
            throw new BusinessException("No se puede cambiar el estado de un pedido cancelado");
        }
    }
    
    private void publishEstadoEvent(Pedido pedido, EstadoPedido estadoAnterior, EstadoPedido estadoNuevo) {
        PedidoEstadoEvent event = PedidoEstadoEvent.builder()
                .pedidoId(pedido.getId())
                .codigoPedido(pedido.getCodigoPedido())
                .estadoAnterior(estadoAnterior.name())
                .estadoNuevo(estadoNuevo.name())
                .repartidorId(pedido.getRepartidorId())
                .clienteId(pedido.getClienteId())
                .timestamp(LocalDateTime.now())
                .tipoEntrega(pedido.getTipoEntrega().name())
                .descripcion(pedido.getDescripcion())
                .build();
        
        eventPublisher.publishPedidoEstadoCambiado(event);
    }
    
    private PedidoResponse mapToResponse(Pedido pedido) {
        return PedidoResponse.builder()
                .id(pedido.getId())
                .codigoPedido(pedido.getCodigoPedido())
                .clienteId(pedido.getClienteId())
                .origenDireccion(pedido.getOrigenDireccion())
                .destinoDireccion(pedido.getDestinoDireccion())
                .origenLatitud(pedido.getOrigenLatitud())
                .origenLongitud(pedido.getOrigenLongitud())
                .destinoLatitud(pedido.getDestinoLatitud())
                .destinoLongitud(pedido.getDestinoLongitud())
                .distanciaKm(pedido.getDistanciaKm())
                .pesoKg(pedido.getPesoKg())
                .tipoEntrega(pedido.getTipoEntrega())
                .estado(pedido.getEstado())
                .descripcion(pedido.getDescripcion())
                .repartidorId(pedido.getRepartidorId())
                .costoEstimado(pedido.getCostoEstimado())
                .fechaAsignacion(pedido.getFechaAsignacion())
                .fechaEntrega(pedido.getFechaEntrega())
                .observaciones(pedido.getObservaciones())
                .fechaCreacion(pedido.getFechaCreacion())
                .fechaActualizacion(pedido.getFechaActualizacion())
                .build();
    }
}
