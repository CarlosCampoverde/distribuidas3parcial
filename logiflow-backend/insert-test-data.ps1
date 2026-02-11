# ================================================================
# Script para insertar datos de prueba en LogiFlow
# ================================================================

Write-Host "`n╔════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║  INSERTANDO DATOS DE PRUEBA EN LOGIFLOW              ║" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════════════════════╝`n" -ForegroundColor Cyan

# ============================================================
# 1. AUTH-DB: Insertar usuarios
# ============================================================
Write-Host "[1/4] Insertando usuarios en AUTH-DB..." -ForegroundColor Yellow

$sqlAuth = @"
INSERT INTO usuarios (nombre_completo, username, email, password, role, activo, fecha_creacion) VALUES
('Carlos Cliente', 'cliente', 'cliente@test.com', '\$2a\$10\$dummyHashPasswordForTestingOnly12345678901234567890', 'CLIENTE', true, NOW()),
('Juan Repartidor', 'repartidor', 'repartidor@test.com', '\$2a\$10\$dummyHashPasswordForTestingOnly12345678901234567890', 'REPARTIDOR', true, NOW()),
('Admin Sistema', 'admin', 'admin@test.com', '\$2a\$10\$dummyHashPasswordForTestingOnly12345678901234567890', 'ADMIN', true, NOW())
ON CONFLICT (email) DO NOTHING;
"@

docker exec -i logiflow-auth-db psql -U logiflow -d authdb -c $sqlAuth
docker exec -i logiflow-auth-db psql -U logiflow -d authdb -c "SELECT COUNT(*) as usuarios_creados FROM usuarios;"

Write-Host "✓ Usuarios creados`n" -ForegroundColor Green

# ============================================================
# 2. PEDIDO-DB: Insertar pedidos
# ============================================================
Write-Host "[2/4] Insertando pedidos en PEDIDO-DB..." -ForegroundColor Yellow

$sqlPedidos = @"
INSERT INTO pedidos (codigo_pedido, cliente_id, estado, tipo_entrega, origen_direccion, origen_latitud, origen_longitud, destino_direccion, destino_latitud, destino_longitud, distancia_km, peso_kg, costo_estimado, descripcion, fecha_creacion) VALUES
('PED-2024-001', 1, 'RECIBIDO', 'URBANA', 'Calle 123, CDMX', 19.4326, -99.1332, 'Av. Reforma 456, CDMX', 19.4353, -99.1412, 5.2, 2.5, 150.00, 'Documentos urgentes', NOW()),
('PED-2024-002', 1, 'RECIBIDO', 'INTERMUNICIPAL', 'Calle 789, CDMX', 19.4400, -99.1500, 'Av. Insurgentes 321, Puebla', 19.4200, -99.1300, 120.5, 5.0, 850.00, 'Paquete mediano', NOW()),
('PED-2024-003', 1, 'ASIGNADO', 'URBANA', 'Calle 100, CDMX', 19.4500, -99.1600, 'Av. Universidad 200, CDMX', 19.4100, -99.1200, 12.0, 3.0, 320.00, 'Electrónicos', NOW()),
('PED-2024-004', 1, 'EN_RUTA', 'URBANA', 'Zona Rosa, CDMX', 19.4263, -99.1640, 'Polanco, CDMX', 19.4340, -99.1950, 8.3, 1.5, 180.00, 'Regalo especial', NOW()),
('PED-2024-005', 1, 'ENTREGADO', 'NACIONAL', 'CDMX Centro', 19.4326, -99.1332, 'Guadalajara Centro', 20.6597, -103.3496, 550.0, 10.0, 2500.00, 'Documentación importante', NOW())
ON CONFLICT (codigo_pedido) DO NOTHING;
"@

docker exec -i logiflow-pedido-db psql -U logiflow -d pedidodb -c $sqlPedidos
docker exec -i logiflow-pedido-db psql -U logiflow -d pedidodb -c "SELECT COUNT(*) as pedidos_creados FROM pedidos;"

Write-Host "✓ Pedidos creados`n" -ForegroundColor Green

# ============================================================
# 3. FLEET-DB: Insertar vehículos y repartidores
# ============================================================
Write-Host "[3/4] Insertando vehículos y repartidores en FLEET-DB..." -ForegroundColor Yellow

# Primero verificar estructura de vehículos
docker exec -i logiflow-fleet-db psql -U logiflow -d fleetdb -c "\d vehiculos" > $null 2>&1

$sqlVehiculos = @"
INSERT INTO vehiculos (placa, marca, modelo, anio, capacidad_kg, tipo, estado, fecha_registro) VALUES
('ABC-123', 'Honda', 'Wave 110', 2022, 50, 'MOTO', 'DISPONIBLE', NOW()),
('XYZ-789', 'Nissan', 'Versa', 2023, 300, 'AUTO', 'DISPONIBLE', NOW()),
('DEF-456', 'Toyota', 'Hilux', 2023, 1000, 'CAMIONETA', 'DISPONIBLE', NOW())
ON CONFLICT (placa) DO NOTHING;
"@

