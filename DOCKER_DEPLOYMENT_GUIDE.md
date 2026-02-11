# 🐳 Guía de Dockerización - LogiFlow

## 📋 Requisitos Previos

- ✅ Docker Desktop instalado (Windows/Mac) o Docker Engine (Linux)
- ✅ Docker Compose v2.0 o superior
- ✅ 8GB RAM mínimo (16GB recomendado)
- ✅ 20GB espacio en disco

**Verificar instalación:**
```bash
docker --version
docker-compose --version
```

---

## 🚀 Inicio Rápido

### Opción 1: Script Automatizado (Recomendado)

**Windows (PowerShell):**
```powershell
cd logiflow-backend
.\build-docker.ps1
docker-compose -f docker-compose-full.yml up -d
```

**Linux/Mac (Bash):**
```bash
cd logiflow-backend
chmod +x build-docker.sh
./build-docker.sh
docker-compose -f docker-compose-full.yml up -d
```

### Opción 2: Manual Paso a Paso

```bash
# 1. Construir módulo común
cd logiflow-backend/common
mvn clean install -DskipTests
cd ..

# 2. Construir todos los servicios
mvn clean package -DskipTests

# 3. Construir imágenes Docker
docker-compose -f docker-compose-full.yml build

# 4. Iniciar todo el stack
docker-compose -f docker-compose-full.yml up -d
```

---

## 📊 Verificar Estado de Contenedores

```bash
# Ver todos los contenedores corriendo
docker-compose -f docker-compose-full.yml ps

# Ver logs de todos los servicios
docker-compose -f docker-compose-full.yml logs -f

# Ver logs de un servicio específico
docker-compose -f docker-compose-full.yml logs -f auth-service

# Verificar salud de los contenedores
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
```

**Estado esperado:**
```
NAME                            STATUS                   PORTS
logiflow-api-gateway           Up (healthy)             0.0.0.0:8080->8080/tcp
logiflow-auth-service          Up (healthy)             0.0.0.0:8081->8081/tcp
logiflow-pedido-service        Up (healthy)             0.0.0.0:8082->8082/tcp
logiflow-fleet-service         Up (healthy)             0.0.0.0:8083->8083/tcp
logiflow-billing-service       Up (healthy)             0.0.0.0:8084->8084/tcp
logiflow-graphql-service       Up (healthy)             0.0.0.0:8085->8085/tcp
logiflow-notification-service  Up (healthy)             0.0.0.0:8086->8086/tcp
logiflow-rabbitmq              Up (healthy)             5672, 15672
logiflow-authdb                Up (healthy)             5532
logiflow-pedidodb              Up (healthy)             5533
logiflow-fleetdb               Up (healthy)             5534
logiflow-billingdb             Up (healthy)             5535
logiflow-pgadmin               Up                       80
```

---

## 🌐 Acceder a los Servicios

| Servicio | URL | Descripción |
|----------|-----|-------------|
| API Gateway | http://localhost:8080 | Punto de entrada principal |
| Auth Service | http://localhost:8081 | Autenticación y JWT |
| Pedido Service | http://localhost:8082 | Gestión de pedidos |
| Fleet Service | http://localhost:8083 | Gestión de flota |
| Billing Service | http://localhost:8084 | Facturación |
| GraphQL Service | http://localhost:8085/graphiql | Interfaz GraphQL |
| Notification Service | http://localhost:8086 | WebSocket en tiempo real |
| RabbitMQ Management | http://localhost:15672 | Usuario: logiflow / logiflow123 |
| pgAdmin | http://localhost:5050 | Usuario: admin@logiflow.com / admin123 |

---

## 🧪 Probar el Sistema

### 1. Verificar GraphQL
```bash
curl -X POST http://localhost:8085/graphql \
  -H "Content-Type: application/json" \
  -d '{"query":"{ pedidos(limit: 5) { id codigoPedido estado } }"}'
```

### 2. Verificar API Gateway
```bash
curl http://localhost:8080/actuator/health
```

### 3. Verificar RabbitMQ
Abrir http://localhost:15672 y verificar que las colas estén creadas:
- `logiflow.pedido.estado.queue`
- `logiflow.ubicacion.queue`

### 4. Probar WebSocket
Abrir http://localhost:8086 en el navegador y verificar la conexión.

---

## 🔧 Comandos Útiles

### Gestión de Contenedores

```bash
# Iniciar todos los servicios
docker-compose -f docker-compose-full.yml up -d

# Detener todos los servicios
docker-compose -f docker-compose-full.yml down

# Detener y eliminar volúmenes (CUIDADO: borra datos)
docker-compose -f docker-compose-full.yml down -v

# Reiniciar un servicio específico
docker-compose -f docker-compose-full.yml restart pedido-service

# Ver uso de recursos
docker stats

# Limpiar contenedores detenidos
docker container prune

# Limpiar imágenes no usadas
docker image prune -a
```

### Debugging

```bash
# Acceder a un contenedor
docker exec -it logiflow-pedido-service /bin/sh

# Ver variables de entorno
docker exec logiflow-pedido-service env

# Inspeccionar un contenedor
docker inspect logiflow-pedido-service

# Ver logs en tiempo real con timestamps
docker-compose -f docker-compose-full.yml logs -f --timestamps

# Ver solo últimas 100 líneas de logs
docker-compose -f docker-compose-full.yml logs --tail=100
```

