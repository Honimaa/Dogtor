-- =====================================================================
-- Dogtor · Paso 1: crear la base de datos y un usuario para la aplicación
-- Ejecutar como root:  mysql -u root -p < database/00-crear-base-de-datos.sql
-- Cambia 'CambiaEstaClave' por una contraseña propia antes de ejecutarlo.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS dogtor
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'dogtor_app'@'localhost' IDENTIFIED BY 'CambiaEstaClave';
GRANT ALL PRIVILEGES ON dogtor.* TO 'dogtor_app'@'localhost';
FLUSH PRIVILEGES;
