package com.entregaexpress.logiflow.authservice.repository;

import com.entregaexpress.logiflow.authservice.entity.TokenRevocado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TokenRevocadoRepository extends JpaRepository<TokenRevocado, Long> {
    
    Optional<TokenRevocado> findByToken(String token);
    
    boolean existsByToken(String token);
    
    void deleteByFechaExpiracionBefore(LocalDateTime fecha);
}
