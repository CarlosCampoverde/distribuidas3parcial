package com.entregaexpress.logiflow.graphqlservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlotaResumen {
    private Integer total;
    private Integer disponibles;
    private Integer enRuta;
    private Integer mantenimiento;
}
