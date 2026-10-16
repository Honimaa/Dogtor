-- =====================================================================
-- Dogtor · Consultas útiles sobre la base de datos MySQL
-- Uso:  mysql -u dogtor_app -p dogtor  y luego pegar la consulta
-- =====================================================================

-- 1. Citas agendadas para hoy, con mascota, propietario y doctor
SELECT c.id_cita, c.fecha_hora, c.sede, c.tipo_consulta, c.motivo,
       m.nombre AS mascota, cl.nombre AS propietario, v.nombre AS doctor
FROM cita c
JOIN mascota m      ON m.id_mascota = c.id_mascota
JOIN cliente cl     ON cl.id_cliente = m.id_cliente
LEFT JOIN veterinario v ON v.id_veterinario = c.id_veterinario
WHERE DATE(c.fecha_hora) = CURDATE() AND c.estado = 'AGENDADA'
ORDER BY c.fecha_hora;

-- 2. Historia clínica completa de una mascota (cambia el id)
SELECT e.fecha, e.tipo, e.titulo, e.descripcion, e.fecha_proxima, v.nombre AS doctor
FROM evento_clinico e
LEFT JOIN veterinario v ON v.id_veterinario = e.id_veterinario
WHERE e.id_mascota = 1
ORDER BY e.fecha DESC;

-- 3. Mascotas con alergias registradas
SELECT m.nombre AS mascota, cl.nombre AS propietario, e.titulo AS alergia
FROM evento_clinico e
JOIN mascota m  ON m.id_mascota = e.id_mascota
JOIN cliente cl ON cl.id_cliente = m.id_cliente
WHERE e.tipo = 'ALERGIA';

-- 4. Vacunas y controles que vencen en los próximos 30 días (recordatorios)
SELECT m.nombre AS mascota, cl.nombre AS propietario, cl.correo, e.titulo, e.fecha_proxima
FROM evento_clinico e
JOIN mascota m  ON m.id_mascota = e.id_mascota
JOIN cliente cl ON cl.id_cliente = m.id_cliente
WHERE e.fecha_proxima BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 30 DAY)
ORDER BY e.fecha_proxima;

-- 5. Inventario con bajo stock
SELECT id_medicamento, nombre, categoria, unidades_disponibles, stock_minimo
FROM medicamento
WHERE unidades_disponibles < stock_minimo
ORDER BY unidades_disponibles;

-- 6. Productos vencidos o que vencen en los próximos 60 días
SELECT id_medicamento, nombre, lote, fecha_vencimiento,
       DATEDIFF(fecha_vencimiento, CURDATE()) AS dias_para_vencer
FROM medicamento
WHERE fecha_vencimiento <= DATE_ADD(CURDATE(), INTERVAL 60 DAY)
ORDER BY fecha_vencimiento;

-- 7. Ventas por mes (COP)
SELECT DATE_FORMAT(fecha_hora, '%Y-%m') AS mes, COUNT(*) AS facturas, SUM(total) AS total_cop
FROM factura
GROUP BY mes
ORDER BY mes DESC;

-- 8. Doctores con turno de urgencias 24 horas por sede
SELECT sede, nombre, especialidad, horario
FROM veterinario
WHERE urgencias24h = 1 AND estado = 1
ORDER BY sede;

-- 9. Catálogo de adopción visible (refugios validados)
SELECT ma.nombre, ma.especie, ma.raza, ma.estado, r.nombre AS refugio, ma.ciudad
FROM mascota_adopcion ma
JOIN refugio r ON r.id_refugio = ma.id_refugio
WHERE r.validado = 1 AND ma.estado IN ('DISPONIBLE', 'EN_PROCESO');

-- 10. Solicitudes de adopción pendientes o en revisión
SELECT s.id_solicitud, s.fecha, s.estado, s.nombre_solicitante, s.telefono, ma.nombre AS mascota
FROM solicitud_adopcion s
JOIN mascota_adopcion ma ON ma.id_mascota_adopcion = s.id_mascota_adopcion
WHERE s.estado IN ('PENDIENTE', 'EN_REVISION')
ORDER BY s.fecha DESC;

-- 11. Avisos de Dogtor Encuentra sin resolver
SELECT tipo, nombre_mascota, especie, lugar, ciudad, contacto_nombre, contacto_telefono, fecha_publicacion
FROM publicacion_mascota
WHERE resuelta = 0
ORDER BY fecha_publicacion DESC;

-- 12. Usuarios y su rol
SELECT u.username, r.name AS rol
FROM users u
JOIN user_roles ur ON ur.user_id = u.id_user
JOIN roles r       ON r.id_rol = ur.role_id
ORDER BY r.name, u.username;
