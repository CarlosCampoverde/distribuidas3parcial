package com.entregaexpress.logiflow.authservice.service;

import com.entregaexpress.logiflow.authservice.dto.*;
import com.entregaexpress.logiflow.authservice.entity.TokenRevocado;
import com.entregaexpress.logiflow.authservice.entity.Usuario;
import com.entregaexpress.logiflow.authservice.repository.TokenRevocadoRepository;
import com.entregaexpress.logiflow.authservice.repository.UsuarioRepository;
import com.entregaexpress.logiflow.common.exception.BusinessException;
import com.entregaexpress.logiflow.common.exception.UnauthorizedException;
import com.entregaexpress.logiflow.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Servicio de autenticación y autorización
 * Implementa operaciones CRUD para autenticación con transacciones ACID
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UsuarioRepository usuarioRepository;
    private final TokenRevocadoRepository tokenRevocadoRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    @Value("${jwt.expiration:86400000}")
    private Long jwtExpiration;
    
    /**
     * Registra un nuevo usuario en el sistema
     * @param request datos del usuario a registrar
     * @return respuesta con token JWT
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registrando nuevo usuario: {}", request.getUsername());
        
        // Validar que no exista el username
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("El username ya está en uso");
        }
        
        // Validar que no exista el email
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("El email ya está en uso");
        }
        
        // Crear usuario
        Usuario usuario = Usuario.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nombreCompleto(request.getNombreCompleto())
                .telefono(request.getTelefono())
                .role(request.getRole())
                .activo(true)
                .zonaId(request.getZonaId())
                .tipoFlota(request.getTipoFlota())
                .build();
        
        usuario = usuarioRepository.save(usuario);
        log.info("Usuario registrado exitosamente con ID: {}", usuario.getId());
        
        // Generar token
        String token = jwtUtil.generateToken(
                usuario.getUsername(),
                usuario.getRole().name(),
                "full-access",
                usuario.getZonaId(),
                usuario.getTipoFlota()
        );
        
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtExpiration)
                .username(usuario.getUsername())
                .role(usuario.getRole().name())
                .userId(usuario.getId())
                .build();
    }
    
    /**
     * Autentica un usuario existente
     * @param request credenciales de login
     * @return respuesta con token JWT
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("Intento de login para usuario: {}", request.getUsername());
        
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));
        
        if (!usuario.getActivo()) {
            throw new UnauthorizedException("Usuario inactivo");
        }
        
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new UnauthorizedException("Credenciales inválidas");
        }
        
        log.info("Login exitoso para usuario: {}", usuario.getUsername());
        
        String token = jwtUtil.generateToken(
                usuario.getUsername(),
                usuario.getRole().name(),
                "full-access",
                usuario.getZonaId(),
                usuario.getTipoFlota()
        );
        
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtExpiration)
                .username(usuario.getUsername())
                .role(usuario.getRole().name())
                .userId(usuario.getId())
                .build();
    }
    
    /**
     * Refresca un token JWT existente
     * @param request token a refrescar
     * @return nuevo token JWT
     */
    @Transactional(readOnly = true)
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String token = request.getToken();
        
        // Validar que el token no esté revocado
        if (tokenRevocadoRepository.existsByToken(token)) {
            throw new UnauthorizedException("Token revocado");
        }
        
        String username = jwtUtil.extractUsername(token);
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("Usuario no encontrado"));
        
        if (!jwtUtil.validateToken(token, username)) {
            throw new UnauthorizedException("Token inválido");
        }
        
        String newToken = jwtUtil.generateToken(
                usuario.getUsername(),
                usuario.getRole().name(),
                "full-access",
                usuario.getZonaId(),
                usuario.getTipoFlota()
        );
        
        return AuthResponse.builder()
                .token(newToken)
                .tokenType("Bearer")
                .expiresIn(jwtExpiration)
                .username(usuario.getUsername())
                .role(usuario.getRole().name())
                .userId(usuario.getId())
                .build();
    }
    
    /**
     * Revoca un token JWT
     * @param token token a revocar
     */
    @Transactional
    public void revokeToken(String token) {
        if (tokenRevocadoRepository.existsByToken(token)) {
            return; // Ya está revocado
        }
        
        LocalDateTime expiration = jwtUtil.extractExpiration(token)
                .toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime();
        
        TokenRevocado tokenRevocado = TokenRevocado.builder()
                .token(token)
                .fechaRevocacion(LocalDateTime.now())
                .fechaExpiracion(expiration)
                .build();
        
        tokenRevocadoRepository.save(tokenRevocado);
        log.info("Token revocado exitosamente");
    }
    
    /**
     * Limpia tokens expirados de la base de datos
     */
    @Transactional
    public void cleanupExpiredTokens() {
        tokenRevocadoRepository.deleteByFechaExpiracionBefore(LocalDateTime.now());
        log.info("Tokens expirados eliminados");
    }
}
