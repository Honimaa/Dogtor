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
| **Adopción** | Catálogo público de mascotas en adopción. Los refugios y fundaciones se registran, publican a sus rescatados con foto y gestionan sus solicitudes; Dogtor valida cada refugio. |
| **Dogtor Encuentra** | Publicaciones de mascotas perdidas y encontradas (requiere sesión; solo el autor puede marcarlas como resueltas). |
| **Blog y herramientas** | Artículos, playlist relajante para mascotas, calculadora de edad humana, calculadora de peso por raza, generador de nombres, ruleta de cumpleaños y test de salud. |

### Roles

| Rol | Acceso principal |
| --- | --- |
| **Administrador** | Panel con indicadores, veterinarios, clientes, mascotas, inventario, servicios y adopciones. |
| **Veterinario** | Sus citas, pacientes atendidos, historias clínicas, tratamientos y facturas de sus pacientes. |
| **Cliente** | Sus mascotas, citas, historia clínica, facturas, compras y publicaciones en Dogtor Encuentra. |
| **Refugio** | Su panel de adopción: publicar y editar sus mascotas, responder solicitudes y editar los datos del refugio. |

Cualquier persona puede crear una cuenta de cliente desde **Registrarme** en la pantalla de inicio de sesión,
y cualquier refugio o fundación puede registrarse en **¿Eres un refugio?** (`/registro-refugio`).
Clientes, veterinarios y administradores editan sus datos y su foto en **Mi perfil**; el refugio lo hace
en la pestaña **Mi refugio** de su panel.

---

## Adopción: cómo publica un refugio

1. **Registro.** El refugio llena el formulario de `/registro-refugio` (nombre, ciudad, contacto, logo, redes sociales,
   usuario y contraseña). Se crea su cuenta con el rol `REFUGIO` y el refugio queda **pendiente de validación**.
2. **Validación.** Un administrador revisa el refugio en **Adopciones › Refugios** y pulsa **Validar**.
   Mientras no esté validado, el refugio puede entrar a su panel pero no publicar.
3. **Publicación.** Desde **Mi refugio** (`/refugio/panel`) el refugio publica cada mascota con su **foto**
   (se sube desde el computador o el celular y se reduce automáticamente), especie, raza, edad, tamaño,
   sexo, descripción y estado de salud. La mascota aparece al instante en el catálogo público (`/adopcion`).
4. **Solicitudes.** Las familias envían su solicitud sin crear cuenta. El refugio ve **solo las solicitudes
   de sus mascotas** y las marca como *En revisión*, *Aprobada* o *Rechazada*. Al aprobar una, la mascota
   pasa a *Adoptada*, sale del catálogo y las demás solicitudes se rechazan.

El administrador sigue viendo todos los refugios, mascotas y solicitudes. También puede registrar un refugio
él mismo y, si quiere, **crearle de una vez su usuario y una contraseña inicial**; o darle acceso más tarde a un
refugio que no tenga cuenta (**Crear acceso**) y ponerle una contraseña nueva si la olvidó. El refugio cambia su
contraseña en **Mi refugio › Cuenta de acceso**. Los refugios sin cuenta los sigue gestionando el administrador. Si retira la validación de un refugio, sus mascotas dejan de verse en el
catálogo. Cada refugio solo puede ver y modificar lo suyo: el backend toma el refugio de la sesión, nunca de la URL.

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
| Refugio (Fundación Huellitas de Amor, ya validado) | `huellitas` | `pass3` |

Para probar el registro de un refugio nuevo, usa `/registro-refugio` y valídalo con la cuenta de administrador.

---

## Base de datos MySQL

Por defecto el backend usa H2 en memoria, así que los datos se reinician en cada arranque. Para usar MySQL:

1. Crea la base `dogtor` y el usuario `dogtor_app`. Ejecuta este script como `root`, por ejemplo en
   MySQL Workbench. Es el mismo de [`00-crear-base-de-datos.sql`](dogtor-backend/database/00-crear-base-de-datos.sql):

   ```sql
   CREATE DATABASE IF NOT EXISTS dogtor
     CHARACTER SET utf8mb4
     COLLATE utf8mb4_unicode_ci;

   CREATE USER IF NOT EXISTS 'dogtor_app'@'localhost' IDENTIFIED BY 'CambiaEstaClave';
   GRANT ALL PRIVILEGES ON dogtor.* TO 'dogtor_app'@'localhost';
   FLUSH PRIVILEGES;
   ```

   También puedes ejecutarlo desde la terminal:

   ```powershell
   & "C:\Program Files\MySQL\MySQL Server 9.4\bin\mysql.exe" -u root -p -e "source dogtor-backend/database/00-crear-base-de-datos.sql"
   ```

2. Arranca el backend con el perfil `mysql` e indica las credenciales:

   ```powershell
   cd dogtor-backend
   $env:DB_USER="dogtor_app"; $env:DB_PASSWORD="CambiaEstaClave"
   java -jar target/dogtor-0.0.1-SNAPSHOT.jar --spring.profiles.active=mysql
   ```

Las tablas se crean solas la primera vez. Si prefieres crearlas manualmente, usa
[`schema-mysql.sql`](dogtor-backend/database/schema-mysql.sql). Las consultas de ejemplo están en
[`consultas-utiles.sql`](dogtor-backend/database/consultas-utiles.sql). Los detalles están en la
[guía de la base de datos](dogtor-backend/database/README.md).

> **¿Ya tenías la base creada antes del módulo de refugios?** Hibernate agrega solo la columna nueva
> (`refugio.user_id_user`), pero no amplía columnas existentes. Ejecuta una vez el ajuste de
> [Actualizar una base existente](dogtor-backend/database/README.md#actualizar-una-base-existente)
> para que las fotos de `mascota_adopcion` y los logos de `refugio` se puedan subir.

| Variable | Valor por defecto |
| --- | --- |
| `DB_HOST` | `localhost` |
| `DB_PORT` | `3306` |
| `DB_NAME` | `dogtor` |
| `DB_USER` | `root` |
| `DB_PASSWORD` | *(vacía)* |
| `JWT_SECRET` | Clave de desarrollo incluida en `application.properties`; cámbiala en producción (mínimo 64 caracteres) |

---

## Créditos

Proyecto desarrollado para **Dogtor S.A.S.** Los GIFs de gatos provienen de Klipy y se usan con fines académicos.
