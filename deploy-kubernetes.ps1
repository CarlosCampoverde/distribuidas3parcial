#!/usr/bin/env pwsh
<#
.DESCRIPTION
Despliega LogiFlow en Kubernetes (Minikube)
#>

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  DESPLEGANDO LOGIFLOW EN KUBERNETES" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# Verificar que minikube está corriendo
Write-Host "✓ Verificando Minikube..." -ForegroundColor Yellow
$minikubeStatus = minikube status --format="{{.Host}}"
if ($minikubeStatus -ine "Running") {
    Write-Host "❌ Minikube no está corriendo. Inicia con: minikube start" -ForegroundColor Red
    exit 1
}
Write-Host "✅ Minikube está corriendo`n" -ForegroundColor Green

# Aplicar manifiestos
$manifestsDir = ".\k8s-manifests"
$manifests = @(
    "00-namespace-configmap.yaml",
    "01-infrastructure.yaml", 
    "02-microservices.yaml",
    "03-api-gateway-ingress.yaml"
)

foreach ($manifest in $manifests) {
    $manifestPath = Join-Path $manifestsDir $manifest
    Write-Host "📋 Aplicando $manifest..." -ForegroundColor Yellow
    kubectl apply -f $manifestPath
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Error al aplicar $manifest" -ForegroundColor Red
        exit 1
    }
    Start-Sleep -Seconds 2
}

Write-Host "`n✅ Manifiestos aplicados correctamente`n" -ForegroundColor Green

# Esperar a que los pods estén listos
Write-Host "⏳ Esperando a que los pods se inicialicen..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

# Mostrar estado de pods
Write-Host "`n📊 ESTADO DE LOS PODS:" -ForegroundColor Cyan
kubectl get pods -n logiflow -o wide

Write-Host "`n📊 ESTADO DE LOS SERVICIOS:" -ForegroundColor Cyan
kubectl get svc -n logiflow -o wide

Write-Host "`n📊 ESTADO DE LOS DEPLOYMENTS:" -ForegroundColor Cyan
kubectl get deployments -n logiflow -o wide

# Mostrar logs de inicialización
Write-Host "`n📝 PRIMEROS LOGS DE API-GATEWAY:" -ForegroundColor Yellow
kubectl logs -n logiflow deployment/api-gateway --tail=20 2>/dev/null || Write-Host "Aún no hay logs disponibles"

Write-Host "`n========================================" -ForegroundColor Green
Write-Host "  ✅ DESPLIEGUE COMPLETADO" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green

Write-Host "`n🌐 ACCESO A LOS SERVICIOS:" -ForegroundColor Cyan
$minikubeIP = minikube ip
Write-Host "  • API Gateway: http://$minikubeIP:8080" -ForegroundColor White
Write-Host "  • Auth Service: http://$minikubeIP:8081" -ForegroundColor White
Write-Host "  • Pedido Service: http://$minikubeIP:8082" -ForegroundColor White
Write-Host "  • Fleet Service: http://$minikubeIP:8083" -ForegroundColor White
Write-Host "  • Billing Service: http://$minikubeIP:8084" -ForegroundColor White
Write-Host "  • GraphQL Service: http://$minikubeIP:8085" -ForegroundColor White
Write-Host "  • Notification Service: http://$minikubeIP:8086" -ForegroundColor White

Write-Host "`n📊 MONITOREO:" -ForegroundColor Cyan
Write-Host "  • Dashboard: minikube dashboard" -ForegroundColor White
Write-Host "  • Logs: kubectl logs -n logiflow deployment/<service-name>" -ForegroundColor White
Write-Host "  • Describir pod: kubectl describe pod -n logiflow <pod-name>" -ForegroundColor White

Write-Host "`n🧹 LIMPIAR DESPLIEGUE:" -ForegroundColor Cyan
Write-Host "  • Eliminar namespace: kubectl delete namespace logiflow" -ForegroundColor White
