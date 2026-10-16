package com.veterinaria.demo.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.veterinaria.demo.entidad.Veterinario;

@Repository
public interface VeterinarioRepository extends JpaRepository<Veterinario, Integer> {
    Veterinario findByCedula(String cedula);
    //Veterinario findByNombreUsuarioAndContrasena(String nombreUsuario,String contrasena);
    List<Veterinario> findBySedeAndEstado(String sede, Integer estado);
    Veterinario findTopBySedeAndEstadoOrderByNumeroAtencionesAsc(String sede, Integer estado);
    long countByEstado(Integer estado);
    List<Veterinario> findByEstado(Integer estado);
    Veterinario findByIdVeterinario(Integer idVeterinario);
    Veterinario findByNombreUsuario(String nombreusuario);
    List<Veterinario> findByNombreContainingIgnoreCase(String nombre);
    List<Veterinario> findByUrgencias24hTrueAndEstado(Integer estado);
}