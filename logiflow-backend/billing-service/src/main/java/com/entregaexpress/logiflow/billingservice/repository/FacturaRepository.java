package com.entregaexpress.logiflow.billingservice.repository;

import com.entregaexpress.logiflow.billingservice.entity.Factura;
import com.entregaexpress.logiflow.common.enums.EstadoFactura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {
    
    Optional<Factura> findByNumeroFactura(String numeroFactura);
    
    List<Factura> findByClienteId(Long clienteId);
    
    List<Factura> findByEstado(EstadoFactura estado);
    
    Optional<Factura> findByPedidoId(Long pedidoId);
}
