# Guía de Pruebas - GraphQL Service LogiFlow

## 🚀 Inicio Rápido

### 1. Iniciar todos los servicios

```powershell
# Opción A: Script automático (recomendado)
.\start-logiflow.ps1

# Opción B: Manual
# Terminal 1: Auth Service
cd logiflow-backend\auth-service
java -jar target\auth-service-1.0.0-SNAPSHOT.jar

# Terminal 2: Pedido Service
cd logiflow-backend\pedido-service
java -jar target\pedido-service-1.0.0-SNAPSHOT.jar

# Terminal 3: Fleet Service
cd logiflow-backend\fleet-service
java -jar target\fleet-service-1.0.0-SNAPSHOT.jar

# Terminal 4: Billing Service
cd logiflow-backend\billing-service
java -jar target\billing-service-1.0.0-SNAPSHOT.jar

# Terminal 5: GraphQL Service
cd logiflow-backend\graphql-service
java -jar target\graphql-service-1.0.0-SNAPSHOT.jar

# Terminal 6: API Gateway
cd logiflow-backend\api-gateway
java -jar target\api-gateway-1.0.0-SNAPSHOT.jar
```

### 2. Acceder a GraphiQL

Abre tu navegador en: **http://localhost:8085/graphiql**

---

## 📝 Preparación: Crear Datos de Prueba

### 1. Registrar Usuario

```powershell
$registerBody = @{
    email = "test@logiflow.com"
    password = "Test1234!"
    nombreCompleto = "Usuario Test"
    telefono = "3001234567"
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" `
    -Method Post `
    -ContentType "application/json" `
    -Body $registerBody

$token = $response.token
Write-Host "Token: $token"
```

### 2. Crear Pedido de Prueba

```powershell
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

$pedidoBody = @{
    clienteId = 1
    origenDireccion = "Calle 123, Bogotá"
    destinoDireccion = "Carrera 45, Bogotá"
    distanciaKm = 5.5
    pesoKg = 2.0
    tipoEntrega = "URBANA"
    descripcion = "Paquete de prueba"
} | ConvertTo-Json

$pedido = Invoke-RestMethod -Uri "http://localhost:8080/api/pedidos" `
    -Method Post `
    -Headers $headers `
    -Body $pedidoBody

Write-Host "Pedido creado: $($pedido.id)"
```

---

## 🔍 Ejemplos de Consultas GraphQL

### Query 1: Lista de Pedidos

```graphql
query {
  pedidos(limit: 5) {
    id
    codigoPedido
    estado
    tipoEntrega
    origenDireccion
    destinoDireccion
    distanciaKm
    pesoKg
    costoEstimado
  }
}
```

**Resultado esperado:**
```json
{
  "data": {
    "pedidos": [
      {
        "id": "1",
        "codigoPedido": "PED-2024-001",
        "estado": "RECIBIDO",
        "tipoEntrega": "URBANA",
        "origenDireccion": "Calle 123, Bogotá",
        "destinoDireccion": "Carrera 45, Bogotá",
        "distanciaKm": 5.5,
        "pesoKg": 2.0,
        "costoEstimado": 15000
      }
    ]
  }
}
```

---

### Query 2: Pedido por ID

```graphql
query {
  pedido(id: 1) {
    id
    codigoPedido
    clienteId
    estado
    tipoEntrega
    origenDireccion
    destinoDireccion
    distanciaKm
    pesoKg
    descripcion
    costoEstimado
    fechaCreacion
    fechaActualizacion
  }
}
```

---

### Query 3: Pedido Completo (con Repartidor y Factura)

Esta es la **ventaja clave de GraphQL**: obtener datos relacionados de múltiples servicios en una sola consulta.

```graphql
query {
  pedidoCompleto(id: 1) {
    pedido {
      id
      codigoPedido
      estado
      tipoEntrega
      origenDireccion
      destinoDireccion
      distanciaKm
      pesoKg
      costoEstimado
    }
    repartidor {
      id
      nombreCompleto
      licencia
      telefono
      tipoVehiculo
      placaVehiculo
      estado
      ubicacionLatitud
      ubicacionLongitud
    }
    factura {
      id
      numeroFactura
      subtotal
      impuestos
      total
      estado
      fechaCreacion
    }
  }
}
```

