package com.veterinaria.demo.servicio;

import java.util.List;

import com.veterinaria.demo.entidad.Cita;
import com.veterinaria.demo.entidad.Tratamiento;
import com.veterinaria.demo.repositorio.CitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.veterinaria.demo.entidad.Mascota;
import com.veterinaria.demo.entidad.Veterinario;
import com.veterinaria.demo.repositorio.TratamientoRepository;
import com.veterinaria.demo.repositorio.VeterinarioRepository;

@Service
public class VeterinarioServiceImpl implements VeterinarioService {
    
    @Autowired
    private VeterinarioRepository veterinarioRepository;
    @Autowired
    private TratamientoRepository tratamientoRepository;
    @Autowired
    private CitaRepository citaRepository;

    @Override
    public List<Veterinario> obtenerTodosVeterinarios() {
        return veterinarioRepository.findAll();
    }

    @Override
    public Veterinario obtenerVeterinarioPorId(Integer id) {
        return veterinarioRepository.findById(id).orElse(null);
    }

    @Override
    public Veterinario crearVeterinario(Veterinario veterinario) {
        return veterinarioRepository.save(veterinario);
    }

    @Override
    public Veterinario editarVeterinario(Integer id, Veterinario veterinario) {
        return veterinarioRepository.findById(id).map(veterinarioActual -> {
            veterinarioActual.setNombre(veterinario.getNombre());
            veterinarioActual.setCedula(veterinario.getCedula());
            veterinarioActual.setEspecialidad(veterinario.getEspecialidad());
            veterinarioActual.setNombreUsuario(veterinario.getNombreUsuario());
            veterinarioActual.setContrasena(veterinario.getContrasena());
            veterinarioActual.setFoto(veterinario.getFoto());
            veterinarioActual.setSede(veterinario.getSede());
            return veterinarioRepository.save(veterinarioActual);
        }).orElse(null);
    }

    @Override
    public void eliminarVeterinario(Integer id) {
        if (veterinarioRepository.existsById(id)) {
            veterinarioRepository.deleteById(id);
        }
    }

    /*@Override
    public Veterinario validarVeterinario(String username, String password) {
        return veterinarioRepository.findByNombreUsuarioAndContrasena(username, password);
    }*/

    @Override 
    public List<Mascota> obtenerMascotasAtendidas(Integer idVeterinario) {
        return tratamientoRepository.findMascotasByVeterinarioId(idVeterinario);
    }

    @Override
    public List<Veterinario> obtenerVeterinariosPorSede(String sede) {
        return veterinarioRepository.findBySedeAndEstado(sede, 1);
    }

    @Override
    public Veterinario obtenerVeterinarioConMenorAtencionesPorSede(String sede) {
        return veterinarioRepository.findTopBySedeAndEstadoOrderByNumeroAtencionesAsc(sede, 1);
    }

    @Override
    public List<Cita> obtenerCitasAgendadas(Integer idVeterinario) {
        return citaRepository.findByVeterinarioIdVeterinarioAndEstado(idVeterinario, "AGENDADA");
    }

    @Override
    public List<Cita> obtenerHistorialCitas(Integer idVeterinario) {
        return citaRepository.findByVeterinarioIdVeterinario(idVeterinario);
    }

    @Override
    public List<Tratamiento> obtenerHistorialTratamientos(Integer idVeterinario, Integer idMascota) {
        return veterinarioRepository.findById(idVeterinario)
                .map(veterinario -> veterinario.getTratamientos().stream()
                        .filter(tratamiento -> tratamiento.getMascota() != null &&
                                tratamiento.getMascota().getIdMascota().equals(idMascota))
                        .toList())
                .orElseThrow(() -> new RuntimeException("Veterinario no encontrado con ID: " + idVeterinario));
    }

    @Override
    public List<Tratamiento> obtenerTodosTratamientosVeterinario(Integer idVeterinario) {
        return veterinarioRepository.findById(idVeterinario)
                .map(Veterinario::getTratamientos)
                .orElseThrow(() -> new RuntimeException("Veterinario no encontrado con ID: " + idVeterinario));
    }
    
    @Override
    public long contarVeterinariosActivos() {
        return veterinarioRepository.countByEstado(1); // 1 = activo
    }

    @Override
    public long contarVeterinariosInactivos() {
        return veterinarioRepository.countByEstado(0); // 0 = inactivo
    }

    @Override
    public List<Veterinario> obtenerVeterinariosActivos() {
        return veterinarioRepository.findByEstado(1);
    }

    @Override
    public List<Veterinario> obtenerVeterinariosInactivos() {
        return veterinarioRepository.findByEstado(0);
    }

    @Override
    public Veterinario guardarVeterinario(Veterinario veterinario){
        return veterinarioRepository.save(veterinario);
    }

    @Override
    public List<Veterinario> buscarPorNombreUsuario(String nombreUsuario) {
        return veterinarioRepository.findByNombreContainingIgnoreCase(nombreUsuario);
    }

    @Override
    public Veterinario obtenerPorNombreUsuario(String nombreUsuario) {
        return veterinarioRepository.findByNombreUsuario(nombreUsuario);
    }

    // REQ-DOC-03 / REQ-DOC-04: doctores activos con atención de urgencias 24 horas
    @Override
    public List<Veterinario> obtenerVeterinariosUrgencias() {
        return veterinarioRepository.findByUrgencias24hTrueAndEstado(1);
    }
}
