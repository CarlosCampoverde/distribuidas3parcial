# Script de Prueba Completo - LogiFlow
# Ejecuta este script para probar todo el sistema

Write-Host "╔════════════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║         PRUEBA COMPLETA DEL SISTEMA LOGIFLOW                   ║" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

# ============================================================================
# PASO 1: Verificar Servicios
# ============================================================================
Write-Host "📋 PASO 1: Verificando servicios..." -ForegroundColor Yellow
Write-Host ""

$services = @(
    @{Name="API Gateway"; Port=8080},
    @{Name="Auth Service"; Port=8081},
    @{Name="Pedido Service"; Port=8082},
    @{Name="Fleet Service"; Port=8083},
    @{Name="Billing Service"; Port=8084},
    @{Name="GraphQL Service"; Port=8085}
)

$allRunning = $true
foreach ($service in $services) {
    try {
        $test = Test-NetConnection -ComputerName localhost -Port $service.Port -WarningAction SilentlyContinue -InformationLevel Quiet
        if ($test) {
            Write-Host "   ✅ $($service.Name) - Puerto $($service.Port)" -ForegroundColor Green
        } else {
            Write-Host "   ❌ $($service.Name) - Puerto $($service.Port) NO RESPONDE" -ForegroundColor Red
            $allRunning = $false
        }
    } catch {
        Write-Host "   ❌ $($service.Name) - Puerto $($service.Port) ERROR" -ForegroundColor Red
        $allRunning = $false
    }
}

if (-not $allRunning) {
    Write-Host ""
    Write-Host "⚠️  ADVERTENCIA: No todos los servicios están corriendo" -ForegroundColor Red
    Write-Host "   Ejecuta: .\start-logiflow.ps1 para iniciar todos los servicios" -ForegroundColor Yellow
    Write-Host ""
    Read-Host "Presiona Enter para continuar de todas formas o Ctrl+C para cancelar"
}

Write-Host ""
Write-Host "─────────────────────────────────────────────────────────────────" -ForegroundColor Gray
Write-Host ""

# ============================================================================
# PASO 2: Registrar Usuario
# ============================================================================
Write-Host "📋 PASO 2: Registrando usuario de prueba..." -ForegroundColor Yellow
Write-Host ""

$registerBody = @{
    email = "test@logiflow.com"
    password = "Test123!"
    nombreCompleto = "Usuario Test"
    telefono = "3001234567"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" `
        -Method Post `
        -ContentType "application/json" `
        -Body $registerBody
    
    Write-Host "   ✅ Usuario registrado exitosamente" -ForegroundColor Green
    Write-Host "      • ID: $($response.usuario.id)" -ForegroundColor Gray
    Write-Host "      • Email: $($response.usuario.email)" -ForegroundColor Gray
    Write-Host "      • Nombre: $($response.usuario.nombreCompleto)" -ForegroundColor Gray
    
    $token = $response.token
    $userId = $response.usuario.id
    
    Write-Host ""
    Write-Host "   🔑 Token JWT obtenido" -ForegroundColor Magenta
    
} catch {
    Write-Host "   ⚠️  Usuario ya existe o error al registrar" -ForegroundColor Yellow
    Write-Host "   Intentando login..." -ForegroundColor Cyan
    
    $loginBody = @{
        email = "test@logiflow.com"
        password = "Test123!"
    } | ConvertTo-Json
    
    try {
        $response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" `
            -Method Post `
            -ContentType "application/json" `
            -Body $loginBody
        
        Write-Host "   ✅ Login exitoso" -ForegroundColor Green
        $token = $response.token
        $userId = $response.usuario.id
    } catch {
        Write-Host "   ❌ Error en autenticación: $($_.Exception.Message)" -ForegroundColor Red
        exit 1
    }
}

Write-Host ""
Write-Host "─────────────────────────────────────────────────────────────────" -ForegroundColor Gray
Write-Host ""

# ============================================================================
# PASO 3: Crear Pedido
# ============================================================================
Write-Host "📋 PASO 3: Creando pedido de prueba..." -ForegroundColor Yellow
Write-Host ""

$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

$pedidoBody = @{
    clienteId = $userId
    origenDireccion = "Calle 100 #15-20, Bogotá"
    destinoDireccion = "Carrera 7 #45-30, Bogotá"
    origenLatitud = 4.6997
    origenLongitud = -74.0354
    destinoLatitud = 4.6533
    destinoLongitud = -74.0836
    distanciaKm = 8.5
    pesoKg = 2.5
    tipoEntrega = "URBANA"
    descripcion = "Paquete de prueba - Documentos"
} | ConvertTo-Json

