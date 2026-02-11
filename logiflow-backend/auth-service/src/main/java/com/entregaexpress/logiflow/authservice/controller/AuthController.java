package com.entregaexpress.logiflow.authservice.controller;

import com.entregaexpress.logiflow.authservice.dto.*;
import com.entregaexpress.logiflow.authservice.service.AuthService;
import com.entregaexpress.logiflow.authservice.service.UsuarioService;
import com.entregaexpress.logiflow.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para autenticación y autorización
 * Endpoints: POST /login, POST /register, POST /token/refresh, POST /logout
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints de autenticación y autorización")
public class AuthController {
    
    private final AuthService authService;
    private final UsuarioService usuarioService;
    
    /**
     * POST /api/auth/register - Registro de nuevo usuario
     */
    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario", description = "Crea una nueva cuenta de usuario en el sistema")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de registro inválidos")
    })
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Usuario registrado exitosamente", response));
    }
    
    /**
     * POST /api/auth/login - Login de usuario existente
     */
    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y devuelve un token JWT")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login exitoso"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login exitoso", response));
    }
    
    /**
     * POST /api/auth/token/refresh - Refrescar token JWT
     */
    @PostMapping("/token/refresh")
    @Operation(summary = "Refrescar token", description = "Genera un nuevo token JWT a partir de uno existente")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token refrescado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token inválido o expirado")
    })
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("Token refrescado exitosamente", response));
    }
    
    /**
     * POST /api/auth/logout - Cerrar sesión (revocar token)
     */
    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión", description = "Revoca el token JWT del usuario")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Sesión cerrada exitosamente")
    })
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        authService.revokeToken(token);
        return ResponseEntity.ok(ApiResponse.success("Sesión cerrada exitosamente", null));
    }
    
    /**
     * GET /api/auth/me - Obtener información del usuario autenticado
     */
    @GetMapping("/me")
    @Operation(summary = "Obtener perfil", description = "Obtiene la información del usuario autenticado")
    public ResponseEntity<ApiResponse<UsuarioResponse>> getCurrentUser(@RequestParam String username) {
        UsuarioResponse response = usuarioService.getUserByUsername(username);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
