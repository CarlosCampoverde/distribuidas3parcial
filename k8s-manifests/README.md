#  LogiFlow - Sistema Distribuido de Entregas

**Plataforma de microservicios para gestión de entregas multinivel**  
**Stack:** Spring Boot 3.2 | Docker | Kubernetes | PostgreSQL | RabbitMQ

---

##  Estado del Proyecto

| componente | Estado | Formato |
|-----------|--------|---------|
| Docker |  Completo | docker-compose-full.yml |
| Kubernetes |  Completo | 4x YAML manifiestos |
| GitHub |  Pusheado | github.com/CarlosCampoverde |
| Docker Hub |  Subido | docker.io/charly25 |
| API Gateway |  Funcional | Port 8080 |
| 7 Servicios |  Corriendo | Ports 8081-8086 |

---

##  INICIO RÁPIDO - DOCKER

\\\ash
cd logiflow-backend
docker-compose -f docker-compose-full.yml up -d
\\\

**URLs:**
- API Gateway: http://localhost:8080
- Auth: http://localhost:8081
- Pedidos: http://localhost:8082
- Fleet: http://localhost:8083
- Billing: http://localhost:8084
- GraphQL: http://localhost:8085
- Notifications: http://localhost:8086

---

##  INICIO RÁPIDO - KUBERNETES

\\\ash
cd k8s-manifests
kubectl apply -f 00-namespace-configmap.yaml
kubectl apply -f 01-infrastructure.yaml && sleep 60
kubectl apply -f 02-microservices.yaml && sleep 120
kubectl apply -f 03-api-gateway-ingress.yaml
\\\

---

##  Servicios

| Servicio | Puerto | BD | Función |
|----------|--------|----|----|
| API Gateway | 8080 | - | Enrutador central |
| Auth Service | 8081 | authdb | Autenticación JWT |
| Pedido Service | 8082 | pedidodb | Gestión de pedidos |
| Fleet Service | 8083 | fleetdb | Flota de vehículos |
| Billing Service | 8084 | billingdb | Facturación |
| GraphQL Service | 8085 | - | Query unificada |
| Notification Svc | 8086 | - | Notificaciones |

---

##  Tecnologías

- **Backend:** Java 17, Spring Boot 3.2, Spring Cloud
- **BD:** PostgreSQL 15 (4x instances)
- **Message Broker:** RabbitMQ 3.12
- **Containers:** Docker 29+, Kubernetes 1.35
- **Build:** Maven 3

---

##  Enlaces

- **GitHub:** https://github.com/CarlosCampoverde/distribuidas3parcial
- **Docker Hub:** https://hub.docker.com/r/charly25
- **API Docs:** http://localhost:8080/swagger-ui.html (en Docker)

---

**Última actualización:** Febrero 10, 2026 | **Estado:**  COMPLETO
