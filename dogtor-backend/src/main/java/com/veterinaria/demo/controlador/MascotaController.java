package com.veterinaria.demo.controlador;

import java.util.List;

import com.veterinaria.demo.entidad.Tratamiento;
import com.veterinaria.demo.servicio.TratamientoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.veterinaria.demo.entidad.Cliente;
import com.veterinaria.demo.entidad.Mascota;
import com.veterinaria.demo.servicio.ClienteService;
import com.veterinaria.demo.servicio.MascotaService;

@RestController
@RequestMapping("mascota")
@CrossOrigin(origins = "http://localhost:4200")
public class MascotaController {

    @Autowired
    private MascotaService mascotaService;

    @Autowired
    private TratamientoService tratamientoService;

    @Autowired
    private ClienteService clienteService;

    /**
     * Muestra todas las mascotas registradas.
     * URL: http://localhost:8082/mascota
     */
    @GetMapping
    public List<Mascota> mostrarMascotas(Model model) {
        return mascotaService.obtenerTodasMascotas();
    }

    /**
     * Muestra los detalles de una mascota por su ID.
     * URL: http://localhost:8082/mascota/{id}
     */
    @GetMapping("/{id}")
    public Mascota detalleMascota(@PathVariable Integer id, Model model) {
        return mascotaService.obtenerMascotaPorId(id);
    }

    /**
     * Lista las mascotas asociadas a un cliente específico.
     * URL: http://localhost:8082/mascota/mascotas?idCliente=1
     */
    @GetMapping("/mascotas")
    public List<Mascota> listarMascotas(@RequestParam("idCliente") Integer idCliente) {
        return mascotaService.obtenerMascotasPorCliente(idCliente);
    }

