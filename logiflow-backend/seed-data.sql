-- ================================================================
-- SCRIPT PARA CREAR DATOS DE PRUEBA EN TODAS LAS BASES DE DATOS
-- ================================================================

-- ======================
-- 1. AUTH-DB (Puerto 5532)
-- ======================
\c auth_db

-- Insertar usuarios
INSERT INTO usuarios (id, nombre_completo, email, password, role, activo, fecha_creacion, fecha_actualizacion) VALUES
(1, 'Carlos Cliente', 'cliente@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J8pTXCd8xd2Z5Q5Z5Z5Z5Z5Z5Z5Z5Z', 'CLIENTE', true, NOW(), NOW()),
(2, 'Juan Repartidor', 'repartidor@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J8pTXCd8xd2Z5Q5Z5Z5Z5Z5Z5Z5Z5Z', 'REPARTIDOR', true, NOW(), NOW()),
(3, 'Admin Sistema', 'admin@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J8pTXCd8xd2Z5Q5Z5Z5Z5Z5Z5Z5Z5Z', 'ADMIN', true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Reiniciar secuencia
SELECT setval('usuarios_id_seq', (SELECT MAX(id) FROM usuarios));

-- ======================
-- 2. PEDIDO-DB (Puerto 5533)
-- ======================
\c pedido_db

-- Insertar pedidos
INSERT INTO pedidos (id, codigo_pedido, cliente_id, estado, tipo_entrega, origen_direccion, origen_latitud, origen_longitud, destino_direccion, destino_latitud, destino_longitud, distancia_km, peso_kg, costo_estimado, descripcion, fecha_creacion, fecha_actualizacion) VALUES
(1, 'PED-2024-001', 1, 'PENDIENTE', 'EXPRESS', 'Calle 123, CDMX', 19.4326, -99.1332, 'Av. Reforma 456, CDMX', 19.4353, -99.1412, 5.2, 2.5, 150.00, 'Documentos urgentes', NOW(), NOW()),
(2, 'PED-2024-002', 1, 'PENDIENTE', 'PROGRAMADA', 'Calle 789, CDMX', 19.4400, -99.1500, 'Av. Insurgentes 321, CDMX', 19.4200, -99.1300, 8.5, 5.0, 250.00, 'Paquete mediano', NOW(), NOW()),
(3, 'PED-2024-003', 1, 'ASIGNADO', 'EXPRESS', 'Calle 100, CDMX', 19.4500, -99.1600, 'Av. Universidad 200, CDMX', 19.4100, -99.1200, 12.0, 3.0, 320.00, 'Electrónicos', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Actualizar el pedido 3 con repartidor
UPDATE pedidos SET repartidor_id = 1, fecha_asignacion = NOW() WHERE id = 3;

SELECT setval('pedidos_id_seq', (SELECT MAX(id) FROM pedidos));

-- ======================
-- 3. FLEET-DB (Puerto 5534)
-- ======================
\c fleet_db

-- Insertar vehículos
INSERT INTO vehiculos (id, tipo_vehiculo, placa, marca, modelo, anio, capacidad_kg, estado, fecha_creacion, fecha_actualizacion) VALUES
(1, 'MOTORIZADO', 'ABC-123', 'Honda', 'Wave', 2022, 50.0, 'DISPONIBLE', NOW(), NOW()),
(2, 'AUTOMOVIL', 'XYZ-789', 'Nissan', 'Versa', 2023, 300.0, 'DISPONIBLE', NOW(), NOW()),
(3, 'CAMIONETA', 'DEF-456', 'Toyota', 'Hilux', 2023, 1000.0, 'DISPONIBLE', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Insertar repartidores
INSERT INTO repartidores (id, usuario_id, nombre_completo, email, telefono, numero_licencia, fecha_contratacion, vehiculo_id, latitud_actual, longitud_actual, disponible, estado, fecha_creacion, fecha_actualizacion) VALUES
(1, 2, 'Juan Repartidor', 'repartidor@test.com', '5551234567', 'LIC-2024-001', '2024-01-15', 1, 19.4326, -99.1332, true, 'ACTIVO', NOW(), NOW()),
(2, 2, 'Maria Lopez', 'maria@test.com', '5559876543', 'LIC-2024-002', '2024-02-01', 2, 19.4400, -99.1500, true, 'ACTIVO', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

SELECT setval('vehiculos_id_seq', (SELECT MAX(id) FROM vehiculos));
SELECT setval('repartidores_id_seq', (SELECT MAX(id) FROM repartidores));

-- ======================
-- 4. BILLING-DB (Puerto 5535)
-- ======================
\c billing_db

-- Insertar facturas
INSERT INTO facturas (id, numero_factura, pedido_id, cliente_id, repartidor_id, tipo_entrega, distancia_km, peso_kg, tarifa_base, tarifa_distancia, tarifa_peso, subtotal, impuestos, total, estado, fecha_creacion, fecha_actualizacion) VALUES
(1, 'FACT-2024-001', 3, 1, 1, 'EXPRESS', 12.0, 3.0, 100.00, 180.00, 30.00, 310.00, 49.60, 359.60, 'PENDIENTE', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

SELECT setval('facturas_id_seq', (SELECT MAX(id) FROM facturas));

-- ======================
-- VERIFICACION
-- ======================
\c auth_db
SELECT 'AUTH_DB: ' || COUNT(*) || ' usuarios' FROM usuarios;

\c pedido_db
SELECT 'PEDIDO_DB: ' || COUNT(*) || ' pedidos' FROM pedidos;

\c fleet_db
SELECT 'FLEET_DB: ' || COUNT(*) || ' vehiculos, ' || (SELECT COUNT(*) FROM repartidores) || ' repartidores' FROM vehiculos;

\c billing_db
SELECT 'BILLING_DB: ' || COUNT(*) || ' facturas' FROM facturas;