**Ventaja REST vs GraphQL:**
- **REST**: 3 peticiones separadas (GET /pedidos/1, GET /repartidores/X, GET /facturas/Y)
- **GraphQL**: 1 sola petición con todos los datos relacionados

---

### Query 4: Dashboard de Cliente

```graphql
query {
  dashboardCliente(clienteId: 1) {
    clienteId
    totalPedidos
    gastoTotal
    pedidosActivos {
      id
      codigoPedido
      estado
      tipoEntrega
      origenDireccion
      destinoDireccion
      distanciaKm
      fechaCreacion
    }
    ultimasFacturas {
      id
      numeroFactura
      total
      estado
      fechaCreacion
    }
  }
}
```

**Caso de uso**: Una sola consulta para obtener toda la información del dashboard del cliente.

---

### Query 5: Repartidores Disponibles

```graphql
query {
  repartidoresDisponibles {
    id
    nombreCompleto
    licencia
    tipoVehiculo
    placaVehiculo
    estado
    ubicacionLatitud
    ubicacionLongitud
  }
}
```

---

### Query 6: Dashboard de Repartidor

```graphql
query {
  dashboardRepartidor(repartidorId: 1) {
    repartidor {
      id
      nombreCompleto
      licencia
      telefono
      tipoVehiculo
      estado
    }
    pedidosEntregados
    kmRecorridos
    pedidosAsignados {
      id
      codigoPedido
      estado
      origenDireccion
      destinoDireccion
      distanciaKm
      fechaAsignacion
    }
  }
}
```

---

## 🔄 Ejemplos de Mutaciones

### Mutation 1: Crear Pedido

```graphql
mutation {
  crearPedido(input: {
    clienteId: 1
    origenDireccion: "Avenida 68 #45-32, Bogotá"
    destinoDireccion: "Carrera 7 #123-45, Bogotá"
    origenLatitud: 4.6533
    origenLongitud: -74.0836
    destinoLatitud: 4.7110
    destinoLongitud: -74.0721
    distanciaKm: 8.5
    pesoKg: 3.5
    tipoEntrega: URBANA
    descripcion: "Documentos importantes"
  }) {
    success
    message
    pedido {
      id
      codigoPedido
      estado
      costoEstimado
    }
  }
}
```

---

### Mutation 2: Crear Repartidor

```graphql
mutation {
  crearRepartidor(input: {
    usuarioId: 2
    nombreCompleto: "Carlos Ramírez"
    licencia: "C1987654321"
    telefono: "3159876543"
    tipoVehiculo: MOTORIZADO
    placaVehiculo: "XYZ789"
  }) {
    success
    message
    repartidor {
      id
      nombreCompleto
      estado
      tipoVehiculo
    }
  }
}
```

---

### Mutation 3: Actualizar Estado de Pedido

```graphql
mutation {
  actualizarEstadoPedido(
    id: 1
    estado: ASIGNADO
    repartidorId: 1
  ) {
    success
    message
    pedido {
      id
      codigoPedido
      estado
      repartidorId
      fechaAsignacion
    }
  }
}
```

---

### Mutation 4: Actualizar Ubicación de Repartidor

```graphql
mutation {
  actualizarUbicacionRepartidor(
    id: 1
    latitud: 4.6836
    longitud: -74.0553
  ) {
    success
    message
    repartidor {
      id
      nombreCompleto
      ubicacionLatitud
      ubicacionLongitud
      ultimaActualizacionUbicacion
    }
  }
}
```

---

### Mutation 5: Generar Factura

```graphql
mutation {
  generarFactura(input: {
    pedidoId: 1
    clienteId: 1
    tipoEntrega: URBANA
    distanciaKm: 5.5
    pesoKg: 2.0
  }) {
    success
    message
    factura {
      id
      numeroFactura
      subtotal
      impuestos
      total
      estado
    }
  }
}
```

---

## 🎯 Consultas Avanzadas

### Query con Variables

```graphql
query ObtenerPedidoPorId($pedidoId: ID!) {
  pedido(id: $pedidoId) {
    id
    codigoPedido
    estado
    tipoEntrega
    distanciaKm
    costoEstimado
  }
}
```

**Variables:**
```json
{
  "pedidoId": "1"
}
```

---

### Query con Fragmentos

