package com.veterinaria.demo.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.veterinaria.demo.dto.RecordatorioDTO;
import com.veterinaria.demo.servicio.RecordatorioService;

@RestController
@RequestMapping("/recordatorios")
@CrossOrigin(origins = "http://localhost:4200")
public class RecordatorioController {

    @Autowired
    private RecordatorioService recordatorioService;

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<RecordatorioDTO>> recordatoriosCliente(@PathVariable Integer idCliente) {
        return ResponseEntity.ok(recordatorioService.recordatoriosCliente(idCliente));
    }

    @GetMapping("/clinica")
    public ResponseEntity<List<RecordatorioDTO>> recordatoriosClinica() {
        return ResponseEntity.ok(recordatorioService.recordatoriosClinica());
    }
}
