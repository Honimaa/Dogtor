package com.veterinaria.demo.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.veterinaria.demo.entidad.SolicitudAdopcion;

@Repository
public interface SolicitudAdopcionRepository extends JpaRepository<SolicitudAdopcion, Integer> {

    List<SolicitudAdopcion> findAllByOrderByFechaDesc();

    List<SolicitudAdopcion> findByMascotaAdopcion_IdMascotaAdopcionOrderByFechaDesc(Integer idMascotaAdopcion);

    List<SolicitudAdopcion> findByMascotaAdopcion_IdMascotaAdopcionAndEstadoIn(Integer idMascotaAdopcion, List<String> estados);

    long countByEstado(String estado);
}
