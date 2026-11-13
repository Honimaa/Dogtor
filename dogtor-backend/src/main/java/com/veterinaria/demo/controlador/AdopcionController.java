package com.veterinaria.demo.controlador;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.veterinaria.demo.entidad.MascotaAdopcion;
import com.veterinaria.demo.entidad.Refugio;
import com.veterinaria.demo.entidad.SolicitudAdopcion;
import com.veterinaria.demo.servicio.AdopcionService;

/**
 * Módulo de adopción de mascotas.
 * Los endpoints públicos (catálogo y solicitudes) no requieren registro; los de /admin requieren rol ADMIN
 * y los de /refugio requieren rol REFUGIO (cada refugio gestiona solo sus mascotas y solicitudes).
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

    // ---------- Panel del refugio (rol REFUGIO) ----------
    // El refugio se toma de la sesión, nunca de la URL: así cada refugio solo ve y modifica lo suyo.

    @GetMapping("/refugio/perfil")
    public ResponseEntity<Refugio> miRefugio(Authentication auth) {
        Refugio refugio = adopcionService.refugioDeUsuario(auth.getName());
        refugio.setNombreUsuario(auth.getName());
        return ResponseEntity.ok(refugio);
    }

    @PutMapping("/refugio/perfil")
    public ResponseEntity<Refugio> actualizarMiRefugio(Authentication auth, @RequestBody Refugio datos) {
        Refugio refugio = adopcionService.refugioDeUsuario(auth.getName());
        Refugio actualizado = adopcionService.actualizarPerfilRefugio(refugio.getIdRefugio(), datos);
        actualizado.setNombreUsuario(auth.getName());
        return ResponseEntity.ok(actualizado);
    }

    @PutMapping("/refugio/contrasena")
    public ResponseEntity<Map<String, String>> cambiarMiContrasena(Authentication auth, @RequestBody Map<String, String> datos) {
        adopcionService.refugioDeUsuario(auth.getName());
        adopcionService.cambiarContrasenaRefugio(auth.getName(), datos.get("contrasenaActual"), datos.get("contrasenaNueva"));
        return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada"));
    }

    @GetMapping("/refugio/mascotas")
    public ResponseEntity<List<MascotaAdopcion>> misMascotas(Authentication auth) {
        Refugio refugio = adopcionService.refugioDeUsuario(auth.getName());
        return ResponseEntity.ok(adopcionService.mascotasDeRefugio(refugio.getIdRefugio()));
    }

    @PostMapping("/refugio/mascotas")
    public ResponseEntity<MascotaAdopcion> publicarComoRefugio(Authentication auth, @RequestBody MascotaAdopcion mascota) {
        Refugio refugio = adopcionService.refugioDeUsuario(auth.getName());
        return ResponseEntity.ok(adopcionService.publicarMascota(refugio.getIdRefugio(), mascota));
    }

    @PutMapping("/refugio/mascotas/{id}")
    public ResponseEntity<MascotaAdopcion> actualizarComoRefugio(Authentication auth, @PathVariable Integer id,
                                                                 @RequestBody MascotaAdopcion mascota) {
        Refugio refugio = adopcionService.refugioDeUsuario(auth.getName());
        return ResponseEntity.ok(adopcionService.actualizarMascotaDeRefugio(refugio.getIdRefugio(), id, mascota));
    }

    @GetMapping("/refugio/solicitudes")
    public ResponseEntity<List<SolicitudAdopcion>> misSolicitudes(Authentication auth) {
        Refugio refugio = adopcionService.refugioDeUsuario(auth.getName());
        return ResponseEntity.ok(adopcionService.solicitudesDeRefugio(refugio.getIdRefugio()));
    }

    @PutMapping("/refugio/solicitudes/{id}")
    public ResponseEntity<SolicitudAdopcion> cambiarEstadoComoRefugio(Authentication auth, @PathVariable Integer id,
                                                                      @RequestParam String estado) {
        Refugio refugio = adopcionService.refugioDeUsuario(auth.getName());
        return ResponseEntity.ok(adopcionService.cambiarEstadoSolicitudDeRefugio(refugio.getIdRefugio(), id, estado));
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

    /** Crea la cuenta de un refugio que no tiene, o le pone una contraseña nueva si ya la tiene. */
    @PutMapping("/admin/refugios/{id}/cuenta")
    public ResponseEntity<Refugio> asignarCuenta(@PathVariable Integer id, @RequestBody Map<String, String> datos) {
        return ResponseEntity.ok(adopcionService.asignarCuentaRefugio(id, datos.get("nombreUsuario"), datos.get("contrasena")));
    }

    @PutMapping("/admin/refugios/{id}/validacion")
    public ResponseEntity<Refugio> validarRefugio(@PathVariable Integer id, @RequestParam boolean validado) {
        return ResponseEntity.ok(adopcionService.cambiarValidacionRefugio(id, validado));
    }
}
