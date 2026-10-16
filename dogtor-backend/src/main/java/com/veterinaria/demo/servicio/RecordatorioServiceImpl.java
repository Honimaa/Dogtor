package com.veterinaria.demo.servicio;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.veterinaria.demo.dto.RecordatorioDTO;
import com.veterinaria.demo.entidad.Cita;
import com.veterinaria.demo.entidad.EventoClinico;
import com.veterinaria.demo.entidad.Mascota;
import com.veterinaria.demo.repositorio.CitaRepository;
import com.veterinaria.demo.repositorio.EventoClinicoRepository;

@Service
public class RecordatorioServiceImpl implements RecordatorioService {

    private static final long DIAS_CITAS = 7;
    private static final long DIAS_VACUNAS = 30;

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private EventoClinicoRepository eventoClinicoRepository;

    @Override
    public List<RecordatorioDTO> recordatoriosCliente(Integer idCliente) {
        Date ahora = new Date();
        List<RecordatorioDTO> recordatorios = new ArrayList<>();
        citaRepository.findByMascota_Cliente_IdClienteAndFechaHoraBetweenAndEstado(
                        idCliente, ahora, sumarDias(ahora, DIAS_CITAS), "AGENDADA")
                .forEach(c -> recordatorios.add(desdeCita(c, ahora)));
        eventoClinicoRepository.findByMascota_Cliente_IdClienteAndFechaProximaBetween(
                        idCliente, sumarDias(ahora, -DIAS_VACUNAS), sumarDias(ahora, DIAS_VACUNAS))
                .forEach(e -> recordatorios.add(desdeEvento(e, ahora)));
        recordatorios.sort(Comparator.comparing(RecordatorioDTO::getFecha));
        return recordatorios;
    }

    @Override
    public List<RecordatorioDTO> recordatoriosClinica() {
        Date ahora = new Date();
        List<RecordatorioDTO> recordatorios = new ArrayList<>();
        citaRepository.findByFechaHoraBetweenAndEstado(ahora, sumarDias(ahora, 2), "AGENDADA")
                .forEach(c -> recordatorios.add(desdeCita(c, ahora)));
        eventoClinicoRepository.findByFechaProximaBetween(sumarDias(ahora, -DIAS_VACUNAS), sumarDias(ahora, DIAS_VACUNAS))
                .forEach(e -> recordatorios.add(desdeEvento(e, ahora)));
        recordatorios.sort(Comparator.comparing(RecordatorioDTO::getFecha));
        return recordatorios;
    }

    // REQ-REC-01: recordatorio de cita, urgente si es en las próximas 24 horas
    private RecordatorioDTO desdeCita(Cita cita, Date ahora) {
        Mascota mascota = cita.getMascota();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        boolean en24h = cita.getFechaHora().getTime() - ahora.getTime() <= TimeUnit.HOURS.toMillis(24);
        String servicio = cita.getServicio() != null ? cita.getServicio().getNombre() : "consulta";
        String mensaje = "Cita de " + servicio + " para " + mascota.getNombre() + " el "
                + formato.format(cita.getFechaHora()) + " en la sede " + cita.getSede() + ".";
        return construir("CITA", "Próxima cita", mensaje, cita.getFechaHora(), mascota, en24h);
    }

    // REQ-REC-02 / REQ-REC-03: vacunas y controles periódicos próximos o vencidos
    private RecordatorioDTO desdeEvento(EventoClinico evento, Date ahora) {
        Mascota mascota = evento.getMascota();
        boolean esVacuna = "VACUNA".equals(evento.getTipo());
        boolean vencido = evento.getFechaProxima().before(ahora);
        String tipo = esVacuna ? "VACUNA" : "CONTROL";
        String titulo = esVacuna
                ? (vencido ? "Vacuna vencida" : "Vacuna próxima")
                : (vencido ? "Control pendiente" : "Control próximo");
        String fecha = new SimpleDateFormat("dd/MM/yyyy").format(evento.getFechaProxima());
        String mensaje = evento.getTitulo() + " de " + mascota.getNombre()
                + (vencido ? " estaba programada para el " : " programada para el ") + fecha + ".";
        long dias = TimeUnit.MILLISECONDS.toDays(evento.getFechaProxima().getTime() - ahora.getTime());
        return construir(tipo, titulo, mensaje, evento.getFechaProxima(), mascota, vencido || dias <= 7);
    }

    private RecordatorioDTO construir(String tipo, String titulo, String mensaje, Date fecha, Mascota mascota, boolean urgente) {
        String propietario = mascota.getCliente() != null ? mascota.getCliente().getNombre() : null;
        String correo = mascota.getCliente() != null ? mascota.getCliente().getCorreo() : null;
        return new RecordatorioDTO(tipo, titulo, mensaje, fecha, mascota.getIdMascota(), mascota.getNombre(),
                propietario, correo, urgente);
    }

    private Date sumarDias(Date fecha, long dias) {
        return new Date(fecha.getTime() + TimeUnit.DAYS.toMillis(dias));
    }
}
