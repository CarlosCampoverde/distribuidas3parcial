# =====================================================
# LogiFlow - Quick Kubernetes Setup (Minikube)
# =====================================================

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "  LogiFlow - Kubernetes Quick Setup" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

# Check if Minikube is installed
Write-Host "`n[1/5] Checking Minikube installation..." -ForegroundColor Yellow
try {
    $minikubeVersion = minikube version --short 2>&1
    Write-Host "OK - Minikube installed: $minikubeVersion" -ForegroundColor Green
} catch {
    Write-Host "ERROR - Minikube not installed" -ForegroundColor Red
    Write-Host "Install from: https://minikube.sigs.k8s.io/docs/start/" -ForegroundColor Yellow
    exit 1
}

# Check Minikube status
Write-Host "`n[2/5] Checking Minikube status..." -ForegroundColor Yellow
$status = minikube status 2>&1 | Out-String

if ($status -match "host: Running") {
    Write-Host "OK - Minikube is already running" -ForegroundColor Green
} else {
    Write-Host "Minikube is stopped. Starting Minikube..." -ForegroundColor Yellow
    Write-Host "This may take 1-2 minutes..." -ForegroundColor Gray
    
    # Start Minikube with recommended resources
    minikube start --cpus=4 --memory=8192 --disk-size=30g
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "OK - Minikube started successfully" -ForegroundColor Green
    } else {
        Write-Host "ERROR - Failed to start Minikube" -ForegroundColor Red
        Write-Host "Try: minikube start --driver=hyperv" -ForegroundColor Yellow
        exit 1
    }
}

# Verify kubectl connection
Write-Host "`n[3/5] Verifying kubectl connection..." -ForegroundColor Yellow
kubectl cluster-info | Out-Null
if ($LASTEXITCODE -eq 0) {
    Write-Host "OK - kubectl connected to cluster" -ForegroundColor Green
    kubectl get nodes
} else {
    Write-Host "ERROR - kubectl cannot connect to cluster" -ForegroundColor Red
    exit 1
}

# Check StorageClass
Write-Host "`n[4/5] Checking StorageClass..." -ForegroundColor Yellow
$storageClasses = kubectl get storageclass --no-headers 2>&1
if ($storageClasses) {
    Write-Host "OK - StorageClass available:" -ForegroundColor Green
    kubectl get storageclass
} else {
    Write-Host "WARNING - No StorageClass found" -ForegroundColor Yellow
}

# Set Docker environment to use Minikube's Docker daemon
Write-Host "`n[5/5] Configuring Docker environment..." -ForegroundColor Yellow
Write-Host "Setting Docker to use Minikube daemon..." -ForegroundColor Gray
minikube docker-env | Invoke-Expression
Write-Host "OK - Docker configured to use Minikube" -ForegroundColor Green

# Summary
Write-Host "`n==========================================" -ForegroundColor Green
Write-Host "  Kubernetes Setup Complete!" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Green

Write-Host "`nCluster Information:"
kubectl cluster-info

Write-Host "`nNext steps:"
Write-Host "  1. Build Docker images: " -NoNewline
Write-Host "cd logiflow-backend; .\build-docker.ps1" -ForegroundColor Yellow

Write-Host "  2. Deploy to Kubernetes: " -NoNewline
Write-Host "cd ..\k8s; .\deploy-k8s.ps1" -ForegroundColor Yellow

Write-Host "  3. Open Dashboard: " -NoNewline
Write-Host "minikube dashboard" -ForegroundColor Yellow

Write-Host "  4. Stop Minikube when done: " -NoNewline
Write-Host "minikube stop" -ForegroundColor Yellow

Write-Host "`nIMPORTANT: Docker is now using Minikube's daemon." -ForegroundColor Cyan
Write-Host "Images built here will be available to Kubernetes directly." -ForegroundColor Cyan
