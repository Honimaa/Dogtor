package com.veterinaria.demo.servicio;

import com.veterinaria.demo.entidad.Factura;

import java.util.List;

public interface FacturaService{
    Factura crearFacturaPorTratamiento(Integer idCliente, Integer idTratamiento, Factura factura);
    List<Factura> crearFacturaPorTratamientos(Integer idCliente, List<Integer> idsTratamientos, Factura factura);
    Factura crearFacturaPorServicio(Integer idCliente,Integer idServicio, Factura factura);
    Factura crearFacturaPorMedicamentos(Integer idCliente, List<Integer> idMedicamentos, Factura factura);
    Factura obtenerFacturaPorID(Integer id);
    List<Factura> obtenerTodasFacturas();
    Factura guardarFactura(Factura factura);
    List<Factura> obtenerFacturasPorIdCliente(Integer idCliente);
    void pagarFactura(Factura factura);
    void pagarFacturas(List<Integer> idsFacturas);
    Factura obtenerFacturaPorTratamiento(Integer idTratamiento);
}