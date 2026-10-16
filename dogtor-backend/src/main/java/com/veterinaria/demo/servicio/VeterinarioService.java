package com.veterinaria.demo.servicio;

import java.util.List;

import com.veterinaria.demo.entidad.Cita;
import com.veterinaria.demo.entidad.Mascota;
import com.veterinaria.demo.entidad.Tratamiento;
import com.veterinaria.demo.entidad.Veterinario;

public interface VeterinarioService {
    List<Veterinario> obtenerTodosVeterinarios();
    Veterinario obtenerVeterinarioPorId(Integer id);
    Veterinario crearVeterinario(Veterinario veterinario);
    Veterinario editarVeterinario(Integer id, Veterinario veterinario);
    void eliminarVeterinario(Integer id);
    //Veterinario validarVeterinario(String username, String password);
    List<Mascota> obtenerMascotasAtendidas(Integer idVeterinario);
    List<Veterinario> obtenerVeterinariosPorSede(String sede);
    Veterinario obtenerVeterinarioConMenorAtencionesPorSede(String sede);
    List<Cita> obtenerCitasAgendadas(Integer idVeterinario);
    List<Cita> obtenerHistorialCitas(Integer idVeterinario);
    List<Tratamiento> obtenerHistorialTratamientos(Integer idVeterinario,Integer idMascota);
    List<Tratamiento> obtenerTodosTratamientosVeterinario(Integer idVeterinario);
    long contarVeterinariosActivos();
    long contarVeterinariosInactivos();
    List<Veterinario> obtenerVeterinariosActivos();
    List<Veterinario> obtenerVeterinariosInactivos();
    Veterinario guardarVeterinario(Veterinario veterinario);
    List<Veterinario> buscarPorNombreUsuario(String nombreUsuario);
    Veterinario obtenerPorNombreUsuario(String nombreUsuario);
    List<Veterinario> obtenerVeterinariosUrgencias();

}