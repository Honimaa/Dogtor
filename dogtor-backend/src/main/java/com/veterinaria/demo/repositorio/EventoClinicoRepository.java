package com.veterinaria.demo.repositorio;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.veterinaria.demo.entidad.EventoClinico;

@Repository
public interface EventoClinicoRepository extends JpaRepository<EventoClinico, Integer> {

    List<EventoClinico> findByMascota_IdMascotaOrderByFechaDescIdEventoDesc(Integer idMascota);

    List<EventoClinico> findByMascota_IdMascotaAndTipoOrderByFechaDescIdEventoDesc(Integer idMascota, String tipo);

    List<EventoClinico> findByMascota_Cliente_IdClienteAndFechaProximaBetween(Integer idCliente, Date inicio, Date fin);

    List<EventoClinico> findByFechaProximaBetween(Date inicio, Date fin);
}
