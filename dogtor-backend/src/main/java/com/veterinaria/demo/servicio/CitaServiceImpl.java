package com.veterinaria.demo.servicio;

import com.veterinaria.demo.entidad.Cita;
import com.veterinaria.demo.entidad.Mascota;
import com.veterinaria.demo.entidad.Servicio;
import com.veterinaria.demo.entidad.Veterinario;
import com.veterinaria.demo.repositorio.CitaRepository;
import com.veterinaria.demo.repositorio.MascotaRepository;
import com.veterinaria.demo.repositorio.ServicioRepository;
import com.veterinaria.demo.repositorio.VeterinarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class CitaServiceImpl implements CitaService {

    // Duración de cada cita y franja de atención usadas para calcular la disponibilidad
    private static final long DURACION_CITA_MIN = 60;
    private static final int HORA_INICIO = 8;
    private static final int HORA_FIN = 18;
    
    private final CitaRepository citaRepository;

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private ServicioRepository servicioRepository;
    
    
    public CitaServiceImpl(CitaRepository citaRepository) {
        this.citaRepository = citaRepository;
    }
    
    @Override
    public Cita crearCita(Cita cita) {
        // Validar que la fecha sea futura
        if (cita.getFechaHora() == null || cita.getFechaHora().before(new Date())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de la cita debe ser futura");
        }

        Veterinario veterinario = veterinarioRepository.findById(cita.getVeterinario().getIdVeterinario())
                .orElseThrow(() -> new RuntimeException("Veterinario no encontrado con ID: " + cita.getVeterinario().getIdVeterinario()));

        Mascota mascota = mascotaRepository.findById(cita.getMascota().getIdMascota())
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada con ID: " + cita.getMascota().getIdMascota()));   

        Servicio servicio = servicioRepository.findById(cita.getServicio().getIdServicio())
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado con ID: " + cita.getServicio().getIdServicio()));    
                
        validarCruceAgenda(veterinario.getIdVeterinario(), cita.getFechaHora(), null);

        cita.setServicio(servicio);
        cita.setVeterinario(veterinario);
        cita.setMascota(mascota);
        if (cita.getEstado() == null) {
            cita.setEstado("AGENDADA");
        }
        if (cita.getSede() == null) {
            cita.setSede(veterinario.getSede());
        }
        return citaRepository.save(cita);
    }
    
    @Override
    public List<Cita> obtenerCitasPorCliente(Integer idCliente) {
        return citaRepository.findByMascota_Cliente_IdCliente(idCliente);
    }
    
    @Override
    public List<Cita> obtenerCitasPorSede(String sede) {
        return citaRepository.findBySede(sede);
    }

    @Override
    public List<Cita> obtenerTodasCitas() {
        return citaRepository.findAll();
    }

    @Override
    public boolean cancelarCita(Integer idCita) {
        return citaRepository.findById(idCita)
                .map(cita -> {
                    cita.setEstado("CANCELADA"); // Asume que tu entidad Cita tiene un campo 'estado'
                    citaRepository.save(cita);
                    return true;
                })
                .orElse(false);
    }
    
    @Override
    public List<Cita> obtenerCitasPendientes() {
        return citaRepository.findByFechaHoraAfter(new Date());
    }
    
    @Override
    public List<Cita> obtenerCitasPorVeterinario(Integer idVeterinario) {
        return citaRepository.findByVeterinario_IdVeterinario(idVeterinario);
    }
    
    @Override
    public Cita actualizarCita(Cita cita) {
        if (!citaRepository.existsById(cita.getIdCita())) {
            throw new RuntimeException("Cita no encontrada con ID: " + cita.getIdCita());
        }
        if (!"CANCELADA".equals(cita.getEstado()) && cita.getVeterinario() != null && cita.getFechaHora() != null) {
            validarCruceAgenda(cita.getVeterinario().getIdVeterinario(), cita.getFechaHora(), cita.getIdCita());
        }
        return citaRepository.save(cita);
    }
    
    @Override
    public Cita obtenerCitaPorId(Integer idCita) {
        return citaRepository.findById(idCita)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada con ID: " + idCita));
    }

    // REQ-AGC-01: horarios libres de un doctor para una fecha (franjas de 1 hora)
    @Override
    public List<String> obtenerHorariosDisponibles(Integer idVeterinario, LocalDate fecha) {
        ZoneId zona = ZoneId.systemDefault();
        Date inicioDia = Date.from(fecha.atStartOfDay(zona).toInstant());
        Date finDia = Date.from(fecha.plusDays(1).atStartOfDay(zona).toInstant());
        List<Cita> ocupadas = citaRepository.findByVeterinario_IdVeterinarioAndFechaHoraBetweenAndEstadoNot(
                idVeterinario, inicioDia, finDia, "CANCELADA");

        LocalDateTime ahora = LocalDateTime.now(zona);
        List<String> disponibles = new ArrayList<>();
        for (int hora = HORA_INICIO; hora < HORA_FIN; hora++) {
            LocalDateTime franja = LocalDateTime.of(fecha, LocalTime.of(hora, 0));
            if (franja.isBefore(ahora)) {
                continue;
            }
            long inicio = franja.atZone(zona).toInstant().toEpochMilli();
            boolean libre = ocupadas.stream().noneMatch(c -> seCruzan(c.getFechaHora().getTime(), inicio));
            if (libre) {
                disponibles.add(String.format("%02d:00", hora));
            }
        }
        return disponibles;
    }

    // REQ-AGC-02: impedir dos citas del mismo doctor en el mismo horario
    private void validarCruceAgenda(Integer idVeterinario, Date fechaHora, Integer idCitaActual) {
        long duracion = TimeUnit.MINUTES.toMillis(DURACION_CITA_MIN);
        Date desde = new Date(fechaHora.getTime() - duracion + 1);
        Date hasta = new Date(fechaHora.getTime() + duracion - 1);
        boolean cruce = citaRepository
                .findByVeterinario_IdVeterinarioAndFechaHoraBetweenAndEstadoNot(idVeterinario, desde, hasta, "CANCELADA")
                .stream()
                .anyMatch(c -> idCitaActual == null || !c.getIdCita().equals(idCitaActual));
        if (cruce) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El doctor ya tiene una cita en ese horario. Por favor elige otro horario.");
        }
    }

    private boolean seCruzan(long a, long b) {
        return Math.abs(a - b) < TimeUnit.MINUTES.toMillis(DURACION_CITA_MIN);
    }
}
