package com.veterinaria.demo.repositorio;

import com.veterinaria.demo.entidad.Testimonio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestimonioRepository extends JpaRepository<Testimonio, Integer> {
    @Query("SELECT t FROM Testimonio t LEFT JOIN FETCH t.cliente c LEFT JOIN FETCH t.servicio")
    List<Testimonio> findAllWithClientAndService();
}