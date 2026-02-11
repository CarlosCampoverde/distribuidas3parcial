package com.entregaexpress.logiflow.pedidoservice.dto;

import com.entregaexpress.logiflow.common.enums.TipoEntrega;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitud de creación de pedido
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearPedidoRequest {
    
    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clienteId;
    
    @NotBlank(message = "La dirección de origen es obligatoria")
    @Size(max = 500)
    private String origenDireccion;
    
    @NotBlank(message = "La dirección de destino es obligatoria")
    @Size(max = 500)
    private String destinoDireccion;
    
    private Double origenLatitud;
    private Double origenLongitud;
    private Double destinoLatitud;
    private Double destinoLongitud;
    
    @NotNull(message = "La distancia es obligatoria")
    @Positive(message = "La distancia debe ser mayor a 0")
    private Double distanciaKm;
    
    @NotNull(message = "El peso es obligatorio")
    @Positive(message = "El peso debe ser mayor a 0")
    private Double pesoKg;
    
    @NotNull(message = "El tipo de entrega es obligatorio")
    private TipoEntrega tipoEntrega;
    
    @Size(max = 1000)
    private String descripcion;
}
