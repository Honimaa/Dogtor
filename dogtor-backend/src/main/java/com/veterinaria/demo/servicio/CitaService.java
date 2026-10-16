package com.veterinaria.demo.servicio;

import com.veterinaria.demo.entidad.Cita;
import java.time.LocalDate;
import java.util.List;

public interface CitaService {
    Cita crearCita(Cita cita);
    List<Cita> obtenerTodasCitas();
    List<Cita> obtenerCitasPorCliente(Integer idCliente);
    List<Cita> obtenerCitasPorSede(String sede);
    boolean cancelarCita(Integer idCita);
    List<Cita> obtenerCitasPendientes();
    List<Cita> obtenerCitasPorVeterinario(Integer idVeterinario);
    Cita actualizarCita(Cita cita);
    Cita obtenerCitaPorId(Integer idCita);
    List<String> obtenerHorariosDisponibles(Integer idVeterinario, LocalDate fecha);
}