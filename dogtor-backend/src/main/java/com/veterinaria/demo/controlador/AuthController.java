package com.veterinaria.demo.controlador;

import com.veterinaria.demo.entidad.Cliente;
import com.veterinaria.demo.repositorio.ClienteRepository;
import com.veterinaria.demo.seguridad.CustomUserDetailsService;

import com.veterinaria.demo.entidad.UserEntity;
import com.veterinaria.demo.repositorio.UserRepository;
import com.veterinaria.demo.seguridad.JWTGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTGenerator jwtGenerator;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    /**
     * Dogtor: registro público de propietarios de mascotas.
     * Crea el cliente y su usuario con rol CLIENTE; luego el front inicia sesión automáticamente.
     */
    @PostMapping("/registro")
    public ResponseEntity<Map<String, Object>> registrar(@RequestBody Map<String, String> datos) {
        String nombre = limpio(datos.get("nombre"));
        String usuario = limpio(datos.get("nombreUsuario"));
        String contrasena = datos.get("contrasena");
        String cedula = limpio(datos.get("cedula"));
        String correo = limpio(datos.get("correo"));
        String celular = limpio(datos.get("celular"));

        if (nombre == null || usuario == null || cedula == null || correo == null || celular == null || contrasena == null) {
            return error("Todos los campos son obligatorios");
        }
        if (!usuario.matches("[a-zA-Z0-9._-]{3,30}")) {
            return error("El usuario debe tener entre 3 y 30 caracteres, sin espacios");
        }
        if (contrasena.length() < 4) {
            return error("La contraseña debe tener al menos 4 caracteres");
        }
        if (!correo.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            return error("Ingresa un correo electrónico válido");
        }
        if (Boolean.TRUE.equals(userRepository.existsByUsername(usuario))) {
            return error("Ese nombre de usuario ya está en uso");
        }
        if (clienteRepository.findByCedula(cedula) != null) {
            return error("Ya existe una cuenta con esa cédula");
        }

        String foto = limpio(datos.get("foto"));
        Cliente cliente = new Cliente(nombre, correo, celular,
                foto != null ? foto : "assets/avatar-placeholder.svg", cedula, usuario, contrasena);
        cliente.setUser(userRepository.save(customUserDetailsService.clienteToUser(cliente)));
        clienteRepository.save(cliente);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Cuenta creada");
        respuesta.put("usuario", usuario);
        return ResponseEntity.ok(respuesta);
    }

    private ResponseEntity<Map<String, Object>> error(String mensaje) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("message", mensaje);
        return ResponseEntity.badRequest().body(respuesta);
    }

    private String limpio(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

    @PostMapping("/inicio_sesion")
    public ResponseEntity<?> login(@RequestBody UserEntity loginRequest) {
        // Autenticación
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        // Establecer contexto
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generar JWT
        String token = jwtGenerator.generateToken(authentication);

        // Obtener usuario desde DB
        UserEntity user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Obtener lista de roles
        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toList());

        // Armar respuesta como Map
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("username", user.getUsername());
        response.put("roles", roles);

        return ResponseEntity.ok(response);
    }
}