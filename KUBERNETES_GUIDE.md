# ☸️ Guía de Deployment Kubernetes - LogiFlow

## 📋 Requisitos Previos

### 1. Kubernetes Cluster
Necesitas un cluster Kubernetes funcional. Opciones:

**Desarrollo Local:**
- ✅ **Minikube** (Recomendado para Windows/Mac/Linux)
- ✅ **Docker Desktop Kubernetes** (Windows/Mac)
- ✅ **Kind** (Kubernetes in Docker)
- ✅ **K3s** (Linux lightweight)

**Cloud:**
- ☁️ Azure Kubernetes Service (AKS)
- ☁️ Google Kubernetes Engine (GKE)
- ☁️ Amazon Elastic Kubernetes Service (EKS)

### 2. Herramientas Necesarias

```powershell
# Verificar kubectl
kubectl version --client

# Verificar conexión al cluster
kubectl cluster-info
kubectl get nodes
```

### 3. Ingress Controller (Opcional pero recomendado)

```powershell
# Instalar NGINX Ingress Controller
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.8.1/deploy/static/provider/cloud/deploy.yaml

# Verificar instalación
kubectl get pods -n ingress-nginx
```

### 4. Storage Class
Verificar que existe una StorageClass por defecto:

```powershell
kubectl get storageclass
```

Si no existe, Minikube/Docker Desktop ya incluyen una llamada `standard`.

---

## 🚀 Deployment Rápido

### Opción 1: Script Automatizado (Recomendado)

**Windows (PowerShell):**
```powershell
cd k8s
.\deploy-k8s.ps1
```

**Linux/Mac (Bash):**
```bash
cd k8s
chmod +x deploy-k8s.sh
./deploy-k8s.sh
```

### Opción 2: Manual Paso a Paso

```bash
# 1. Crear namespace
kubectl apply -f k8s/namespace.yaml

# 2. Crear ConfigMap y Secrets
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secrets.yaml

# 3. Crear PVCs
kubectl apply -f k8s/storage/

# 4. Deploy Databases
kubectl apply -f k8s/databases/

# Esperar a que DBs estén listas (~60 segundos)
kubectl get pods -n logiflow -w

# 5. Deploy Infrastructure (RabbitMQ)
kubectl apply -f k8s/infrastructure/

# Esperar a que RabbitMQ esté listo (~30 segundos)

# 6. Deploy Microservices
kubectl apply -f k8s/services/

# 7. Deploy Ingress
kubectl apply -f k8s/ingress.yaml
```

---

## 🔍 Verificar Deployment

### Ver Estado de Pods

```powershell
# Ver todos los pods
kubectl get pods -n logiflow

# Ver con más detalles
kubectl get pods -n logiflow -o wide

# Watch modo (actualización en vivo)
kubectl get pods -n logiflow -w
```

**Estado esperado (después de ~3 minutos):**
```
NAME                                  READY   STATUS    RESTARTS   AGE
api-gateway-xxxxx                     1/1     Running   0          2m
auth-service-xxxxx                    1/1     Running   0          2m
billing-service-xxxxx                 1/1     Running   0          2m
fleet-service-xxxxx                   1/1     Running   0          2m
graphql-service-xxxxx                 1/1     Running   0          2m
notification-service-xxxxx            1/1     Running   0          2m
pedido-service-xxxxx                  1/1     Running   0          2m
authdb-0                              1/1     Running   0          3m
billingdb-0                           1/1     Running   0          3m
fleetdb-0                             1/1     Running   0          3m
pedidodb-0                            1/1     Running   0          3m
rabbitmq-0                            1/1     Running   0          3m
```

### Ver Servicios

```powershell
kubectl get svc -n logiflow
```

