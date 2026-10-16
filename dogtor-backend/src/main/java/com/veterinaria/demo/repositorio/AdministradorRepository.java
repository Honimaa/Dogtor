package com.veterinaria.demo.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.veterinaria.demo.entidad.Administrador;

@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, Integer> {
    Administrador findByCedula(String cedula);
    Administrador findByNombreUsuario(String nombreUsuario);
    //Administrador findByNombreUsuarioAndContrasena(String nombreUsuario,String contrasena);
}