```graphql
fragment PedidoBasico on Pedido {
  id
  codigoPedido
  estado
  tipoEntrega
  distanciaKm
}

fragment RepartidorBasico on Repartidor {
  id
  nombreCompleto
  tipoVehiculo
  estado
}

query {
  pedidoCompleto(id: 1) {
    pedido {
      ...PedidoBasico
      origenDireccion
      destinoDireccion
    }
    repartidor {
      ...RepartidorBasico
      telefono
      licencia
    }
    factura {
      numeroFactura
      total
    }
  }
}
```

---

### Query Condicional con Directivas

```graphql
query ObtenerPedido($id: ID!, $incluirRepartidor: Boolean!) {
  pedidoCompleto(id: $id) {
    pedido {
      id
      codigoPedido
      estado
    }
    repartidor @include(if: $incluirRepartidor) {
      nombreCompleto
      tipoVehiculo
    }
  }
}
```

**Variables:**
```json
{
  "id": "1",
  "incluirRepartidor": true
}
```

---

## 📊 Comparación REST vs GraphQL

### Escenario: Obtener pedido con repartidor y factura

#### REST (3 peticiones)
```bash
# Petición 1: Obtener pedido
GET http://localhost:8080/api/pedidos/1

# Petición 2: Obtener repartidor
GET http://localhost:8080/api/repartidores/5

# Petición 3: Obtener factura
GET http://localhost:8080/api/facturas/pedido/1
```

#### GraphQL (1 petición)
```graphql
query {
  pedidoCompleto(id: 1) {
    pedido { id, codigoPedido, estado }
    repartidor { nombreCompleto, tipoVehiculo }
    factura { numeroFactura, total }
  }
}
```

**Ventajas:**
- ✅ Menos peticiones HTTP
- ✅ Menos latencia de red
- ✅ Datos exactos que necesitas (no over-fetching)
- ✅ Una sola consulta para datos de múltiples servicios

---

## 🛠️ Herramientas de Prueba

### 1. GraphiQL (Incluido)
- URL: http://localhost:8085/graphiql
- Autocompletado
- Documentación interactiva
- Historial de consultas

### 2. Postman
- Crear colección GraphQL
- Endpoint: `POST http://localhost:8085/graphql`
- Header: `Content-Type: application/json`
- Body:
  ```json
  {
    "query": "query { pedidos(limit: 5) { id, codigoPedido } }"
  }
  ```

### 3. PowerShell

```powershell
$query = @{
    query = "query { pedidos(limit: 5) { id, codigoPedido, estado } }"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8085/graphql" `
    -Method Post `
    -ContentType "application/json" `
    -Body $query
```

---

## 🐛 Troubleshooting

### Error: "Cannot query field X on type Y"
- **Causa**: El campo no existe en el schema
- **Solución**: Verifica el schema en `schema.graphqls` o usa la documentación de GraphiQL

### Error: "Cannot return null for non-nullable field"
- **Causa**: Un campo requerido está retornando null
- **Solución**: Verifica que los servicios REST estén retornando datos correctos

### Error: Connection refused
- **Causa**: El servicio backend no está ejecutándose
- **Solución**: Verifica que todos los servicios estén corriendo en sus puertos

### Error 500: Internal Server Error
- **Causa**: Error en el resolver o servicio backend
- **Solución**: Revisa los logs del servicio GraphQL:
  ```powershell
  # Ver logs en tiempo real
  Get-Content logiflow-backend\graphql-service\logs\application.log -Wait
  ```

---

## 📈 Métricas y Monitoreo

### Ver métricas de Spring Boot
```
http://localhost:8085/actuator/health
http://localhost:8085/actuator/metrics
```

### Logging
Los logs se guardan en:
- Console output
- `logiflow-backend/graphql-service/logs/application.log`

---

## 🎓 Próximos Pasos

1. ✅ **Completado**: GraphQL Service básico
2. 🔜 **Siguiente**: Agregar autenticación JWT a GraphQL
3. 🔜 **Futuro**: Implementar subscriptions (real-time)
4. 🔜 **Optimización**: DataLoader para N+1 queries
5. 🔜 **Cache**: Redis para cachear consultas frecuentes

---

## 📚 Documentación

- **GraphQL**: https://graphql.org/learn/
- **Spring GraphQL**: https://spring.io/projects/spring-graphql
- **Schema**: `logiflow-backend/graphql-service/src/main/resources/graphql/schema.graphqls`
- **README**: `logiflow-backend/graphql-service/README.md`
