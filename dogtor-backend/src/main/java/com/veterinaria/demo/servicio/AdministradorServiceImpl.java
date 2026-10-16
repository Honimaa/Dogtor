package com.veterinaria.demo.servicio;

import com.veterinaria.demo.entidad.Administrador ;
import com.veterinaria.demo.repositorio.AdministradorRepository;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AdministradorServiceImpl implements AdministradorService{
    @Autowired
    AdministradorRepository administradorRepository;

    @Override
    public List<Administrador> obtenerTodosAdministradors() {
        return administradorRepository.findAll();
    }

    @Override
    public Administrador obtenerAdministradorPorId(Integer id) {
        return administradorRepository.findById(id).orElse(null);
    }

    @Override
    public Administrador crearAdministrador(Administrador administrador) {
        return administradorRepository.save(administrador);
    }

    @Override
    public Administrador editarAdministrador(Integer id, Administrador administradorActualizado) {
        Administrador adminExistente = administradorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Administrador no encontrado con ID: " + id));

        adminExistente.setNombre(administradorActualizado.getNombre());
        adminExistente.setCorreo(administradorActualizado.getCorreo());
        adminExistente.setFoto(administradorActualizado.getFoto());
        adminExistente.setCedula(administradorActualizado.getCedula());
        adminExistente.setNombreUsuario(administradorActualizado.getNombreUsuario());
        adminExistente.setContrasena(administradorActualizado.getContrasena());

        return administradorRepository.save(adminExistente);
    }

    @Override
    public void eliminarAdministrador(Integer id) {
        administradorRepository.deleteById(id);
    }

    /*@Override
    public Administrador validarAdministrador(String username, String password) {
        return administradorRepository.findByNombreUsuarioAndContrasena(username, password);
    }*/

    @Override
    public Administrador buscarAdministradorPorNombreUsuario(String nombreUsuario) {
        return administradorRepository.findByNombreUsuario(nombreUsuario);
    }
}