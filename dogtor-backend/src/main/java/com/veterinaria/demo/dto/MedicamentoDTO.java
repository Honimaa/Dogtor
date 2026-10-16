package com.veterinaria.demo.dto;

import java.util.Date;

import lombok.Data;

@Data
public class MedicamentoDTO {
    private Integer idMedicamento;
    private String nombre;
    private Float precioCompra;
    private Float precioVenta;
    private Integer unidadesDisponibles;
    private Integer unidadesVendidas;
    private Integer stockMinimo;
    private Date fechaVencimiento;
    private String lote;
    private String categoria;
}