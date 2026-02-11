# 🧪 Queries GraphQL para Copiar y Pegar en GraphiQL

Abre **http://localhost:8085/graphiql** y copia estas queries para probar el sistema.

---

## ✅ Query 1: Verificar que GraphQL funciona

```graphql
query {
  __schema {
    queryType {
      name
    }
  }
}
```

**Resultado esperado:**
```json
{
  "data": {
    "__schema": {
      "queryType": {
        "name": "Query"
      }
    }
  }
}
```

---

## 📦 Query 2: Ver todos los pedidos

```graphql
query {
  pedidos(limit: 10) {
    id
    codigoPedido
    estado
    tipoEntrega
    distanciaKm
    pesoKg
    costoEstimado
    origenDireccion
    destinoDireccion
    descripcion
    fechaCreacion
  }
}
```

---

## 🔍 Query 3: Buscar pedido por ID

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
    costoEstimado
    descripcion
    fechaCreacion
  }
}
```

---

## 🚀 Query 4: Pedido completo (con repartidor y factura)

**Esta es la ventaja de GraphQL - 1 query obtiene datos de 3 servicios diferentes**

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
      costoEstimado
    }
    repartidor {
      id
      nombreCompleto
      licencia
      tipoVehiculo
      placaVehiculo
      estado
      telefono
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

---

## 📊 Query 5: Dashboard de Cliente

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
      costoEstimado
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

---

## 🚚 Query 6: Repartidores disponibles

```graphql
query {
  repartidoresDisponibles {
    id
    nombreCompleto
    licencia
    telefono
    tipoVehiculo
    placaVehiculo
    estado
  }
}
```

---

## 🎯 Mutation 1: Crear un nuevo pedido

```graphql
mutation {
  crearPedido(input: {
    clienteId: 1
    origenDireccion: "Calle 100 #15-20, Bogotá"
    destinoDireccion: "Carrera 7 #45-30, Bogotá"
    origenLatitud: 4.6997
    origenLongitud: -74.0354
    destinoLatitud: 4.6533
    destinoLongitud: -74.0836
    distanciaKm: 8.5
    pesoKg: 2.5
    tipoEntrega: URBANA
    descripcion: "Paquete importante - Documentos"
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

## 🔄 Mutation 2: Actualizar estado de pedido

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

## 👤 Mutation 3: Crear repartidor

```graphql
mutation {
  crearRepartidor(input: {
    usuarioId: 2
    nombreCompleto: "Juan Pérez Ramos"
    licencia: "C1234567890"
    telefono: "3009876543"
    tipoVehiculo: MOTORIZADO
    placaVehiculo: "ABC123"
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

## 📍 Mutation 4: Actualizar ubicación del repartidor

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

## 💰 Mutation 5: Generar factura

```graphql
mutation {
  generarFactura(input: {
    pedidoId: 1
    clienteId: 1
    tipoEntrega: URBANA
    distanciaKm: 8.5
    pesoKg: 2.5
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

## 🎓 Query Avanzada: Usando Variables

**Query:**
```graphql
query ObtenerPedido($pedidoId: ID!) {
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

**Variables (panel inferior de GraphiQL):**
```json
{
  "pedidoId": "1"
}
```

---

## 🔀 Query con Fragmentos

```graphql
fragment PedidoBasico on Pedido {
  id
  codigoPedido
  estado
  tipoEntrega
}

fragment DireccionesPedido on Pedido {
  origenDireccion
  destinoDireccion
  distanciaKm
}

query {
  pedidos(limit: 5) {
    ...PedidoBasico
    ...DireccionesPedido
    costoEstimado
  }
}
```

---

## 📝 Instrucciones de Uso en GraphiQL

### 1. Ejecutar una Query
- Copia la query
- Pégala en el panel izquierdo de GraphiQL
- Presiona **Ctrl + Enter** o haz clic en el botón ▶️

### 2. Ver Documentación
- Haz clic en el botón **"Docs"** (arriba a la derecha)
- Explora todos los tipos, queries y mutations disponibles

### 3. Autocompletado
- Presiona **Ctrl + Space** para ver sugerencias
- Escribe `query {` y presiona Ctrl+Space para ver todas las queries disponibles

### 4. Múltiples Queries en el mismo archivo
- Puedes tener varias queries/mutations
- GraphiQL te permitirá elegir cuál ejecutar

### 5. Panel de Variables
- Para queries con variables, usa el panel "Query Variables" en la parte inferior
- Las variables deben estar en formato JSON

---

## ✅ Checklist de Pruebas

- [ ] Query 1: ¿GraphQL responde correctamente?
- [ ] Query 2: ¿Puedes ver los pedidos?
- [ ] Query 3: ¿Puedes buscar un pedido específico?
- [ ] Query 4: ¿El pedido completo trae repartidor y factura?
- [ ] Query 5: ¿El dashboard del cliente funciona?
- [ ] Mutation 1: ¿Puedes crear un pedido nuevo?
- [ ] Mutation 2: ¿Puedes actualizar el estado?
- [ ] ¿El autocompletado funciona con Ctrl+Space?
- [ ] ¿La documentación se abre con el botón Docs?

---

## 🐛 Solución de Problemas

### Error: "Cannot query field X on type Y"
- El campo no existe en el schema
- Verifica la ortografía o consulta la documentación (botón Docs)

### Error: "Cannot return null for non-nullable field"
- Estás consultando datos que no existen aún
- Crea primero un pedido/repartidor con una mutation

### Error: Variables no encontradas
- Asegúrate de definir las variables en el panel "Query Variables"
- Las variables deben coincidir con el nombre en la query

### GraphiQL no carga
- Presiona Ctrl+Shift+R para recargar sin caché
- Abre F12 y revisa la consola para errores JavaScript

---

## 📚 Siguiente Paso

Si todas las queries funcionan correctamente, revisa:
- [GRAPHQL_TESTING_GUIDE.md](GRAPHQL_TESTING_GUIDE.md) - Guía completa de pruebas
- [PHASE2_GRAPHQL_SUMMARY.md](PHASE2_GRAPHQL_SUMMARY.md) - Resumen de la implementación

---

**🎉 ¡Disfruta probando tu API GraphQL!**
