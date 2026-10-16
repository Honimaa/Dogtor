# Dogtor

Plataforma web de **gestión de clínicas veterinarias y adopción de mascotas**.
Dogtor reúne en un mismo sistema la historia clínica digital de cada paciente, el agendamiento de citas,
la atención de urgencias 24 horas, el inventario de medicamentos, la facturación, los recordatorios de
vacunas y controles, y un catálogo de adopción responsable.

| Carpeta | Contenido |
| --- | --- |
| [`dogtor-frontend`](dogtor-frontend) | Aplicación web en Angular (puerto 4200) |
| [`dogtor-backend`](dogtor-backend) | API REST en Spring Boot (puerto 8082) |
| [`dogtor-backend/database`](dogtor-backend/database) | Scripts SQL para MySQL y consultas de ejemplo |

---

## Módulos

| Módulo | Qué permite hacer |
| --- | --- |
| **Historia clínica** | Registro digital de consultas, diagnósticos, tratamientos, vacunas y peso de cada mascota. |
| **Citas** | Agendar, editar y cancelar citas con horarios disponibles por veterinario. |
| **Doctores y urgencias 24 h** | Directorio de veterinarios activos, especialidades y página de urgencias con contacto inmediato. |
| **Inventario** | Medicamentos e insumos con stock, precio en pesos colombianos (COP) y alta/edición desde el panel de administración. |
| **Ventas y facturación** | Compra de medicamentos y servicios, y facturas por cliente. |
| **Recordatorios** | Avisos de vacunas, desparasitaciones y controles próximos. |
| **Adopción** | Catálogo público de mascotas en adopción y gestión de solicitudes por parte del administrador. |
| **Dogtor Encuentra** | Publicaciones de mascotas perdidas y encontradas (requiere sesión; solo el autor puede marcarlas como resueltas). |
| **Blog y herramientas** | Artículos, playlist relajante para mascotas, calculadora de edad humana, calculadora de peso por raza, generador de nombres, ruleta de cumpleaños y test de salud. |

### Roles

| Rol | Acceso principal |
| --- | --- |
| **Administrador** | Panel con indicadores, veterinarios, clientes, mascotas, inventario, servicios y adopciones. |
| **Veterinario** | Sus citas, pacientes atendidos, historias clínicas, tratamientos y facturas de sus pacientes. |
| **Cliente** | Sus mascotas, citas, historia clínica, facturas, compras y publicaciones en Dogtor Encuentra. |

Cualquier persona puede crear una cuenta de cliente desde **Registrarme** en la pantalla de inicio de sesión.
Todos los roles pueden editar sus datos y su foto en **Mi perfil**.

---

## Tecnologías

**Frontend**
- Angular 16 y TypeScript 5.1
- RxJS 7.8
- ngx-bootstrap y Bootstrap Icons / Ionicons
- Chart.js para las gráficas del panel
- Formato de moneda y fechas en `es-CO`

**Backend**
- Java 17 y Spring Boot 3.2.2 (Web, Data JPA, Security)
- Autenticación con JWT (jjwt 0.11.5) y contraseñas cifradas con BCrypt
- Hibernate / JPA
- MapStruct 1.5.5 y Lombok para DTOs y entidades
- JUnit, Mockito y Selenium para pruebas

**Base de datos**
- H2 en memoria por defecto, para desarrollo rápido
- MySQL 8 o superior con el perfil `mysql`, para guardar los datos de forma permanente

**Herramientas**: Maven (wrapper incluido), npm, Git.

---

## Arquitectura

```
Navegador ──► Angular (4200) ──HTTP + JWT──► Spring Boot (8082) ──JPA──► H2 / MySQL
```

- **Frontend** (`dogtor-frontend/src/app`): un módulo de Angular organizado por funcionalidad
  (`cita`, `mascota`, `historia-clinica`, `adopcion`, `urgencias`, `recordatorios`, `perfil`…).
  Las rutas privadas se protegen con `roleGuard` según el rol guardado en el token.
- **Backend** (`dogtor-backend/src/main/java/com/veterinaria/demo`), organizado por capas:
  - `controlador`: endpoints REST
  - `servicio`: reglas de negocio
  - `repositorio`: acceso a datos con Spring Data
  - `entidad`: modelo JPA
  - `dto`: objetos de transferencia
  - `seguridad`: filtros JWT y configuración de Spring Security

  Al arrancar, `DataBaseInit` y `DogtorDataInit` cargan datos de prueba si la base está vacía.

---

## Cómo ejecutarlo

### Requisitos
- JDK 17 o superior
- Node.js 18 o superior y npm
- (Opcional) MySQL 8 o superior

### 1. Backend

```bash
cd dogtor-backend
./mvnw clean package -DskipTests
java -jar target/dogtor-0.0.1-SNAPSHOT.jar
```

La API queda en `http://localhost:8082`.

### 2. Frontend

```bash
cd dogtor-frontend
npm install --legacy-peer-deps   # solo la primera vez
npm start
```

Abre `http://localhost:4200`.

### Usuarios de prueba

| Rol | Usuario | Contraseña |
| --- | --- | --- |
| Administrador | `admin` | `123` |
| Veterinario | `vet1` | `pass1` |
| Cliente | `carlosg` | `pass2` |

---

## Base de datos MySQL

Por defecto el backend usa H2 en memoria, así que los datos se reinician en cada arranque. Para usar MySQL:

1. Ejecuta [`00-crear-base-de-datos.sql`](dogtor-backend/database/00-crear-base-de-datos.sql) como `root`.
   Crea la base `dogtor` y el usuario `dogtor_app`.
2. Arranca el backend con el perfil `mysql` e indica las credenciales:

   ```powershell
   $env:DB_USER="dogtor_app"; $env:DB_PASSWORD="tu_clave"
   java -jar target/dogtor-0.0.1-SNAPSHOT.jar --spring.profiles.active=mysql
   ```

Las tablas se crean solas la primera vez. Si prefieres crearlas manualmente, usa
[`schema-mysql.sql`](dogtor-backend/database/schema-mysql.sql). Las consultas de ejemplo están en
[`consultas-utiles.sql`](dogtor-backend/database/consultas-utiles.sql). Los detalles están en la
[guía de la base de datos](dogtor-backend/database/README.md).

| Variable | Valor por defecto |
| --- | --- |
| `DB_HOST` | `localhost` |
| `DB_PORT` | `3306` |
| `DB_NAME` | `dogtor` |
| `DB_USER` | `root` |
| `DB_PASSWORD` | *(vacía)* |

---

## Créditos

Proyecto basado en *Beagle Giggle* (frontend Angular y backend Spring Boot) y adaptado a los
requerimientos de **Dogtor S.A.S.** Los GIFs de gatos provienen de Klipy y se usan con fines académicos.
