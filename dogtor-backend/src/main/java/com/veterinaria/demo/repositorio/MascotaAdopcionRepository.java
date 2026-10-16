package com.veterinaria.demo.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.veterinaria.demo.entidad.MascotaAdopcion;

@Repository
public interface MascotaAdopcionRepository extends JpaRepository<MascotaAdopcion, Integer> {

    List<MascotaAdopcion> findByEstadoInAndRefugio_ValidadoTrueOrderByFechaPublicacionDesc(List<String> estados);

    long countByEstado(String estado);
}
