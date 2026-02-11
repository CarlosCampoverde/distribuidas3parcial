# 🚀 LogiFlow - Sistema Distribuido de Entregas

**Sistema de gestión de entregas basado en microservicios con Spring Boot 3.2 y Kubernetes**

> Proyecto para el 3er Parcial de Sistemas Distribuidos

## 📋 Contenido Rápido

- [Inicio Rápido](#-inicio-rápido-docker)
- [Kubernetes](#-kubernetes--minikube)
- [Arquitectura](#-arquitectura)
- [Servicios](#-servicios)
- [Acceso a APIs](#-acceso-a-las-apis)

---

# LogiFlow - Plataforma Integral de Gestión de Operaciones para Delivery

**Fase 1: Backend - Servicios REST y API Gateway**

## Descripción

LogiFlow es una plataforma de microservicios para gestionar operaciones de delivery multinivel de EntregaExpress S.A. Este proyecto implementa la arquitectura backend con servicios REST, autenticación JWT y API Gateway como punto de entrada único.

## Arquitectura

### Microservicios Implementados

1. **common** - Módulo compartido con DTOs, excepciones y utilidades
2. **auth-service** (Puerto 8081) - Autenticación y autorización con JWT
3. **pedido-service** (Puerto 8082) - Gestión de pedidos y tipos de entrega
4. **fleet-service** (Puerto 8083) - Gestión de flota y repartidores
5. **billing-service** (Puerto 8084) - Facturación básica (versión mínima)
6. **api-gateway** (Puerto 8080) - Gateway con enrutamiento y validación JWT

### Patrones de Diseño Implementados

#### 1. Factory Pattern
- **Ubicación**: `pedido-service/factory/EntregaFactory.java`
- **Propósito**: Creación de tipos específicos de entrega (Urbana, Intermunicipal, Nacional)

#### 2. Template Method Pattern
- **Ubicación**: `pedido-service/model/EntregaBase.java`
- **Propósito**: Define flujo común de procesamiento de entregas con pasos personalizables

#### 3. Strategy Pattern (mediante Interfaces)
- **Interface**: `IProcesableEntrega`, `IRegistrableGPS`
- **Propósito**: Contrato de comportamiento interoperable para entregas

### Principios de OOP Aplicados

#### Clases Abstractas (No Instanciables)
- `EntregaBase` - Comportamiento común de tipos de entrega
- `VehiculoEntrega` - Comportamiento común de vehículos de flota

#### Subclases Concretas
**Entregas**:
- `EntregaUrbana` - Para motorizados (max 15 km, 10 kg)
- `EntregaIntermunicipal` - Para vehículos livianos (15-200 km, 100 kg)
- `EntregaNacional` - Para camiones (>200 km, 5000 kg)

**Vehículos**:
- `Motorizado` - Entregas urbanas
- `VehiculoLiviano` - Entregas intermunicipales
- `Camion` - Entregas nacionales

## Tecnologías

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Cloud Gateway 2023.0.0**
- **Spring Data JPA**
- **H2 Database** (en memoria para desarrollo)
- **JWT (jjwt 0.12.3)**
- **OpenAPI 3.0 / Swagger UI**
- **Lombok**
- **Maven**

## Estructura del Proyecto

```
logiflow-backend/
├── common/                  # Módulo compartido
│   └── src/main/java/
│       ├── enums/          # Role, EstadoPedido, TipoEntrega, etc.
│       ├── dto/            # ApiResponse, ErrorResponse
│       ├── exception/      # Excepciones personalizadas
│       └── util/           # JwtUtil
│
├── auth-service/           # Servicio de autenticación
│   └── src/main/java/
│       ├── entity/         # Usuario, TokenRevocado
│       ├── repository/     # UsuarioRepository
│       ├── service/        # AuthService
│       └── controller/     # AuthController
│
├── pedido-service/         # Servicio de pedidos
│   └── src/main/java/
│       ├── model/          # Clases abstractas e interfaces
│       ├── factory/        # EntregaFactory
│       ├── entity/         # Pedido
│       └── service/        # PedidoService (con transacciones ACID)
│
├── fleet-service/          # Servicio de flota
│   └── src/main/java/
│       ├── model/          # VehiculoEntrega (abstracta) y subclases
│       └── entity/         # Repartidor
│
├── billing-service/        # Servicio de facturación
│   └── src/main/java/
│       └── entity/         # Factura
│
└── api-gateway/            # API Gateway
    └── src/main/java/
        ├── filter/         # JwtAuthenticationFilter, LoggingFilter
        └── config/         # GatewayConfig (enrutamiento)
```

## Instalación y Ejecución

### Prerrequisitos
- JDK 17 o superior
- Maven 3.8+

### Compilar el proyecto

```bash
cd logiflow-backend
mvn clean install
```

### Ejecutar microservicios (en orden)

```bash
# Terminal 1 - Auth Service
cd auth-service
mvn spring-boot:run

# Terminal 2 - Pedido Service
cd pedido-service
mvn spring-boot:run

# Terminal 3 - Fleet Service
cd fleet-service
mvn spring-boot:run

# Terminal 4 - Billing Service
cd billing-service
mvn spring-boot:run

# Terminal 5 - API Gateway
cd api-gateway
mvn spring-boot:run
```

### Acceso a Servicios

#### API Gateway (Punto de entrada único)
- **URL Base**: `http://localhost:8080`

#### Documentación Swagger
- Auth Service: http://localhost:8080/auth/swagger-ui.html
- Pedido Service: http://localhost:8080/pedido/swagger-ui.html
- Fleet Service: http://localhost:8080/fleet/swagger-ui.html
- Billing Service: http://localhost:8080/billing/swagger-ui.html

#### Consolas H2 (bases de datos en memoria)
- Auth DB: http://localhost:8081/h2-console
- Pedido DB: http://localhost:8082/h2-console
- Fleet DB: http://localhost:8083/h2-console
- Billing DB: http://localhost:8084/h2-console

Credenciales H2:
- JDBC URL: `jdbc:h2:mem:[nombre]db`
- Username: `sa`
- Password: (vacío)

## Endpoints Principales

### Auth Service (vía Gateway: `http://localhost:8080/api/auth`)

```bash
# Registro de usuario
POST /api/auth/register
{
  "username": "cliente1",
  "email": "cliente1@example.com",
  "password": "123456",
  "nombreCompleto": "Cliente Uno",
  "telefono": "0999999999",
  "role": "CLIENTE"
}

# Login
POST /api/auth/login
{
  "username": "cliente1",
  "password": "123456"
}

# Refresh Token
POST /api/auth/token/refresh
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}

# Logout (Requiere token)
POST /api/auth/logout
Header: Authorization: Bearer {token}
```

### Pedido Service (vía Gateway: `http://localhost:8080/api/pedidos`)

```bash
# Crear pedido (Requiere autenticación)
POST /api/pedidos
Header: Authorization: Bearer {token}
{
  "clienteId": 1,
  "origenDireccion": "Av. Amazonas y Naciones Unidas, Quito",
  "destinoDireccion": "La Mariscal, Quito",
  "distanciaKm": 5.0,
  "pesoKg": 2.5,
  "tipoEntrega": "URBANA",
  "descripcion": "Documentos urgentes"
}

# Obtener pedido por ID
GET /api/pedidos/{id}

# Listar pedidos por cliente
GET /api/pedidos?clienteId=1

# Actualizar estado (PATCH)
PATCH /api/pedidos/{id}
{
  "estado": "ASIGNADO",
  "repartidorId": 1
}

# Cancelar pedido
DELETE /api/pedidos/{id}
```

### Fleet Service (vía Gateway: `http://localhost:8080/api/repartidores`)

```bash
# Crear repartidor
POST /api/repartidores
{
  "usuarioId": 2,
  "nombreCompleto": "Juan Pérez",
  "licencia": "EC-123456",
  "telefono": "098765432",
  "tipoVehiculo": "MOTORIZADO",
  "placaVehiculo": "PBA-1234"
}

# Listar repartidores disponibles
GET /api/repartidores?estado=DISPONIBLE

# Actualizar estado
PATCH /api/repartidores/{id}/estado
{
  "estado": "EN_RUTA",
  "ubicacionLatitud": -0.1807,
  "ubicacionLongitud": -78.4678
}
```

### Billing Service (vía Gateway: `http://localhost:8080/api/facturas`)

```bash
# Generar factura
POST /api/facturas
{
  "pedidoId": 1,
  "clienteId": 1,
  "tipoEntrega": "URBANA",
  "distanciaKm": 5.0,
  "pesoKg": 2.5
}

# Obtener factura
GET /api/facturas/{id}

# Listar facturas de cliente
GET /api/facturas/cliente/{clienteId}
```

## Características Implementadas (Fase 1)

### ✅ Cumplimiento de Requisitos

#### Microservicios REST con CRUD
- ✅ AuthService: registro, login, refresh/revoke token
- ✅ PedidoService: crear, consultar, modificar (PATCH), cancelar
- ✅ FleetService: gestión de repartidores y vehículos
- ✅ BillingService: cálculo de tarifa + generación de factura BORRADOR

#### API Gateway
- ✅ Enrutamiento por prefijo (`/api/auth/**`, `/api/pedidos/**`, etc.)
- ✅ Validación JWT en todas las rutas protegidas (401/403)
- ✅ Rate limiting configurado (implementación básica en Fase 1)
- ✅ Logging centralizado (método, URI, código, userId)

#### Transacciones ACID
- ✅ Todas las operaciones de escritura usan `@Transactional`
- ✅ Validación de esquema con Bean Validation
- ✅ Manejo de excepciones con rollback automático

#### Documentación OpenAPI 3.0
- ✅ Swagger UI accesible para cada microservicio
- ✅ Ejemplos de request/response
- ✅ Códigos de estado HTTP documentados

#### Principios de Diseño OOP
- ✅ Clase abstracta `EntregaBase` (comportamiento común, no instanciable)
- ✅ Subclases: `EntregaUrbana`, `EntregaIntermunicipal`, `EntregaNacional`
- ✅ Clase abstracta `VehiculoEntrega` con subclases por tipo
- ✅ Interfaces: `IProcesableEntrega`, `IRegistrableGPS`

#### Patrones de Diseño
- ✅ **Factory**: `EntregaFactory` para creación de tipos de entrega
- ✅ **Template Method**: `EntregaBase.procesarEntrega()`
- ✅ **Strategy**: Interfaces para comportamiento polimórfico

## Pruebas

### Pruebas Manuales con cURL

```bash
# 1. Registrar usuario
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test1",
    "email": "test1@example.com",
    "password": "123456",
    "nombreCompleto": "Usuario Test",
    "role": "CLIENTE"
  }'

# 2. Login y obtener token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test1",
    "password": "123456"
  }'

# 3. Crear pedido con token
TOKEN="eyJhbGciOiJIUzI1NiJ9..."  # Usar el token del login
curl -X POST http://localhost:8080/api/pedidos \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": 1,
    "origenDireccion": "Quito Norte",
    "destinoDireccion": "Quito Sur",
    "distanciaKm": 10.0,
    "pesoKg": 5.0,
    "tipoEntrega": "URBANA"
  }'
```

## Próximas Fases

### Fase 2: GraphQL, Mensajería y WebSocket
- API GraphQL para consultas complejas
- Sistema de mensajería (Kafka/RabbitMQ)
- WebSocket para actualizaciones en tiempo real

### Fase 3: Frontend
- Panel web con React/Angular/Vue
- Integración con backend via REST y GraphQL
- Conexión WebSocket para notificaciones

## Contacto

Proyecto académico - EntregaExpress S.A.
Fase 1 completada: Backend con microservicios REST y API Gateway

---

**Estado**: ✅ Fase 1 Completa
**Fecha**: Febrero 2025
