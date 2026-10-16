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
        return refugioRepository.findAll();
    }

    @Override
    public Refugio registrarRefugio(Refugio refugio) {
        if (vacio(refugio.getNombre()) || vacio(refugio.getCiudad())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nombre y ciudad del refugio son obligatorios");
        }
        refugio.setIdRefugio(null);
        if (refugio.getValidado() == null) {
            refugio.setValidado(false);
        }
        return refugioRepository.save(refugio);
    }

    @Override
    public Refugio cambiarValidacionRefugio(Integer idRefugio, boolean validado) {
        Refugio refugio = refugioRepository.findById(idRefugio)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Refugio no encontrado"));
        refugio.setValidado(validado);
        return refugioRepository.save(refugio);
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
