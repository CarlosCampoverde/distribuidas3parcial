# 🧪 Guía de Pruebas - Notificaciones en Tiempo Real (Fase 2)

## 📋 Resumen

Esta guía te ayudará a probar el flujo completo de eventos desde los servicios de negocio hasta las notificaciones en tiempo real vía WebSocket.

---

## 🔧 Pre-requisitos

Asegúrate de que todos los servicios estén corriendo:

### Servicios Fase 1
- ✅ PostgreSQL (4 bases de datos): `docker ps`
- ✅ RabbitMQ: `docker ps` - puertos 5672 y 15672
- ✅ Auth Service: puerto 8081
- ✅ Pedido Service: puerto 8082
- ✅ Fleet Service: puerto 8083
- ✅ Billing Service: puerto 8084
- ✅ API Gateway: puerto 8080

### Servicios Fase 2
- ✅ GraphQL Service: puerto 8085
- ✅ **Notification Service: puerto 8086** ← NUEVO

---

## 📊 1. Verificar RabbitMQ Management

### Abrir la consola de administración
```
http://localhost:15672
```

**Credenciales:**
- Usuario: `logiflow`
- Contraseña: `logiflow123`

### Verificar Exchanges
Ve a la pestaña **"Exchanges"** y verifica que existan:
- `logiflow.pedido.exchange` (tipo: topic)
- `logiflow.fleet.exchange` (tipo: topic)

### Verificar Queues
Ve a la pestaña **"Queues"** y verifica que existan:
- `logiflow.pedido.estado.queue`
- `logiflow.ubicacion.queue`

**Ambas colas deben tener:**
- ✅ 1 consumer activo (Notification Service)
- ✅ 0 mensajes en cola (si no hay actividad)

---

## 🌐 2. Abrir el Cliente WebSocket

### Opción A: Desde el navegador
1. Abre tu navegador (Chrome/Edge/Firefox)
2. Ve a: **http://localhost:8086**
3. Deberías ver la interfaz de notificaciones en tiempo real
4. Verifica que el estado de conexión diga: **"Conectado ✅"**

### Opción B: Desde archivo local
1. Navega a:
   ```
   c:\Users\carlo\OneDrive\Desktop\Proyecto3ParcialDistribuidas\logiflow-backend\notification-service\src\main\resources\static\index.html
   ```
2. Haz doble clic para abrir en el navegador

---

## 🧪 3. Probar el Flujo de Eventos de Pedidos

### 3.1. Cambiar el estado de un pedido

Abre PowerShell y ejecuta:

```powershell
# Actualizar estado de pedido a ASIGNADO
$body = @{
    estado = "ASIGNADO"
    repartidorId = 1
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8082/api/pedidos/1/estado" `
                  -Method PUT `
                  -Body $body `
                  -ContentType "application/json"
