# Base de datos MySQL de Dogtor

Por defecto el backend usa **H2 en memoria** (se borra al reiniciar). Para guardar los datos de
forma permanente se usa **MySQL 8 o superior** con el perfil `mysql`.

| Archivo | Para qué sirve |
| --- | --- |
| `00-crear-base-de-datos.sql` | Crea la base `dogtor` y el usuario `dogtor_app` |
| `schema-mysql.sql` | Las 22 tablas, llaves primarias y foráneas (generado desde las entidades JPA) |
| `consultas-utiles.sql` | Consultas de ejemplo: citas del día, inventario, recordatorios, adopciones… |

## 1. Crear la base y el usuario

Edita la contraseña en `00-crear-base-de-datos.sql` y ejecútalo como root:

```bash
mysql -u root -p < database/00-crear-base-de-datos.sql
```

(En Windows, si `mysql` no está en el PATH: `"C:\Program Files\MySQL\MySQL Server 9.4\bin\mysql.exe" -u root -p < database\00-crear-base-de-datos.sql`)

## 2. Crear las tablas

Hay dos formas; usa **una**:

- **A. Con el script** (recomendado si quieres revisar las tablas antes):

  ```bash
  mysql -u dogtor_app -p dogtor < database/schema-mysql.sql
  ```

- **B. Automática**: omite este paso. Al arrancar con el perfil `mysql`, Hibernate crea o actualiza
  las tablas que falten (`spring.jpa.hibernate.ddl-auto=update`).

## 3. Arrancar el backend con MySQL

PowerShell:

```powershell
$env:DB_USER = "dogtor_app"; $env:DB_PASSWORD = "CambiaEstaClave"
java -jar target/dogtor-0.0.1-SNAPSHOT.jar --spring.profiles.active=mysql
```

Bash / Git Bash:

```bash
DB_USER=dogtor_app DB_PASSWORD=CambiaEstaClave java -jar target/dogtor-0.0.1-SNAPSHOT.jar --spring.profiles.active=mysql
```

Variables opcionales: `DB_HOST` (localhost), `DB_PORT` (3306) y `DB_NAME` (dogtor).

La **primera vez** se cargan los datos de ejemplo (usuarios de prueba, mascotas, medicamentos,
refugios…). En los siguientes arranques no se duplican, porque se verifica si ya existen.

## Tablas de adopción y refugios

Los hogares de rescate (refugios) publican sus mascotas y reciben las solicitudes de las familias
interesadas. Lo guardan tres tablas:

| Tabla | Qué guarda | Relaciones |
| --- | --- | --- |
| `refugio` | Nombre, ciudad, contacto, descripción, logo, redes sociales (`instagram`, `facebook`, `tiktok`, `sitio_web`) y si está `validado` por el admin | `user_id_user` → `users` (cuenta con rol `REFUGIO`, única por refugio; vacía si el refugio no tiene acceso) |
| `mascota_adopcion` | Mascotas publicadas: especie, raza, edad, tamaño, salud, `foto` y `estado` (`DISPONIBLE`, `EN_PROCESO`, `ADOPTADA`) | `id_refugio` → `refugio` |
| `solicitud_adopcion` | Datos de quien quiere adoptar, su mensaje y `estado` (`PENDIENTE`, `EN_REVISION`, `APROBADA`, `RECHAZADA`) | `id_mascota_adopcion` → `mascota_adopcion` |

Reglas importantes:

- El catálogo público solo muestra mascotas `DISPONIBLE` o `EN_PROCESO` de refugios con `validado = 1`.
  Un refugio que se registra solo queda con `validado = 0` hasta que el administrador lo valida.
- `foto` y `logo` son `MEDIUMTEXT` porque guardan la imagen subida (comprimida en JPEG, como texto base64).
- La contraseña del refugio no está en `refugio`: vive en `users.password` (cifrada con BCrypt), igual que
  la de los demás usuarios.

Consulta para ver cada refugio con su cuenta:

```sql
SELECT r.id_refugio, r.nombre, r.ciudad, r.validado, u.username AS usuario
FROM refugio r
LEFT JOIN users u ON u.id_user = r.user_id_user
ORDER BY r.nombre;
```

## 4. Regenerar `schema-mysql.sql`

Si cambias las entidades, vuelve a generar el script sin necesidad de tener MySQL encendido:

```bash
./mvnw clean package -DskipTests
java -jar target/dogtor-0.0.1-SNAPSHOT.jar --spring.profiles.active=ddl
```

## Actualizar una base existente

`ddl-auto=update` crea las tablas y columnas que falten, pero **no cambia el tipo** de una columna que ya existe.
Si tu base se creó antes de que las fotos de adopción se subieran como imagen, ejecuta una vez:

```sql
ALTER TABLE mascota_adopcion MODIFY foto MEDIUMTEXT NOT NULL;
ALTER TABLE refugio MODIFY logo MEDIUMTEXT;
```

Las columnas nuevas de `refugio` (cuenta y redes sociales) las agrega Hibernate al arrancar. Si prefieres
crearlas a mano, o arrancas con `ddl-auto=none`:

```sql
ALTER TABLE refugio
  ADD COLUMN user_id_user INTEGER,
  ADD COLUMN instagram VARCHAR(255),
  ADD COLUMN facebook VARCHAR(255),
  ADD COLUMN tiktok VARCHAR(255),
  ADD COLUMN sitio_web VARCHAR(255),
  ADD CONSTRAINT UK_refugio_user UNIQUE (user_id_user),
  ADD CONSTRAINT FK_refugio_user FOREIGN KEY (user_id_user) REFERENCES users (id_user);
INSERT INTO roles (name) SELECT 'REFUGIO' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'REFUGIO');
```

El rol `REFUGIO` también se crea solo cuando se registra el primer refugio con cuenta.

## Empezar de cero

```sql
DROP DATABASE dogtor;
```

y repite desde el paso 1.