**Resultado esperado:**
```
NAME                   TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)
api-gateway            NodePort    10.96.x.x       <none>        8080:30080/TCP
auth-service           ClusterIP   10.96.x.x       <none>        8081/TCP
authdb-service         ClusterIP   None            <none>        5432/TCP
billing-service        ClusterIP   10.96.x.x       <none>        8084/TCP
billingdb-service      ClusterIP   None            <none>        5432/TCP
fleet-service          ClusterIP   10.96.x.x       <none>        8083/TCP
fleetdb-service        ClusterIP   None            <none>        5432/TCP
graphql-service        ClusterIP   10.96.x.x       <none>        8085/TCP
notification-service   ClusterIP   10.96.x.x       <none>        8086/TCP
pedido-service         ClusterIP   10.96.x.x       <none>        8082/TCP
pedidodb-service       ClusterIP   None            <none>        5432/TCP
rabbitmq-service       ClusterIP   10.96.x.x       <none>        5672/TCP, 15672/TCP
rabbitmq-management    NodePort    10.96.x.x       <none>        15672:30672/TCP
```

### Ver Logs

```powershell
# Logs de un deployment
kubectl logs -f deployment/api-gateway -n logiflow

# Logs de un pod específico
kubectl logs -f <pod-name> -n logiflow

# Logs de todos los pods de un deployment
kubectl logs -l app=pedido-service -n logiflow --all-containers=true

# Ver eventos
kubectl get events -n logiflow --sort-by='.lastTimestamp'
```

---

## 🌐 Acceder a los Servicios

### Opción 1: Port Forwarding (Desarrollo)

```powershell
# API Gateway
kubectl port-forward svc/api-gateway 8080:8080 -n logiflow

# GraphQL Service
kubectl port-forward svc/graphql-service 8085:8085 -n logiflow

# Notification Service (WebSocket)
kubectl port-forward svc/notification-service 8086:8086 -n logiflow

# RabbitMQ Management
kubectl port-forward svc/rabbitmq-service 15672:15672 -n logiflow
```

Luego accede:
- API Gateway: http://localhost:8080
- GraphQL: http://localhost:8085/graphiql
- WebSocket: http://localhost:8086
- RabbitMQ: http://localhost:15672

### Opción 2: NodePort

El API Gateway ya está expuesto como NodePort en el puerto 30080.

```powershell
# Obtener IP del nodo
kubectl get nodes -o wide

# Si usas Minikube
minikube ip
```

Accede en: `http://<NODE-IP>:30080`

### Opción 3: Ingress (Recomendado para Producción)

**1. Configurar DNS local:**

Editar archivo `C:\Windows\System32\drivers\etc\hosts`:
```
127.0.0.1 logiflow.local
```

**2. Obtener IP del Ingress:**
```powershell
kubectl get ingress -n logiflow
```

**3. Acceder vía Ingress:**
- http://logiflow.local/
- http://logiflow.local/graphiql
- http://logiflow.local/ws

---

## 🧪 Probar el Sistema

### 1. Health Checks

```powershell
# Port forward API Gateway
kubectl port-forward svc/api-gateway 8080:8080 -n logiflow

# En otra terminal
curl http://localhost:8080/actuator/health
```

### 2. GraphQL

```powershell
# Port forward GraphQL Service
kubectl port-forward svc/graphql-service 8085:8085 -n logiflow
```

Abrir navegador: http://localhost:8085/graphiql

Ejecutar query:
```graphql
query {
  pedidos(limit: 5) {
    id
    codigoPedido
    estado
  }
}
```

### 3. WebSocket

```powershell
# Port forward Notification Service
kubectl port-forward svc/notification-service 8086:8086 -n logiflow
```

Abrir navegador: http://localhost:8086

---

## 📊 Monitoreo

### Dashboard de Kubernetes

**Minikube:**
```powershell
minikube dashboard
```

**Docker Desktop:**
Usa Lens o K9s (herramientas de terceros).

### Métricas de Pods

```powershell
# Ver uso de recursos
kubectl top pods -n logiflow

# Ver uso de nodos
kubectl top nodes
```

### Describe Resources

```powershell
# Detalles de un pod
kubectl describe pod <pod-name> -n logiflow

# Detalles de un deployment
kubectl describe deployment api-gateway -n logiflow

# Detalles de un service
kubectl describe svc api-gateway -n logiflow
```

---

## 🔧 Troubleshooting

