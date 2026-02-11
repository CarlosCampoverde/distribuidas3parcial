package com.entregaexpress.logiflow.fleetservice.controller;

import com.entregaexpress.logiflow.common.dto.ApiResponse;
import com.entregaexpress.logiflow.common.enums.EstadoRepartidor;
import com.entregaexpress.logiflow.fleetservice.dto.*;
import com.entregaexpress.logiflow.fleetservice.service.RepartidorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repartidores")
@RequiredArgsConstructor
@Tag(name = "Fleet", description = "Endpoints de gestión de flota y repartidores")
public class RepartidorController {
    
    private final RepartidorService repartidorService;
    
    @PostMapping
    @Operation(summary = "Crear repartidor")
    public ResponseEntity<ApiResponse<RepartidorResponse>> crear(
            @Valid @RequestBody CrearRepartidorRequest request) {
        RepartidorResponse response = repartidorService.crearRepartidor(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Repartidor creado", response));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtener repartidor por ID")
    public ResponseEntity<ApiResponse<RepartidorResponse>> obtener(@PathVariable Long id) {
        RepartidorResponse response = repartidorService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping
    @Operation(summary = "Listar repartidores")
    public ResponseEntity<ApiResponse<List<RepartidorResponse>>> listar(
            @RequestParam(required = false) EstadoRepartidor estado) {
        List<RepartidorResponse> lista = estado != null 
                ? repartidorService.obtenerPorEstado(estado)
                : repartidorService.obtenerTodos();
        return ResponseEntity.ok(ApiResponse.success(lista));
    }
    
    @PatchMapping("/{id}/estado")
    @Operation(summary = "Actualizar estado de repartidor")
    public ResponseEntity<ApiResponse<RepartidorResponse>> actualizarEstado(
            @PathVariable Long id, 
            @Valid @RequestBody ActualizarRepartidorRequest request) {
        RepartidorResponse response = repartidorService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.success("Estado actualizado", response));
    }
}
