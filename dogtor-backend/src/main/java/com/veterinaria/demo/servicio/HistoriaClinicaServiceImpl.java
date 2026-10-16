package com.veterinaria.demo.servicio;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.veterinaria.demo.entidad.EventoClinico;
import com.veterinaria.demo.entidad.Mascota;
import com.veterinaria.demo.entidad.Veterinario;
import com.veterinaria.demo.repositorio.EventoClinicoRepository;
import com.veterinaria.demo.repositorio.MascotaRepository;
import com.veterinaria.demo.repositorio.VeterinarioRepository;

@Service
public class HistoriaClinicaServiceImpl implements HistoriaClinicaService {

    public static final Set<String> TIPOS = Set.of("VACUNA", "DIAGNOSTICO", "TRATAMIENTO", "CIRUGIA", "ALERGIA");

    @Autowired
    private EventoClinicoRepository eventoClinicoRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    // REQ-HCL-04: historial ordenado cronológicamente, filtrable por tipo
    @Override
    public List<EventoClinico> obtenerHistorial(Integer idMascota, String tipo) {
        if (tipo == null || tipo.isBlank()) {
            return eventoClinicoRepository.findByMascota_IdMascotaOrderByFechaDescIdEventoDesc(idMascota);
        }
        return eventoClinicoRepository.findByMascota_IdMascotaAndTipoOrderByFechaDescIdEventoDesc(idMascota, tipo.toUpperCase());
    }

    // REQ-HCL-06: alergias registradas para mostrar la alerta al doctor
    @Override
    public List<EventoClinico> obtenerAlergias(Integer idMascota) {
        return eventoClinicoRepository.findByMascota_IdMascotaAndTipoOrderByFechaDescIdEventoDesc(idMascota, "ALERGIA");
    }

    @Override
    public EventoClinico registrarEvento(Integer idMascota, Integer idVeterinario, EventoClinico evento) {
        // REQ-HCL-03: la mascota y su propietario deben estar informados
        Mascota mascota = mascotaRepository.findById(idMascota)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mascota no encontrada"));
        if (mascota.getCliente() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La mascota no tiene un propietario asociado");
        }
        if (evento.getTipo() == null || !TIPOS.contains(evento.getTipo().toUpperCase())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de evento clínico inválido");
        }
        if (evento.getTitulo() == null || evento.getTitulo().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El título del evento es obligatorio");
        }

        // REQ-HCL-02: fecha y doctor responsable
        Veterinario veterinario = null;
        if (idVeterinario != null) {
            veterinario = veterinarioRepository.findById(idVeterinario)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veterinario no encontrado"));
        }

        evento.setIdEvento(null);
        evento.setTipo(evento.getTipo().toUpperCase());
        evento.setMascota(mascota);
        evento.setVeterinario(veterinario);
        if (evento.getFecha() == null) {
            evento.setFecha(new Date());
        }
        evento.setFechaRegistro(new Date());
        return eventoClinicoRepository.save(evento);
    }

    @Override
    public void eliminarEvento(Integer idEvento) {
        if (!eventoClinicoRepository.existsById(idEvento)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento clínico no encontrado");
        }
        eventoClinicoRepository.deleteById(idEvento);
    }
}
