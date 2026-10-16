package com.veterinaria.demo.servicio;

import java.util.List;
import java.util.Map;

import com.veterinaria.demo.entidad.Medicamento;

public interface MedicamentoService{
    List<Medicamento> obtenerTodosTratamientos();
    Medicamento obtenerMedicamentoPorId(Integer id);
    List<Medicamento> buscarPorNombre(String nombre);
    Map<String, List<Medicamento>> obtenerAlertasInventario(int diasVencimiento);
    Medicamento guardarMedicamento(Medicamento medicamento);

}