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

## 4. Regenerar `schema-mysql.sql`

Si cambias las entidades, vuelve a generar el script sin necesidad de tener MySQL encendido:

```bash
./mvnw clean package -DskipTests
java -jar target/dogtor-0.0.1-SNAPSHOT.jar --spring.profiles.active=ddl
```

## Empezar de cero

```sql
DROP DATABASE dogtor;
```

y repite desde el paso 1.
