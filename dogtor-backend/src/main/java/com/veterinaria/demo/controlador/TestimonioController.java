package com.veterinaria.demo.controlador;

import com.veterinaria.demo.dto.TestimonioDTO;
import com.veterinaria.demo.dto.TestimonioMapper;
import com.veterinaria.demo.entidad.Testimonio;
import com.veterinaria.demo.servicio.TestimonioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/testimonios")
@CrossOrigin(origins = "http://localhost:4200")
public class TestimonioController {
    
    private final TestimonioService testimonioService;
    
    public TestimonioController(TestimonioService testimonioService) {
        this.testimonioService = testimonioService;
    }
    
    @GetMapping
    public ResponseEntity<List<TestimonioDTO>> obtenerTodosTestimonios() {
        List<TestimonioDTO> dtos = testimonioService.obtenerTodosTestimonios().stream()
            .map(TestimonioMapper.INSTANCE::convert)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/crear")
    public ResponseEntity<Testimonio> crearTestimonio(@RequestBody Testimonio testimonio) {
        Testimonio testimonioCreado = testimonioService.crearTestimonio(testimonio);
        return ResponseEntity.ok(testimonioCreado);
    }
}