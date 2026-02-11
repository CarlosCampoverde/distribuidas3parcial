# Guía de Desarrollo - LogiFlow Fase 1

## Decisiones Técnicas

### 1. Arquitectura de Microservicios

**Decisión**: Separar funcionalidades en servicios independientes
**Justificación**:
- Escalabilidad independiente por servicio
- Despliegue y desarrollo independiente
- Aislamiento de fallos
- Permite equipos especializados por dominio

### 2. Spring Cloud Gateway vs otros API Gateways

**Decisión**: Spring Cloud Gateway
**Justificación**:
- Integración nativa con Spring Boot
- Soporte reactivo (WebFlux)
- Filtros personalizables para JWT
- Configuración declarativa en YAML
- Rate limiting integrado
- Alternativas consideradas: Kong, Apigee (más complejos para Fase 1)

### 3. H2 Database en memoria

**Decisión**: H2 para desarrollo, PostgreSQL preparado
**Justificación**:
- Rapidez en desarrollo y testing
- No requiere configuración externa
- Fácil migración a PostgreSQL (drivers incluidos)
- Schema auto-creado con JPA

### 4. Transacciones ACID Locales

**Decisión**: `@Transactional` en cada servicio, sin saga en Fase 1
**Justificación**:
- Fase 1 solo requiere transacciones locales
- Sagas orquestadas se implementan en fases posteriores
- Simplicidad y cumplimiento de ACID por servicio

### 5. Patrones de Diseño Implementados

#### Factory Pattern  (`EntregaFactory`)
- **Razón**: Encapsular lógica de creación de objetos complejos
- **Beneficio**: Desacopla creación de uso

#### Template Method (`EntregaBase`, `VehiculoEntrega`)
- **Razón**: Definir estructura de algoritmo, dejando pasos a subclases
- **Beneficio**: Reutilización de código + flexibilidad

#### Strategy (via Interfaces)
- **Razón**: Permitir intercambio de algoritmos en tiempo de ejecución
- **Beneficio**: Polimorfismo, extensibilidad

### 6. JWT para Autenticación

**Decisión**: JWT stateless con firma HMAC-SHA256
**Justificación**:
- Sin estado en servidor (escalable)
- Claims personalizados (role, zone_id, fleet_type)
- Validación rápida en Gateway
- Revocación mediante blacklist en base de datos

## Diagrama de Arquitectura

```
┌─────────────────────────────────────────────────────────────┐
│                         Clientes                             │
│    (Apps móviles, Frontend web, Sistemas externos)          │
└────────────────┬────────────────────────────────────────────┘
                 │
                 │ HTTP/HTTPS
                 ▼
┌─────────────────────────────────────────────────────────────┐
│                     API GATEWAY :8080                        │
│  ┌──────────────────────────────────────────────────────┐   │
│  │ • Enrutamiento por prefijo                          │   │
│  │ • Validación JWT (JwtAuthenticationFilter)          │   │
│  │ • Rate Limiting (100 req/min configurado)           │   │
│  │ • Logging centralizado (método, URI, userId, tiempo)│   │
│  │ • CORS configurado                                  │   │
│  └──────────────────────────────────────────────────────┘   │
└────────┬──────────┬──────────┬──────────┬────────────────────┘
         │          │          │          │
    ┌────▼───┐ ┌───▼────┐ ┌───▼────┐ ┌──▼─────┐
    │ Auth   │ │ Pedido │ │ Fleet  │ │Billing │
    │Service │ │Service │ │Service │ │Service │
    │ :8081  │ │ :8082  │ │ :8083  │ │ :8084  │
    └────┬───┘ └───┬────┘ └───┬────┘ └──┬─────┘
         │         │          │          │
    ┌────▼─────────▼──────────▼──────────▼─────┐
    │          Bases de Datos H2                │
    │  (authdb, pedidodb, fleetdb, billingdb)   │
    └───────────────────────────────────────────┘
```

## Flujo de Autenticación

```
1. Cliente → POST /api/auth/register
2. Auth Service → Crear usuario + hash password
3. Auth Service → Generar JWT con claims
4. Cliente ← { token, role, userId }

5. Cliente → POST /api/pedidos (Header: Authorization: Bearer {token})
6. API Gateway → JwtAuthenticationFilter.filter()
7. Gateway → Validar firma + expiraci ón
8. Gateway → Agregar headers X-User-Id, X-User-Role
9. Pedido Service ← Request con headers enriquecidos
10. Pedido Service → Procesar request
11. Cliente ← Response
```

