package com.entregaexpress.logiflow.authservice.service;

import com.entregaexpress.logiflow.authservice.dto.UsuarioResponse;
import com.entregaexpress.logiflow.authservice.entity.Usuario;
import com.entregaexpress.logiflow.authservice.repository.UsuarioRepository;
import com.entregaexpress.logiflow.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de gestión de usuarios
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    
    /**
     * Obtiene un usuario por su ID
     * @param id ID del usuario
     * @return información del usuario
     */
    @Transactional(readOnly = true)
    public UsuarioResponse getUserById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        
        return mapToResponse(usuario);
    }
    
    /**
     * Obtiene un usuario por su username
     * @param username username del usuario
     * @return información del usuario
     */
    @Transactional(readOnly = true)
    public UsuarioResponse getUserByUsername(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "username", username));
        
        return mapToResponse(usuario);
    }
    
    /**
     * Obtiene todos los usuarios activos
     * @return lista de usuarios activos
     */
    @Transactional(readOnly = true)
    public List<UsuarioResponse> getAllActiveUsers() {
        return usuarioRepository.findByActivoTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Desactiva un usuario
     * @param id ID del usuario
     */
    @Transactional
    public void deactivateUser(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
        log.info("Usuario {} desactivado", id);
    }
    
    /**
     * Activa un usuario
     * @param id ID del usuario
     */
    @Transactional
    public void activateUser(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
        log.info("Usuario {} activado", id);
    }
    
    private UsuarioResponse mapToResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .nombreCompleto(usuario.getNombreCompleto())
                .telefono(usuario.getTelefono())
                .role(usuario.getRole())
                .activo(usuario.getActivo())
                .zonaId(usuario.getZonaId())
                .tipoFlota(usuario.getTipoFlota())
                .fechaCreacion(usuario.getFechaCreacion())
                .build();
    }
}
