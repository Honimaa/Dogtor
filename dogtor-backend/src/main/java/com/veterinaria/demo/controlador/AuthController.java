package com.veterinaria.demo.controlador;

import com.veterinaria.demo.entidad.Cliente;
import com.veterinaria.demo.entidad.Refugio;
import com.veterinaria.demo.repositorio.ClienteRepository;
import com.veterinaria.demo.repositorio.RefugioRepository;
import org.springframework.transaction.annotation.Transactional;
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

    @Autowired
    private RefugioRepository refugioRepository;

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

    /**
     * Dogtor: registro público de refugios y fundaciones.
     * Crea el refugio sin validar y su cuenta con rol REFUGIO. Puede iniciar sesión de inmediato,
     * pero solo publica mascotas cuando un administrador de Dogtor lo valida.
     */
    @PostMapping("/registro-refugio")
    @Transactional
    public ResponseEntity<Map<String, Object>> registrarRefugio(@RequestBody Map<String, String> datos) {
        String nombre = limpio(datos.get("nombre"));
        String ciudad = limpio(datos.get("ciudad"));
        String correo = limpio(datos.get("correo"));
        String telefono = limpio(datos.get("telefono"));
        String usuario = limpio(datos.get("nombreUsuario"));
        String contrasena = datos.get("contrasena");

        if (nombre == null || ciudad == null || correo == null || telefono == null || usuario == null || contrasena == null) {
            return error("Todos los campos marcados son obligatorios");
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
        for (String red : new String[] {"instagram", "facebook", "tiktok", "sitioWeb"}) {
            String valor = limpio(datos.get(red));
            if (valor != null && valor.length() > 255) {
                return error("El enlace de la red social es demasiado largo");
            }
        }

        UserEntity user = userRepository.save(customUserDetailsService.refugioToUser(usuario, contrasena));
        refugioRepository.save(Refugio.builder()
                .nombre(nombre)
                .ciudad(ciudad)
                .correo(correo)
                .telefono(telefono)
                .descripcion(limpio(datos.get("descripcion")))
                .logo(limpio(datos.get("logo")))
                .instagram(limpio(datos.get("instagram")))
                .facebook(limpio(datos.get("facebook")))
                .tiktok(limpio(datos.get("tiktok")))
                .sitioWeb(limpio(datos.get("sitioWeb")))
                .validado(false)
                .user(user)
                .build());

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Refugio registrado. Dogtor revisará tus datos antes de que puedas publicar.");
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
        // Dogtor: se ignoran los espacios al inicio y al final del usuario (los usuarios no
        // pueden tener espacios). La contraseña se envía tal cual.
        String usuario = loginRequest.getUsername() == null ? "" : loginRequest.getUsername().trim();

        // Autenticación
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        usuario,
                        loginRequest.getPassword()
                )
        );

        // Establecer contexto
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generar JWT
        String token = jwtGenerator.generateToken(authentication);

        // Obtener usuario desde DB
        UserEntity user = userRepository.findByUsername(usuario)
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