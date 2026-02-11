package com.entregaexpress.logiflow.authservice.exception;

import com.entregaexpress.logiflow.common.dto.ErrorResponse;
import com.entregaexpress.logiflow.common.exception.BusinessException;
import com.entregaexpress.logiflow.common.exception.ResourceNotFoundException;
import com.entregaexpress.logiflow.common.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Manejador global de excepciones
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(
            UnauthorizedException ex, HttpServletRequest request) {
        log.error("Error de autorización: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                ex.getMessage(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException ex, HttpServletRequest request) {
        log.error("Error de negocio: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "Business Error",
                ex.getMessage(),
                request.getRequestURI()
        );
        
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {
        log.error("Recurso no encontrado: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.error("Error de validación: {}", ex.getMessage());
        
        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    return fieldName + ": " + errorMessage;
                })
                .collect(Collectors.toList());
        
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                "Errores de validación en los datos enviados",
                request.getRequestURI()
        );
        error.setDetails(errors);
        
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        log.error("Error interno del servidor", ex);
        
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "Ha ocurrido un error interno. Por favor contacte al administrador.",
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
