package com.veterinaria.demo.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.veterinaria.demo.entidad.MascotaAdopcion;
import com.veterinaria.demo.entidad.Refugio;
import com.veterinaria.demo.entidad.SolicitudAdopcion;
import com.veterinaria.demo.servicio.AdopcionService;

/**
 * Módulo de adopción de mascotas.
 * Los endpoints públicos (catálogo y solicitudes) no requieren registro; los de /admin requieren rol ADMIN.
 */
@RestController
@RequestMapping("/adopcion")
@CrossOrigin(origins = "http://localhost:4200")
public class AdopcionController {

    @Autowired
    private AdopcionService adopcionService;

    // ---------- Público ----------

    @GetMapping
    public ResponseEntity<List<MascotaAdopcion>> catalogo(@RequestParam(required = false) String especie,
                                                          @RequestParam(required = false) String tamano,
                                                          @RequestParam(required = false) String ciudad,
                                                          @RequestParam(required = false) String edad) {
        return ResponseEntity.ok(adopcionService.catalogo(especie, tamano, ciudad, edad));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MascotaAdopcion> detalle(@PathVariable Integer id) {
        return ResponseEntity.ok(adopcionService.obtenerMascota(id));
    }

    @PostMapping("/{id}/solicitudes")
    public ResponseEntity<SolicitudAdopcion> enviarSolicitud(@PathVariable Integer id,
                                                             @RequestBody SolicitudAdopcion solicitud) {
        return ResponseEntity.ok(adopcionService.enviarSolicitud(id, solicitud));
    }

    @GetMapping("/refugios")
    public ResponseEntity<List<Refugio>> refugiosValidados() {
        return ResponseEntity.ok(adopcionService.obtenerRefugiosValidados());
    }

    // ---------- Administración ----------

    @GetMapping("/admin/mascotas")
    public ResponseEntity<List<MascotaAdopcion>> todasLasMascotas() {
        return ResponseEntity.ok(adopcionService.obtenerTodasMascotas());
    }

    @PostMapping("/admin/mascotas")
    public ResponseEntity<MascotaAdopcion> publicar(@RequestParam Integer idRefugio,
                                                    @RequestBody MascotaAdopcion mascota) {
        return ResponseEntity.ok(adopcionService.publicarMascota(idRefugio, mascota));
    }

    @PutMapping("/admin/mascotas/{id}")
    public ResponseEntity<MascotaAdopcion> actualizar(@PathVariable Integer id,
                                                      @RequestBody MascotaAdopcion mascota) {
        return ResponseEntity.ok(adopcionService.actualizarMascota(id, mascota));
    }

    @GetMapping("/admin/mascotas/{id}/solicitudes")
    public ResponseEntity<List<SolicitudAdopcion>> solicitudesPorMascota(@PathVariable Integer id) {
        return ResponseEntity.ok(adopcionService.obtenerSolicitudesPorMascota(id));
    }

    @GetMapping("/admin/solicitudes")
    public ResponseEntity<List<SolicitudAdopcion>> solicitudes() {
        return ResponseEntity.ok(adopcionService.obtenerSolicitudes());
    }

    @PutMapping("/admin/solicitudes/{id}")
    public ResponseEntity<SolicitudAdopcion> cambiarEstado(@PathVariable Integer id, @RequestParam String estado) {
        return ResponseEntity.ok(adopcionService.cambiarEstadoSolicitud(id, estado));
    }

    @GetMapping("/admin/refugios")
    public ResponseEntity<List<Refugio>> todosLosRefugios() {
        return ResponseEntity.ok(adopcionService.obtenerTodosRefugios());
    }

    @PostMapping("/admin/refugios")
    public ResponseEntity<Refugio> registrarRefugio(@RequestBody Refugio refugio) {
        return ResponseEntity.ok(adopcionService.registrarRefugio(refugio));
    }

    @PutMapping("/admin/refugios/{id}/validacion")
    public ResponseEntity<Refugio> validarRefugio(@PathVariable Integer id, @RequestParam boolean validado) {
        return ResponseEntity.ok(adopcionService.cambiarValidacionRefugio(id, validado));
    }
}
