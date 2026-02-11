package com.entregaexpress.logiflow.pedidoservice.controller;

import com.entregaexpress.logiflow.common.dto.ApiResponse;
import com.entregaexpress.logiflow.common.enums.EstadoPedido;
import com.entregaexpress.logiflow.pedidoservice.dto.*;
import com.entregaexpress.logiflow.pedidoservice.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de pedidos
 * Endpoints CRUD: GET, POST, PATCH, DELETE
 */
@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Endpoints de gestión de pedidos")
public class PedidoController {
    
    private final PedidoService pedidoService;
    
    /**
     * POST /api/pedidos - Crear nuevo pedido
     */
    @PostMapping
    @Operation(summary = "Crear pedido", description = "Crea un nuevo pedido en el sistema")
    public ResponseEntity<ApiResponse<PedidoResponse>> crearPedido(
            @Valid @RequestBody CrearPedidoRequest request) {
        PedidoResponse response = pedidoService.crearPedido(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Pedido creado exitosamente", response));
    }
    
    /**
     * GET /api/pedidos/{id} - Obtener pedido por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener pedido por ID", description = "Obtiene la información detallada de un pedido")
    public ResponseEntity<ApiResponse<PedidoResponse>> obtenerPedido(@PathVariable Long id) {
        PedidoResponse response = pedidoService.obtenerPedidoPorId(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * GET /api/pedidos/codigo/{codigo} - Obtener pedido por código
     */
    @GetMapping("/codigo/{codigo}")
    @Operation(summary = "Obtener pedido por código", description = "Obtiene un pedido usando su código único")
    public ResponseEntity<ApiResponse<PedidoResponse>> obtenerPedidoPorCodigo(
            @PathVariable String codigo) {
        PedidoResponse response = pedidoService.obtenerPedidoPorCodigo(codigo);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * GET /api/pedidos - Obtener todos los pedidos o filtrar por parámetros
     */
    @GetMapping
    @Operation(summary = "Listar pedidos", description = "Obtiene lista de pedidos, opcionalmente filtrados")
    public ResponseEntity<ApiResponse<List<PedidoResponse>>> listarPedidos(
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) Long repartidorId,
            @RequestParam(required = false) EstadoPedido estado) {
        
        List<PedidoResponse> pedidos;
        
        if (clienteId != null) {
            pedidos = pedidoService.obtenerPedidosPorCliente(clienteId);
        } else if (repartidorId != null) {
            pedidos = pedidoService.obtenerPedidosPorRepartidor(repartidorId);
        } else if (estado != null) {
            pedidos = pedidoService.obtenerPedidosPorEstado(estado);
        } else {
            pedidos = pedidoService.obtenerTodosPedidos();
        }
        
        return ResponseEntity.ok(ApiResponse.success(pedidos));
    }
    
    /**
     * PATCH /api/pedidos/{id} - Actualización parcial de pedido
     */
    @PatchMapping("/{id}")
    @Operation(summary = "Actualizar pedido", 
               description = "Actualización parcial de campos del pedido (estado, repartidor, etc.)")
    public ResponseEntity<ApiResponse<PedidoResponse>> actualizarPedido(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarPedidoRequest request) {
        PedidoResponse response = pedidoService.actualizarPedido(id, request);
        return ResponseEntity.ok(ApiResponse.success("Pedido actualizado exitosamente", response));
    }
    
    /**
     * DELETE /api/pedidos/{id} - Cancelar pedido (cancelación lógica)
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar pedido", description = "Cancela un pedido cambiándolo a estado CANCELADO")
    public ResponseEntity<ApiResponse<Void>> cancelarPedido(@PathVariable Long id) {
        pedidoService.cancelarPedido(id);
        return ResponseEntity.ok(ApiResponse.success("Pedido cancelado exitosamente", null));
    }
}