### Pod en estado CrashLoopBackOff

```powershell
# Ver logs del pod
kubectl logs <pod-name> -n logiflow --previous

# Describir el pod para ver eventos
kubectl describe pod <pod-name> -n logiflow
```

**Causas comunes:**
- Database no está ready
- Variables de entorno incorrectas
- Falta RabbitMQ connection

### Pod en estado ImagePullBackOff

```powershell
kubectl describe pod <pod-name> -n logiflow
```

**Solución:**
Las imágenes Docker deben estar disponibles localmente o en un registry.

**Para Minikube:**
```powershell
# Usar Docker daemon de Minikube
minikube docker-env | Invoke-Expression

# Reconstruir imágenes dentro de Minikube
cd logiflow-backend
.\build-docker.ps1
```

**Para Docker Desktop:**
Las imágenes ya deberían estar disponibles.

### Servicio no conecta a Database

```powershell
# Verificar que StatefulSet está running
kubectl get statefulsets -n logiflow

# Verificar que Service de DB existe
kubectl get svc -n logiflow | Select-String "db"

# Probar conectividad desde un pod
kubectl exec -it deployment/pedido-service -n logiflow -- sh
# Dentro del pod:
nc -zv pedidodb-service 5432
```

### RabbitMQ no funciona

```powershell
# Ver logs de RabbitMQ
kubectl logs rabbitmq-0 -n logiflow

# Verificar que está ready
kubectl get pods -n logiflow | Select-String rabbitmq

# Port forward y acceder a management
kubectl port-forward svc/rabbitmq-service 15672:15672 -n logiflow
# Abrir: http://localhost:15672
# Usuario: logiflow / logiflow123
```

---

## 🔄 Actualizar Deployment

### Actualizar imagen de un servicio

```powershell
# Reconstruir imagen Docker
cd logiflow-backend
mvn clean package -DskipTests
docker build -t logiflow-backend-pedido-service:latest pedido-service/

# Reiniciar deployment
kubectl rollout restart deployment/pedido-service -n logiflow

# Ver progreso del rollout
kubectl rollout status deployment/pedido-service -n logiflow
```

### Actualizar ConfigMap

```powershell
# Editar configmap.yaml
# Luego aplicar
kubectl apply -f k8s/configmap.yaml

# Reiniciar pods para que tomen nuevos valores
kubectl rollout restart deployment/pedido-service -n logiflow
```

### Escalar Réplicas

```powershell
# Escalar manualmente
kubectl scale deployment/api-gateway --replicas=3 -n logiflow

# Ver réplicas
kubectl get deployment -n logiflow
```

---

## 🗑️ Eliminar Deployment

### Opción 1: Script Automatizado

```powershell
cd k8s
.\delete-k8s.ps1
```

### Opción 2: Manual

```powershell
# Eliminar todo el namespace (más rápido)
kubectl delete namespace logiflow

# O eliminar por tipo
kubectl delete -f k8s/ingress.yaml
kubectl delete -f k8s/services/
kubectl delete -f k8s/infrastructure/
kubectl delete -f k8s/databases/
kubectl delete -f k8s/storage/
kubectl delete -f k8s/configmap.yaml
kubectl delete -f k8s/secrets.yaml
kubectl delete -f k8s/namespace.yaml
```

---

## 🏗️ Arquitectura de Kubernetes

