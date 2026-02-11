# 🚀 LogiFlow - Sistema Distribuido de Entregas

**Sistema de gestión de entregas basado en microservicios con Spring Boot 3.2 y Kubernetes**

> Proyecto 3er Parcial - Sistemas Distribuidos

---

## 📊 Tabla de Contenidos

1. [Inicio Rápido con Docker](#-inicio-rápido-con-docker)
2. [Despliegue en Kubernetes](#-despliegue-en-kubernetes-minikube)
3. [Arquitectura del Sistema](#-arquitectura-del-sistema)
4. [Servicios Microservicios](#-servicios-microservicios)
5. [Acceso a APIs](#-acceso-a-las-apis)
6. [Tecnologías Usadas](#-tecnologías-usadas)

---

## 🐳 Inicio Rápido con Docker

### Requisitos

- **Docker**: v29+
- **Docker Compose**: v2+
- **Git**: para clonar el repositorio

### Pasos para Iniciar

#### 1. Clonar el Repositorio
```bash
git clone https://github.com/CarlosCampoverde/distribuidas3parcial.git
cd distribuidas3parcial
```

#### 2. Construir las Imágenes Docker
```bash
cd logiflow-backend
.\build-docker.ps1  # En Windows PowerShell
# o en Linux/Mac:
# bash build-docker.ps1
```

**Resultado:** Se construyen 7 imágenes de microservicios
```
✓ logiflow-backend-api-gateway:latest (258MB)
✓ logiflow-backend-auth-service:latest (295MB)
✓ logiflow-backend-pedido-service:latest (293MB)
✓ logiflow-backend-fleet-service:latest (293MB)
✓ logiflow-backend-billing-service:latest (289MB)
✓ logiflow-backend-graphql-service:latest (252MB)
✓ logiflow-backend-notification-service:latest (233MB)
```

#### 3. Iniciar la Pila Completa con Docker Compose
```bash
docker-compose -f docker-compose-full.yml up -d
```

**Servicios que se inician:**
- 🟦 7 Microservicios (puertos 8080-8086)
- 🟩 4 Bases de datos PostgreSQL
- 🐰 RabbitMQ Message Broker (puerto 5672, UI en 15672)
- 🔧 PgAdmin para administar BDs (puerto 5050)

#### 4. Verificar Estado
```bash
docker ps --filter "name=logiflow-" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
```

#### 5. Acceso a Servicios
```
API Gateway:              http://localhost:8080
Auth Service:             http://localhost:8081
Pedido Service:           http://localhost:8082
Fleet Service:            http://localhost:8083
Billing Service:          http://localhost:8084
GraphQL Service:          http://localhost:8085
Notification Service:     http://localhost:8086
PgAdmin:                  http://localhost:5050
RabbitMQ Management:      http://localhost:15672 (guest/guest)
```

#### 6. Detener Servicios
```bash
docker-compose -f docker-compose-full.yml down
```

---

## ☸️ Despliegue en Kubernetes (Minikube)

### Requisitos Previos

- **Minikube**: v1.38.0+
- **kubectl**: v1.35.0+
- **Docker Hub Account**: credenciales para descargar imágenes

### Pasos de Despliegue

#### 1. Iniciar Minikube
```bash
minikube start --driver=docker --cpus=4 --memory=4096
minikube addons enable ingress
```

#### 2. Aplicar Manifiestos Kubernetes
```bash
cd k8s-manifests

# Crear namespace y configuración
kubectl apply -f 00-namespace-configmap.yaml

# Desplegar infraestructura (PostgreSQL, RabbitMQ)
kubectl apply -f 01-infrastructure.yaml
# ⏳ Esperar 60 segundos a que se inicializen...
Start-Sleep -Seconds 60

# Desplegar microservicios
kubectl apply -f 02-microservices.yaml
# ⏳ Esperar 120 segundos a que se inicializen...
Start-Sleep -Seconds 120

# Desplegar API Gateway e Ingress
kubectl apply -f 03-api-gateway-ingress.yaml
```

#### 3. Verificar Despliegue
```bash
# Ver los pods
kubectl get pods -n logiflow

# Ver los servicios
kubectl get svc -n logiflow

# Ver los deployments
kubectl get deployments -n logiflow

# Ver el Ingress
kubectl get ingress -n logiflow
```

#### 4. Obtener IP de Minikube
```bash
minikube ip
# Resultado: 192.168.49.2 (ejemplo)
```

#### 5. Acceso a Servicios en Kubernetes
```
API Gateway:              http://<MINIKUBE_IP>:8080
Auth Service:             http://<MINIKUBE_IP>:8081
Pedido Service:           http://<MINIKUBE_IP>:8082
Fleet Service:            http://<MINIKUBE_IP>:8083
Billing Service:           http://<MINIKUBE_IP>:8084
GraphQL Service:          http://<MINIKUBE_IP>:8085
Notification Service:     http://<MINIKUBE_IP>:8086
```

#### 6. Ver Logs
```bash
# Logs del API Gateway
kubectl logs -n logiflow deployment/api-gateway -f

# Logs de un servicio específico
kubectl logs -n logiflow deployment/auth-service -f

# Últimas 50 líneas
kubectl logs -n logiflow deployment/api-gateway --tail=50
```

#### 7. Limpiar Despliegue
```bash
# Eliminar todo el namespace (todos los recursos)
kubectl delete namespace logiflow

# Detener Minikube
minikube stop
```

---

## 🏗️ Arquitectura del Sistema

```
┌─────────────────────────────────────────────────────────────┐
│                     API GATEWAY (8080)                      │
│              Spring Cloud Gateway / Ingress                 │
└────────┬────────────────────────────────────────────┬───────┘
         │                                            │
    ┌────▼────┐  ┌──────────┐  ┌──────────┐  ┌───────▼──┐
    │  AUTH   │  │ PEDIDOS  │  │  FLEET   │  │ BILLING  │
    │ (8081)  │  │ (8082)   │  │ (8083)   │  │ (8084)   │
    └────┬────┘  └────┬─────┘  └────┬─────┘  └───────┬──┘
         │            │             │               │
    ┌────▼────┐  ┌────▼─────┐  ┌────▼─────┐   ┌────▼────┐
    │ AuthDB  │  │ PedidoDB │  │ FleetDB  │   │BillingDB│
    │(PG 5532)│  │(PG 5533) │  │(PG 5534) │   │(PG 5535)│
    └─────────┘  └──────────┘  └──────────┘   └─────────┘

    ┌─────────────────────────────────────────┐
    │           RabbitMQ (5672)               │
    │    Message Broker / Event Bus           │
    └─────────────────────────────────────────┘

    ┌─────────────────────────────────────────┐
    │  GraphQL Service (8085)                 │
    │  NotificationService (8086)             │
    └─────────────────────────────────────────┘
```

---

## 🔧 Servicios Microservicios

### 1. **API Gateway** 🌐
- **Puerto:** 8080
- **Función:** Enrutador central, punto de entrada único
- **Stack:** Spring Cloud Gateway
- **Rutas:**
  - `/auth/*` → Auth Service
  - `/pedidos/*` → Pedido Service
  - `/flota/*` → Fleet Service
  - `/billing/*` → Billing Service
  - `/graphql/*` → GraphQL Service
  - `/notificaciones/*` → Notification Service

### 2. **Authentication Service** 🔐
- **Puerto:** 8081
- **Base de Datos:** PostgreSQL (authdb)
- **Función:** Manejo de usuarios, login, JWT
- **Endpoints Principales:**
  - `POST /api/auth/register` - Registrar usuario
  - `POST /api/auth/login` - Login
  - `GET /api/auth/validate` - Validar token

### 3. **Pedido Service** 📦
- **Puerto:** 8082
- **Base de Datos:** PostgreSQL (pedidodb)
- **Función:** Gestión de pedidos de entregas
- **Dependencias:** Auth Service
- **Endpoints Principales:**
  - `GET /api/pedidos` - Listar pedidos
  - `POST /api/pedidos` - Crear pedido
  - `PUT /api/pedidos/:id` - Actualizar estado

### 4. **Fleet Service** 🚚
- **Puerto:** 8083
- **Base de Datos:** PostgreSQL (fleetdb)
- **Función:** Gestión de flota de vehículos
- **Endpoints Principales:**
  - `GET /api/fleet/vehicles` - Listar vehículos
  - `PUT /api/fleet/vehicles/:id/location` - Actualizar ubicación

### 5. **Billing Service** 💳
- **Puerto:** 8084
- **Base de Datos:** PostgreSQL (billingdb)
- **Función:** Facturación y pagos
- **Dependencias:** Pedido Service
- **Endpoints Principales:**
  - `GET /api/billing/invoices` - Listar facturas
  - `POST /api/billing/invoices` - Crear factura

### 6. **GraphQL Service** 📊
- **Puerto:** 8085
- **Función:** API GraphQL unificada
- **Endpoint:** `POST /graphql`

### 7. **Notification Service** 📧
- **Puerto:** 8086
- **Función:** Sistema de notificaciones
- **Método:** Message Broker (RabbitMQ)

---

## 📡 Acceso a las APIs

### Health Check
```bash
curl http://localhost:8080/actuator/health
# Response: {"status":"UP"}
```

### Ejemplo: Crear Usuario
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "carlos",
    "email": "carlos@example.com",
    "password": "password123"
  }'
```

### Ejemplo: Login
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "carlos@example.com",
    "password": "password123"
  }'
```

### Ejemplo: Crear Pedido
```bash
curl -X POST http://localhost:8082/api/pedidos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN_JWT>" \
  -d '{
    "origen": "Av. Principal 123",
    "destino": "Calle Secundaria 456",
    "peso": 5.5,
    "cliente": "Juan Pérez"
  }'
```

---

## 🛠️ Tecnologías Usadas

### Backend
- **Java 17** - Lenguaje de programación
- **Spring Boot 3.2.0** - Framework principal
- **Spring Cloud 2023.0.0** - Microservicios
- **Spring Data JPA** - ORM
- **Spring Cloud Gateway** - API Gateway
- **Spring Security** - Autenticación/Autorización
- **JWT 0.12.3** - Tokens seguros

### Base de Datos
- **PostgreSQL 15** - Database relacional
- **Hibernate** - ORM

### Message Broker
- **RabbitMQ 3.12** - Message queue

### Containerización
- **Docker 29+** - Containerización
- **Docker Compose** - Orquestación local

### Orquestación
- **Kubernetes 1.35** - Orquestación de contenedores
- **Minikube** - Kubernetes local para desarrollo

### Build
- **Maven 3** - Gestión de dependencias
- **GraphQL** - API query language

---

## 📁 Estructura del Proyecto

```
distribuidas3parcial/
├── logiflow-backend/
│   ├── api-gateway/              # API Gateway service
│   ├── auth-service/             # Authentication service
│   ├── pedido-service/           # Order management
│   ├── fleet-service/            # Fleet management
│   ├── billing-service/          # Billing service
│   ├── graphql-service/          # GraphQL unified API
│   ├── notification-service/     # Notifications
│   ├── common/                   # Shared DTOs & utilities
│   ├── pom.xml                   # Maven parent POM
│   ├── docker-compose-full.yml   # Docker Compose config
│   └── Dockerfiles/              # Individual service Dockerfiles
├── k8s-manifests/                # Kubernetes manifiestos
│   ├── 00-namespace-configmap.yaml
│   ├── 01-infrastructure.yaml
│   ├── 02-microservices.yaml
│   └── 03-api-gateway-ingress.yaml
├── deploy-kubernetes.ps1         # Kubernetes deployment script
└── README.md                      # Este archivo
```

---

## 🔗 Enlaces Importantes

- **GitHub Repository:** [CarlosCampoverde/distribuidas3parcial](https://github.com/CarlosCampoverde/distribuidas3parcial)
- **Docker Hub:** [charly25/logiflow-*](https://hub.docker.com/r/charly25)
- **Author:** Carlos Campoverde

---

## 📝 Notas Importantes

### Docker
- Las imágenes están en Docker Hub bajo usuario `charly25`
- Total: ~2GB de imágenes (7 servicios + bases de datos)
- tiempo de compilación: ~10 minutos (primera vez)

### Kubernetes
- Requiere Minikube en ejecución
- Los pods descargarán imágenes desde Docker Hub automáticamente
- Tiempo de inicialización completo: ~5 minutos

### Base de Datos
- PostgreSQL se inicializa automáticamente con 4 bases de datos
- Credenciales por defecto: usuario `postgres`, contraseña `postgres123`

---

## 🤝 Soporte

Para reportar problemas o preguntas:
1. Abre un issue en GitHub
2. Verifica los logs de los servicios
3. Consulta la sección de troubleshooting

---

**Última actualización:** Febrero 2026  
**Estado:** ✅ Dockerizado | ✅ Kubernetes Ready | ✅ GitHub Hosted
