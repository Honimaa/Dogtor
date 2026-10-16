package com.veterinaria.demo.servicio;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.veterinaria.demo.entidad.Medicamento;
import com.veterinaria.demo.repositorio.MedicamentoRepository;

@Service
public class MedicamentoServiceImpl implements MedicamentoService {
    @Autowired
    MedicamentoRepository medicamentoRepository;

    @Override
    public List<Medicamento> obtenerTodosTratamientos() {
        return medicamentoRepository.findAll();
    }

    @Override
    public Medicamento obtenerMedicamentoPorId(Integer id) {
        return medicamentoRepository.findById(id).orElse(null);
    }

      @Override
    public List<Medicamento> buscarPorNombre(String nombre) {
        return medicamentoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    // REQ-INV-01 / REQ-INV-02 / REQ-INV-07: bajo stock, próximos a vencer y vencidos
    @Override
    public Map<String, List<Medicamento>> obtenerAlertasInventario(int diasVencimiento) {
        List<Medicamento> medicamentos = medicamentoRepository.findAll();
        Date hoy = new Date();
        Date limite = new Date(hoy.getTime() + TimeUnit.DAYS.toMillis(diasVencimiento));

        Map<String, List<Medicamento>> alertas = new LinkedHashMap<>();
        alertas.put("bajoStock", medicamentos.stream()
                .filter(m -> m.getStockMinimo() != null && m.getUnidadesDisponibles() < m.getStockMinimo())
                .toList());
        alertas.put("porVencer", medicamentos.stream()
                .filter(m -> m.getFechaVencimiento() != null
                        && !m.getFechaVencimiento().before(hoy) && !m.getFechaVencimiento().after(limite))
                .toList());
        alertas.put("vencidos", medicamentos.stream()
                .filter(m -> m.getFechaVencimiento() != null && m.getFechaVencimiento().before(hoy))
                .toList());
        return alertas;
    }

    @Override
    public Medicamento guardarMedicamento(Medicamento medicamento) {
        return medicamentoRepository.save(medicamento);
    }
}
