# 🔍 Estado de Kubernetes en tu Sistema

## ✅ Lo que TIENES instalado:

1. **kubectl** v1.34.1 ✅
2. **Docker Desktop** v29.2.0 ✅
3. **Minikube** v1.38.0 ✅

## ❌ Problema Actual:

**No hay ningún cluster de Kubernetes corriendo.**

- Minikube está instalado pero **detenido**
- Docker Desktop Kubernetes probablemente no está activado

---

## 🚀 Soluciones (Elige UNA)

### Opción 1: Minikube (Recomendado - Más Rápido) ⭐

**Ventajas:**
- Ya está instalado
- Más ligero que Docker Desktop K8s
- Incluye dashboard integrado
- Mejor para desarrollo y pruebas

**Iniciar Minikube:**

```powershell
# Iniciar Minikube
minikube start

# Verificar que funciona
kubectl get nodes

# Dashboard (opcional)
minikube dashboard
```

**Comandos útiles:**

```powershell
# Ver status
minikube status

# Detener
minikube stop

# Eliminar cluster
minikube delete

# Ver IP del cluster
minikube ip

# SSH al nodo
minikube ssh
```

---

### Opción 2: Docker Desktop Kubernetes

**Ventajas:**
- Integrado con Docker Desktop
- No requiere máquina virtual separada
- Usa el mismo Docker daemon

**Pasos para activar:**

1. **Abrir Docker Desktop**
   - Click en el ícono de Docker en la bandeja del sistema
   - Click en "Settings" (⚙️)

2. **Habilitar Kubernetes**
   - Ve a la sección "Kubernetes" en el menú lateral
   - Marca la casilla "Enable Kubernetes"
   - Click en "Apply & Restart"
   - **Espera 2-5 minutos** mientras descarga e instala

3. **Verificar instalación**
   ```powershell
   kubectl cluster-info
   kubectl get nodes
   ```

---

## 🎯 Recomendación para LogiFlow

**Usa Minikube** porque:

1. ✅ Ya está instalado
2. ✅ Inicia en ~30 segundos
3. ✅ Incluye StorageClass por defecto
4. ✅ Tiene dashboard visual
5. ✅ Fácil de resetear si algo falla

---

## 📦 Verificar Requisitos de LogiFlow

Una vez que tengas el cluster corriendo:

```powershell
# 1. Verificar conexión
kubectl cluster-info

# 2. Verificar nodos
kubectl get nodes

# 3. Verificar StorageClass (necesario para PVCs)
kubectl get storageclass

# 4. Ver contexto actual
kubectl config current-context

# 5. Ver recursos disponibles (si Minikube)
minikube status
```

---

## 🚀 Siguiente Paso: Deployment de LogiFlow

### Con Minikube (después de `minikube start`):

```powershell
# 1. Iniciar Minikube
minikube start

# 2. Usar el Docker daemon de Minikube (importante)
minikube docker-env | Invoke-Expression

# 3. Construir las imágenes Docker DENTRO de Minikube
cd logiflow-backend
.\build-docker.ps1

# 4. Deployar a Kubernetes
cd ..\k8s
.\deploy-k8s.ps1

# 5. Ver pods iniciando
kubectl get pods -n logiflow -w

# 6. Port forward para acceder
kubectl port-forward svc/api-gateway 8080:8080 -n logiflow
```

### Con Docker Desktop Kubernetes:

```powershell
# 1. Asegúrate de que las imágenes Docker ya estén construidas
cd logiflow-backend
.\build-docker.ps1

# 2. Deployar a Kubernetes
cd ..\k8s
.\deploy-k8s.ps1

# 3. Ver pods
kubectl get pods -n logiflow -w

# 4. Port forward
kubectl port-forward svc/api-gateway 8080:8080 -n logiflow
```

---

## 🎨 Dashboard de Kubernetes

### Minikube Dashboard (integrado):
```powershell
minikube dashboard
```
Se abrirá automáticamente en tu navegador.

### Kubernetes Dashboard (Docker Desktop):
```powershell
# Instalar dashboard
kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/v2.7.0/aio/deploy/recommended.yaml

# Crear usuario admin
kubectl create serviceaccount dashboard-admin -n kubernetes-dashboard
kubectl create clusterrolebinding dashboard-admin --clusterrole=cluster-admin --serviceaccount=kubernetes-dashboard:dashboard-admin

# Obtener token
kubectl -n kubernetes-dashboard create token dashboard-admin

# Port forward
kubectl port-forward -n kubernetes-dashboard service/kubernetes-dashboard 8443:443

# Abrir: https://localhost:8443
```

---

## ⚠️ Troubleshooting

### Si Minikube no inicia:

```powershell
# Ver logs
minikube logs

# Probar con más recursos
minikube start --cpus=4 --memory=8192

# Eliminar y recrear
minikube delete
minikube start

# Probar con driver específico
minikube start --driver=hyperv
# o
minikube start --driver=docker
```

### Si Docker Desktop Kubernetes falla:

1. Reiniciar Docker Desktop completamente
2. "Reset Kubernetes Cluster" en Settings → Kubernetes
3. Aumentar recursos en Settings → Resources (mínimo 4GB RAM)

---

## 📊 Recursos Necesarios para LogiFlow

**Mínimos:**
- CPU: 2 cores
- RAM: 4GB
- Disco: 20GB

**Recomendados:**
- CPU: 4 cores
- RAM: 8GB
- Disco: 30GB

Para configurar en Minikube:
```powershell
minikube start --cpus=4 --memory=8192 --disk-size=30g
```

---

## ✅ Checklist Pre-Deployment

- [ ] Cluster Kubernetes corriendo
- [ ] kubectl conecta correctamente
- [ ] StorageClass disponible
- [ ] Suficiente RAM y CPU
- [ ] Imágenes Docker construidas
- [ ] Namespace logiflow no existe previamente

---

**¿Listo para continuar?**

1. Elige Minikube u Docker Desktop Kubernetes
2. Activa/Inicia el cluster
3. Ejecuta los comandos de verificación
4. Luego procede con el deployment de LogiFlow
