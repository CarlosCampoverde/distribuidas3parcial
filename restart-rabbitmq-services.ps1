# Script para reiniciar servicios con RabbitMQ
Write-Host "🔧 Reiniciando servicios con configuración RabbitMQ..." -ForegroundColor Cyan

# Detener servicios existentes
Write-Host "`n🛑 Deteniendo servicios en puertos 8082 y 8083..." -ForegroundColor Yellow

# Método alternativo: Buscar por puerto y detener
$port8082 = Get-NetTCPConnection -LocalPort 8082 -ErrorAction SilentlyContinue
if ($port8082) {
    $pid8082 = $port8082.OwningProcess
    Write-Host "Deteniendo proceso en puerto 8082 (PID: $pid8082)"
    Stop-Process -Id $pid8082 -Force -ErrorAction SilentlyContinue
}

$port8083 = Get-NetTCPConnection -LocalPort 8083 -ErrorAction SilentlyContinue
if ($port8083) {
    $pid8083 = $port8083.OwningProcess
    Write-Host "Deteniendo proceso en puerto 8083 (PID: $pid8083)"
    Stop-Process -Id $pid8083 -Force -ErrorAction SilentlyContinue
}

Write-Host "Esperando 10 segundos para que los procesos se liberen..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

# Recompilar servicios
Write-Host "`n🔨 Recompilando Pedido Service..." -ForegroundColor Cyan
Set-Location "c:\Users\carlo\OneDrive\Desktop\Proyecto3ParcialDistribuidas\logiflow-backend\pedido-service"
mvn clean package -DskipTests -q
if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Pedido Service compilado" -ForegroundColor Green
} else {
    Write-Host "❌ Error compilando Pedido Service" -ForegroundColor Red
    exit 1
}

Write-Host "`n🔨 Recompilando Fleet Service..." -ForegroundColor Cyan
Set-Location "c:\Users\carlo\OneDrive\Desktop\Proyecto3ParcialDistribuidas\logiflow-backend\fleet-service"
mvn clean package -DskipTests -q
if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Fleet Service compilado" -ForegroundColor Green
} else {
    Write-Host "❌ Error compilando Fleet Service" -ForegroundColor Red
    exit 1
}

# Iniciar servicios
Write-Host "`n🚀 Iniciando Pedido Service (8082)..." -ForegroundColor Cyan
Set-Location "c:\Users\carlo\OneDrive\Desktop\Proyecto3ParcialDistribuidas\logiflow-backend\pedido-service"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "java -jar target\pedido-service-1.0.0-SNAPSHOT.jar" -WindowStyle Minimized
Start-Sleep -Seconds 8

Write-Host "🚀 Iniciando Fleet Service (8083)..." -ForegroundColor Cyan
Set-Location "c:\Users\carlo\OneDrive\Desktop\Proyecto3ParcialDistribuidas\logiflow-backend\fleet-service"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "java -jar target\fleet-service-1.0.0-SNAPSHOT.jar" -WindowStyle Minimized
Start-Sleep -Seconds 8

Write-Host "🚀 Iniciando Notification Service (8086)..." -ForegroundColor Cyan
Set-Location "c:\Users\carlo\OneDrive\Desktop\Proyecto3ParcialDistribuidas\logiflow-backend\notification-service"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "java -jar target\notification-service-1.0.0-SNAPSHOT.jar" -WindowStyle Normal

Write-Host "`n✅ Todos los servicios iniciados!" -ForegroundColor Green
Write-Host "`n📊 Verifica RabbitMQ Management: http://localhost:15672" -ForegroundColor Cyan
Write-Host "🌐 Abre el cliente WebSocket: http://localhost:8086" -ForegroundColor Cyan
Write-Host "`nPresiona cualquier tecla para salir..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
