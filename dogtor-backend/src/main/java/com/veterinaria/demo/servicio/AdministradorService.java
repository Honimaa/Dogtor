package com.veterinaria.demo.servicio;

import java.util.List;
import com.veterinaria.demo.entidad.Administrador;

public interface AdministradorService{
    List<Administrador> obtenerTodosAdministradors();
    Administrador obtenerAdministradorPorId(Integer id);
    Administrador crearAdministrador(Administrador administrador);
    Administrador editarAdministrador(Integer id, Administrador administrador);
    void eliminarAdministrador(Integer id);
    //Administrador validarAdministrador(String username, String password);
    Administrador buscarAdministradorPorNombreUsuario(String nombreUsuario);
}