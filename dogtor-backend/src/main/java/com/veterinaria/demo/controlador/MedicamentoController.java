package com.veterinaria.demo.controlador;

import com.veterinaria.demo.dto.MedicamentoDTO;
import com.veterinaria.demo.dto.MedicamentoMapper;
import com.veterinaria.demo.entidad.Medicamento;
import com.veterinaria.demo.entidad.Tratamiento;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.veterinaria.demo.servicio.MedicamentoService;
import com.veterinaria.demo.servicio.TratamientoService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/medicamento")
@CrossOrigin(origins = "http://localhost:4200")
public class MedicamentoController {

    @Autowired 
    MedicamentoService medicamentoService;

    @Autowired
    TratamientoService tratamientoService;

    @GetMapping
    public ResponseEntity<List<MedicamentoDTO>> mostrarMedicamentos() {
        List<MedicamentoDTO> dtos = medicamentoService.obtenerTodosTratamientos().stream()
            .map(MedicamentoMapper.INSTANCE::convert)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicamentoDTO> detalleMedicamento(@PathVariable Integer id) {
        Medicamento medicamento = medicamentoService.obtenerMedicamentoPorId(id);
        if (medicamento == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(MedicamentoMapper.INSTANCE.convert(medicamento));
    }

    @GetMapping("/por-tratamiento/{idTratamiento}")
    public ResponseEntity<List<MedicamentoDTO>> obtenerMedicamentosPorTratamiento(@PathVariable Integer idTratamiento) {
        Tratamiento tratamiento = tratamientoService.obtenerTratamientoPorId(idTratamiento);

        if (tratamiento == null) {
            return ResponseEntity.notFound().build();
        }

        List<MedicamentoDTO> medicamentos = tratamiento.getTratamientoMedicamentos().stream()
                .map(tm -> MedicamentoMapper.INSTANCE.convert(tm.getMedicamento()))
                .collect(Collectors.toList());
    
        return ResponseEntity.ok(medicamentos);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Medicamento>> buscarPorNombre(@RequestParam String nombre) {
        List<Medicamento> medicamentos = medicamentoService.buscarPorNombre(nombre);
        return ResponseEntity.ok(medicamentos);
    }

    // Dogtor: registrar un producto nuevo en el inventario (solo ADMIN, ver SecurityConfig)
    @PostMapping
    public ResponseEntity<MedicamentoDTO> crearProducto(@RequestBody MedicamentoDTO datos) {
        if (datos.getNombre() == null || datos.getNombre().isBlank()
                || datos.getPrecioVenta() == null || datos.getPrecioVenta() < 0
                || datos.getPrecioCompra() == null || datos.getPrecioCompra() < 0) {
            return ResponseEntity.badRequest().build();
        }
        Medicamento nuevo = Medicamento.builder()
                .nombre(datos.getNombre().trim())
                .precioCompra(datos.getPrecioCompra())
                .precioVenta(datos.getPrecioVenta())
                .unidadesDisponibles(datos.getUnidadesDisponibles() != null ? Math.max(0, datos.getUnidadesDisponibles()) : 0)
                .unidadesVendidas(0)
                .stockMinimo(datos.getStockMinimo() != null ? datos.getStockMinimo() : 5)
                .fechaVencimiento(datos.getFechaVencimiento())
                .lote(datos.getLote())
                .categoria(datos.getCategoria() != null ? datos.getCategoria() : "MEDICAMENTO")
                .build();
        return ResponseEntity.ok(MedicamentoMapper.INSTANCE.convert(medicamentoService.guardarMedicamento(nuevo)));
    }

    // Dogtor: alertas de bajo stock y vencimiento (REQ-INV-01, 02, 07)
    @GetMapping("/alertas")
    public ResponseEntity<Map<String, List<MedicamentoDTO>>> alertasInventario(
            @RequestParam(defaultValue = "60") int dias) {
        Map<String, List<MedicamentoDTO>> respuesta = new LinkedHashMap<>();
        medicamentoService.obtenerAlertasInventario(dias).forEach((clave, lista) ->
                respuesta.put(clave, lista.stream().map(MedicamentoMapper.INSTANCE::convert).collect(Collectors.toList())));
        return ResponseEntity.ok(respuesta);
    }

    // Dogtor: entrada de inventario con lote y vencimiento, y configuración de stock mínimo (REQ-INV-04, 06)
    @PutMapping("/{id}/inventario")
    public ResponseEntity<MedicamentoDTO> actualizarInventario(@PathVariable Integer id,
                                                               @RequestBody MedicamentoDTO datos,
                                                               @RequestParam(defaultValue = "0") int entrada) {
        Medicamento medicamento = medicamentoService.obtenerMedicamentoPorId(id);
        if (medicamento == null) {
            return ResponseEntity.notFound().build();
        }
        if (entrada < 0) {
            return ResponseEntity.badRequest().build();
        }
        if (datos.getNombre() != null && !datos.getNombre().isBlank()) {
            medicamento.setNombre(datos.getNombre().trim());
        }
        if (datos.getPrecioCompra() != null && datos.getPrecioCompra() >= 0) {
            medicamento.setPrecioCompra(datos.getPrecioCompra());
        }
        if (datos.getPrecioVenta() != null && datos.getPrecioVenta() >= 0) {
            medicamento.setPrecioVenta(datos.getPrecioVenta());
        }
        medicamento.setUnidadesDisponibles(medicamento.getUnidadesDisponibles() + entrada);
        if (datos.getStockMinimo() != null) {
            medicamento.setStockMinimo(datos.getStockMinimo());
        }
        if (datos.getFechaVencimiento() != null) {
            medicamento.setFechaVencimiento(datos.getFechaVencimiento());
        }
        if (datos.getLote() != null) {
            medicamento.setLote(datos.getLote());
        }
        if (datos.getCategoria() != null) {
            medicamento.setCategoria(datos.getCategoria());
        }
        return ResponseEntity.ok(MedicamentoMapper.INSTANCE.convert(medicamentoService.guardarMedicamento(medicamento)));
    }
}
