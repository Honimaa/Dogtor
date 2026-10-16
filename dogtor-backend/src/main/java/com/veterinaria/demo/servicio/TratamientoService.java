package com.veterinaria.demo.servicio;

import com.veterinaria.demo.entidad.Tratamiento;

import java.util.List;
import java.util.Map;

public interface TratamientoService{
    List<Tratamiento> obtenerTodosTratamientos();
    Tratamiento obtenerTratamientoPorId(Integer id);
    long contarTratamientosUltimos30Dias();
    List<Tratamiento> obtenerTratamientosUltimos30Dias();
    Map<String, Integer> obtenerMedicamentosMasUsadosUltimos30Dias();
    List<Map<String, Object>> obtenerTop3MedicamentosMasVendidos();
    List<Tratamiento> obtenerTratamientosPorVeterinario(Integer idVeterinario);
    Tratamiento crearTratamiento(Tratamiento tratamiento, Integer idMascota, Integer idServicio, Integer idVeterinario, List<Integer> idsMedicamentos);
    Tratamiento editarTratamiento(Integer id, Tratamiento tratamientoActualizado, Integer idMascota, Integer idServicio, Integer idVeterinario, List<Integer> idsMedicamentos);
    void eliminarTratamientoPorId(Integer id);
    List<Tratamiento> obtenerTratamientosPorMascota(Integer idMascota);
    float calcularGananciasTotales();
    float calcularVentasTotales();
}