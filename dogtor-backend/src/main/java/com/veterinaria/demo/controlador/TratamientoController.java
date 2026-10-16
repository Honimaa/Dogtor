package com.veterinaria.demo.controlador;

import com.veterinaria.demo.entidad.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.veterinaria.demo.servicio.TratamientoService;

import java.util.List;
import java.util.Map;

import com.veterinaria.demo.servicio.MascotaService;

@RestController
@RequestMapping("/tratamiento")
@CrossOrigin(origins = "http://localhost:4200")
public class TratamientoController{
    @Autowired
    private MascotaService mascotaService;

    @Autowired
    private TratamientoService tratamientoService;

    @GetMapping
    public ResponseEntity<List<Tratamiento>> mostrarTratamientos() {
        List<Tratamiento> tratamientos = tratamientoService.obtenerTodosTratamientos();
        return ResponseEntity.ok(tratamientos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tratamiento> detalleTratamiento(@PathVariable Integer id) {
        Tratamiento tratamiento = tratamientoService.obtenerTratamientoPorId(id);
        if (tratamiento == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tratamiento);
    }

    @PostMapping("/crear")
    public ResponseEntity<Tratamiento> crearTratamiento(
            @RequestBody Tratamiento tratamiento,
            @RequestParam Integer idMascota,
            @RequestParam Integer idServicio,
            @RequestParam(required = false) Integer idVeterinario,
            @RequestParam List<Integer> idsMedicamentos) {
    
        Tratamiento nuevoTratamiento = tratamientoService.crearTratamiento(tratamiento, idMascota, idServicio, idVeterinario, idsMedicamentos);
        return ResponseEntity.ok(nuevoTratamiento);
    }

    @PutMapping("/editar/{id}")
    public ResponseEntity<Tratamiento> editarTratamiento(
            @PathVariable Integer id,
            @RequestBody Tratamiento tratamientoActualizado,
            @RequestParam Integer idMascota,
            @RequestParam Integer idServicio,
            @RequestParam(required = false) Integer idVeterinario,
            @RequestParam List<Integer> idsMedicamentos) {
    
        Tratamiento actualizado = tratamientoService.editarTratamiento(id, tratamientoActualizado, idMascota, idServicio, idVeterinario, idsMedicamentos);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminarTratamiento(@PathVariable Integer id) {
        tratamientoService.eliminarTratamientoPorId(id);
        return ResponseEntity.ok("Tratamiento eliminado correctamente");
    }

    @GetMapping("/por-mascota/{idMascota}")
    public ResponseEntity<List<Tratamiento>> obtenerTratamientosPorMascota(@PathVariable Integer idMascota) {
        Mascota mascota = mascotaService.obtenerMascotaPorId(idMascota);
        
        if (mascota == null) {
            return ResponseEntity.notFound().build();
        }
    
        List<Tratamiento> tratamientos = tratamientoService.obtenerTratamientosPorMascota(idMascota);
        return ResponseEntity.ok(tratamientos);
    }
    
    @GetMapping("/cantidad-ultimos-30-dias")
    public ResponseEntity<Long> contarTratamientosUltimos30Dias() {
        Long cantidad = tratamientoService.contarTratamientosUltimos30Dias();
        return ResponseEntity.ok(cantidad);
    }

    @GetMapping("/ultimos-30-dias")
    public ResponseEntity<List<Tratamiento>> obtenerTratamientosUltimos30Dias() {
        List<Tratamiento> tratamientos = tratamientoService.obtenerTratamientosUltimos30Dias();
        return ResponseEntity.ok(tratamientos);
    }

    @GetMapping("/medicamentos-mas-usados")
    public ResponseEntity<Map<String, Integer>> obtenerMedicamentosMasUsados() {
        Map<String, Integer> medicamentos = tratamientoService.obtenerMedicamentosMasUsadosUltimos30Dias();
        return ResponseEntity.ok(medicamentos);
    }

    @GetMapping("/top3-medicamentos-vendidos")
    public ResponseEntity<List<Map<String, Object>>> obtenerTop3MedicamentosMasVendidos() {
        List<Map<String, Object>> medicamentos = tratamientoService.obtenerTop3MedicamentosMasVendidos();
        return ResponseEntity.ok(medicamentos);
    }


    @GetMapping("/veterinario/{idVeterinario}")
    public ResponseEntity<List<Tratamiento>> obtenerTratamientosPorVeterinario(@PathVariable Integer idVeterinario) {
        List<Tratamiento> tratamientos = tratamientoService.obtenerTratamientosPorVeterinario(idVeterinario);
        return ResponseEntity.ok(tratamientos);
    }

    @GetMapping("/mascota/{idMascota}")
    public ResponseEntity<List<Tratamiento>> obtenerTratamientoPorMascota(@PathVariable Integer idMascota) {
        List<Tratamiento> tratamientos = tratamientoService.obtenerTratamientosPorMascota(idMascota);
        return ResponseEntity.ok(tratamientos);
    }
}