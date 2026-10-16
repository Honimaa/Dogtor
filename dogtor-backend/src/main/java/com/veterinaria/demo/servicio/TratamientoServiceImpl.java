package com.veterinaria.demo.servicio;

import com.veterinaria.demo.entidad.Mascota;
import com.veterinaria.demo.entidad.Medicamento;
import com.veterinaria.demo.entidad.Servicio;
import com.veterinaria.demo.entidad.Tratamiento;
import com.veterinaria.demo.entidad.TratamientoMedicamento;
import com.veterinaria.demo.entidad.Veterinario;
import com.veterinaria.demo.repositorio.MascotaRepository;
import com.veterinaria.demo.repositorio.MedicamentoRepository;
import com.veterinaria.demo.repositorio.ServicioRepository;
import com.veterinaria.demo.repositorio.TratamientoRepository;
import com.veterinaria.demo.repositorio.VeterinarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class TratamientoServiceImpl implements TratamientoService{

    @Autowired
    private TratamientoRepository tratamientoRepository;
    @Autowired
    private MascotaRepository mascotaRepository;
    @Autowired
    private ServicioRepository servicioRepository;
    @Autowired
    private MedicamentoRepository medicamentoRepository;
    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @Override
    public List<Tratamiento> obtenerTodosTratamientos() {
        return tratamientoRepository.findAll();
    }

    @Override
    public Tratamiento obtenerTratamientoPorId(Integer id) {
        return tratamientoRepository.findById(id).orElse(null);
    }

    @Override
    public long contarTratamientosUltimos30Dias() {
        Calendar calendar = Calendar.getInstance();
        Date fechaFin = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, -30);
        Date fechaInicio = calendar.getTime();
        
        return tratamientoRepository.countByFechaBetween(fechaInicio, fechaFin);
    }

    @Override
    public List<Tratamiento> obtenerTratamientosUltimos30Dias() {
        Calendar calendar = Calendar.getInstance();
        Date fechaFin = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, -30);
        Date fechaInicio = calendar.getTime();
        
        return tratamientoRepository.findByFechaBetween(fechaInicio, fechaFin);
    }

    @Override
    public Map<String, Integer> obtenerMedicamentosMasUsadosUltimos30Dias() {
        Calendar calendar = Calendar.getInstance();
        Date fechaFin = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, -30);
        Date fechaInicio = calendar.getTime();
        
        List<Object[]> resultados = tratamientoRepository.findMedicamentosMasUsados(fechaInicio, fechaFin);
        
        Map<String, Integer> medicamentosMap = new LinkedHashMap<>();
        for (Object[] resultado : resultados) {
            medicamentosMap.put((String) resultado[0], ((Number) resultado[1]).intValue());
        }
        
        return medicamentosMap;
    }

    @Override
    public List<Map<String, Object>> obtenerTop3MedicamentosMasVendidos() {
        return tratamientoRepository.findTop3MedicamentosMasVendidos();
    }

    @Override
    public List<Tratamiento> obtenerTratamientosPorVeterinario(Integer idVeterinario) {
        return tratamientoRepository.findByVeterinarioId(idVeterinario);
    }

    @Override
    public Tratamiento crearTratamiento(Tratamiento tratamiento, Integer idMascota, Integer idServicio, Integer idVeterinario, List<Integer> idsMedicamentos) {

        Mascota mascota = mascotaRepository.findById(idMascota)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));
        Servicio servicio = servicioRepository.findById(idServicio)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

        Veterinario veterinario = null;
        if (idVeterinario != null) {
            veterinario = veterinarioRepository.findById(idVeterinario)
                    .orElseThrow(() -> new RuntimeException("Veterinario no encontrado"));
        }

        List<Medicamento> medicamentos = medicamentoRepository.findAllById(idsMedicamentos);
        for (Medicamento medicamento : medicamentos) {
            if (medicamento.getUnidadesDisponibles() <= 0) {
                throw new RuntimeException("No hay unidades disponibles del medicamento: " + medicamento.getNombre());
            }
        }

        tratamiento.setMascota(mascota);
        tratamiento.setServicio(servicio);
        tratamiento.setVeterinario(veterinario);

        List<TratamientoMedicamento> tratamientoMedicamentos = new ArrayList<>();
        for (Medicamento medicamento : medicamentos) {
            medicamento.setUnidadesDisponibles(medicamento.getUnidadesDisponibles() - 1);
            medicamento.setUnidadesVendidas(medicamento.getUnidadesVendidas() + 1);
            medicamentoRepository.save(medicamento);

            TratamientoMedicamento tm = new TratamientoMedicamento();
            tm.setTratamiento(tratamiento);
            tm.setMedicamento(medicamento);
            tm.setCantidad(1);
            tratamientoMedicamentos.add(tm);
        }

        tratamiento.setTratamientoMedicamentos(tratamientoMedicamentos);

        return tratamientoRepository.save(tratamiento);
    }

    @Override
    public Tratamiento editarTratamiento(Integer id, Tratamiento tratamientoActualizado, Integer idMascota, Integer idServicio, Integer idVeterinario, List<Integer> idsMedicamentos) {

        Tratamiento tratamientoExistente = tratamientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tratamiento no encontrado"));

        tratamientoExistente.setCodigo(tratamientoActualizado.getCodigo());
        tratamientoExistente.setFecha(tratamientoActualizado.getFecha());
        tratamientoExistente.setDetalles(tratamientoActualizado.getDetalles());

        Mascota mascota = mascotaRepository.findById(idMascota)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));
        tratamientoExistente.setMascota(mascota);

        Servicio servicio = servicioRepository.findById(idServicio)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
        tratamientoExistente.setServicio(servicio);

        if (idVeterinario != null) {
            Veterinario veterinario = veterinarioRepository.findById(idVeterinario)
                    .orElseThrow(() -> new RuntimeException("Veterinario no encontrado"));
            tratamientoExistente.setVeterinario(veterinario);
        } else {
            tratamientoExistente.setVeterinario(null);
        }

        tratamientoExistente.getTratamientoMedicamentos().clear();

        List<TratamientoMedicamento> nuevosTM = new ArrayList<>();
        List<Medicamento> medicamentos = medicamentoRepository.findAllById(idsMedicamentos);

        for (Medicamento medicamento : medicamentos) {
            TratamientoMedicamento tm = new TratamientoMedicamento();
            tm.setTratamiento(tratamientoExistente);
            tm.setMedicamento(medicamento);
            tm.setCantidad(1);
            nuevosTM.add(tm);
        }

        tratamientoExistente.getTratamientoMedicamentos().addAll(nuevosTM);

        return tratamientoRepository.save(tratamientoExistente);
    }

    @Override
    public void eliminarTratamientoPorId(Integer id) {
        Tratamiento tratamiento = tratamientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tratamiento no encontrado"));

        tratamientoRepository.delete(tratamiento);
    }

    @Override
    public List<Tratamiento> obtenerTratamientosPorMascota(Integer idMascota) {
        Mascota mascota = mascotaRepository.findById(idMascota)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));

        return tratamientoRepository.findByMascota(mascota);
    }

    @Override
    public float calcularGananciasTotales() {
        List<Tratamiento> tratamientos = tratamientoRepository.findAll();
        float gananciaTotal = 0.0f;

        for (Tratamiento tratamiento : tratamientos) {
            // Ganancia del servicio asociado
            Servicio servicio = tratamiento.getServicio();
            if (servicio != null) {
                gananciaTotal += servicio.getPrecioBase();
            }

            // Ganancia de los medicamentos usados en el tratamiento
            List<TratamientoMedicamento> tratamientoMedicamentos = tratamiento.getTratamientoMedicamentos();
            if (tratamientoMedicamentos != null) {
                for (TratamientoMedicamento tm : tratamientoMedicamentos) {
                    Medicamento medicamento = tm.getMedicamento();
                    if (medicamento != null) {
                        float gananciaPorUnidad = medicamento.getPrecioVenta() - medicamento.getPrecioCompra();
                        gananciaTotal += gananciaPorUnidad * tm.getCantidad();
                    }
                }
            }
        }

        return gananciaTotal;
    }


    @Override
    public float calcularVentasTotales() {
        List<Tratamiento> tratamientos = tratamientoRepository.findAll();
        float gananciaTotal = 0.0f;

        for (Tratamiento tratamiento : tratamientos) {
            // Ganancia del servicio asociado
            Servicio servicio = tratamiento.getServicio();
            if (servicio != null) {
                gananciaTotal += servicio.getPrecioBase();
            }

            // Ganancia de los medicamentos usados en el tratamiento
            List<TratamientoMedicamento> tratamientoMedicamentos = tratamiento.getTratamientoMedicamentos();
            if (tratamientoMedicamentos != null) {
                for (TratamientoMedicamento tm : tratamientoMedicamentos) {
                    Medicamento medicamento = tm.getMedicamento();
                    if (medicamento != null) {
                        float gananciaPorUnidad = medicamento.getPrecioVenta();
                        gananciaTotal += gananciaPorUnidad * tm.getCantidad();
                    }
                }
            }
        }

        return gananciaTotal;
    }
}