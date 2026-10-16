package com.veterinaria.demo.repositorio;

import com.veterinaria.demo.entidad.Factura;
import com.veterinaria.demo.entidad.Tratamiento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Integer> {

    @Query("SELECT f FROM Factura f WHERE f.cliente.idCliente = :idCliente")
    List<Factura> findAllByCliente(@Param("idCliente") Integer idCliente);

    Factura findByTratamiento(Tratamiento tratamiento);
}