```
┌─────────────────────────────────────────────────────────┐
│                    Namespace: logiflow                   │
│                                                          │
│  ┌────────────────────────────────────────────────────┐ │
│  │                    Ingress                          │ │
│  │  Rules: /graphql, /ws, /rabbitmq, /                │ │
│  └────────────┬───────────────────────────────────────┘ │
│               │                                          │
│  ┌────────────┴───────────────────────────────────────┐ │
│  │              API Gateway Service                    │ │
│  │              (NodePort 30080)                       │ │
│  └────────────┬───────────────────────────────────────┘ │
│               │                                          │
│    ┌──────────┼──────────┬───────────┬─────────────┐   │
│    │          │          │           │             │   │
│    ▼          ▼          ▼           ▼             ▼   │
│  ┌────┐   ┌──────┐   ┌──────┐   ┌────────┐   ┌───────┐│
│  │Auth│   │Pedido│   │Fleet │   │Billing │   │GraphQL││
│  │Svc │   │ Svc  │   │ Svc  │   │  Svc   │   │  Svc  ││
│  │x2  │   │  x2  │   │  x2  │   │   x2   │   │  x2   ││
│  └─┬──┘   └──┬───┘   └──┬───┘   └───┬────┘   └───┬───┘│
│    │         │          │           │            │    │
│    ▼         ▼          ▼           ▼            │    │
│  ┌────┐   ┌──────┐   ┌──────┐   ┌────────┐     │    │
│  │Auth│   │Pedido│   │Fleet │   │Billing │     │    │
│  │ DB │   │  DB  │   │  DB  │   │   DB   │     │    │
│  │SS  │   │  SS  │   │  SS  │   │   SS   │     │    │
│  │PVC │   │ PVC  │   │ PVC  │   │  PVC   │     │    │
│  └────┘   └──┬───┘   └──┬───┘   └────────┘     │    │
│             │          │                        │    │
│             └─────┬────┴────────────────────────┘    │
│                   │                                  │
│                   ▼                                  │
│              ┌─────────┐         ┌────────────┐     │
│              │RabbitMQ │◄────────┤Notification│     │
│              │   SS    │         │    Svc     │     │
│              │   PVC   │         │     x2     │     │
│              └─────────┘         └────────────┘     │
│                                                      │
│  Legend:                                             │
│  Svc = Deployment + Service                          │
│  SS  = StatefulSet                                   │
│  PVC = PersistentVolumeClaim                         │
│  x2  = 2 replicas                                    │
└──────────────────────────────────────────────────────┘
```

---

## ✅ Checklist de Verificación

### Infraestructura
- [ ] Namespace `logiflow` creado
- [ ] ConfigMap y Secrets aplicados
- [ ] 5 PVCs creados (4 DBs + RabbitMQ)
- [ ] StorageClass disponible

### Databases y Messaging
- [ ] 4 PostgreSQL StatefulSets running (authdb, pedidodb, fleetdb, billingdb)
- [ ] RabbitMQ StatefulSet running
- [ ] Todos los pods de DB en estado `1/1 Running`
- [ ] Services de DB con `clusterIP: None`

### Microservicios
- [ ] 7 Deployments creados
- [ ] Cada deployment tiene 2 réplicas
- [ ] Todos los pods en estado `Running`
- [ ] Health checks pasando (liveness + readiness)
- [ ] 7 ClusterIP Services creados

### Networking
- [ ] API Gateway accesible vía NodePort (30080)
- [ ] Ingress creado (si NGINX Controller instalado)
- [ ] Port forwarding funciona correctamente

### Funcionalidad
- [ ] GraphQL responde a queries
- [ ] WebSocket conecta correctamente
- [ ] RabbitMQ Management accesible
- [ ] Logs no muestran errores críticos

---

## 📚 Recursos Adicionales

- [Kubernetes Official Docs](https://kubernetes.io/docs/)
- [kubectl Cheat Sheet](https://kubernetes.io/docs/reference/kubectl/cheatsheet/)
- [Minikube Docs](https://minikube.sigs.k8s.io/docs/)
- [NGINX Ingress Controller](https://kubernetes.github.io/ingress-nginx/)

---

## 🎯 Próximos Pasos

1. ✅ **Probar el deployment localmente** - Port forwarding + tests
2. ✅ **Monitorear recursos** - kubectl top, describe
3. ✅ **Optimizar configuración** - Resource limits, HPA
4. ➡️ **CI/CD Pipeline** - GitHub Actions / Azure DevOps
5. ➡️ **Deploy a Cloud** - AKS, GKE, EKS
6. ➡️ **Monitoring Stack** - Prometheus + Grafana
7. ➡️ **Service Mesh** - Istio / Linkerd (opcional)

---

**¡Deployment Exitoso! ☸️🎉**
