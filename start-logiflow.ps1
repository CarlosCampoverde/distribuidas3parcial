# Script para iniciar todos los microservicios de LogiFlow

Write-Host "🚀 Iniciando LogiFlow - Sistema de entrega express" -ForegroundColor Cyan
Write-Host ""

# Verificar que Docker esté ejecutándose
$dockerRunning = docker ps 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Docker no está ejecutándose. Por favor, inicia Docker Desktop." -ForegroundColor Red
    exit 1
}

# Verificar contenedores de PostgreSQL
Write-Host "📦 Verificando contenedores de base de datos..." -ForegroundColor Yellow
$containers = docker ps --filter "name=logiflow" --format "{{.Names}}"
if ($containers.Count -lt 4) {
    Write-Host "⚠️  Iniciando contenedores de PostgreSQL..." -ForegroundColor Yellow
    cd logiflow-backend
    docker-compose up -d
    cd ..
    Start-Sleep -Seconds 5
}

Write-Host "✅ Contenedores de PostgreSQL activos" -ForegroundColor Green
Write-Host ""

# Función para iniciar un microservicio
function Start-Service {
    param(
        [string]$ServiceName,
        [string]$Port,
        [string]$JarPath
    )
    
    Write-Host "🔧 Iniciando $ServiceName en puerto $Port..." -ForegroundColor Cyan
    
    $process = Start-Process -FilePath "java" `
        -ArgumentList "-jar", $JarPath `
        -WindowStyle Minimized `
        -PassThru
    
    Start-Sleep -Seconds 3
    
    if ($process -and !$process.HasExited) {
        Write-Host "✅ $ServiceName iniciado (PID: $($process.Id))" -ForegroundColor Green
        return $process.Id
    } else {
        Write-Host "❌ Error iniciando $ServiceName" -ForegroundColor Red
        return $null
    }
}

# Iniciar servicios
$pids = @{}

Write-Host "➡️  Iniciando microservicios..." -ForegroundColor Yellow
Write-Host ""

# Auth Service
$pids["auth"] = Start-Service -ServiceName "Auth Service" `
    -Port "8081" `
    -JarPath "logiflow-backend\auth-service\target\auth-service-1.0.0-SNAPSHOT.jar"

Start-Sleep -Seconds 5

# Pedido Service
$pids["pedido"] = Start-Service -ServiceName "Pedido Service" `
    -Port "8082" `
    -JarPath "logiflow-backend\pedido-service\target\pedido-service-1.0.0-SNAPSHOT.jar"

# Fleet Service
$pids["fleet"] = Start-Service -ServiceName "Fleet Service" `
    -Port "8083" `
    -JarPath "logiflow-backend\fleet-service\target\fleet-service-1.0.0-SNAPSHOT.jar"

# Billing Service
$pids["billing"] = Start-Service -ServiceName "Billing Service" `
    -Port "8084" `
    -JarPath "logiflow-backend\billing-service\target\billing-service-1.0.0-SNAPSHOT.jar"

Start-Sleep -Seconds 5

# GraphQL Service
$pids["graphql"] = Start-Service -ServiceName "GraphQL Service" `
    -Port "8085" `
    -JarPath "logiflow-backend\graphql-service\target\graphql-service-1.0.0-SNAPSHOT.jar"

Start-Sleep -Seconds 5

# API Gateway
$pids["gateway"] = Start-Service -ServiceName "API Gateway" `
    -Port "8080" `
    -JarPath "logiflow-backend\api-gateway\target\api-gateway-1.0.0-SNAPSHOT.jar"

Write-Host ""
Write-Host "═══════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host " ✅ TODOS LOS SERVICIOS ESTÁN EJECUTÁNDOSE" -ForegroundColor Green
Write-Host "═══════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""
Write-Host "📊 Servicios activos:" -ForegroundColor Yellow
Write-Host "  🔐 Auth Service        → http://localhost:8081" -ForegroundColor White
Write-Host "  📦 Pedido Service      → http://localhost:8082" -ForegroundColor White
Write-Host "  🚚 Fleet Service       → http://localhost:8083" -ForegroundColor White
Write-Host "  💰 Billing Service     → http://localhost:8084" -ForegroundColor White
Write-Host "  🔷 GraphQL Service     → http://localhost:8085" -ForegroundColor White
Write-Host "  🚪 API Gateway         → http://localhost:8080" -ForegroundColor White
Write-Host ""
Write-Host "🌐 URLs importantes:" -ForegroundColor Yellow
Write-Host "  📊 API Gateway         → http://localhost:8080" -ForegroundColor Cyan
Write-Host "  🔷 GraphiQL IDE        → http://localhost:8085/graphiql" -ForegroundColor Cyan
Write-Host "  📚 Swagger Auth        → http://localhost:8080/auth/swagger-ui.html" -ForegroundColor Cyan
Write-Host "  📚 Swagger Pedidos     → http://localhost:8080/pedido/swagger-ui.html" -ForegroundColor Cyan
Write-Host "  🗄️  pgAdmin            → http://localhost:5050" -ForegroundColor Cyan
Write-Host ""
Write-Host "💡 PIDs guardados:" -ForegroundColor Yellow
foreach ($key in $pids.Keys) {
    if ($pids[$key]) {
        Write-Host "  $key → PID $($pids[$key])" -ForegroundColor Gray
    }
}
Write-Host ""
Write-Host "⚠️  Para detener todos los servicios, ejecuta: stop-logiflow.ps1" -ForegroundColor Yellow
Write-Host ""

# Guardar PIDs en archivo
$pids | ConvertTo-Json | Out-File -FilePath "logiflow-pids.json"
Write-Host "💾 PIDs guardados en logiflow-pids.json" -ForegroundColor Green
Write-Host ""
Write-Host "Presiona Ctrl+C para detener este script (los servicios seguirán corriendo)" -ForegroundColor Gray

# Esperar indefinidamente
while ($true) {
    Start-Sleep -Seconds 60
}
