package com.entregaexpress.logiflow.pedidoservice.repository;

import com.entregaexpress.logiflow.common.enums.EstadoPedido;
import com.entregaexpress.logiflow.common.enums.TipoEntrega;
import com.entregaexpress.logiflow.pedidoservice.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    Optional<Pedido> findByCodigoPedido(String codigoPedido);
    
    List<Pedido> findByClienteId(Long clienteId);
    
    List<Pedido> findByRepartidorId(Long repartidorId);
    
    List<Pedido> findByEstado(EstadoPedido estado);
    
    List<Pedido> findByTipoEntrega(TipoEntrega tipoEntrega);
    
    List<Pedido> findByEstadoAndTipoEntrega(EstadoPedido estado, TipoEntrega tipoEntrega);
    
    @Query("SELECT p FROM Pedido p WHERE p.fechaCreacion BETWEEN :inicio AND :fin")
    List<Pedido> findByFechaCreacionBetween(
            @Param("inicio") LocalDateTime inicio, 
            @Param("fin") LocalDateTime fin);
    
    @Query("SELECT p FROM Pedido p WHERE p.repartidorId = :repartidorId AND p.estado IN :estados")
    List<Pedido> findByRepartidorIdAndEstadoIn(
            @Param("repartidorId") Long repartidorId, 
            @Param("estados") List<EstadoPedido> estados);
    
    long countByEstado(EstadoPedido estado);
}
