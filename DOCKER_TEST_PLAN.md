# 🧪 Plan de Pruebas - Docker Stack

## 🎯 Objetivo
Verificar que todo el stack LogiFlow funciona correctamente en Docker antes de migrar a Kubernetes.

---

## 📋 Pre-requisitos

```powershell
# Verificar Docker
docker --version
docker-compose --version

# Verificar memoria disponible
docker info | Select-String "Total Memory"

# Limpiar ambiente (opcional)
docker-compose -f logiflow-backend/docker-compose-full.yml down -v
docker system prune -f
```

---

## 🚀 Fase 1: Build y Deploy

### 1.1 Build de Imágenes

```powershell
cd logiflow-backend
.\build-docker.ps1
```

**✅ Criterios de éxito:**
- [ ] `common` module builds successfully
- [ ] Todos los 7 servicios compilan sin errores
- [ ] Docker images creadas (verificar con `docker images | Select-String logiflow`)

**Expected output:**
```
logiflow-backend-api-gateway            latest
logiflow-backend-auth-service           latest
logiflow-backend-pedido-service         latest
logiflow-backend-fleet-service          latest
logiflow-backend-billing-service        latest
logiflow-backend-graphql-service        latest
logiflow-backend-notification-service   latest
```

### 1.2 Iniciar Stack

```powershell
docker-compose -f docker-compose-full.yml up -d
```

**✅ Criterios de éxito:**
- [ ] 13 contenedores inician (4 DBs + RabbitMQ + pgAdmin + 7 servicios)
- [ ] No hay errores en logs iniciales

---

## 🏥 Fase 2: Health Checks

### 2.1 Verificar Estado de Contenedores

```powershell
docker-compose -f docker-compose-full.yml ps
```

**✅ Criterios de éxito (esperar ~2 minutos):**
- [ ] Todos los contenedores en estado `Up`
- [ ] Servicios Spring Boot muestran `(healthy)`
- [ ] Databases muestran `(healthy)`
- [ ] RabbitMQ muestra `(healthy)`

### 2.2 Healthcheck Individual

```powershell
# API Gateway
curl http://localhost:8080/actuator/health

# Auth Service
curl http://localhost:8081/actuator/health

# Pedido Service
curl http://localhost:8082/actuator/health

# Fleet Service
curl http://localhost:8083/actuator/health

# Billing Service
curl http://localhost:8084/actuator/health

# GraphQL Service
curl http://localhost:8085/actuator/health

# Notification Service
curl http://localhost:8086/actuator/health
```

**✅ Criterios de éxito:**
Todos deben responder:
```json
{"status":"UP"}
```

---

## 🗄️ Fase 3: Verificar Bases de Datos

### 3.1 Conectividad

```powershell
# Auth DB
docker exec -it logiflow-authdb pg_isready -U logiuser

# Pedido DB
docker exec -it logiflow-pedidodb pg_isready -U logiuser

# Fleet DB
docker exec -it logiflow-fleetdb pg_isready -U logiuser

# Billing DB
docker exec -it logiflow-billingdb pg_isready -U logiuser
```

**✅ Criterios de éxito:**
Todos responden: `<container>:5432 - accepting connections`

### 3.2 Verificar Esquemas

```powershell
# Ver tablas en authdb
docker exec -it logiflow-authdb psql -U logiuser -d authdb -c "\dt"

# Ver tablas en pedidodb
docker exec -it logiflow-pedidodb psql -U logiuser -d pedidodb -c "\dt"
```

**✅ Criterios de éxito:**
- [ ] Tablas JPA creadas automáticamente (usuarios, pedidos, etc.)
- [ ] Secuencias creadas
- [ ] No hay errores de permisos

### 3.3 Poblar Datos de Prueba

