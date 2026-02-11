# =====================================================
# LogiFlow - Build Script for Docker Images (Windows)
# =====================================================

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "  LogiFlow - Building Docker Images" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

# Build common module first
Write-Host "`nBuilding common module..." -ForegroundColor Yellow
Set-Location common
mvn clean install -DskipTests
if ($LASTEXITCODE -eq 0) {
    Write-Host "OK - Common module built successfully" -ForegroundColor Green
} else {
    Write-Host "ERROR - Failed to build common module" -ForegroundColor Red
    exit 1
}
Set-Location ..

# Services to build
$services = @("auth-service", "pedido-service", "fleet-service", "billing-service", "graphql-service", "notification-service", "api-gateway")

# Build each service JAR
foreach ($service in $services) {
    Write-Host "`nBuilding $service..." -ForegroundColor Yellow
    Set-Location $service
    mvn clean package -DskipTests
    if ($LASTEXITCODE -eq 0) {
        Write-Host "OK - $service built successfully" -ForegroundColor Green
    } else {
        Write-Host "ERROR - Failed to build $service" -ForegroundColor Red
        exit 1
    }
    Set-Location ..
}

# Build Docker images
Write-Host "`nBuilding Docker images..." -ForegroundColor Yellow
docker-compose -f docker-compose-full.yml build

if ($LASTEXITCODE -eq 0) {
    Write-Host "`n==========================================" -ForegroundColor Green
    Write-Host "  All services built successfully!" -ForegroundColor Green
    Write-Host "==========================================" -ForegroundColor Green
    Write-Host "`nNext steps:"
    Write-Host "  1. Run: " -NoNewline
    Write-Host "docker-compose -f docker-compose-full.yml up -d" -ForegroundColor Yellow
    Write-Host "  2. Check logs: " -NoNewline
    Write-Host "docker-compose -f docker-compose-full.yml logs -f" -ForegroundColor Yellow
    Write-Host "  3. Stop: " -NoNewline
    Write-Host "docker-compose -f docker-compose-full.yml down" -ForegroundColor Yellow
} else {
    Write-Host "ERROR - Failed to build Docker images" -ForegroundColor Red
    exit 1
}
