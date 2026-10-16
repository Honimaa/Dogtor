package com.veterinaria.demo.repositorio;

import java.util.List;

import com.veterinaria.demo.entidad.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.veterinaria.demo.entidad.Mascota;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Integer> {
    List<Mascota> findByClienteIdCliente(Integer idCliente);
    List<Mascota> findByNombreContainingIgnoreCase(String nombre);
    List<Mascota> findByCliente(Cliente cliente);
    Integer countByClienteIdCliente(Integer idCliente);
    long count();
    List<Mascota> findByEstado(Integer estado);
    long countByEstado(Integer estado);
}