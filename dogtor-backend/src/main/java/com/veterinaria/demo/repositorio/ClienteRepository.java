package com.veterinaria.demo.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.veterinaria.demo.entidad.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    Cliente findByCedula(String cedula);
    //Cliente findByNombreUsuarioAndContrasena(String nombreUsuario,String contrasena);
    Cliente findByNombreUsuario(String nombreUsuario);
    List<Cliente> findByNombreContainingIgnoreCase(String nombre);
}