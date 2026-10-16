package com.veterinaria.demo.controlador;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.veterinaria.demo.entidad.*;
import com.veterinaria.demo.repositorio.UserRepository;
import com.veterinaria.demo.seguridad.CustomUserDetailsService;
import com.veterinaria.demo.seguridad.JWTGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.veterinaria.demo.servicio.VeterinarioService;

@RestController
@RequestMapping("/veterinario")
@CrossOrigin(origins = "http://localhost:4200")
public class VeterinarioController{

    @Autowired
    private VeterinarioService veterinarioService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTGenerator jwtGenerator;

    @PostMapping("/login")
    public ResponseEntity<String> loginVeterinario(@RequestBody Veterinario veterinario) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(veterinario.getNombreUsuario(), veterinario.getContrasena())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtGenerator.generateToken(authentication);

        return new ResponseEntity<String>(token, HttpStatus.OK);
    }

    @GetMapping("/details")
    public ResponseEntity<Veterinario> obtenerDetallesVeterinario() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String nombreUsuario = authentication.getName();

        Veterinario veterinario = veterinarioService.obtenerPorNombreUsuario(nombreUsuario);
        if (veterinario == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(veterinario);
    }

    @GetMapping
    public List<Veterinario> listar(Model model){
        return veterinarioService.obtenerTodosVeterinarios();
    }

    @GetMapping("/{id}")
    public Veterinario detalleVeterinario(@PathVariable Integer id){
        return veterinarioService.obtenerVeterinarioPorId(id);
    }

    @PostMapping("/crear")
    public ResponseEntity<String> crearVeterinario(@RequestBody Veterinario veterinario,
                                           @RequestParam("confirm_password") String confirmPassword) {

        if(userRepository.existsByUsername(veterinario.getNombreUsuario())) {
            return ResponseEntity.badRequest().body("El nombre de usuario ya está registrado");
        }

        if (!veterinario.getContrasena().equals(confirmPassword)) {
            return ResponseEntity.badRequest().body("Las contraseñas no coinciden");
        }

        UserEntity  userEntity = customUserDetailsService.veterinarioToUser(veterinario);
        veterinario.setUser(userEntity);
        veterinarioService.crearVeterinario(veterinario);
        return ResponseEntity.ok("Veterinario creado");
    }


    @PutMapping("/editar/{id}")
    public ResponseEntity<Veterinario> actualizarVeterinario(@PathVariable Integer id, @RequestBody Veterinario veterinarioactualizado) {
        Veterinario veterinarioExistente = veterinarioService.obtenerVeterinarioPorId(id);
        if (veterinarioExistente == null) {
            return ResponseEntity.notFound().build();
        }

        veterinarioExistente.setNombre(veterinarioactualizado.getNombre());
        veterinarioExistente.setCedula(veterinarioactualizado.getCedula());
        veterinarioExistente.setEspecialidad(veterinarioactualizado.getEspecialidad());
        veterinarioExistente.setFoto(veterinarioactualizado.getFoto());
        veterinarioExistente.setEstado(veterinarioactualizado.getEstado());
        veterinarioExistente.setNumeroAtenciones(veterinarioactualizado.getNumeroAtenciones());
        veterinarioExistente.setNombreUsuario(veterinarioactualizado.getNombreUsuario());
        veterinarioExistente.setContrasena(veterinarioactualizado.getContrasena());
        veterinarioExistente.setTarjetaProfesional(veterinarioactualizado.getTarjetaProfesional());
        veterinarioExistente.setHorario(veterinarioactualizado.getHorario());
        veterinarioExistente.setUrgencias24h(Boolean.TRUE.equals(veterinarioactualizado.getUrgencias24h()));
        if (veterinarioactualizado.getSede() != null) {
            veterinarioExistente.setSede(veterinarioactualizado.getSede());
        }

        Veterinario veterinarioGuardado = veterinarioService.guardarVeterinario(veterinarioExistente);
        return ResponseEntity.ok(veterinarioGuardado);

    }

    // Mascotas atendidas
    @GetMapping("/mascotas_atendidas/{idVeterinario}")
    public ResponseEntity<List<Mascota>> obtenerMascotasAtendidas(@PathVariable Integer idVeterinario) {

        if (idVeterinario == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Mascota> mascotasAtendidas = veterinarioService.obtenerMascotasAtendidas(idVeterinario);
        return ResponseEntity.ok(mascotasAtendidas);
    }

    // Citas agendadas del veterinario
    @GetMapping("/citas_agendadas/{idVeterinario}")
    public ResponseEntity<List<Cita>> obtenerCitasAgendadas(@PathVariable Integer idVeterinario) {


        if (idVeterinario == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Cita> citasAgendadas = veterinarioService.obtenerCitasAgendadas(idVeterinario);
        return ResponseEntity.ok(citasAgendadas);
    }

    // Historial de citas del veterinario
    @GetMapping("/historial-citas/{idVeterinario}")
    public ResponseEntity<List<Cita>> obtenerHistorialCitas(@PathVariable Integer idVeterinario) {

        if (idVeterinario == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Cita> historialCitas = veterinarioService.obtenerHistorialCitas(idVeterinario);
        return ResponseEntity.ok(historialCitas);
    }

    // Historial de tratamientos realizados por el veterinario a una mascota específica
    @GetMapping("/historial-tratamientos-Mascota/{idVeterinario}/{idMascota}")
    public ResponseEntity<List<Tratamiento>> obtenerHistorialTratamientos(
            @PathVariable Integer idVeterinario,
            @PathVariable Integer idMascota) {

        if (idVeterinario == null || idMascota == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Tratamiento> historialTratamientos = veterinarioService.obtenerHistorialTratamientos(idVeterinario, idMascota);
        return ResponseEntity.ok(historialTratamientos);
    }

    // Historial de todos los tratamientos realizados por el veterinario
    @GetMapping("/historial-tratamientos/{idVeterinario}")
    public ResponseEntity<List<Tratamiento>> obtenerTodosTratamientosVeterinario(
            @PathVariable Integer idVeterinario) {

        if (idVeterinario == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Tratamiento> tratamientos = veterinarioService.obtenerTodosTratamientosVeterinario(idVeterinario);
        return ResponseEntity.ok(tratamientos);
    }

    @GetMapping("/cantidadVeterinariosActivos")
    public ResponseEntity<Long> contarVeterinariosActivos() {
        Long cantidad = veterinarioService.contarVeterinariosActivos();
        return ResponseEntity.ok(cantidad);
    }

    @GetMapping("/cantidadVeterinariosInactivos")
    public ResponseEntity<Long> contarVeterinariosInactivos() {
        Long cantidad = veterinarioService.contarVeterinariosInactivos();
        return ResponseEntity.ok(cantidad);
    }

    @GetMapping("/activos")
    public ResponseEntity<List<Veterinario>> obtenerVeterinariosActivos() {
        List<Veterinario> veterinarios = veterinarioService.obtenerVeterinariosActivos();
        return ResponseEntity.ok(veterinarios);
    }

    @GetMapping("/inactivos")
    public ResponseEntity<List<Veterinario>> obtenerVeterinariosInactivos() {
        List<Veterinario> veterinarios = veterinarioService.obtenerVeterinariosInactivos();
        return ResponseEntity.ok(veterinarios);
    }

    @PutMapping("/cambiar-estado/{id}")
    public ResponseEntity<String> cambiarEstadoVeterinario(@PathVariable Integer id) {
        Veterinario veterinario = veterinarioService.obtenerVeterinarioPorId(id);
        if (veterinario == null) {
            return ResponseEntity.notFound().build();
        }

        veterinario.setEstado(veterinario.getEstado() == 1 ? 0 : 1);
        veterinarioService.guardarVeterinario(veterinario);

        return ResponseEntity.ok("Estado del veterinario cambiado exitosamente");
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Veterinario>> buscarPorNombre(@RequestParam String nombre) {
        List<Veterinario> veterinarios = veterinarioService.buscarPorNombreUsuario(nombre);
        return ResponseEntity.ok(veterinarios);
    }

    // Dogtor: sedes con atención de urgencias 24 horas disponibles en este momento (REQ-DOC-03, REQ-DOC-04)
    @GetMapping("/urgencias")
    public ResponseEntity<List<Map<String, Object>>> obtenerSedesUrgencias() {
        Map<String, List<Map<String, Object>>> porSede = new LinkedHashMap<>();
        for (Veterinario v : veterinarioService.obtenerVeterinariosUrgencias()) {
            Map<String, Object> doctor = new LinkedHashMap<>();
            doctor.put("idVeterinario", v.getIdVeterinario());
            doctor.put("nombre", v.getNombre());
            doctor.put("especialidad", v.getEspecialidad());
            doctor.put("foto", v.getFoto());
            doctor.put("horario", v.getHorario());
            porSede.computeIfAbsent(v.getSede(), s -> new ArrayList<>()).add(doctor);
        }
        List<Map<String, Object>> sedes = new ArrayList<>();
        porSede.forEach((sede, doctores) -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("sede", sede);
            item.put("disponibleAhora", !doctores.isEmpty());
            item.put("doctores", doctores);
            sedes.add(item);
        });
        return ResponseEntity.ok(sedes);
    }
}
