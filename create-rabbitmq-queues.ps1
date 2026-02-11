# Script para crear colas de RabbitMQ manualmente
Write-Host "Creando colas de RabbitMQ..." -ForegroundColor Cyan

# Configuracion
$rabbitHost = "localhost"
$rabbitPort = 15672
$rabbitUser = "logiflow"
$rabbitPass = "logiflow123"
$vhost = "%2F"  # "/" encoded

# Credenciales
$pair = "$($rabbitUser):$($rabbitPass)"
$encodedCreds = [System.Convert]::ToBase64String([System.Text.Encoding]::ASCII.GetBytes($pair))
$headers = @{
    Authorization = "Basic $encodedCreds"
    "Content-Type" = "application/json"
}

# Crear Exchange para Pedidos
Write-Host "Creando exchange: logiflow.pedido.exchange" -ForegroundColor Yellow
$pedidoExchange = @{
    type = "topic"
    durable = $true
    auto_delete = $false
} | ConvertTo-Json

try {
    Invoke-RestMethod -Uri "http://${rabbitHost}:${rabbitPort}/api/exchanges/${vhost}/logiflow.pedido.exchange" `
                      -Method Put `
                      -Headers $headers `
                      -Body $pedidoExchange
    Write-Host "Exchange creado OK" -ForegroundColor Green
} catch {
    Write-Host "Error creando exchange: $_" -ForegroundColor Red
}

# Crear Exchange para Fleet
Write-Host "Creando exchange: logiflow.fleet.exchange" -ForegroundColor Yellow
$fleetExchange = @{
    type = "topic"
    durable = $true
    auto_delete = $false
} | ConvertTo-Json

try {
    Invoke-RestMethod -Uri "http://${rabbitHost}:${rabbitPort}/api/exchanges/${vhost}/logiflow.fleet.exchange" `
                      -Method Put `
                      -Headers $headers `
                      -Body $fleetExchange
    Write-Host "Exchange creado OK" -ForegroundColor Green
} catch {
    Write-Host "Error creando exchange: $_" -ForegroundColor Red
}

# Crear Cola para Pedidos
Write-Host "Creando cola: logiflow.pedido.estado.queue" -ForegroundColor Yellow
$pedidoQueue = @{
    durable = $true
    auto_delete = $false
} | ConvertTo-Json

try {
    Invoke-RestMethod -Uri "http://${rabbitHost}:${rabbitPort}/api/queues/${vhost}/logiflow.pedido.estado.queue" `
                      -Method Put `
                      -Headers $headers `
                      -Body $pedidoQueue
    Write-Host "Cola creada OK" -ForegroundColor Green
} catch {
    Write-Host "Error creando cola: $_" -ForegroundColor Red
}

# Crear Cola para Ubicaciones
Write-Host "Creando cola: logiflow.ubicacion.queue" -ForegroundColor Yellow
$ubicacionQueue = @{
    durable = $true
    auto_delete = $false
} | ConvertTo-Json

try {
    Invoke-RestMethod -Uri "http://${rabbitHost}:${rabbitPort}/api/queues/${vhost}/logiflow.ubicacion.queue" `
                      -Method Put `
                      -Headers $headers `
                      -Body $ubicacionQueue
    Write-Host "Cola creada OK" -ForegroundColor Green
} catch {
    Write-Host "Error creando cola: $_" -ForegroundColor Red
}

# Crear Binding para Pedidos
Write-Host "Creando binding: pedido exchange -> queue" -ForegroundColor Yellow
$pedidoBinding = @{
    routing_key = "pedido.estado.changed"
} | ConvertTo-Json

try {
    Invoke-RestMethod -Uri "http://${rabbitHost}:${rabbitPort}/api/bindings/${vhost}/e/logiflow.pedido.exchange/q/logiflow.pedido.estado.queue" `
                      -Method Post `
                      -Headers $headers `
                      -Body $pedidoBinding
    Write-Host "Binding creado OK" -ForegroundColor Green
} catch {
    Write-Host "Error creando binding: $_" -ForegroundColor Red
}

# Crear Binding para Ubicaciones
Write-Host "Creando binding: fleet exchange -> queue" -ForegroundColor Yellow
$ubicacionBinding = @{
    routing_key = "repartidor.ubicacion.updated"
} | ConvertTo-Json

try {
    Invoke-RestMethod -Uri "http://${rabbitHost}:${rabbitPort}/api/bindings/${vhost}/e/logiflow.fleet.exchange/q/logiflow.ubicacion.queue" `
                      -Method Post `
                      -Headers $headers `
                      -Body $ubicacionBinding
    Write-Host "Binding creado OK" -ForegroundColor Green
} catch {
    Write-Host "Error creando binding: $_" -ForegroundColor Red
}

Write-Host ""
Write-Host "Proceso completado!" -ForegroundColor Green
Write-Host "Verifica en: http://localhost:15672" -ForegroundColor Cyan