try {
    $pedido = Invoke-RestMethod -Uri "http://localhost:8080/api/pedidos" `
        -Method Post `
        -Headers $headers `
        -Body $pedidoBody
    
    Write-Host "   ✅ Pedido creado exitosamente" -ForegroundColor Green
    Write-Host "      • ID: $($pedido.id)" -ForegroundColor Gray
    Write-Host "      • Código: $($pedido.codigoPedido)" -ForegroundColor Gray
    Write-Host "      • Estado: $($pedido.estado)" -ForegroundColor Gray
    Write-Host "      • Tipo: $($pedido.tipoEntrega)" -ForegroundColor Gray
    Write-Host "      • Distancia: $($pedido.distanciaKm) km" -ForegroundColor Gray
    Write-Host "      • Costo estimado: $$($pedido.costoEstimado)" -ForegroundColor Gray
    
    $pedidoId = $pedido.id
    
} catch {
    Write-Host "   ❌ Error al crear pedido: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "─────────────────────────────────────────────────────────────────" -ForegroundColor Gray
Write-Host ""

# ============================================================================
# PASO 4: Probar GraphQL - Consulta Simple
# ============================================================================
Write-Host "📋 PASO 4: Probando GraphQL - Consulta de Schema..." -ForegroundColor Yellow
Write-Host ""

$graphqlQuery1 = @{
    query = "{ __schema { queryType { name } } }"
} | ConvertTo-Json

try {
    $result1 = Invoke-RestMethod -Uri "http://localhost:8085/graphql" `
        -Method Post `
        -ContentType "application/json" `
        -Body $graphqlQuery1
    
    Write-Host "   ✅ GraphQL respondiendo correctamente" -ForegroundColor Green
    Write-Host "      • Query Type: $($result1.data.__schema.queryType.name)" -ForegroundColor Gray
    
} catch {
    Write-Host "   ❌ Error en GraphQL: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "─────────────────────────────────────────────────────────────────" -ForegroundColor Gray
Write-Host ""

# ============================================================================
# PASO 5: Probar GraphQL - Consulta de Pedidos
# ============================================================================
Write-Host "📋 PASO 5: Probando GraphQL - Consulta de Pedidos..." -ForegroundColor Yellow
Write-Host ""

$graphqlQuery2 = @{
    query = @"
query {
  pedidos(limit: 3) {
    id
    codigoPedido
    estado
    tipoEntrega
    distanciaKm
    costoEstimado
    origenDireccion
    destinoDireccion
  }
}
"@
} | ConvertTo-Json

try {
    $result2 = Invoke-RestMethod -Uri "http://localhost:8085/graphql" `
        -Method Post `
        -ContentType "application/json" `
        -Body $graphqlQuery2
    
    if ($result2.data.pedidos) {
        Write-Host "   ✅ Pedidos obtenidos via GraphQL" -ForegroundColor Green
        Write-Host "      • Total pedidos: $($result2.data.pedidos.Count)" -ForegroundColor Gray
        
        foreach ($p in $result2.data.pedidos) {
            Write-Host ""
            Write-Host "      📦 Pedido: $($p.codigoPedido)" -ForegroundColor Cyan
            Write-Host "         Estado: $($p.estado)" -ForegroundColor Gray
            Write-Host "         Tipo: $($p.tipoEntrega)" -ForegroundColor Gray
            Write-Host "         Origen: $($p.origenDireccion)" -ForegroundColor Gray
            Write-Host "         Destino: $($p.destinoDireccion)" -ForegroundColor Gray
            Write-Host "         Distancia: $($p.distanciaKm) km" -ForegroundColor Gray
            Write-Host "         Costo: $$($p.costoEstimado)" -ForegroundColor Gray
        }
    } else {
        Write-Host "   ⚠️  No hay pedidos en el sistema" -ForegroundColor Yellow
    }
    
} catch {
    Write-Host "   ❌ Error consultando pedidos: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "─────────────────────────────────────────────────────────────────" -ForegroundColor Gray
Write-Host ""

# ============================================================================
# PASO 6: Probar GraphQL - Dashboard Cliente
# ============================================================================
Write-Host "📋 PASO 6: Probando GraphQL - Dashboard de Cliente..." -ForegroundColor Yellow
Write-Host ""

$graphqlQuery3 = @{
    query = @"
query {
  dashboardCliente(clienteId: $userId) {
    clienteId
    totalPedidos
    gastoTotal
    pedidosActivos {
      codigoPedido
      estado
      tipoEntrega
    }
  }
}
"@
} | ConvertTo-Json

try {
    $result3 = Invoke-RestMethod -Uri "http://localhost:8085/graphql" `
        -Method Post `
        -ContentType "application/json" `
        -Body $graphqlQuery3
    
    if ($result3.data.dashboardCliente) {
        $dashboard = $result3.data.dashboardCliente
        Write-Host "   ✅ Dashboard del cliente obtenido" -ForegroundColor Green
        Write-Host "      • Cliente ID: $($dashboard.clienteId)" -ForegroundColor Gray
        Write-Host "      • Total pedidos: $($dashboard.totalPedidos)" -ForegroundColor Gray
        Write-Host "      • Gasto total: $$($dashboard.gastoTotal)" -ForegroundColor Gray
        Write-Host "      • Pedidos activos: $($dashboard.pedidosActivos.Count)" -ForegroundColor Gray
    }
    
} catch {
    Write-Host "   ❌ Error obteniendo dashboard: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "╔════════════════════════════════════════════════════════════════╗" -ForegroundColor Green
Write-Host "║              ✅ PRUEBA COMPLETA FINALIZADA                      ║" -ForegroundColor Green
Write-Host "╚════════════════════════════════════════════════════════════════╝" -ForegroundColor Green
Write-Host ""
Write-Host "🌐 URLs del Sistema:" -ForegroundColor Cyan
Write-Host "   • API Gateway:       http://localhost:8080" -ForegroundColor White
Write-Host "   • GraphiQL IDE:      http://localhost:8085/graphiql" -ForegroundColor White
Write-Host "   • Swagger Auth:      http://localhost:8080/auth/swagger-ui.html" -ForegroundColor White
Write-Host "   • Swagger Pedidos:   http://localhost:8080/pedido/swagger-ui.html" -ForegroundColor White
Write-Host "   • pgAdmin:           http://localhost:5050" -ForegroundColor White
Write-Host ""
Write-Host "📚 Documentación:" -ForegroundColor Cyan
Write-Host "   • Guía GraphQL:      GRAPHQL_TESTING_GUIDE.md" -ForegroundColor White
Write-Host "   • Resumen Fase 2:    PHASE2_GRAPHQL_SUMMARY.md" -ForegroundColor White
Write-Host ""
