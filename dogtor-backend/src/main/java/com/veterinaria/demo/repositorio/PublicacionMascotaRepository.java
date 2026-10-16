package com.veterinaria.demo.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.veterinaria.demo.entidad.PublicacionMascota;

@Repository
public interface PublicacionMascotaRepository extends JpaRepository<PublicacionMascota, Integer> {

    List<PublicacionMascota> findAllByOrderByResueltaAscFechaPublicacionDesc();

    List<PublicacionMascota> findByTipoOrderByResueltaAscFechaPublicacionDesc(String tipo);
}