## Modelo de Datos

### Auth Service
```sql
usuarios (id, username, email, password, nombre_completo, 
          telefono, role, activo, zona_id, tipo_flota,
          fecha_creacion, fecha_actualizacion)

tokens_revocados (id, token, fecha_revocacion, fecha_expiracion)
```

### Pedido Service
```sql
pedidos (id, codigo_pedido, cliente_id, 
         origen_direccion, destino_direccion,
         origen_latitud, origen_longitud,
         destino_latitud, destino_longitud,
         distancia_km, peso_kg, tipo_entrega, estado,
         descripcion, repartidor_id, costo_estimado,
         fecha_asignacion, fecha_entrega, observaciones,
         fecha_creacion, fecha_actualizacion)
```

### Fleet Service
```sql
repartidores (id, usuario_id, nombre_completo, licencia,
              telefono, tipo_vehiculo, placa_vehiculo, estado,
              ubicacion_latitud, ubicacion_longitud,
              ultima_actualizacion_ubicacion,
              fecha_creacion, fecha_actualizacion)
```

### Billing Service
```sql
facturas (id, numero_factura, pedido_id, cliente_id,
          tipo_entrega, distancia_km, peso_kg,
          subtotal, impuestos, total, estado,
          fecha_creacion)
```

## Testing

### Prueba de Integración Completa

```bash
#!/bin/bash

echo "=== Test de Integración LogiFlow Fase 1 ==="

# 1. Registro
echo "\n1. Registrando usuario..."
REGISTER_RESP=$(curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "123456",
    "nombreCompleto": "Test User",
    "role": "CLIENTE"
  }')
echo $REGISTER_RESP

# 2. Login
echo "\n2. Haciendo login..."
LOGIN_RESP=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "123456"
  }')
TOKEN=$(echo $LOGIN_RESP | jq -r '.data.token')
echo "Token obtenido: ${TOKEN:0:20}..."

# 3. Crear pedido
echo "\n3. Creando pedido urbano..."
PEDIDO_RESP=$(curl -s -X POST http://localhost:8080/api/pedidos \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": 1,
    "origenDireccion": "Av. Amazonas, Quito",
    "destinoDireccion": "La Carolina, Quito",
    "distanciaKm": 5.0,
    "pesoKg": 3.0,
    "tipoEntrega": "URBANA",
    "descripcion": "Documentos"
  }')
PEDIDO_ID=$(echo $PEDIDO_RESP | jq -r '.data.id')
echo "Pedido creado: $PEDIDO_ID"

# 4. Generar factura
echo "\n4. Generando factura..."
FACTURA_RESP=$(curl -s -X POST http://localhost:8080/api/facturas \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"pedidoId\": $PEDIDO_ID,
    \"clienteId\": 1,
    \"tipoEntrega\": \"URBANA\",
    \"distanciaKm\": 5.0,
    \"pesoKg\": 3.0
  }")
echo $FACTURA_RESP | jq '.data'

echo "\n=== Test Completado ==="
```

## Troubleshooting

### Problema: "401 Unauthorized" en todas las peticiones

**Solución**:
1. Verificar que el token esté en el header: `Authorization: Bearer {token}`
2. Revisar que el secret JWT sea el mismo en Gateway y Auth Service
3. Verificar que el token no esté expirado (24h por defecto)

### Problema: "Connection refused" al Gateway

**Solución**:
1. Verificar que todos los servicios estén corriendo en sus puertos
2. Confirmar URLs en GatewayConfig (`http://localhost:808X`)
3. Revisar logs del API Gateway para ver errores de routing

### Problema: "Could not autowire JwtUtil" en Common

**Solución**:
- Agregar `@ComponentScan` en las aplicaciones de los servicios
- Incluir paquete `com.entregaexpress.logiflow.common`

## Próximos Pasos

1. **Migrar a PostgreSQL**: Cambiar configuración en `application.yml`
2. **Agregar tests unitarios**: JUnit 5 + Mockito
3. **Implementar Saga Pattern**: Para operaciones distribuidas
4. **CI/CD**: GitHub Actions para build y tests automáticos
5. **Dockerización**: Crear Dockerfiles para cada servicio
