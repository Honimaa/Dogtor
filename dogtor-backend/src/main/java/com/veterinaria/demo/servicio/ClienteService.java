package com.veterinaria.demo.servicio;

import java.util.List;
import java.util.Map;

import com.veterinaria.demo.entidad.Cliente;

public interface ClienteService{
    List<Cliente> obtenerTodosClientes();
    Cliente obtenerClientePorId(Integer id);
    Cliente crearCliente(Cliente cliente);
    Cliente editarCliente(Integer id, Cliente clienteActualizado);
    Map<String, String> eliminarCliente(Integer id);
    //Cliente validarCliente(String username, String password);
    Integer obtenerIdClientePorNombreUsuario(String nombreUsuario);
    List<Cliente> buscarPorNombre(String nombre);
    Cliente buscarPorNombreUsuario(String nombreUsuario);
}