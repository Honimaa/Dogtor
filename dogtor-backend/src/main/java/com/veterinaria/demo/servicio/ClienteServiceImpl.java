package com.veterinaria.demo.servicio;

import com.veterinaria.demo.entidad.Cliente;
import com.veterinaria.demo.repositorio.ClienteRepository;
import com.veterinaria.demo.repositorio.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.stereotype.Service;


@Service
public class ClienteServiceImpl implements ClienteService{
    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public List<Cliente> obtenerTodosClientes() {
        return clienteRepository.findAll();
    }

    @Override
    public Cliente obtenerClientePorId(Integer id) {
        return clienteRepository.findById(id).orElse(null);
    }

    @Override
    public Cliente crearCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    @Override
    public Cliente editarCliente(Integer id, Cliente clienteActualizado) {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + id));
    
        clienteExistente.setNombre(clienteActualizado.getNombre());
        clienteExistente.setCorreo(clienteActualizado.getCorreo());
        clienteExistente.setCelular(clienteActualizado.getCelular());
        clienteExistente.setFoto(clienteActualizado.getFoto());
        clienteExistente.setCedula(clienteActualizado.getCedula());
        clienteExistente.setNombreUsuario(clienteActualizado.getNombreUsuario());
        clienteExistente.setContrasena(clienteActualizado.getContrasena());
    
        return clienteRepository.save(clienteExistente);
    }
    

    @Override
    public Map<String, String> eliminarCliente(Integer id) {
        Map<String, String> respuesta = new HashMap<>();

        if (!clienteRepository.existsById(id)) {
            respuesta.put("mensaje", "Cliente no encontrado con ID: " + id);
            return respuesta;
        }

        clienteRepository.deleteById(id);
        respuesta.put("mensaje", "Cliente eliminado correctamente");
        return respuesta;
    }


    /*@Override
    public Cliente validarCliente(String username, String password) {
        return clienteRepository.findByNombreUsuarioAndContrasena(username, password);
    }*/

    @Override
    public Integer obtenerIdClientePorNombreUsuario(String nombreUsuario) {
        Cliente cliente = clienteRepository.findByNombreUsuario(nombreUsuario);
        if (cliente != null) {
            return cliente.getIdCliente();
        } else {
            return null;
        }
    }

    @Override
    public List<Cliente> buscarPorNombre(String nombre) {
        return clienteRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Override
    public Cliente buscarPorNombreUsuario(String nombreUsuario) {
        return clienteRepository.findByNombreUsuario(nombreUsuario);
    }
}