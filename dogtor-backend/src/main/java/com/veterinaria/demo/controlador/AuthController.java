package com.veterinaria.demo.controlador;

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