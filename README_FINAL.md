# 🚀 LogiFlow - Sistema Distribuido de Entregas

**Plataforma completa de microservicios con Docker + Kubernetes**  
**Stack:** Spring Boot 3.2 | Docker | Kubernetes | PostgreSQL | RabbitMQ

---

## ✅ ESTADO DEL PROYECTO

| Componente | Estado | Detalles |
|-----------|--------|---------|
| **Docker** | ✅ Completo | 7 imágenes en Docker Hub (charly25/logiflow-*) |
| **Kubernetes** | ✅ Listo | K8s 1.35 + Minikube + 4 manifiestos YAML |
| **GitHub** | ✅ Pusheado | github.com/CarlosCampoverde/distribuidas3parcial |
| **Docker Hub** | ✅ Subido | docker.io/charly25 |
| **API Gateway** | ✅ UP | Spring Cloud Gateway en puerto 8080 |
| **7 Servicios** | ✅ UP | Todos corriendo en puertos 8081-8086 |
| **BD & Broker** | ✅ UP | PostgreSQL 15 (4x) + RabbitMQ 3.12 |

---

## 🚀 INICIO RÁPIDO - OPCIÓN 1: DOCKER COMPOSE

### Paso 1: Clonar repositorio
```bash
git clone https://github.com/CarlosCampoverde/distribuidas3parcial.git
cd distribuidas3parcial/logiflow-backend
```

### Paso 2: Levantar todo
```bash
docker-compose -f docker-compose-full.yml up -d
docker ps --filter "name=logiflow-"
```

### Paso 3: Acceder a servicios
```
🌐 API Gateway:              http://localhost:8080
🔐 Auth Service:             http://localhost:8081
📦 Pedido Service:           http://localhost:8082
🚚 Fleet Service:            http://localhost:8083
💳 Billing Service:          http://localhost:8084
📊 GraphQL Service:          http://localhost:8085
📧 Notification Service:     http://localhost:8086
```

### Paso 4: Detener
```bash
docker-compose -f docker-compose-full.yml down
```

---

## ☸️ INICIO RÁPIDO - OPCIÓN 2: KUBERNETES

### Paso 1: Iniciar Minikube
```bash
minikube start --cpus=4 --memory=4096
minikube addons enable ingress
```

### Paso 2: Aplicar manifiestos (EN ORDEN)
```bash
cd k8s-manifests

# 1. Namespace y configuración
kubectl apply -f 00-namespace-configmap.yaml

# 2. Infraestructura (PostgreSQL + RabbitMQ)
kubectl apply -f 01-infrastructure.yaml
# ⏳ Esperar 60 segundos...
Start-Sleep -Seconds 60

# 3. Microservicios
kubectl apply -f 02-microservices.yaml
# ⏳ Esperar 120 segundos...
Start-Sleep -Seconds 120

# 4. API Gateway + Ingress
kubectl apply -f 03-api-gateway-ingress.yaml
```

### Paso 3: Verificar despliegue
```bash
kubectl get all -n logiflow
kubectl get pods -n logiflow -o wide
```

### Paso 4: Obtener IP de Minikube
```bash
minikube ip  # Ej: 192.168.49.2
```

### Paso 5: Acceder a servicios
```
http://<MINIKUBE_IP>:8080  # API Gateway
http://<MINIKUBE_IP>:8081  # Auth Service
...etc
```

### Paso 6: Ver logs
```bash
kubectl logs -n logiflow deployment/api-gateway -f
```

### Paso 7: Limpiar
```bash
kubectl delete namespace logiflow
minikube stop
```

---

## 📦 SERVICIOS IMPLEMENTADOS

| # | Servicio | Puerto | Base de Datos | Stack |
|---|----------|--------|---------------|-------|
| 1 | **API Gateway** | 8080 | - | Spring Cloud Gateway |
| 2 | **Auth Service** | 8081 | authdb | Spring Boot + JWT |
| 3 | **Pedido Service** | 8082 | pedidodb | Spring Boot + JPA |
| 4 | **Fleet Service** | 8083 | fleetdb | Spring Boot + GPS |
| 5 | **Billing Service** | 8084 | billingdb | Spring Boot |
| 6 | **GraphQL Service** | 8085 | - | Spring Boot + GraphQL |
| 7 | **Notification Svc** | 8086 | - | Spring Boot + RabbitMQ |

---

## 🏗️ ARQUITECTURA

```
                        ┌─────────────────────────┐
                        │   🌐 API GATEWAY      │
                        │  (Port 8080 / 80)     │
                        └──────────┬──────────────┘
                                   │
           ┌───────────────────────┼───────────────────────┐
           │                       │                       │
      ┌────▼────┐          ┌──────▼──────┐        ┌───────▼──┐
      │ 🔐 AUTH │          │ 📦 PEDIDOS  │        │ 🚚 FLEET │
      │ (8081)  │          │ (8082)      │        │ (8083)   │
      └────┬────┘          └────┬────────┘        └───────┬──┘
           │                    │                        │
      ┌────▼────┐          ┌────▼────┐            ┌──────▼───┐
      │ authdb  │          │pedidodb │            │ fleetdb  │
      └─────────┘          └─────────┘            └──────────┘

                    ┌──────────────────────┐
                    │  💳 BILLING (8084)   │
                    └──────────┬───────────┘
                               │
                          ┌────▼────┐
                          │billingdb│
                          └─────────┘

         ┌────────────────────────────────────────┐
         │    🐰 RabbitMQ (5672) Message Bus    │
         └────────────────────────────────────────┘
         
    ┌──────────────────┬──────────────────┐
    │  📊 GraphQL      │  📧 Notification │
    │  (8085)          │  (8086)          │
    └──────────────────┴──────────────────┘
```

