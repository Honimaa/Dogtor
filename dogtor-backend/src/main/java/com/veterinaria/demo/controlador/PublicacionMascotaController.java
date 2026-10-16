package com.veterinaria.demo.controlador;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.veterinaria.demo.entidad.PublicacionMascota;
import com.veterinaria.demo.repositorio.PublicacionMascotaRepository;

/**
 * "Dogtor Encuentra": publicaciones públicas de mascotas perdidas y encontradas.
 * No requieren cuenta, para que cualquier persona pueda reportar o buscar.
 */
@RestController
@RequestMapping("/publicaciones")
@CrossOrigin(origins = "http://localhost:4200")
public class PublicacionMascotaController {

    private static final Set<String> TIPOS = Set.of("PERDIDA", "ENCONTRADA");

    @Autowired
    private PublicacionMascotaRepository publicacionRepository;

    @GetMapping
    public ResponseEntity<List<PublicacionMascota>> listar(@RequestParam(required = false) String tipo) {
        if (tipo == null || tipo.isBlank()) {
            return ResponseEntity.ok(publicacionRepository.findAllByOrderByResueltaAscFechaPublicacionDesc());
        }
        return ResponseEntity.ok(publicacionRepository.findByTipoOrderByResueltaAscFechaPublicacionDesc(tipo.toUpperCase()));
    }

    @PostMapping
    public ResponseEntity<PublicacionMascota> publicar(@RequestBody PublicacionMascota publicacion) {
        String tipo = publicacion.getTipo() == null ? "" : publicacion.getTipo().toUpperCase();
        if (!TIPOS.contains(tipo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Indica si la mascota está perdida o fue encontrada");
        }
        if (vacio(publicacion.getLugar()) || vacio(publicacion.getContactoNombre()) || vacio(publicacion.getContactoTelefono())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El lugar y los datos de contacto son obligatorios");
        }
        publicacion.setIdPublicacion(null);
        publicacion.setTipo(tipo);
        publicacion.setResuelta(false);
        publicacion.setFechaPublicacion(new Date());
        return ResponseEntity.ok(publicacionRepository.save(publicacion));
    }

    @PutMapping("/{id}/resuelta")
    public ResponseEntity<PublicacionMascota> marcarResuelta(@PathVariable Integer id) {
        PublicacionMascota publicacion = publicacionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Publicación no encontrada"));
        publicacion.setResuelta(true);
        return ResponseEntity.ok(publicacionRepository.save(publicacion));
    }

    private boolean vacio(String valor) {
        return valor == null || valor.isBlank();
    }
}