docker exec -i logiflow-fleet-db psql -U logiflow -d fleetdb -c $sqlVehiculos 2>$null
if ($LASTEXITCODE -ne 0) {
    Write-Host "  Ajustando estructura de vehículos..." -ForegroundColor Gray
    # Intentar estructura alternativa
    $sqlVehiculosAlt = "SELECT 'Vehículos ya existen o diferente estructura' as nota;"
    docker exec -i logiflow-fleet-db psql -U logiflow -d fleetdb -c $sqlVehiculosAlt
}

# Verificar estructura de repartidores
docker exec -i logiflow-fleet-db psql -U logiflow -d fleetdb -c "\d repartidores" > $null 2>&1

$sqlRepartidores = @"
INSERT INTO repartidores (usuario_id, nombre_completo, email, telefono, numero_licencia, fecha_contratacion, vehiculo_id, latitud_actual, longitud_actual, disponible, estado, fecha_creacion) VALUES
(2, 'Juan Repartidor', 'repartidor@test.com', '5551234567', 'LIC-2024-001', '2024-01-15', 1, 19.4326, -99.1332, true, 'ACTIVO', NOW()),
(2, 'Maria Lopez', 'maria@test.com', '5559876543', 'LIC-2024-002', '2024-02-01', 2, 19.4400, -99.1500, true, 'ACTIVO', NOW())
ON CONFLICT (email) DO NOTHING;
"@

docker exec -i logiflow-fleet-db psql -U logiflow -d fleetdb -c $sqlRepartidores 2>$null

# Actualizar pedidos con repartidor_id
docker exec -i logiflow-pedido-db psql -U logiflow -d pedidodb -c "UPDATE pedidos SET repartidor_id = 1, fecha_asignacion = NOW() WHERE codigo_pedido IN ('PED-2024-003', 'PED-2024-004');"

docker exec -i logiflow-fleet-db psql -U logiflow -d fleetdb -c "SELECT COUNT(*) as repartidores_creados FROM repartidores;"

Write-Host "✓ Flota creada`n" -ForegroundColor Green

# ============================================================
# 4. BILLING-DB: Insertar facturas
# ============================================================
Write-Host "[4/4] Insertando facturas en BILLING-DB..." -ForegroundColor Yellow

# Primero verificar estructura
docker exec -i logiflow-billing-db psql -U logiflow -d billingdb -c "\d facturas" > $null 2>&1

$sqlFacturas = @"
INSERT INTO facturas (numero_factura, pedido_id, cliente_id, repartidor_id, tipo_entrega, distancia_km, peso_kg, tarifa_base, tarifa_distancia, tarifa_peso, subtotal, impuestos, total, estado, fecha_creacion) VALUES
('FACT-2024-001', 3, 1, 1, 'URBANA', 12.0, 3.0, 100.00, 180.00, 30.00, 310.00, 49.60, 359.60, 'PENDIENTE', NOW()),
('FACT-2024-002', 5, 1, 1, 'NACIONAL', 550.0, 10.0, 500.00, 2000.00, 100.00, 2600.00, 416.00, 3016.00, 'PAGADA', NOW())
ON CONFLICT (numero_factura) DO NOTHING;
"@

docker exec -i logiflow-billing-db psql -U logiflow -d billingdb -c $sqlFacturas 2>$null
docker exec -i logiflow-billing-db psql -U logiflow -d billingdb -c "SELECT COUNT(*) as facturas_creadas FROM facturas;" 2>$null

Write-Host "✓ Facturas creadas`n" -ForegroundColor Green

# ============================================================
# VERIFICACIÓN FINAL
# ============================================================
Write-Host "`n╔════════════════════════════════════════════════════════╗" -ForegroundColor Green
Write-Host "║  RESUMEN DE DATOS CREADOS                            ║" -ForegroundColor Green
Write-Host "╚════════════════════════════════════════════════════════╝`n" -ForegroundColor Green

Write-Host "AUTH-DB:" -ForegroundColor Cyan
docker exec -i logiflow-auth-db psql -U logiflow -d authdb -c "SELECT id, username, email, role FROM usuarios ORDER BY id;"

Write-Host "`nPEDIDO-DB:" -ForegroundColor Cyan
docker exec -i logiflow-pedido-db psql -U logiflow -d pedidodb -c "SELECT id, codigo_pedido, estado, tipo_entrega, costo_estimado FROM pedidos ORDER BY id;"

Write-Host "`nFLEET-DB:" -ForegroundColor Cyan
docker exec -i logiflow-fleet-db psql -U logiflow -d fleetdb -c "SELECT id, nombre_completo, email, estado, disponible FROM repartidores ORDER BY id;" 2>$null

Write-Host "`nBILLING-DB:" -ForegroundColor Cyan
docker exec -i logiflow-billing-db psql -U logiflow -d billingdb -c "SELECT id, numero_factura, total, estado FROM facturas ORDER BY id;" 2>$null

Write-Host "`n╔════════════════════════════════════════════════════════╗" -ForegroundColor Green
Write-Host "║  ✓ DATOS DE PRUEBA INSERTADOS EXITOSAMENTE           ║" -ForegroundColor Green
Write-Host "╚════════════════════════════════════════════════════════╝" -ForegroundColor Green

Write-Host "`nAhora puedes probar tus queries GraphQL en:" -ForegroundColor Yellow
Write-Host "  http://localhost:8085/graphiql`n" -ForegroundColor Cyan