---

## 💾 INFRAESTRUCTURA

### Bases de Datos (PostgreSQL 15)
- **authdb** (Puerto 5532) - Usuarios y autenticación
- **pedidodb** (Puerto 5533) - Pedidos y entregas
- **fleetdb** (Puerto 5534) - Vehículos y repartidores
- **billingdb** (Puerto 5535) - Facturas y costos

### Message Broker
- **RabbitMQ 3.12** (Puerto 5672)
  - UI Management: http://localhost:15672 (guest/guest)

### Admin Tools
- **PgAdmin 4** (Puerto 5050)
  - Admin de bases de datos PostgreSQL

---

## 🛠️ TECNOLOGÍAS

### Backend
```
✓ Java 17 (LTS)
✓ Spring Boot 3.2.0
✓ Spring Cloud 2023.0.0
✓ Spring Cloud Gateway
✓ Spring Data JPA / Hibernate
✓ Spring Security + JWT (0.12.3)
✓ Lombok
✓ OpenAPI/Swagger 2.3.0
```

### Containerización
```
✓ Docker 29.0+
✓ Docker Compose
✓ Multi-stage builds (optimizado)
✓ Alpine Linux (imágenes pequeñas)
```

### Orquestación
```
✓ Kubernetes 1.35
✓ Minikube 1.38
✓ Ingress Controller (Nginx)
```

### DevOps
```
✓ Maven 3 (Build)
✓ Git / GitHub (Version Control)
✓ Docker Hub (Registry)
```

---

## 📡 EJEMPLOS DE USO

### Health Check
```bash
curl http://localhost:8080/actuator/health
# {"status":"UP"}
```

### Registrar Usuario
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "carlos",
    "email": "carlos@example.com",
    "password": "pass123"
  }'
```

### Login
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "carlos@example.com",
    "password": "pass123"
  }'
# Obtiene JWT token
```

### Crear Pedido
```bash
curl -X POST http://localhost:8082/api/pedidos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -d '{
    "origen": "Av. Principal 123",
    "destino": "Calle Secundaria 456",
    "peso": 5.5,
    "cliente": "Juan Pérez"
  }'
```

---

## 📁 ESTRUCTURA DEL PROYECTO

```
distribuidas3parcial/
├── logiflow-backend/
│   ├── api-gateway/                    # Spring Cloud Gateway
│   ├── auth-service/                   # Autenticación JWT
│   ├── pedido-service/                 # Gestión de pedidos
│   ├── fleet-service/                  # Gestión de flota
│   ├── billing-service/                # Facturación
│   ├── graphql-service/                # GraphQL API
│   ├── notification-service/           # Notificaciones
│   ├── common/                         # DTOs y utilidades compartidas
│   ├── pom.xml                         # Maven parent POM
│   ├── docker-compose-full.yml         # Docker Compose config
│   ├── build-docker.ps1                # Script de build
│   └── Dockerfiles/                    # Individual service Dockerfiles
│
├── k8s-manifests/                      # Kubernetes manifiestos
│   ├── 00-namespace-configmap.yaml     # NS + ConfigMaps
│   ├── 01-infrastructure.yaml          # PostgreSQL + RabbitMQ
│   ├── 02-microservices.yaml           # 7 servicios
│   └── 03-api-gateway-ingress.yaml     # API Gateway + Ingress
│
├── deploy-kubernetes.ps1               # Script de despliegue
└── README.md                           # Documentación
```

---

## 🔐 CREDENCIALES POR DEFECTO

### PostgreSQL
- **Usuario:** postgres
- **Contraseña:** postgres123

### RabbitMQ
- **Usuario:** guest
- **Contraseña:** guest

---

## 🔗 ENLACES IMPORTANTES

- **GitHub Repository:** https://github.com/CarlosCampoverde/distribuidas3parcial
- **Docker Hub:** https://hub.docker.com/r/charly25
- **API Swagger:** http://localhost:8080/swagger-ui.html (en Docker)

---

## 📊 ESTADÍSTICAS

| Métrica | Valor |
|---------|-------|
| Microservicios | 7 |
| Bases de Datos | 4 (PostgreSQL) |
| Imágenes Docker | 7 servicios + infraestructura |
| Tamaño total imágenes | ~2 GB |
| Manifiestos Kubernetes | 4 archivos YAML |
| Replicas por servicio | 1-2 (configurable) |
| Tiempo deployment Docker | ~2 minutos |
| Tiempo deployment K8s | ~5 minutos |

---

## 🚦 TROUBLESHOOTING

### Docker
```bash
# Ver logs de un servicio
docker-compose logs [service-name]

# Reiniciar servicio
docker-compose restart [service-name]

# Limpiar volúmenes
docker-compose down -v
```

### Kubernetes
```bash
# Ver descripción de un pod
kubectl describe pod -n logiflow [pod-name]

# Ver eventos
kubectl get events -n logiflow

# Ejecutar comando en pod
kubectl exec -it -n logiflow [pod-name] -- sh
```

---

## 👨‍💻 INFORMACIÓN DEL PROYECTO

- **Autor:** Carlos Campoverde
- **Proyecto:** 3er Parcial de Sistemas Distribuidos
- **Año:** 2026
- **Estado:** ✅ COMPLETADO Y FUNCIONAL

---

**Última actualización:** 10 Febrero 2026  
**Versión:** 1.0.0  
**Licencia:** Proyecto académico