```

### 3.2. Verificar el resultado

**En el cliente WebSocket (http://localhost:8086):**
- ✅ Deberías ver un evento nuevo en la sección **"Eventos de Pedidos"**
- ✅ Muestra el cambio de estado del pedido
- ✅ El evento aparece automáticamente sin recargar la página

**En RabbitMQ Management:**
1. Ve a **"Queues"** → `logiflow.pedido.estado.queue`
2. Verifica que el contador de **"Total messages"** haya incrementado y luego decrementado (mensaje consumido)

**En los logs del Notification Service:**
```
🔔 Received PedidoEstadoEvent: Pedido PED-xxxxx changed from PENDIENTE to ASIGNADO
📡 Broadcasting pedido estado event to /topic/pedidos
```

### 3.3. Probar otros estados

```powershell
# Estado EN_CAMINO
$body = @{
    estado = "EN_CAMINO"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8082/api/pedidos/1/estado" `
                  -Method PUT `
                  -Body $body `
                  -ContentType "application/json"

# Estado ENTREGADO
$body = @{
    estado = "ENTREGADO"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8082/api/pedidos/1/estado" `
                  -Method PUT `
                  -Body $body `
                  -ContentType "application/json"
```

---

## 📍 4. Probar el Flujo de Ubicaciones del Repartidor

### 4.1. Actualizar ubicación GPS

```powershell
# Actualizar ubicación del repartidor
$body = @{
    latitud = 4.6988
    longitud = -74.0378
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8083/api/repartidores/1/ubicacion" `
                  -Method PUT `
                  -Body $body `
                  -ContentType "application/json"
```

### 4.2. Verificar el resultado

**En el cliente WebSocket:**
- ✅ Aparece un evento en **"Ubicaciones de Repartidores"**
- ✅ Muestra el nombre del repartidor y las coordenadas GPS
- ✅ Indica si tiene un pedido asignado

**En RabbitMQ Management:**
1. Ve a **"Queues"** → `logiflow.ubicacion.queue`
2. Verifica que el mensaje fue consumido

**En los logs del Notification Service:**
```
📍 Received UbicacionRepartidorEvent: Carlos Méndez at (4.6988, -74.0378)
📍 Broadcasting ubicacion event to /topic/ubicaciones
```

### 4.3. Simular un recorrido

Ejecuta este script para simular el movimiento del repartidor:

```powershell
# Simular 5 actualizaciones de ubicación
for ($i = 1; $i -le 5; $i++) {
    $lat = 4.698 + ($i * 0.001)
    $lon = -74.037 + ($i * 0.001)
    
    $body = @{
        latitud = $lat
        longitud = $lon
    } | ConvertTo-Json
    
    Invoke-RestMethod -Uri "http://localhost:8083/api/repartidores/1/ubicacion" `
                      -Method PUT `
                      -Body $body `
                      -ContentType "application/json"
    
    Write-Host "✅ Ubicación actualizada: ($lat, $lon)"
    Start-Sleep -Seconds 2
}
```

---

## 🎯 5. Prueba Completa de Flujo End-to-End

### Escenario: Cliente solicita pedido → Repartidor lo entrega

```powershell
# Paso 1: Crear un nuevo pedido
$nuevoPedido = @{
    clienteId = 1
    origenDireccion = "Calle 80 #10-20, Bogotá"
    destinoDireccion = "Carrera 15 #50-30, Bogotá"
    origenLatitud = 4.6650
    origenLongitud = -74.0544
    destinoLatitud = 4.6486
    destinoLongitud = -74.0771
    distanciaKm = 5.2
    pesoKg = 1.5
    tipoEntrega = "URBANA"
    descripcion = "Prueba de flujo completo"
} | ConvertTo-Json

$pedidoCreado = Invoke-RestMethod -Uri "http://localhost:8082/api/pedidos" `
                                   -Method POST `
                                   -Body $nuevoPedido `
                                   -ContentType "application/json"

$pedidoId = $pedidoCreado.data.id
Write-Host "✅ Pedido creado con ID: $pedidoId"

# Paso 2: Asignar a un repartidor
Start-Sleep -Seconds 2
$body = @{
    estado = "ASIGNADO"
    repartidorId = 1
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8082/api/pedidos/$pedidoId/estado" `
                  -Method PUT `
                  -Body $body `
                  -ContentType "application/json"
Write-Host "✅ Pedido asignado al repartidor"

# Paso 3: Repartidor inicia el viaje
Start-Sleep -Seconds 2
$body = @{
    estado = "EN_CAMINO"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8082/api/pedidos/$pedidoId/estado" `
                  -Method PUT `
                  -Body $body `
                  -ContentType "application/json"
Write-Host "✅ Repartidor en camino"

# Paso 4: Actualizar ubicación cada 3 segundos
for ($i = 1; $i -le 3; $i++) {
    Start-Sleep -Seconds 3
    $lat = 4.665 + ($i * 0.003)
    $lon = -74.054 + ($i * 0.004)
    
    $body = @{
        latitud = $lat
        longitud = $lon
    } | ConvertTo-Json
    
    Invoke-RestMethod -Uri "http://localhost:8083/api/repartidores/1/ubicacion" `
                      -Method PUT `
                      -Body $body `
                      -ContentType "application/json"
    Write-Host "📍 Ubicación actualizada: ($lat, $lon)"
}

# Paso 5: Marcar como entregado
Start-Sleep -Seconds 3
$body = @{
    estado = "ENTREGADO"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8082/api/pedidos/$pedidoId/estado" `
                  -Method PUT `
                  -Body $body `
                  -ContentType "application/json"
Write-Host "✅ Pedido entregado exitosamente!"
```

**En el cliente WebSocket deberías ver:**
1. ✅ Evento: Pedido cambia a **ASIGNADO**
2. ✅ Evento: Pedido cambia a **EN_CAMINO**
3. ✅ 3 eventos de **ubicación** del repartidor moviéndose
4. ✅ Evento: Pedido cambia a **ENTREGADO**

---

## 🐛 6. Solución de Problemas

### El cliente WebSocket no se conecta

**Verificar que el Notification Service esté corriendo:**
```powershell
netstat -ano | Select-String "8086"
```

**Revisar los logs del servicio:**
- Busca errores de conexión a RabbitMQ
- Verifica que Spring Boot haya iniciado correctamente

**Reiniciar el servicio si es necesario:**
1. Cierra la ventana del Notification Service
2. Ejecuta:
   ```powershell
   cd c:\Users\carlo\OneDrive\Desktop\Proyecto3ParcialDistribuidas\logiflow-backend\notification-service
   Start-Process powershell -ArgumentList "-NoExit", "-Command", "java -jar target\notification-service-1.0.0-SNAPSHOT.jar" -WindowStyle Normal
   ```

### No llegan eventos al cliente WebSocket

**Verificar RabbitMQ:**
1. Abre http://localhost:15672
2. Ve a **"Queues"**
3. Si hay mensajes acumulados sin consumir:
   - El Notification Service no está conectado
   - Revisa los logs del servicio

**Verificar que los publishers estén activos:**
```powershell
# Verificar que Pedido Service tenga la configuración de RabbitMQ
Get-Content c:\Users\carlo\OneDrive\Desktop\Proyecto3ParcialDistribuidas\logiflow-backend\pedido-service\src\main\resources\application.yml | Select-String "rabbitmq"

# Verificar que Fleet Service tenga la configuración de RabbitMQ
Get-Content c:\Users\carlo\OneDrive\Desktop\Proyecto3ParcialDistribuidas\logiflow-backend\fleet-service\src\main\resources\application.yml | Select-String "rabbitmq"
```

### Los eventos llegan a RabbitMQ pero no al WebSocket

**Verificar suscripción en el navegador:**
1. Abre las herramientas de desarrollador (F12)
2. Ve a la pestaña **"Console"**
3. Deberías ver:
   ```
   ✅ Conectado: CONNECTED
   ```

**Verificar logs del Notification Service:**
- Busca líneas con `@RabbitListener`
- Verifica que los eventos se estén recibiendo

---

## 📈 7. Métricas de Éxito

### ✅ Prueba Exitosa

- ✅ Cliente WebSocket conectado
- ✅ RabbitMQ con 2 consumers activos (1 por cola)
- ✅ Eventos aparecen en tiempo real sin recargar la página
- ✅ Los mensajes en RabbitMQ son consumidos inmediatamente
- ✅ Timeline de eventos coherente (PENDIENTE → ASIGNADO → EN_CAMINO → ENTREGADO)
- ✅ Ubicaciones del repartidor se actualizan en tiempo real

### ❌ Señales de Problema

- ❌ Cliente WebSocket muestra "Desconectado"
- ❌ RabbitMQ muestra 0 consumers
- ❌ Mensajes se acumulan en las colas
- ❌ Eventos no aparecen en la interfaz
- ❌ Errores en la consola del navegador

---

## 🎓 8. Conceptos Técnicos Clave

### Event-Driven Architecture
Los servicios publican eventos cuando ocurren cambios importantes, sin necesidad de conocer quién los consumirá.

### Message Broker (RabbitMQ)
Gestiona la entrega confiable de mensajes entre servicios, con soporte para reintentos y persistencia.

### WebSocket + STOMP
Protocolo bidireccional para comunicación en tiempo real entre servidor y cliente web.

### Routing Keys
- `pedido.estado.changed` → Cambios de estado de pedidos
- `repartidor.ubicacion.updated` → Actualizaciones de GPS

### Topic Exchange
Permite routing flexible usando patterns. Los consumers se suscriben a topics específicos.

---

## 📚 Próximos Pasos

### Mejoras Posibles
- [ ] Autenticación WebSocket basada en JWT
- [ ] Notificaciones push para clientes móviles
- [ ] Dashboard de administración con estadísticas en tiempo real
- [ ] Replay de eventos para recuperación de estado
- [ ] Rate limiting para prevenir spam de eventos

### Integración con Frontend
- [ ] Crear cliente React/Angular/Vue
- [ ] Implementar reconexión automática
- [ ] Agregar notificaciones toast
- [ ] Mostrar mapa con ubicación en tiempo real

---

## ✅ Checklist Final

- [ ] RabbitMQ corriendo y accesible
- [ ] Todos los servicios iniciados (8080-8086)
- [ ] Cliente WebSocket conectado
- [ ] Eventos de pedido funcionando
- [ ] Eventos de ubicación funcionando
- [ ] Flujo end-to-end completo ejecutado exitosamente
- [ ] Sin errores en los logs de los servicios

---

**🎉 ¡Felicidades! Has implementado un sistema de notificaciones en tiempo real completo con arquitectura event-driven.**
