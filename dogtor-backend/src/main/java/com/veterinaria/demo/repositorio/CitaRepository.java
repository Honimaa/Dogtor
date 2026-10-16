package com.veterinaria.demo.repositorio;

import com.veterinaria.demo.entidad.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Integer> {
    
    List<Cita> findByMascota_Cliente_IdCliente(Integer idCliente);
    
    List<Cita> findBySede(String sede);
    
    List<Cita> findByFechaHoraAfter(Date fecha);
    
    List<Cita> findByVeterinario_IdVeterinario(Integer idVeterinario);

    List<Cita> findByVeterinarioIdVeterinario(Integer idVeterinario);

    List<Cita> findByVeterinarioIdVeterinarioAndEstado(Integer idVeterinario, String estado);

    @Query("SELECT c FROM Cita c WHERE c.fechaHora BETWEEN :inicio AND :fin")
    List<Cita> findCitasEntreFechas(Date inicio, Date fin);
    
    // Dogtor: detección de cruces de agenda (REQ-AGC-02)
    List<Cita> findByVeterinario_IdVeterinarioAndFechaHoraBetweenAndEstadoNot(Integer idVeterinario, Date inicio, Date fin, String estado);

    List<Cita> findByMascota_Cliente_IdClienteAndFechaHoraBetweenAndEstado(Integer idCliente, Date inicio, Date fin, String estado);

    List<Cita> findByFechaHoraBetweenAndEstado(Date inicio, Date fin, String estado);

    @Query("SELECT c FROM Cita c WHERE c.sede = :sede AND c.fechaHora BETWEEN :inicio AND :fin")
    List<Cita> findCitasPorSedeYFecha(String sede, Date inicio, Date fin);
}