**Opción A: Via API (recomendado)**
```powershell
# Crear usuario via Auth Service
Invoke-RestMethod -Uri "http://localhost:8081/api/auth/register" `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"username":"carlos","email":"carlos@test.com","password":"test123","role":"ADMIN"}'
```

**Opción B: Via SQL directo**
```powershell
# Cargar script SQL (si existe)
Get-Content .\seed-data.sql | docker exec -i logiflow-pedidodb psql -U logiuser -d pedidodb
```

---

## 🐰 Fase 4: Verificar RabbitMQ

### 4.1 Management Console

Abrir navegador: http://localhost:15672
- **Usuario:** logiflow
- **Password:** logiflow123

**✅ Criterios de éxito:**
- [ ] Login exitoso
- [ ] Vhost `/` visible
- [ ] Connections muestra servicios conectados

### 4.2 Verificar Colas

```powershell
curl -u logiflow:logiflow123 http://localhost:15672/api/queues | ConvertFrom-Json | Select-Object name, consumers
```

**✅ Criterios de éxito:**
```
name                               consumers
----                               ---------
logiflow.pedido.estado.queue       1
logiflow.ubicacion.queue           1
```

### 4.3 Verificar Exchanges

```powershell
curl -u logiflow:logiflow123 http://localhost:15672/api/exchanges | ConvertFrom-Json | Where-Object {$_.name -like "logiflow*"} | Select-Object name, type
```

**✅ Criterios de éxito:**
```
name                          type
----                          ----
logiflow.pedido.exchange      topic
logiflow.fleet.exchange       topic
```

---

## 🌐 Fase 5: Pruebas Funcionales

### 5.1 GraphQL Service

**Test 1: GraphiQL Interface**
1. Abrir: http://localhost:8085/graphiql
2. Ejecutar query:
```graphql
{
  pedidos(limit: 5) {
    id
    codigoPedido
    estado
    fechaCreacion
  }
}
```

**✅ Criterios de éxito:**
- [ ] GraphiQL carga correctamente
- [ ] Query ejecuta sin errores
- [ ] Retorna datos (si hay pedidos) o array vacío

**Test 2: Mutation**
```graphql
mutation {
  crearPedido(input: {
    usuarioId: 1
    direccionRecogida: "Calle 123"
    direccionEntrega: "Calle 456"
    descripcion: "Paquete test"
    pesoKg: 5.0
  }) {
    id
    codigoPedido
    estado
  }
}
```

### 5.2 WebSocket Service

1. Abrir: http://localhost:8086
2. Verificar conexión WebSocket

**✅ Criterios de éxito:**
- [ ] Mensaje "Connected to WebSocket server"
- [ ] Subscripción a `/topic/pedidos` activa
- [ ] Subscripción a `/topic/ubicaciones` activa

### 5.3 API Gateway Routing

```powershell
# Route a Auth Service
curl http://localhost:8080/api/auth/health

# Route a Pedido Service
curl http://localhost:8080/api/pedidos

# Route a Fleet Service
curl http://localhost:8080/api/fleet/repartidores
```

**✅ Criterios de éxito:**
- [ ] Gateway rutea correctamente a cada servicio
- [ ] No hay errores 404 o 503

---

## 🔄 Fase 6: Flujo End-to-End

### 6.1 Crear Pedido y Verificar Evento

**Terminal 1: Monitorear WebSocket**
Abrir http://localhost:8086

**Terminal 2: Crear pedido via GraphQL**
```graphql
mutation {
  crearPedido(input: {
    usuarioId: 1
    direccionRecogida: "Test Origin"
    direccionEntrega: "Test Destination"
    descripcion: "E2E Test"
    pesoKg: 10.0
  }) {
    id
    codigoPedido
  }
}
```

**✅ Criterios de éxito:**
- [ ] Mutation retorna pedido creado
- [ ] WebSocket recibe evento en tiempo real
- [ ] RabbitMQ queue muestra mensaje procesado

### 6.2 Actualizar Estado y Verificar Evento

```graphql
mutation {
  actualizarEstadoPedido(id: 1, nuevoEstado: EN_TRANSITO) {
    id
    estado
  }
}
```

**✅ Criterios de éxito:**
- [ ] Estado actualiza correctamente
- [ ] WebSocket recibe evento de cambio de estado
- [ ] Logs de Notification Service muestran evento recibido:
```powershell
docker-compose -f docker-compose-full.yml logs notification-service | Select-String "Pedido actualizado"
```

### 6.3 Actualizar Ubicación Fleet

```powershell
curl -X PATCH http://localhost:8083/api/fleet/repartidores/1/ubicacion `
  -H "Content-Type: application/json" `
  -d '{"latitud":19.4326,"longitud":-99.1332}'
```

**✅ Criterios de éxito:**
- [ ] Ubicación actualiza correctamente
- [ ] WebSocket recibe evento de ubicación
- [ ] Evento muestra coordenadas correctas

---

## 📊 Fase 7: Monitoreo y Logs

### 7.1 Ver Logs en Tiempo Real

```powershell
# Todos los servicios
docker-compose -f docker-compose-full.yml logs -f

# Solo servicios Spring Boot
docker-compose -f docker-compose-full.yml logs -f auth-service pedido-service fleet-service

# Solo infraestructura
docker-compose -f docker-compose-full.yml logs -f authdb pedidodb rabbitmq
```

### 7.2 Verificar Errores

```powershell
# Buscar errores en logs
docker-compose -f docker-compose-full.yml logs | Select-String "ERROR"

# Buscar warnings
docker-compose -f docker-compose-full.yml logs | Select-String "WARN"
```

**✅ Criterios de éxito:**
- [ ] No hay ERRORs críticos
- [ ] Warnings son solo informativos (ej: "Bean overriding")

### 7.3 Uso de Recursos

```powershell
docker stats --no-stream
```

**✅ Criterios de éxito esperados:**
```
Auth Service:      ~300MB RAM, <5% CPU
Pedido Service:    ~300MB RAM, <5% CPU
Fleet Service:     ~300MB RAM, <5% CPU
Billing Service:   ~300MB RAM, <5% CPU
GraphQL Service:   ~350MB RAM, <5% CPU
Notification:      ~300MB RAM, <5% CPU
API Gateway:       ~350MB RAM, <5% CPU
PostgreSQL x4:     ~50MB RAM cada uno
RabbitMQ:          ~150MB RAM
```

