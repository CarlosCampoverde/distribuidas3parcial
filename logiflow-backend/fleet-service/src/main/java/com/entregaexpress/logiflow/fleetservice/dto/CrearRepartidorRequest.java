package com.entregaexpress.logiflow.fleetservice.dto;

import com.entregaexpress.logiflow.common.enums.TipoVehiculo;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearRepartidorRequest {
    
    @NotNull(message = "El ID de usuario es obligatorio")
    private Long usuarioId;
    
    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(max = 200)
    private String nombreCompleto;
    
    @Size(max = 20)
    private String licencia;
    
    @Size(max = 20)
    private String telefono;
    
    @NotNull(message = "El tipo de vehículo es obligatorio")
    private TipoVehiculo tipoVehiculo;
    
    @Size(max = 20)
    private String placaVehiculo;
}