### Reconstruir Imágenes

```bash
# Reconstruir todo forzando no usar caché
docker-compose -f docker-compose-full.yml build --no-cache

# Reconstruir un servicio específico
docker-compose -f docker-compose-full.yml build auth-service

# Reconstruir e iniciar
docker-compose -f docker-compose-full.yml up -d --build
```

---

## 🗄️ Gestión de Bases de Datos

### Conectar via pgAdmin

1. Abrir http://localhost:5050
2. Login: `admin@logiflow.com` / `admin123`
3. Agregar nuevo servidor:
   - **Host**: nombre del contenedor (ej: `authdb`)
   - **Port**: `5432`
   - **Username**: `logiuser`
   - **Password**: `logipass123`

### Conectar via CLI

```bash
# Conectar a authdb
docker exec -it logiflow-authdb psql -U logiuser -d authdb

# Conectar a pedidodb
docker exec -it logiflow-pedidodb psql -U logiuser -d pedidodb

# Ejecutar SQL desde archivo
docker exec -i logiflow-pedidodb psql -U logiuser -d pedidodb < seed-data.sql
```

### Backup y Restore

```bash
# Backup
docker exec logiflow-pedidodb pg_dump -U logiuser pedidodb > backup.sql

# Restore
docker exec -i logiflow-pedidodb psql -U logiuser -d pedidodb < backup.sql
```

---

## 🐛 Solución de Problemas

### Servicio no inicia

```bash
# Ver logs del servicio
docker-compose -f docker-compose-full.yml logs pedido-service

# Ver healthcheck
docker inspect logiflow-pedido-service | grep -A 10 Health
```

**Problemas comunes:**
- Base de datos no está lista → Esperar a que healthcheck pase
- Puerto en uso → Cambiar puerto en docker-compose-full.yml
- Falta de memoria → Aumentar límites de Docker Desktop

### Base de datos no conecta

```bash
# Verificar que PostgreSQL esté corriendo
docker-compose -f docker-compose-full.yml ps authdb

# Probar conexión
docker exec -it logiflow-authdb pg_isready -U logiuser
```

### RabbitMQ no funciona

```bash
# Ver estado de RabbitMQ
docker-compose -f docker-compose-full.yml logs rabbitmq

# Verificar colas
curl -u logiflow:logiflow123 http://localhost:15672/api/queues
```

### Rebuilding después de cambios

```bash
# Si cambiaste código Java
cd service-name
mvn clean package -DskipTests
cd ..
docker-compose -f docker-compose-full.yml up -d --build service-name
```

---

## 📈 Optimización de Imágenes

### Ver tamaño de imágenes
```bash
docker images | grep logiflow
```

### Multi-stage builds
Los Dockerfiles ya usan multi-stage builds para:
- ✅ Separar build y runtime
- ✅ Reducir tamaño de imagen final
- ✅ No incluir Maven/JDK en imagen final
- ✅ Solo JRE necesario

### Mejores prácticas aplicadas
- ✅ Imágenes alpine (más pequeñas)
- ✅ Usuario no-root
- ✅ Healthchecks configurados
- ✅ Límites de memoria via JVM
- ✅ Logs a stdout/stderr

---

## 🔒 Seguridad

### Secrets en Producción

**NO usar en producción los valores por defecto:**

```yaml
# Cambiar en producción:
- POSTGRES_PASSWORD: usar Docker secrets
- JWT_SECRET: generar con 256+ bits
- RABBITMQ_DEFAULT_PASS: usar contraseña fuerte
```

### Uso de Docker Secrets (Swarm/Kubernetes)

```bash
# Crear secret
echo "mi-password-super-segura" | docker secret create db_password -

# Usar en docker-compose (Swarm)
secrets:
  db_password:
    external: true
```

---

## 🚀 Próximo Paso: Kubernetes

Una vez que todo funcione en Docker Compose:

1. ✅ Verificar que todos los servicios estén healthy
2. ✅ Probar flujos end-to-end
3. ✅ Documentar configuraciones específicas
4. ➡️ Migrar a Kubernetes (siguiente fase)

**Archivos necesarios para K8s:**
- Deployments (7 servicios)
- Services (LoadBalancer/ClusterIP)
- ConfigMaps (configuración)
- Secrets (credenciales)
- PersistentVolumeClaims (bases de datos)
- Ingress (routing externo)

---

## 📚 Recursos Adicionales

- [Docker Compose Docs](https://docs.docker.com/compose/)
- [Spring Boot Docker](https://spring.io/guides/gs/spring-boot-docker/)
- [PostgreSQL Docker](https://hub.docker.com/_/postgres)
- [RabbitMQ Docker](https://hub.docker.com/_/rabbitmq)

---

## ✅ Checklist de Verificación

- [ ] Docker y Docker Compose instalados
- [ ] Script build-docker ejecutado sin errores
- [ ] Todos los contenedores en estado "Up (healthy)"
- [ ] GraphQL responde en http://localhost:8085/graphiql
- [ ] WebSocket conecta en http://localhost:8086
- [ ] RabbitMQ Management accesible
- [ ] pgAdmin funcional
- [ ] Logs sin errores críticos

**¡Listo para Kubernetes! 🎉**
