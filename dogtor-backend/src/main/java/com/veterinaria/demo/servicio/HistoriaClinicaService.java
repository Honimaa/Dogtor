package com.veterinaria.demo.servicio;

import java.util.List;

import com.veterinaria.demo.entidad.EventoClinico;

public interface HistoriaClinicaService {
    List<EventoClinico> obtenerHistorial(Integer idMascota, String tipo);
    List<EventoClinico> obtenerAlergias(Integer idMascota);
    EventoClinico registrarEvento(Integer idMascota, Integer idVeterinario, EventoClinico evento);
    void eliminarEvento(Integer idEvento);
}
