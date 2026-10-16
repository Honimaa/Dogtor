package com.veterinaria.demo.servicio;

import com.veterinaria.demo.entidad.Cliente;
import com.veterinaria.demo.entidad.Servicio;
import com.veterinaria.demo.entidad.Testimonio;
import com.veterinaria.demo.repositorio.ClienteRepository;
import com.veterinaria.demo.repositorio.ServicioRepository;
import com.veterinaria.demo.repositorio.TestimonioRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TestimonioServiceImpl implements TestimonioService {
    
    private final TestimonioRepository testimonioRepository;
    private final ClienteRepository clienteRepository;
    private final ServicioRepository servicioRepository;
    
    public TestimonioServiceImpl(TestimonioRepository testimonioRepository,
                                ClienteRepository clienteRepository,
                                ServicioRepository servicioRepository) {
        this.testimonioRepository = testimonioRepository;
        this.clienteRepository = clienteRepository;
        this.servicioRepository = servicioRepository;
    }
    
    @Override
    public List<Testimonio> obtenerTodosTestimonios() {
        return testimonioRepository.findAllWithClientAndService();
    }

    @Override
public Testimonio crearTestimonio(Testimonio testimonio) {

    // Buscar cliente por ID, lanzar excepción si no existe
    Cliente cliente = clienteRepository.findById(testimonio.getCliente().getIdCliente())
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

    // Buscar servicio por ID, lanzar excepción si no existe
    Servicio servicio = servicioRepository.findById(testimonio.getServicio().getIdServicio())
            .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

    // Asignar cliente y servicio existentes al testimonio
    testimonio.setCliente(cliente);
    testimonio.setServicio(servicio);

    // Establecer la fecha actual como fecha del testimonio
    testimonio.setFecha(new Date());

    // Guardar el testimonio en la base de datos
    return testimonioRepository.save(testimonio);
}

}