package com.veterinaria.demo.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.veterinaria.demo.entidad.EventoClinico;
import com.veterinaria.demo.servicio.HistoriaClinicaService;

/**
 * Historia clínica digital por mascota.
 * La creación y eliminación de eventos está restringida a VETERINARIO y ADMIN en SecurityConfig (REQ-HCL-07).
 */
@RestController
@RequestMapping("/historia-clinica")
@CrossOrigin(origins = "http://localhost:4200")
public class HistoriaClinicaController {

    @Autowired
    private HistoriaClinicaService historiaClinicaService;

    @GetMapping("/mascota/{idMascota}")
    public ResponseEntity<List<EventoClinico>> obtenerHistorial(@PathVariable Integer idMascota,
                                                                @RequestParam(required = false) String tipo) {
        return ResponseEntity.ok(historiaClinicaService.obtenerHistorial(idMascota, tipo));
    }

    @GetMapping("/mascota/{idMascota}/alergias")
    public ResponseEntity<List<EventoClinico>> obtenerAlergias(@PathVariable Integer idMascota) {
        return ResponseEntity.ok(historiaClinicaService.obtenerAlergias(idMascota));
    }

    @PostMapping("/mascota/{idMascota}")
    public ResponseEntity<EventoClinico> registrarEvento(@PathVariable Integer idMascota,
                                                         @RequestParam(required = false) Integer idVeterinario,
                                                         @RequestBody EventoClinico evento) {
        return ResponseEntity.ok(historiaClinicaService.registrarEvento(idMascota, idVeterinario, evento));
    }

    @DeleteMapping("/{idEvento}")
    public ResponseEntity<Void> eliminarEvento(@PathVariable Integer idEvento) {
        historiaClinicaService.eliminarEvento(idEvento);
        return ResponseEntity.noContent().build();
    }
}
