package com.veterinaria.demo.controlador;

import com.veterinaria.demo.entidad.Cita;
import com.veterinaria.demo.servicio.CitaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

@RestController
@RequestMapping("/cita")
@CrossOrigin(origins = "http://localhost:4200")
public class CitaController {
    
    private final CitaService citaService;
    
    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    @GetMapping
    public List<Cita> obtenerTodasMascotas() {
        return citaService.obtenerTodasCitas();
    }
    
    @PostMapping("/crear")
    public ResponseEntity<Cita> crearCita(@RequestBody Cita cita) {
        Cita nuevaCita = citaService.crearCita(cita);
        return ResponseEntity.ok(nuevaCita);
    }
    
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<Cita>> obtenerCitasPorCliente(@PathVariable Integer idCliente) {
        List<Cita> citas = citaService.obtenerCitasPorCliente(idCliente);
        return ResponseEntity.ok(citas);
    }
    
    @GetMapping("/sede/{sede}")
    public ResponseEntity<List<Cita>> obtenerCitasPorSede(@PathVariable String sede) {
        List<Cita> citas = citaService.obtenerCitasPorSede(sede);
        return ResponseEntity.ok(citas);
    }

    @PutMapping("cancelar-cita/{idCita}")
    public ResponseEntity<Void> cancelarCita(@PathVariable Integer idCita) {
        boolean cancelada = citaService.cancelarCita(idCita);
        if (!cancelada) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/pendientes")
    public ResponseEntity<List<Cita>> obtenerCitasPendientes() {
        List<Cita> citas = citaService.obtenerCitasPendientes();
        return ResponseEntity.ok(citas);
    }
    
    @GetMapping("/veterinario/{idVeterinario}")
    public ResponseEntity<List<Cita>> obtenerCitasPorVeterinario(@PathVariable Integer idVeterinario) {
        List<Cita> citas = citaService.obtenerCitasPorVeterinario(idVeterinario);
        return ResponseEntity.ok(citas);
    }
    
    @PutMapping("/actualizar/{idCita}")
    public ResponseEntity<Cita> actualizarCita(@RequestBody Cita cita, @PathVariable Integer idCita) {
        Cita citaActualizada = citaService.actualizarCita(cita);
        return ResponseEntity.ok(citaActualizada);
    }
    
    @GetMapping("/{idCita}")
    public ResponseEntity<Cita> obtenerCitaPorId(@PathVariable Integer idCita) {
        Cita cita = citaService.obtenerCitaPorId(idCita);
        return ResponseEntity.ok(cita);
    }

    // Dogtor: horarios disponibles de un doctor en una fecha (REQ-AGC-01)
    @GetMapping("/disponibilidad/{idVeterinario}")
    public ResponseEntity<List<String>> obtenerDisponibilidad(@PathVariable Integer idVeterinario,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(citaService.obtenerHorariosDisponibles(idVeterinario, fecha));
    }
}
