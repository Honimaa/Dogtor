# Dogtor

Sistema web de gestión veterinaria y adopción de mascotas — Dogtor S.A.S.
Historias clínicas digitales, citas en línea, doctores y urgencias 24 horas, inventario,
ventas, recordatorios y adopción responsable en una sola plataforma.

| Carpeta | Contenido |
| --- | --- |
| [`dogtor-backend`](dogtor-backend) | API REST en Spring Boot (puerto 8082) |
| [`dogtor-frontend`](dogtor-frontend) | Aplicación web en Angular 16 (puerto 4200) |

## Cómo ejecutarlo

1. Backend: `cd dogtor-backend` y `./mvnw clean package -DskipTests && java -jar target/dogtor-0.0.1-SNAPSHOT.jar`
2. Frontend: `cd dogtor-frontend`, `npm install --legacy-peer-deps` (solo la primera vez) y `npm start`
3. Abrir `http://localhost:4200`

Usuarios de prueba: `admin` / `123`, `vet1` / `pass1`, `carlosg` / `pass2`.

## Base de datos

Por defecto usa H2 en memoria (los datos se reinician en cada arranque). Para guardarlos en MySQL,
sigue la guía de [`dogtor-backend/database`](dogtor-backend/database/README.md) y arranca el backend
con `--spring.profiles.active=mysql`.

