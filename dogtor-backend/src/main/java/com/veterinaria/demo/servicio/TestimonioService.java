package com.veterinaria.demo.servicio;

import java.util.List;

import com.veterinaria.demo.entidad.Testimonio;

public interface TestimonioService {
    List<Testimonio> obtenerTodosTestimonios();
    Testimonio crearTestimonio(Testimonio testimonio);
}