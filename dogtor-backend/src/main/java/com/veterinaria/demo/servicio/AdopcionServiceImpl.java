package com.veterinaria.demo.servicio;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.veterinaria.demo.entidad.MascotaAdopcion;
import com.veterinaria.demo.entidad.Refugio;
import com.veterinaria.demo.entidad.SolicitudAdopcion;
import com.veterinaria.demo.repositorio.MascotaAdopcionRepository;
import com.veterinaria.demo.repositorio.RefugioRepository;
import com.veterinaria.demo.repositorio.SolicitudAdopcionRepository;
import com.veterinaria.demo.repositorio.UserRepository;
import com.veterinaria.demo.seguridad.CustomUserDetailsService;
import com.veterinaria.demo.entidad.UserEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class AdopcionServiceImpl implements AdopcionService {

    private static final List<String> ESTADOS_VISIBLES = List.of("DISPONIBLE", "EN_PROCESO");
    private static final List<String> SOLICITUDES_ACTIVAS = List.of("PENDIENTE", "EN_REVISION");
    private static final Set<String> ESTADOS_SOLICITUD = Set.of("PENDIENTE", "EN_REVISION", "APROBADA", "RECHAZADA");

    @Autowired
    private MascotaAdopcionRepository mascotaAdopcionRepository;

    @Autowired
    private SolicitudAdopcionRepository solicitudAdopcionRepository;

    @Autowired
    private RefugioRepository refugioRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // REQ-ADO-05: filtro por especie, edad, tamaño y ciudad. Las adoptadas no se muestran (REQ-ADO-03)
    @Override
    public List<MascotaAdopcion> catalogo(String especie, String tamano, String ciudad, String edad) {
        return mascotaAdopcionRepository.findByEstadoInAndRefugio_ValidadoTrueOrderByFechaPublicacionDesc(ESTADOS_VISIBLES)
                .stream()
                .filter(m -> vacio(especie) || especie.equalsIgnoreCase(m.getEspecie()))
                .filter(m -> vacio(tamano) || tamano.equalsIgnoreCase(m.getTamano()))
                .filter(m -> vacio(ciudad) || (m.getCiudad() != null && m.getCiudad().toLowerCase().contains(ciudad.toLowerCase())))
                .filter(m -> vacio(edad) || coincideEdad(m.getEdadMeses(), edad))
                .toList();
    }

    @Override
    public MascotaAdopcion obtenerMascota(Integer id) {
        return mascotaAdopcionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mascota en adopción no encontrada"));
    }

    @Override
    public List<MascotaAdopcion> obtenerTodasMascotas() {
        return mascotaAdopcionRepository.findAll();
    }

    @Override
    public MascotaAdopcion publicarMascota(Integer idRefugio, MascotaAdopcion mascota) {
        Refugio refugio = refugioRepository.findById(idRefugio)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Refugio no encontrado"));
        // Regla de negocio: solo refugios validados pueden publicar (REQ-ADO-04)
        if (!Boolean.TRUE.equals(refugio.getValidado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El refugio debe ser validado por Dogtor antes de publicar mascotas");
        }
        mascota.setIdMascotaAdopcion(null);
        mascota.setRefugio(refugio);
        mascota.setEstado("DISPONIBLE");
        mascota.setFechaPublicacion(new Date());
        if (vacio(mascota.getCiudad())) {
            mascota.setCiudad(refugio.getCiudad());
        }
        return mascotaAdopcionRepository.save(mascota);
    }

    @Override
    public MascotaAdopcion actualizarMascota(Integer id, MascotaAdopcion datos) {
        MascotaAdopcion mascota = obtenerMascota(id);
        mascota.setNombre(datos.getNombre());
        mascota.setEspecie(datos.getEspecie());
        mascota.setRaza(datos.getRaza());
        mascota.setEdadMeses(datos.getEdadMeses());
        mascota.setTamano(datos.getTamano());
        mascota.setSexo(datos.getSexo());
        mascota.setCiudad(datos.getCiudad());
        mascota.setDescripcion(datos.getDescripcion());
        mascota.setEstadoSalud(datos.getEstadoSalud());
        mascota.setFoto(datos.getFoto());
        if (datos.getEstado() != null) {
            mascota.setEstado(datos.getEstado());
        }
        return mascotaAdopcionRepository.save(mascota);
    }

    @Override
    public SolicitudAdopcion enviarSolicitud(Integer idMascota, SolicitudAdopcion solicitud) {
        MascotaAdopcion mascota = obtenerMascota(idMascota);
        if ("ADOPTADA".equals(mascota.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta mascota ya fue adoptada");
        }
        if (vacio(solicitud.getNombreSolicitante()) || vacio(solicitud.getCorreo()) || vacio(solicitud.getTelefono())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nombre, correo y teléfono son obligatorios");
        }
        solicitud.setIdSolicitud(null);
        solicitud.setMascotaAdopcion(mascota);
        solicitud.setEstado("PENDIENTE");
        solicitud.setFecha(new Date());
        return solicitudAdopcionRepository.save(solicitud);
    }

    @Override
    public List<SolicitudAdopcion> obtenerSolicitudes() {
        return solicitudAdopcionRepository.findAllByOrderByFechaDesc();
    }

    // REQ-ADO-07: historial de solicitudes por mascota
    @Override
    public List<SolicitudAdopcion> obtenerSolicitudesPorMascota(Integer idMascota) {
        return solicitudAdopcionRepository.findByMascotaAdopcion_IdMascotaAdopcionOrderByFechaDesc(idMascota);
    }

    @Override
    @Transactional
    public SolicitudAdopcion cambiarEstadoSolicitud(Integer idSolicitud, String estado) {
        String nuevoEstado = estado == null ? "" : estado.toUpperCase();
        if (!ESTADOS_SOLICITUD.contains(nuevoEstado)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado de solicitud inválido");
        }
        SolicitudAdopcion solicitud = solicitudAdopcionRepository.findById(idSolicitud)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada"));
        MascotaAdopcion mascota = solicitud.getMascotaAdopcion();

        if ("ADOPTADA".equals(mascota.getEstado()) && !"RECHAZADA".equals(nuevoEstado)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La mascota ya fue adoptada");
        }

        switch (nuevoEstado) {
            case "EN_REVISION" -> {
                // Regla: una mascota no puede tener más de un proceso de adopción activo
                boolean otroEnRevision = solicitudAdopcionRepository
                        .findByMascotaAdopcion_IdMascotaAdopcionAndEstadoIn(mascota.getIdMascotaAdopcion(), List.of("EN_REVISION"))
                        .stream().anyMatch(s -> !s.getIdSolicitud().equals(idSolicitud));
                if (otroEnRevision) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un proceso de adopción activo para esta mascota");
                }
                // REQ-ADO-06: en proceso, sin retirarla del catálogo
                mascota.setEstado("EN_PROCESO");
            }
            case "APROBADA" -> {
                // REQ-ADO-03: se marca como adoptada y se oculta del catálogo
                mascota.setEstado("ADOPTADA");
                solicitudAdopcionRepository
                        .findByMascotaAdopcion_IdMascotaAdopcionAndEstadoIn(mascota.getIdMascotaAdopcion(), SOLICITUDES_ACTIVAS)
                        .stream()
                        .filter(s -> !s.getIdSolicitud().equals(idSolicitud))
                        .forEach(s -> s.setEstado("RECHAZADA"));
            }
            default -> {
                // RECHAZADA o PENDIENTE: si era el proceso activo, la mascota vuelve a estar disponible
                if ("EN_REVISION".equals(solicitud.getEstado()) && "EN_PROCESO".equals(mascota.getEstado())) {
                    mascota.setEstado("DISPONIBLE");
                }
            }
        }

        solicitud.setEstado(nuevoEstado);
        mascotaAdopcionRepository.save(mascota);
        return solicitudAdopcionRepository.save(solicitud);
    }

    @Override
    public List<Refugio> obtenerRefugiosValidados() {
        return refugioRepository.findByValidadoTrue();
    }

    @Override
    public List<Refugio> obtenerTodosRefugios() {
        // Solo el administrador recibe el usuario de cada cuenta, para poder dárselo al refugio
        List<Refugio> refugios = refugioRepository.findAll();
        refugios.forEach(r -> r.setNombreUsuario(r.getUser() != null ? r.getUser().getUsername() : null));
        return refugios;
    }

    @Override
    @Transactional
    public Refugio registrarRefugio(Refugio refugio) {
        if (vacio(refugio.getNombre()) || vacio(refugio.getCiudad())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nombre y ciudad del refugio son obligatorios");
        }
        // El administrador puede crearle de una vez la cuenta con la que el refugio entrará a su panel
        String usuario = vacio(refugio.getNombreUsuario()) ? null : refugio.getNombreUsuario().trim();
        refugio.setUser(usuario != null ? crearCuenta(usuario, refugio.getContrasena()) : null);
        refugio.setIdRefugio(null);
        refugio.setInstagram(red(refugio.getInstagram()));
        refugio.setFacebook(red(refugio.getFacebook()));
        refugio.setTiktok(red(refugio.getTiktok()));
        refugio.setSitioWeb(red(refugio.getSitioWeb()));
        if (refugio.getValidado() == null) {
            refugio.setValidado(false);
        }
        Refugio guardado = refugioRepository.save(refugio);
        guardado.setNombreUsuario(usuario);
        return guardado;
    }

    @Override
    public Refugio cambiarValidacionRefugio(Integer idRefugio, boolean validado) {
        Refugio refugio = refugioRepository.findById(idRefugio)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Refugio no encontrado"));
        refugio.setValidado(validado);
        return refugioRepository.save(refugio);
    }

    @Override
    @Transactional
    public Refugio asignarCuentaRefugio(Integer idRefugio, String nombreUsuario, String contrasena) {
        Refugio refugio = refugioRepository.findById(idRefugio)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Refugio no encontrado"));
        if (refugio.getUser() == null) {
            if (vacio(nombreUsuario)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Escribe el usuario de la cuenta");
            }
            refugio.setUser(crearCuenta(nombreUsuario.trim(), contrasena));
        } else {
            // Ya tiene cuenta: el administrador le asigna una contraseña nueva (p. ej. si la olvidó)
            validarContrasena(contrasena);
            UserEntity user = refugio.getUser();
            user.setPassword(passwordEncoder.encode(contrasena));
            userRepository.save(user);
        }
        Refugio guardado = refugioRepository.save(refugio);
        guardado.setNombreUsuario(guardado.getUser().getUsername());
        return guardado;
    }

    @Override
    public void cambiarContrasenaRefugio(String nombreUsuario, String actual, String nueva) {
        UserEntity user = userRepository.findByUsername(nombreUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        if (actual == null || !passwordEncoder.matches(actual, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña actual no es correcta");
        }
        validarContrasena(nueva);
        user.setPassword(passwordEncoder.encode(nueva));
        userRepository.save(user);
    }

    private UserEntity crearCuenta(String usuario, String contrasena) {
        if (!usuario.matches("[a-zA-Z0-9._-]{3,30}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario debe tener entre 3 y 30 caracteres, sin espacios");
        }
        validarContrasena(contrasena);
        if (Boolean.TRUE.equals(userRepository.existsByUsername(usuario))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ese nombre de usuario ya está en uso");
        }
        return userRepository.save(customUserDetailsService.refugioToUser(usuario, contrasena));
    }

    private void validarContrasena(String contrasena) {
        if (contrasena == null || contrasena.length() < 4) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe tener al menos 4 caracteres");
        }
    }

    // ---------- Panel del refugio ----------

    @Override
    public Refugio refugioDeUsuario(String nombreUsuario) {
        return refugioRepository.findByUser_Username(nombreUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Tu cuenta no está asociada a ningún refugio"));
    }

    @Override
    public Refugio actualizarPerfilRefugio(Integer idRefugio, Refugio datos) {
        if (vacio(datos.getNombre()) || vacio(datos.getCiudad())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nombre y ciudad del refugio son obligatorios");
        }
        Refugio refugio = refugioRepository.findById(idRefugio)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Refugio no encontrado"));
        // El refugio no puede validarse a sí mismo: solo cambia sus datos de contacto
        refugio.setNombre(datos.getNombre().trim());
        refugio.setCiudad(datos.getCiudad().trim());
        refugio.setTelefono(datos.getTelefono());
        refugio.setCorreo(datos.getCorreo());
        refugio.setDescripcion(datos.getDescripcion());
        refugio.setLogo(datos.getLogo());
        refugio.setInstagram(red(datos.getInstagram()));
        refugio.setFacebook(red(datos.getFacebook()));
        refugio.setTiktok(red(datos.getTiktok()));
        refugio.setSitioWeb(red(datos.getSitioWeb()));
        return refugioRepository.save(refugio);
    }

    @Override
    public List<MascotaAdopcion> mascotasDeRefugio(Integer idRefugio) {
        return mascotaAdopcionRepository.findByRefugio_IdRefugioOrderByFechaPublicacionDesc(idRefugio);
    }

    @Override
    public List<SolicitudAdopcion> solicitudesDeRefugio(Integer idRefugio) {
        return solicitudAdopcionRepository.findByMascotaAdopcion_Refugio_IdRefugioOrderByFechaDesc(idRefugio);
    }

    @Override
    public MascotaAdopcion actualizarMascotaDeRefugio(Integer idRefugio, Integer idMascota, MascotaAdopcion datos) {
        verificarDueno(obtenerMascota(idMascota), idRefugio);
        return actualizarMascota(idMascota, datos);
    }

    @Override
    public SolicitudAdopcion cambiarEstadoSolicitudDeRefugio(Integer idRefugio, Integer idSolicitud, String estado) {
        SolicitudAdopcion solicitud = solicitudAdopcionRepository.findById(idSolicitud)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada"));
        verificarDueno(solicitud.getMascotaAdopcion(), idRefugio);
        return cambiarEstadoSolicitud(idSolicitud, estado);
    }

    private void verificarDueno(MascotaAdopcion mascota, Integer idRefugio) {
        if (mascota.getRefugio() == null || !mascota.getRefugio().getIdRefugio().equals(idRefugio)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Esta mascota no pertenece a tu refugio");
        }
    }

    /** Usuario o enlace de una red social: sin espacios sobrantes, vacío como null y como máximo 255 caracteres. */
    private String red(String valor) {
        if (vacio(valor)) {
            return null;
        }
        String limpio = valor.trim();
        if (limpio.length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El enlace de la red social es demasiado largo");
        }
        return limpio;
    }

    private boolean vacio(String valor) {
        return valor == null || valor.isBlank();
    }

    // CACHORRO: menos de 1 año, ADULTO: de 1 a 7 años, SENIOR: más de 7 años
    private boolean coincideEdad(Integer meses, String edad) {
        if (meses == null) {
            return false;
        }
        return switch (edad.toUpperCase()) {
            case "CACHORRO" -> meses < 12;
            case "ADULTO" -> meses >= 12 && meses <= 84;
            case "SENIOR" -> meses > 84;
            default -> true;
        };
    }
}
