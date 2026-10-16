package com.veterinaria.demo.controlador;

import com.veterinaria.demo.entidad.Factura;
import com.veterinaria.demo.servicio.FacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("factura")
@CrossOrigin(origins = "http://localhost:4200")
public class FacturaController {
    @Autowired
    private FacturaService facturaService;

    @GetMapping
    public List<Factura> ObtenerTodasFacturas() {
        return facturaService.obtenerTodasFacturas();
    }

    @GetMapping("/{id}")
    public Factura obtenerFacturaPorId(@PathVariable Integer id){
        return facturaService.obtenerFacturaPorID(id);
    }

    @GetMapping("/cliente/{idCliente}")
    public List<Factura> obtenerFacturaPorCliente(@PathVariable Integer idCliente){
        return facturaService.obtenerFacturasPorIdCliente(idCliente);
    }

    @PostMapping("/crear/tratamiento")
    public ResponseEntity<Factura> crearFacturaPorTratamiento(@RequestParam Integer idCliente,
                                                                    @RequestParam Integer idTratamiento,
                                                                    @RequestBody Factura factura){
        Factura guardada = facturaService.crearFacturaPorTratamiento(idCliente, idTratamiento, factura);
        return ResponseEntity.ok(guardada);
    }

    @PostMapping("/crear/tratamientos")
    public ResponseEntity<List<Factura>> crearFacturaPorTratamientos(@RequestParam Integer idCliente,
                                                    @RequestParam List<Integer> idsTratamiento,
                                                    @RequestBody Factura factura){
        List<Factura> guardadas = facturaService.crearFacturaPorTratamientos(idCliente, idsTratamiento, factura);
        return ResponseEntity.ok(guardadas);
    }

    @PostMapping("/crear/servicio")
    public ResponseEntity<Factura> crearFacturaPorServicio(@RequestParam Integer idCliente,
                                                           @RequestParam Integer idServicio,
                                                           @RequestBody Factura factura){
        Factura guardada = facturaService.crearFacturaPorServicio(idCliente, idServicio, factura);
        return ResponseEntity.ok(guardada);
    }

    @PostMapping("/crear/medicamentos")
    public ResponseEntity<Factura> crearFacturaPorMedicamentos(@RequestParam Integer idCliente,
                                                               @RequestParam List<Integer> idMedicamentos,
                                                               @RequestBody Factura factura){
        Factura guardada = facturaService.crearFacturaPorMedicamentos(idCliente, idMedicamentos, factura);
        return ResponseEntity.ok(guardada);
    }

    @PutMapping("/pagar/{id}")
    public ResponseEntity<Factura> pagarFactura(@PathVariable Integer id){
        Factura factura = facturaService.obtenerFacturaPorID(id);
        if(factura == null) return ResponseEntity.notFound().build();

        facturaService.pagarFactura(factura);

        return ResponseEntity.ok(factura);
    }

    @PutMapping("/pagar/facturas")
    public ResponseEntity<String> pagarFacturas(@RequestParam List<Integer> idsFacturas) {
        facturaService.pagarFacturas(idsFacturas);
        return ResponseEntity.ok("Facturas pagadas correctamente");
    }

    @GetMapping("/por-tratamiento/{idTratamiento}")
    public ResponseEntity<Factura> getFacturaPorTratamiento(@PathVariable Integer idTratamiento) {
        Factura factura = facturaService.obtenerFacturaPorTratamiento(idTratamiento);
        if (factura == null) {
            return ResponseEntity.ok(null);
        }
        return ResponseEntity.ok(factura);
    }

}
