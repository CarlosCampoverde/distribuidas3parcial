# Kubernetes Manifests - LogiFlow

Este directorio contiene todos los manifests de Kubernetes necesarios para deployar LogiFlow.

## 📁 Estructura

```
k8s/
├── namespace.yaml                    # Namespace logiflow
├── configmap.yaml                    # Variables de configuración
├── secrets.yaml                      # Credenciales (base64)
├── ingress.yaml                      # Routing externo
├── deploy-k8s.ps1                    # Script deployment Windows
├── deploy-k8s.sh                     # Script deployment Linux/Mac
├── delete-k8s.ps1                    # Script cleanup
├── storage/                          # Persistent Volume Claims
│   ├── pvc-authdb.yaml              # 2Gi
│   ├── pvc-pedidodb.yaml            # 2Gi
│   ├── pvc-fleetdb.yaml             # 2Gi
│   ├── pvc-billingdb.yaml           # 2Gi
│   └── pvc-rabbitmq.yaml            # 1Gi
├── databases/                        # PostgreSQL StatefulSets
│   ├── authdb-statefulset.yaml
│   ├── pedidodb-statefulset.yaml
│   ├── fleetdb-statefulset.yaml
│   └── billingdb-statefulset.yaml
├── infrastructure/                   # Messaging
│   └── rabbitmq-statefulset.yaml
└── services/                         # Microservices Deployments
    ├── auth-service-deployment.yaml
    ├── pedido-service-deployment.yaml
    ├── fleet-service-deployment.yaml
    ├── billing-service-deployment.yaml
    ├── graphql-service-deployment.yaml
    ├── notification-service-deployment.yaml
    └── api-gateway-deployment.yaml
```

## 🚀 Quick Start

### Opción 1: Script Automatizado (Recomendado)

**Windows:**
```powershell
.\deploy-k8s.ps1
```

**Linux/Mac:**
```bash
chmod +x deploy-k8s.sh
./deploy-k8s.sh
```

### Opción 2: Manual

```bash
kubectl apply -f namespace.yaml
kubectl apply -f configmap.yaml
kubectl apply -f secrets.yaml
kubectl apply -f storage/
kubectl apply -f databases/
# Wait 60 seconds
kubectl apply -f infrastructure/
# Wait 30 seconds
kubectl apply -f services/
kubectl apply -f ingress.yaml
```

## 📊 Recursos Totales

- **Namespace:** 1 (logiflow)
- **ConfigMaps:** 1 (17 variables)
- **Secrets:** 1 (4 secretos)
- **PVCs:** 5 (9Gi total)
- **StatefulSets:** 5 (4 DBs + RabbitMQ)
- **Deployments:** 7 (microservices, 2 réplicas cada uno)
- **Services:** 13 (11 ClusterIP + 2 NodePort)
- **Ingress:** 1 (5 rules)
- **Pods esperados:** 19 (si todos healthy)

## 🌐 Puertos Expuestos

### NodePort (acceso externo)
- API Gateway: `30080`
- RabbitMQ Management: `30672`

### Port Forwarding (desarrollo)
```bash
kubectl port-forward svc/api-gateway 8080:8080 -n logiflow
kubectl port-forward svc/graphql-service 8085:8085 -n logiflow
kubectl port-forward svc/notification-service 8086:8086 -n logiflow
kubectl port-forward svc/rabbitmq-service 15672:15672 -n logiflow
```

## 🔍 Verificación

```bash
# Ver todos los pods
kubectl get pods -n logiflow

# Ver servicios
kubectl get svc -n logiflow

# Ver logs
kubectl logs -f deployment/api-gateway -n logiflow

# Ver eventos
kubectl get events -n logiflow --sort-by='.lastTimestamp'

# Métricas
kubectl top pods -n logiflow
```

## 🗑️ Cleanup

```powershell
# Windows
.\delete-k8s.ps1

# Linux/Mac o manual
kubectl delete namespace logiflow
```

## 📚 Documentación

Ver [KUBERNETES_GUIDE.md](../KUBERNETES_GUIDE.md) para guía completa de deployment, troubleshooting y mejores prácticas.

## ⚙️ Requisitos

- Kubernetes cluster (Minikube, Docker Desktop, AKS, GKE, EKS)
- kubectl configurado
- StorageClass disponible
- 4.5Gi RAM mínimo
- 2.5 CPU cores mínimo
- Imágenes Docker disponibles

## ✅ Checklist

- [ ] Cluster Kubernetes running
- [ ] kubectl version funciona
- [ ] StorageClass existe: `kubectl get sc`
- [ ] Imágenes Docker construidas
- [ ] Suficiente capacidad en cluster
- [ ] Deploy ejecutado sin errores
- [ ] Todos los pods Running
- [ ] Health checks pasando
