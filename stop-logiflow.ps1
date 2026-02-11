# Script para detener todos los microservicios de LogiFlow

Write-Host "🛑 Deteniendo LogiFlow - Sistema de entrega express" -ForegroundColor Red
Write-Host ""

# Leer PIDs del archivo
if (Test-Path "logiflow-pids.json") {
    Write-Host "📖 Leyendo PIDs guardados..." -ForegroundColor Yellow
    $pids = Get-Content "logiflow-pids.json" | ConvertFrom-Json
    
    foreach ($key in $pids.PSObject.Properties.Name) {
        $pid = $pids.$key
        if ($pid) {
            try {
                $process = Get-Process -Id $pid -ErrorAction SilentlyContinue
                if ($process) {
                    Write-Host "🔪 Deteniendo $key (PID: $pid)..." -ForegroundColor Yellow
                    Stop-Process -Id $pid -Force
                    Write-Host "✅ $key detenido" -ForegroundColor Green
                } else {
                    Write-Host "⚠️  Proceso $key (PID: $pid) ya no está ejecutándose" -ForegroundColor Gray
                }
            } catch {
                Write-Host "❌ Error deteniendo $key (PID: $pid): $_" -ForegroundColor Red
            }
        }
    }
    
    Remove-Item "logiflow-pids.json"
    Write-Host "🗑️  Archivo de PIDs eliminado" -ForegroundColor Gray
} else {
    Write-Host "⚠️  No se encontró archivo logiflow-pids.json" -ForegroundColor Yellow
    Write-Host "ℹ️  Intentando detener por puertos..." -ForegroundColor Cyan
    
    # Detener por puertos
    $ports = @(8080, 8081, 8082, 8083, 8084, 8085)
    foreach ($port in $ports) {
        try {
            $connections = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue
            foreach ($conn in $connections) {
                $pid = $conn.OwningProcess
                $process = Get-Process -Id $pid -ErrorAction SilentlyContinue
                if ($process) {
                    Write-Host "🔪 Deteniendo proceso en puerto $port (PID: $pid)..." -ForegroundColor Yellow
                    Stop-Process -Id $pid -Force
                    Write-Host "✅ Proceso detenido" -ForegroundColor Green
                }
            }
        } catch {
            Write-Host "⚠️  No hay proceso en puerto $port" -ForegroundColor Gray
        }
    }
}

Write-Host ""
Write-Host "═══════════════════════════════════════════════════════════" -ForegroundColor Red
Write-Host " ✅ TODOS LOS SERVICIOS HAN SIDO DETENIDOS" -ForegroundColor Green
Write-Host "═══════════════════════════════════════════════════════════" -ForegroundColor Red
Write-Host ""
Write-Host "💡 Los contenedores Docker siguen ejecutándose." -ForegroundColor Yellow
Write-Host "   Para detenerlos, ejecuta: cd logiflow-backend; docker-compose down" -ForegroundColor Yellow
Write-Host ""
