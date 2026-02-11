# =====================================================
# LogiFlow - Kubernetes Deployment Script
# =====================================================

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "  LogiFlow - Kubernetes Deployment" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

# Check kubectl
Write-Host "`nVerifying kubectl..." -ForegroundColor Yellow
try {
    kubectl version --client --short
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR - kubectl not found or not working" -ForegroundColor Red
        exit 1
    }
    Write-Host "OK - kubectl is ready" -ForegroundColor Green
} catch {
    Write-Host "ERROR - kubectl is not installed" -ForegroundColor Red
    exit 1
}

# Step 1: Create namespace
Write-Host "`n[1/7] Creating namespace..." -ForegroundColor Yellow
kubectl apply -f k8s/namespace.yaml
if ($LASTEXITCODE -eq 0) {
    Write-Host "OK - Namespace created" -ForegroundColor Green
} else {
    Write-Host "ERROR - Failed to create namespace" -ForegroundColor Red
    exit 1
}

# Step 2: Create ConfigMap and Secrets
Write-Host "`n[2/7] Creating ConfigMap and Secrets..." -ForegroundColor Yellow
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secrets.yaml
if ($LASTEXITCODE -eq 0) {
    Write-Host "OK - ConfigMap and Secrets created" -ForegroundColor Green
} else {
    Write-Host "ERROR - Failed to create ConfigMap/Secrets" -ForegroundColor Red
    exit 1
}

# Step 3: Create PVCs
Write-Host "`n[3/7] Creating Persistent Volume Claims..." -ForegroundColor Yellow
kubectl apply -f k8s/storage/
if ($LASTEXITCODE -eq 0) {
    Write-Host "OK - PVCs created" -ForegroundColor Green
} else {
    Write-Host "ERROR - Failed to create PVCs" -ForegroundColor Red
    exit 1
}

# Step 4: Deploy Databases
Write-Host "`n[4/7] Deploying databases..." -ForegroundColor Yellow
kubectl apply -f k8s/databases/
if ($LASTEXITCODE -eq 0) {
    Write-Host "OK - Databases deployed" -ForegroundColor Green
} else {
    Write-Host "ERROR - Failed to deploy databases" -ForegroundColor Red
    exit 1
}

# Wait for databases to be ready
Write-Host "`nWaiting for databases to be ready (60 seconds)..." -ForegroundColor Yellow
Start-Sleep -Seconds 60

# Step 5: Deploy Infrastructure (RabbitMQ)
Write-Host "`n[5/7] Deploying infrastructure..." -ForegroundColor Yellow
kubectl apply -f k8s/infrastructure/
if ($LASTEXITCODE -eq 0) {
    Write-Host "OK - Infrastructure deployed" -ForegroundColor Green
} else {
    Write-Host "ERROR - Failed to deploy infrastructure" -ForegroundColor Red
    exit 1
}

# Wait for RabbitMQ
Write-Host "`nWaiting for RabbitMQ to be ready (30 seconds)..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

# Step 6: Deploy Microservices
Write-Host "`n[6/7] Deploying microservices..." -ForegroundColor Yellow
kubectl apply -f k8s/services/
if ($LASTEXITCODE -eq 0) {
    Write-Host "OK - Microservices deployed" -ForegroundColor Green
} else {
    Write-Host "ERROR - Failed to deploy microservices" -ForegroundColor Red
    exit 1
}

# Step 7: Deploy Ingress
Write-Host "`n[7/7] Deploying Ingress..." -ForegroundColor Yellow
kubectl apply -f k8s/ingress.yaml
if ($LASTEXITCODE -eq 0) {
    Write-Host "OK - Ingress deployed" -ForegroundColor Green
} else {
    Write-Host "WARNING - Ingress deployment failed (may need Ingress Controller)" -ForegroundColor Yellow
}

# Summary
Write-Host "`n==========================================" -ForegroundColor Green
Write-Host "  Deployment Complete!" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Green

Write-Host "`nVerifying deployment status..." -ForegroundColor Yellow
kubectl get pods -n logiflow

Write-Host "`nNext steps:"
Write-Host "  1. Check pods: " -NoNewline
Write-Host "kubectl get pods -n logiflow -w" -ForegroundColor Yellow
Write-Host "  2. Check services: " -NoNewline
Write-Host "kubectl get svc -n logiflow" -ForegroundColor Yellow
Write-Host "  3. View logs: " -NoNewline
Write-Host "kubectl logs -f deployment/api-gateway -n logiflow" -ForegroundColor Yellow
Write-Host "  4. Access API Gateway: " -NoNewline
Write-Host "kubectl port-forward svc/api-gateway 8080:8080 -n logiflow" -ForegroundColor Yellow
Write-Host "  5. Access GraphQL: " -NoNewline
Write-Host "kubectl port-forward svc/graphql-service 8085:8085 -n logiflow" -ForegroundColor Yellow
Write-Host "  6. Delete deployment: " -NoNewline
Write-Host ".\k8s-delete.ps1" -ForegroundColor Yellow
