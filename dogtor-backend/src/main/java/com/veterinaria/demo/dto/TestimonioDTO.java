package com.veterinaria.demo.dto;

import lombok.Data;

@Data
public class TestimonioDTO {
    private Integer idTestimonio;
    private String texto;
    private Integer calificacion;
    private String fecha;
    private String cliente;
    private String servicio;
    private String imagenCliente;
}