    /**
     * Crea una nueva mascota y la asocia a un cliente existente.
     * URL: http://localhost:8082/mascota/agregar?idCliente=123
     * Body: JSON con datos de la mascota.
     */
    @PostMapping("/agregar")
    public ResponseEntity<Mascota> agregarMascota(@RequestBody Mascota mascota, @RequestParam Integer idCliente) {
        Cliente cliente = clienteService.obtenerClientePorId(idCliente);
        if (cliente == null) {
            return ResponseEntity.badRequest().body(null); // Cliente no encontrado
        }

        // Dogtor: un propietario solo puede registrar mascotas en su propia cuenta
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean esCliente = auth != null && auth.getAuthorities().stream().anyMatch(a -> "CLIENTE".equals(a.getAuthority()));
        if (esCliente && !auth.getName().equals(cliente.getNombreUsuario())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (mascota.getEstado() == null) {
            mascota.setEstado(1);
        }
        if (mascota.getFechaIngreso() == null) {
            mascota.setFechaIngreso(new java.util.Date());
        }

        mascota.setCliente(cliente);

        Mascota guardada = mascotaService.guardarMascota(mascota);

        return ResponseEntity.ok(guardada);
    }
    
    @PutMapping("/editar/{id}")
    public ResponseEntity<Mascota> editarMascota(@PathVariable Integer id, @RequestBody Mascota mascotaActualizada) {
        // Verifica si la mascota existe
        Mascota mascotaExistente = mascotaService.obtenerMascotaPorId(id);
        if (mascotaExistente == null) {
            return ResponseEntity.notFound().build();
        }

        mascotaExistente.setNombre(mascotaActualizada.getNombre());
        mascotaExistente.setRaza(mascotaActualizada.getRaza());
        mascotaExistente.setEdad(mascotaActualizada.getEdad());
        mascotaExistente.setPeso(mascotaActualizada.getPeso());
        mascotaExistente.setEstado(mascotaActualizada.getEstado());
        mascotaExistente.setEnfermedad(mascotaActualizada.getEnfermedad());
        mascotaExistente.setFoto(mascotaActualizada.getFoto());
        mascotaExistente.setFechaNacimiento(mascotaActualizada.getFechaNacimiento());
        mascotaExistente.setFechaIngreso(mascotaActualizada.getFechaIngreso());
        mascotaExistente.setFechaSalida(mascotaActualizada.getFechaSalida());

        if (mascotaActualizada.getCliente() != null) {
            Integer idCliente = mascotaActualizada.getCliente().getIdCliente();
            Cliente cliente = clienteService.obtenerClientePorId(idCliente);
            if (cliente == null) {
                return ResponseEntity.badRequest().body(null); // Cliente no encontrado
            }
            mascotaExistente.setCliente(cliente);
        }

        Mascota mascotaGuardada = mascotaService.guardarMascota(mascotaExistente);
        return ResponseEntity.ok(mascotaGuardada);
    }

    @PutMapping("/eliminar/{id}")
    public ResponseEntity<Mascota> eliminarMascota(@PathVariable Integer id) {
        // Verifica si la mascota existe
        Mascota mascota = mascotaService.obtenerMascotaPorId(id);
        if (mascota == null) {
            return ResponseEntity.notFound().build();
        }

        // Cambia el estado a 'inactiva' o el que uses para marcar eliminación lógica
        mascotaService.cambiarEstado(id, mascota);

        // Guarda la mascota con el nuevo estado
        mascotaService.guardarMascota(mascota);

        return ResponseEntity.ok(mascota);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Mascota>> buscarPorNombre(@RequestParam String nombre) {
        List<Mascota> mascotas = mascotaService.buscarPorNombre(nombre);
        return ResponseEntity.ok(mascotas);
    }

    @GetMapping("/mascotas-en-tratamiento/{idCliente}")
    public ResponseEntity<List<Mascota>> obtenerMascotasEnTratamientoPorCliente(@PathVariable Integer idCliente) {
        Cliente cliente = clienteService.obtenerClientePorId(idCliente);
        if (cliente == null) {
            return ResponseEntity.badRequest().body(null); // Cliente no encontrado
        }

        List<Mascota> mascotas = mascotaService.obtenerMascotasPorCliente(cliente.getIdCliente());

        // Filtrar mascotas que tengan al menos un tratamiento
        List<Mascota> mascotasConTratamiento = mascotas.stream()
                .filter(mascota -> !tratamientoService.obtenerTratamientosPorMascota(mascota.getIdMascota()).isEmpty())
                .toList();

        return ResponseEntity.ok(mascotasConTratamiento);
    }

    @GetMapping("/cantidadMascotas")
    public ResponseEntity<Long> cantidadMascotas() {
        Long cantidad = mascotaService.contarMascotas();
        return ResponseEntity.ok(cantidad);
    }
    
    @GetMapping("/cantidadMascotasPorCliente/{idCliente}")
    public ResponseEntity<Integer> cantidadMascotasPorCliente(@PathVariable Integer idCliente) {
        Integer cantidad = mascotaService.countByClienteIdCliente(idCliente);
        return ResponseEntity.ok(cantidad);
    }

    /**
     * Obtiene la cantidad de mascotas activas (estado = 1)
     * URL: GET http://localhost:8082/mascota/cantidadMascotasActivas
     */
    @GetMapping("/cantidadMascotasActivas")
    public ResponseEntity<Long> contarMascotasActivas() {
        Long cantidad = mascotaService.contarMascotasActivas();
        return ResponseEntity.ok(cantidad);
    }

    /**
     * Obtiene la lista de mascotas activas (estado = 1)
     * URL: GET http://localhost:8082/mascota/activas
     */
    @GetMapping("/activas")
    public ResponseEntity<List<Mascota>> obtenerMascotasActivas() {
        List<Mascota> mascotasActivas = mascotaService.obtenerMascotasActivas();
        return ResponseEntity.ok(mascotasActivas);
    }

    @GetMapping("/por-tratamiento/{id}")
    public ResponseEntity<Mascota> obtenerMascotaPorTratamiento(@PathVariable Integer id) {
        Tratamiento tratamiento = tratamientoService.obtenerTratamientoPorId(id);
        Mascota mascota = tratamiento.getMascota();
        if (mascota == null) {
            throw new EntityNotFoundException("No hay mascota asociada a este tratamiento");
        }
        return ResponseEntity.ok(mascota);
    }

    @GetMapping("/mascotas-cliente/{idCliente}")
    public ResponseEntity<List<Mascota>> obtenerMascotasCliente(@PathVariable Integer idCliente) {
        List<Mascota> mascotas = mascotaService.obtenerMascotasPorCliente(idCliente);
        return ResponseEntity.ok(mascotas);
    }
}