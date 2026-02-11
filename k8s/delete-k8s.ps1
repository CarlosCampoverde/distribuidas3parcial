# =====================================================
# LogiFlow - Kubernetes Cleanup Script
# =====================================================

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "  LogiFlow - Kubernetes Cleanup" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

Write-Host "`nWARNING: This will delete ALL LogiFlow resources in Kubernetes" -ForegroundColor Yellow
$confirmation = Read-Host "Are you sure you want to continue? (yes/no)"

if ($confirmation -ne "yes") {
    Write-Host "`nOperation cancelled" -ForegroundColor Yellow
    exit 0
}

# Delete in reverse order
Write-Host "`n[1/7] Deleting Ingress..." -ForegroundColor Yellow
kubectl delete -f k8s/ingress.yaml --ignore-not-found=true

Write-Host "`n[2/7] Deleting Microservices..." -ForegroundColor Yellow
kubectl delete -f k8s/services/ --ignore-not-found=true

Write-Host "`n[3/7] Deleting Infrastructure..." -ForegroundColor Yellow
kubectl delete -f k8s/infrastructure/ --ignore-not-found=true

Write-Host "`n[4/7] Deleting Databases..." -ForegroundColor Yellow
kubectl delete -f k8s/databases/ --ignore-not-found=true

Write-Host "`n[5/7] Deleting PVCs..." -ForegroundColor Yellow
kubectl delete -f k8s/storage/ --ignore-not-found=true

Write-Host "`n[6/7] Deleting ConfigMap and Secrets..." -ForegroundColor Yellow
kubectl delete -f k8s/configmap.yaml --ignore-not-found=true
kubectl delete -f k8s/secrets.yaml --ignore-not-found=true

Write-Host "`n[7/7] Deleting Namespace..." -ForegroundColor Yellow
kubectl delete -f k8s/namespace.yaml --ignore-not-found=true

Write-Host "`n==========================================" -ForegroundColor Green
Write-Host "  Cleanup Complete!" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Green

Write-Host "`nVerifying cleanup..." -ForegroundColor Yellow
kubectl get all -n logiflow 2>$null
if ($LASTEXITCODE -ne 0) {
    Write-Host "OK - Namespace deleted successfully" -ForegroundColor Green
} else {
    Write-Host "Some resources may still be terminating..." -ForegroundColor Yellow
    Write-Host "Run: " -NoNewline
    Write-Host "kubectl get all -n logiflow" -ForegroundColor Yellow
}
