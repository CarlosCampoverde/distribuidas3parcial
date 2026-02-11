package com.entregaexpress.logiflow.fleetservice.repository;

import com.entregaexpress.logiflow.common.enums.EstadoRepartidor;
import com.entregaexpress.logiflow.common.enums.TipoVehiculo;
import com.entregaexpress.logiflow.fleetservice.entity.Repartidor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepartidorRepository extends JpaRepository<Repartidor, Long> {
    
    List<Repartidor> findByEstado(EstadoRepartidor estado);
    
    List<Repartidor> findByTipoVehiculo(TipoVehiculo tipoVehiculo);
    
    List<Repartidor> findByEstadoAndTipoVehiculo(EstadoRepartidor estado, TipoVehiculo tipoVehiculo);
}
