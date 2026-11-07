package com.veterinaria.demo.servicio;

import com.veterinaria.demo.entidad.Servicio;
import com.veterinaria.demo.repositorio.ServicioRepository;
import com.veterinaria.demo.repositorio.TratamientoRepository;
import com.veterinaria.demo.repositorio.TratamientoMedicamentoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ServicioServiceImpl implements ServicioService {

    private static final String IMAGEN_POR_DEFECTO = "assets/logo-beagle.png";

    private final ServicioRepository servicioRepository;
    private final TratamientoMedicamentoRepository tratamientoMedicamentoRepository;
    @SuppressWarnings("unused")
    private final TratamientoRepository tratamientoRepository;

    // Constructor modificado para recibir los 3 repositorios
    public ServicioServiceImpl(ServicioRepository servicioRepository,
                             TratamientoRepository tratamientoRepository,
                             TratamientoMedicamentoRepository tratamientoMedicamentoRepository) {
        this.servicioRepository = servicioRepository;
        this.tratamientoRepository = tratamientoRepository;
        this.tratamientoMedicamentoRepository = tratamientoMedicamentoRepository;
    }

    @Override
    public Servicio obtenerServicioPorId(Integer id) {
        return servicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado con ID: " + id));
    }

    @Override
    public List<Servicio> obtenerTodosServicios() {
        return servicioRepository.findAllByOrderByNombreAsc();
    }

    @Override
    public BigDecimal calcularVentasTotales() {
        // Sum of all medication sales
        BigDecimal totalMedicamentos = tratamientoMedicamentoRepository.sumPrecioVentaMedicamentos();
        
        // Sum of all service prices
        BigDecimal totalServicios = servicioRepository.sumPrecioBaseAllServicios();
        
        // Combine both
        return (totalMedicamentos != null ? totalMedicamentos : BigDecimal.ZERO)
                .add(totalServicios != null ? totalServicios : BigDecimal.ZERO);
    }
    
    @Override
    public BigDecimal calcularGananciasTotales() {
        // Sum of medication profits (price - cost)
        BigDecimal gananciaMedicamentos = tratamientoMedicamentoRepository.sumPrecioVentaMedicamentos()
                .subtract(tratamientoMedicamentoRepository.sumPrecioCompraMedicamentos());
        
        // Sum of service prices (assuming full profit for services)
        BigDecimal gananciaServicios = servicioRepository.sumPrecioBaseAllServicios();
        
        return (gananciaMedicamentos != null ? gananciaMedicamentos : BigDecimal.ZERO)
                .add(gananciaServicios != null ? gananciaServicios : BigDecimal.ZERO);
    }

    // Dogtor: el administrador crea y edita servicios desde el listado
    @Override
    public Servicio crearServicio(Servicio datos) {
        Servicio servicio = new Servicio();
        copiarDatos(datos, servicio);
        return servicioRepository.save(servicio);
    }

    @Override
    public Servicio actualizarServicio(Integer id, Servicio datos) {
        Servicio servicio = obtenerServicioPorId(id);
        copiarDatos(datos, servicio);
        return servicioRepository.save(servicio);
    }

    private void copiarDatos(Servicio origen, Servicio destino) {
        if (origen.getNombre() == null || origen.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del servicio es obligatorio.");
        }
        if (origen.getDescripcion() == null || origen.getDescripcion().isBlank()) {
            throw new IllegalArgumentException("La descripción del servicio es obligatoria.");
        }
        if (origen.getPrecioBase() == null || origen.getPrecioBase() < 0) {
            throw new IllegalArgumentException("El precio base debe ser mayor o igual a 0.");
        }
        String imagen = origen.getImagenFrontal() == null || origen.getImagenFrontal().isBlank()
                ? IMAGEN_POR_DEFECTO : origen.getImagenFrontal().trim();
        String imagenTrasera = origen.getImagenTrasera() == null || origen.getImagenTrasera().isBlank()
                ? imagen : origen.getImagenTrasera().trim();
        destino.setNombre(origen.getNombre().trim());
        destino.setDescripcion(origen.getDescripcion().trim());
        destino.setPrecioBase(origen.getPrecioBase());
        destino.setImagenFrontal(imagen);
        destino.setImagenTrasera(imagenTrasera);
    }
}