package com.entregaexpress.logiflow.billingservice.controller;

import com.entregaexpress.logiflow.billingservice.dto.FacturaResponse;
import com.entregaexpress.logiflow.billingservice.dto.GenerarFacturaRequest;
import com.entregaexpress.logiflow.billingservice.service.FacturaService;
import com.entregaexpress.logiflow.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/facturas")
@RequiredArgsConstructor
@Tag(name = "Billing", description = "Endpoints de facturación (versión mínima)")
public class FacturaController {
    
    private final FacturaService facturaService;
    
    @PostMapping
    @Operation(summary = "Generar factura", description = "Genera factura en estado BORRADOR")
    public ResponseEntity<ApiResponse<FacturaResponse>> generar(
            @Valid @RequestBody GenerarFacturaRequest request) {
        FacturaResponse response = facturaService.generarFactura(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Factura generada", response));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtener factura")
    public ResponseEntity<ApiResponse<FacturaResponse>> obtener(@PathVariable Long id) {
        FacturaResponse response = facturaService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Listar facturas por cliente")
    public ResponseEntity<ApiResponse<List<FacturaResponse>>> listarPorCliente(
            @PathVariable Long clienteId) {
        List<FacturaResponse> lista = facturaService.obtenerPorCliente(clienteId);
        return ResponseEntity.ok(ApiResponse.success(lista));
    }
}