---

## 🔧 Fase 8: Resilencia y Recovery

### 8.1 Reinicio de Servicio

```powershell
# Detener servicio
docker-compose -f docker-compose-full.yml stop pedido-service

# Esperar 10 segundos
Start-Sleep -Seconds 10

# Reiniciar
docker-compose -f docker-compose-full.yml start pedido-service

# Verificar healthcheck
curl http://localhost:8082/actuator/health
```

**✅ Criterios de éxito:**
- [ ] Servicio reinicia correctamente
- [ ] Healthcheck pasa después de ~30 segundos
- [ ] Otros servicios no afectados

### 8.2 Dependencias (DB down)

```powershell
# Detener pedidodb
docker-compose -f docker-compose-full.yml stop pedidodb

# Verificar pedido-service
docker-compose -f docker-compose-full.yml logs pedido-service | Select-String -Pattern "connection" -Context 2

# Reiniciar pedidodb
docker-compose -f docker-compose-full.yml start pedidodb
```

**✅ Criterios de éxito:**
- [ ] Pedido service detecta pérdida de conexión
- [ ] Pedido service reconecta automáticamente cuando DB vuelve
- [ ] No hay pérdida de datos

---

## 🧹 Fase 9: Limpieza y Reset

### 9.1 Reset Completo con Datos

```powershell
# Detener sin borrar volúmenes
docker-compose -f docker-compose-full.yml down

# Reiniciar (datos persisten)
docker-compose -f docker-compose-full.yml up -d
```

**✅ Criterios de éxito:**
- [ ] Servicios reinician
- [ ] Datos en DB siguen ahí

### 9.2 Reset Completo Limpio

```powershell
# Detener y borrar TODO
docker-compose -f docker-compose-full.yml down -v

# Reconstruir imágenes
.\build-docker.ps1

# Iniciar fresh
docker-compose -f docker-compose-full.yml up -d
```

**✅ Criterios de éxito:**
- [ ] Volúmenes eliminados
- [ ] DBs vacías en reinicio
- [ ] Sistema funciona como nueva instalación

---

## ✅ Checklist Final

### Infraestructura
- [ ] 4 PostgreSQL databases UP y HEALTHY
- [ ] RabbitMQ UP y HEALTHY
- [ ] pgAdmin accesible
- [ ] Volúmenes creados y persistiendo datos

### Microservicios
- [ ] Auth Service UP y HEALTHY
- [ ] Pedido Service UP y HEALTHY
- [ ] Fleet Service UP y HEALTHY
- [ ] Billing Service UP y HEALTHY
- [ ] GraphQL Service UP y HEALTHY
- [ ] Notification Service UP y HEALTHY
- [ ] API Gateway UP y HEALTHY

### Funcionalidad
- [ ] GraphQL queries funcionan
- [ ] GraphQL mutations funcionan
- [ ] WebSocket conecta y recibe eventos
- [ ] RabbitMQ tiene 2 exchanges y 2 queues
- [ ] RabbitMQ tiene 2 consumers activos
- [ ] API Gateway rutea correctamente
- [ ] Eventos fluyen de servicios → RabbitMQ → WebSocket

### Eventos End-to-End
- [ ] Crear pedido genera evento WebSocket
- [ ] Actualizar estado genera evento WebSocket
- [ ] Actualizar ubicación genera evento WebSocket
- [ ] Logs muestran eventos publicados y consumidos

### Performance
- [ ] Todos los servicios < 400MB RAM
- [ ] CPU usage < 10% en idle
- [ ] Healthchecks pasan en < 30 segundos
- [ ] No memory leaks después de 10 minutos

### Resilencia
- [ ] Servicios reinician automáticamente si crashean
- [ ] Servicios esperan a que dependencies estén ready
- [ ] Reconexión automática a DB
- [ ] Datos persisten después de restart

---

## 🎯 Resultado Esperado

**TODOS LOS CHECKS VERDES = Listo para Kubernetes! 🚀**

Si algún check falla, consultar [DOCKER_DEPLOYMENT_GUIDE.md](DOCKER_DEPLOYMENT_GUIDE.md) sección "Solución de Problemas".

---

## 📝 Documentar Resultados

Crear archivo `DOCKER_TEST_RESULTS.md`:

```markdown
# Resultados de Pruebas Docker - [FECHA]

## Resumen
- Total tests: XX
- Passed: XX
- Failed: XX
- Success rate: XX%

## Tests Fallidos
1. [Nombre del test]
   - Error: [descripción]
   - Fix aplicado: [solución]

## Configuraciones Ajustadas
- [Cambio 1]
- [Cambio 2]

## Próximos Pasos
- [ ] Fix pending issues
- [ ] Optimize resource usage
- [ ] Preparar para Kubernetes
```

---

**¡Éxito en las pruebas! 🎉**
