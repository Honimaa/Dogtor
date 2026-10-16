# Dogtor — Frontend

Sistema web de gestión veterinaria y adopción de mascotas (Dogtor S.A.S.).
Aplicación Angular 16 que consume la API REST del backend Spring Boot (`dogtor-backend`, puerto 8082).

## Ejecutar

```bash
npm install --legacy-peer-deps   # ng2-charts declara un peer de @angular/cdk incompatible con Angular 16
npm start                        # http://localhost:4200
```

El backend debe estar corriendo en `http://localhost:8082`.

## Módulos

| Módulo | Ruta | Acceso |
| --- | --- | --- |
| Historia clínica digital (vacunas, diagnósticos, tratamientos, cirugías, alergias, PDF) | `/historia-clinica/:id` | Admin y doctor editan; propietario en modo lectura |
| Agendamiento de citas en 3 pasos con horarios disponibles reales | `/agendar-cita` | Usuarios autenticados |
| Urgencias 24 horas por sede | `/urgencias` | Público |
| Inventario con alertas de bajo stock y vencimiento | `/medicamentos` | Admin |
| Recordatorios de citas, vacunas y controles | Panel en "Mis mascotas", dashboard admin y panel del doctor | Autenticados |
| Catálogo de adopción y solicitudes | `/adopcion` | Público |
| Gestión de adopciones, solicitudes y refugios | `/admin/adopciones` | Admin |

## Guía de estilo

Los colores de marca y los componentes base (`dt-btn`, `dt-card`, `dt-badge`, `dt-alert`, `dt-field`, `dt-modal`…)
están definidos como variables CSS en `src/styles.css`. Paleta: azul original (`--dt-brand-*`) y dorado de acento (`--dt-gold-*`).
El logo (beagle) está en `src/assets/logo-beagle.png`.

## Usuarios de prueba

| Rol | Usuario | Contraseña |
| --- | --- | --- |
| Administrador | `admin` | `123` |
| Doctor | `vet1` | `pass1` |
| Propietario | `carlosg` | `pass2` |
