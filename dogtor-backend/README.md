# Dogtor — Backend

API REST de Dogtor, sistema web de gestión veterinaria y adopción de mascotas (Dogtor S.A.S.).
Spring Boot 3.2 · Java 17+ · Spring Security con JWT · JPA/Hibernate · base de datos H2 en memoria.

## Ejecutar

```bash
./mvnw clean package -DskipTests        # en Windows: mvnw.cmd
java -jar target/dogtor-0.0.1-SNAPSHOT.jar
```

La API queda en `http://localhost:8082`. Al arrancar se cargan datos de ejemplo
(`DataBaseInit` y `DogtorDataInit`). La consola de H2 está en `/h2-console`
(JDBC URL `jdbc:h2:mem:testdb`, usuario `sa`, sin contraseña).

## Módulos

| Módulo | Endpoints principales |
| --- | --- |
| Historia clínica | `/historia-clinica/mascota/{id}` |
| Citas y disponibilidad | `/cita`, `/cita/disponibilidad/{idVeterinario}` |
| Doctores y urgencias 24h | `/veterinario`, `/veterinario/urgencias` |
| Inventario | `/medicamento`, `/medicamento/alertas` |
| Recordatorios | `/recordatorios/cliente/{id}`, `/recordatorios/clinica` |
| Adopción | `/adopcion`, `/adopcion/admin/**` |
| Dogtor Encuentra | `/publicaciones` |

## Usuarios de prueba

| Rol | Usuario | Contraseña |
| --- | --- | --- |
| Administrador | `admin` | `123` |
| Doctor | `vet1` | `pass1` |
| Propietario | `carlosg` | `pass2` |
