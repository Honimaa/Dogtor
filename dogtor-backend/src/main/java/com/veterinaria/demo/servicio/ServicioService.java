package com.veterinaria.demo.servicio; 

import com.veterinaria.demo.entidad.Servicio;

import java.math.BigDecimal;
import java.util.List;

public interface ServicioService {
    List<Servicio> obtenerTodosServicios();
    Servicio obtenerServicioPorId(Integer id);
    BigDecimal calcularVentasTotales();
    BigDecimal calcularGananciasTotales();
    Servicio crearServicio(Servicio datos);
    Servicio actualizarServicio(Integer id, Servicio datos);
}