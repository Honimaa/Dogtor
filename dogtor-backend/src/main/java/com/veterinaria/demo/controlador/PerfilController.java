package com.veterinaria.demo.controlador;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.veterinaria.demo.entidad.Administrador;
import com.veterinaria.demo.entidad.Cliente;
import com.veterinaria.demo.entidad.UserEntity;
import com.veterinaria.demo.entidad.Veterinario;
import com.veterinaria.demo.repositorio.AdministradorRepository;
import com.veterinaria.demo.repositorio.ClienteRepository;
import com.veterinaria.demo.repositorio.UserRepository;
import com.veterinaria.demo.repositorio.VeterinarioRepository;

/**
 * "Mi perfil": cada usuario (propietario, doctor o administrador) consulta y edita sus propios datos,
 * incluida su foto y su contraseña. El usuario se identifica por la sesión, nunca por un id enviado.
 */
@RestController
@RequestMapping("/perfil")
@CrossOrigin(origins = "http://localhost:4200")
public class PerfilController {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<Map<String, Object>> obtener() {
        return ResponseEntity.ok(perfilDe(usuarioActual()));
    }

    @PutMapping
    public ResponseEntity<Map<String, Object>> actualizar(@RequestBody Map<String, String> datos) {
        String usuario = usuarioActual();
        String nombre = texto(datos, "nombre");
        String foto = texto(datos, "foto");

        Cliente cliente = clienteRepository.findByNombreUsuario(usuario);
        Veterinario veterinario = veterinarioRepository.findByNombreUsuario(usuario);
        Administrador administrador = administradorRepository.findByNombreUsuario(usuario);

        if (cliente != null) {
            if (nombre != null) cliente.setNombre(nombre);
            if (foto != null) cliente.setFoto(foto);
            if (texto(datos, "correo") != null) cliente.setCorreo(texto(datos, "correo"));
            if (texto(datos, "celular") != null) cliente.setCelular(texto(datos, "celular"));
            clienteRepository.save(cliente);
        } else if (veterinario != null) {
            if (nombre != null) veterinario.setNombre(nombre);
            if (foto != null) veterinario.setFoto(foto);
            if (texto(datos, "especialidad") != null) veterinario.setEspecialidad(texto(datos, "especialidad"));
            if (texto(datos, "horario") != null) veterinario.setHorario(texto(datos, "horario"));
            veterinarioRepository.save(veterinario);
        } else if (administrador != null) {
            if (nombre != null) administrador.setNombre(nombre);
            if (foto != null) administrador.setFoto(foto);
            if (texto(datos, "correo") != null) administrador.setCorreo(texto(datos, "correo"));
            administradorRepository.save(administrador);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil no encontrado");
        }

        cambiarContrasenaSiAplica(usuario, texto(datos, "contrasenaActual"), texto(datos, "contrasenaNueva"));
        return ResponseEntity.ok(perfilDe(usuario));
    }

    private void cambiarContrasenaSiAplica(String usuario, String actual, String nueva) {
        if (nueva == null) {
            return;
        }
        if (nueva.length() < 4) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La nueva contraseña debe tener al menos 4 caracteres");
        }
        UserEntity user = userRepository.findByUsername(usuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        if (actual == null || !passwordEncoder.matches(actual, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña actual no es correcta");
        }
        user.setPassword(passwordEncoder.encode(nueva));
        userRepository.save(user);
    }

    private Map<String, Object> perfilDe(String usuario) {
        Map<String, Object> perfil = new LinkedHashMap<>();
        perfil.put("usuario", usuario);
        Cliente cliente = clienteRepository.findByNombreUsuario(usuario);
        if (cliente != null) {
            perfil.put("tipo", "Cliente");
            perfil.put("id", cliente.getIdCliente());
            perfil.put("nombre", cliente.getNombre());
            perfil.put("correo", cliente.getCorreo());
            perfil.put("celular", cliente.getCelular());
            perfil.put("cedula", cliente.getCedula());
            perfil.put("foto", cliente.getFoto());
            return perfil;
        }
        Veterinario veterinario = veterinarioRepository.findByNombreUsuario(usuario);
        if (veterinario != null) {
            perfil.put("tipo", "Veterinario");
            perfil.put("id", veterinario.getIdVeterinario());
            perfil.put("nombre", veterinario.getNombre());
            perfil.put("especialidad", veterinario.getEspecialidad());
            perfil.put("horario", veterinario.getHorario());
            perfil.put("sede", veterinario.getSede());
            perfil.put("cedula", veterinario.getCedula());
            perfil.put("foto", veterinario.getFoto());
            return perfil;
        }
        Administrador administrador = administradorRepository.findByNombreUsuario(usuario);
        if (administrador != null) {
            perfil.put("tipo", "Admin");
            perfil.put("id", administrador.getIdAdministrador());
            perfil.put("nombre", administrador.getNombre());
            perfil.put("correo", administrador.getCorreo());
            perfil.put("cedula", administrador.getCedula());
            perfil.put("foto", administrador.getFoto());
            return perfil;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil no encontrado");
    }

    private String usuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    private String texto(Map<String, String> datos, String clave) {
        String valor = datos.get(clave);
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
