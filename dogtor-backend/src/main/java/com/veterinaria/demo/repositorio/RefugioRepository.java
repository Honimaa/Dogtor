package com.veterinaria.demo.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.veterinaria.demo.entidad.Refugio;

@Repository
public interface RefugioRepository extends JpaRepository<Refugio, Integer> {

    List<Refugio> findByValidadoTrue();
}
