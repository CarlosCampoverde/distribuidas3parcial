package com.entregaexpress.logiflow.authservice.repository;

import com.entregaexpress.logiflow.authservice.entity.Usuario;
import com.entregaexpress.logiflow.common.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByUsername(String username);
    
    Optional<Usuario> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    List<Usuario> findByRole(Role role);
    
    List<Usuario> findByActivoTrue();
}
