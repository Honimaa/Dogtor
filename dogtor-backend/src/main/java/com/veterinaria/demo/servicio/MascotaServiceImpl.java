package com.veterinaria.demo.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.veterinaria.demo.entidad.Cliente;
import com.veterinaria.demo.entidad.Mascota;
import com.veterinaria.demo.repositorio.ClienteRepository;
import com.veterinaria.demo.repositorio.MascotaRepository;

@Service
public class MascotaServiceImpl implements MascotaService {

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    public MascotaServiceImpl(MascotaRepository mascotaRepository) {
        this.mascotaRepository = mascotaRepository;
    }

    @Override
    public List<Mascota> obtenerTodasMascotas() {
        return mascotaRepository.findAll();
    }

    @Override
    public Mascota obtenerMascotaPorId(Integer id) {
        return mascotaRepository.findById(id).orElse(null);
    }

    @Override
    public Mascota crearMascota(Mascota mascota, Integer idCliente) {
        Cliente cliente = clienteRepository.findById(idCliente).orElse(null);
        mascota.setCliente(cliente);
        return mascotaRepository.save(mascota);
    }

    @Override
    public Mascota actualizarMascota(Integer id, Mascota mascota) {
        Mascota mascotaActual = mascotaRepository.findById(id).orElse(null);
        mascotaActual.setNombre(mascota.getNombre());
        mascotaActual.setRaza(mascota.getRaza());
        mascotaActual.setEdad(mascota.getEdad());
        mascotaActual.setPeso(mascota.getPeso());
        mascotaActual.setEnfermedad(mascota.getEnfermedad());
        mascotaActual.setEstado(mascota.getEstado());
        mascotaActual.setFoto(mascota.getFoto());
        return mascotaRepository.save(mascotaActual);
    }
    @Override
    public void cambiarEstado(Integer id, Mascota mascota) {
        Mascota mascotaActual = mascotaRepository.findById(id).orElse(null);
        if (mascotaActual != null) {
            // Si el estado actual es 1, lo cambiamos a 0
            if (mascotaActual.getEstado().equals(1)) {
                mascotaActual.setEstado(0);
                mascotaActual.setFechaSalida(new java.util.Date());
            }
            // Si el estado actual es 0, lo cambiamos a 1
            else if (mascotaActual.getEstado().equals(0)) {
                mascotaActual.setEstado(1);
                mascotaActual.setFechaSalida(null);
            }
            mascotaRepository.save(mascotaActual);
        }
    }


    @Override
    public void eliminarMascota(Integer id) {
        mascotaRepository.deleteById(id);
    }

    @Override
    public List<Mascota> obtenerMascotasPorCliente(Integer idCliente) {
        return mascotaRepository.findByClienteIdCliente(idCliente);
    }
    
    @Override
    public List<Mascota> buscarPorNombre(String nombre) {
        return mascotaRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Override
    public Integer countByClienteIdCliente(Integer idCliente) {
        return mascotaRepository.countByClienteIdCliente(idCliente);
    }

    @Override
    public long contarTodasLasMascotas() {
        return mascotaRepository.count();
    }

    @Override
    public List<Mascota> obtenerMascotasActivas() {
        return mascotaRepository.findByEstado(1); // 1 representa estado activo
    }

    @Override
    public long contarMascotasActivas() {
        return mascotaRepository.countByEstado(1); // 1 representa estado activo
    }

    @Override
    public Mascota agregarMascotaConCliente(Mascota mascota, Integer idCliente) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + idCliente));

        mascota.setCliente(cliente);

        return mascotaRepository.save(mascota);
    }

    @Override
    public Mascota guardarMascota(Mascota mascota) {
        return mascotaRepository.save(mascota);
    }

    @Override
    public Long contarMascotas() {
        return mascotaRepository.count();
    }
}