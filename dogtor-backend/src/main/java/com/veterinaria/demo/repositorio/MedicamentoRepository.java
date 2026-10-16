package com.veterinaria.demo.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.veterinaria.demo.entidad.Medicamento;

@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Integer> {
    List<Medicamento> findByNombreContainingIgnoreCase(String nombre);

}