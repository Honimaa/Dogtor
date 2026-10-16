package com.veterinaria.demo.controlador;

import com.veterinaria.demo.entidad.Cliente;
import com.veterinaria.demo.entidad.UserEntity;
import com.veterinaria.demo.repositorio.UserRepository;
import com.veterinaria.demo.seguridad.CustomUserDetailsService;
import com.veterinaria.demo.seguridad.JWTGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.veterinaria.demo.servicio.ClienteService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cliente")
@CrossOrigin(origins = "http://localhost:4200")
public class ClienteController{

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTGenerator jwtGenerator;

    @PostMapping("/login")
    public ResponseEntity<String> LoginCLiente(@RequestBody Cliente cliente) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(cliente.getNombreUsuario(), cliente.getContrasena())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtGenerator.generateToken(authentication);

        return new ResponseEntity<String>(token, HttpStatus.OK);
    }

    @GetMapping("/details")
    public ResponseEntity<Cliente> buscarCliente(){
        Cliente cliente = clienteService.buscarPorNombreUsuario(
                SecurityContextHolder.getContext().getAuthentication().getName()
        );

        if(cliente == null){
            return new ResponseEntity<Cliente>(cliente,HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Cliente>(cliente,HttpStatus.OK);
    }

    @GetMapping
    public List<Cliente> listar(){
        return clienteService.obtenerTodosClientes();
    }

    @GetMapping("/{id}")
    public Cliente detalleCliente(@PathVariable Integer id, Model model){
        return clienteService.obtenerClientePorId(id);
    }

    @PostMapping("/crear")
    public ResponseEntity<Map<String, String>> crearCliente(
            @RequestBody Cliente cliente,
            @RequestParam("confirm_password") String confirmPassword
    ) {
        if(userRepository.existsByUsername(cliente.getNombreUsuario())) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "El nombre de usuario ya está registrado");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Verificar si la contraseña está vacía
        if (cliente.getContrasena() == null || cliente.getContrasena().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "La contraseña no puede estar vacía"));
        }

        // Verificar si las contraseñas coinciden
        if (!cliente.getContrasena().equals(confirmPassword)) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Las contraseñas no coinciden"));
        }

        try {
            UserEntity userEntity = customUserDetailsService.clienteToUser(cliente);
    
    // Primero guardas el userEntity
            userRepository.save(userEntity);
            
            // Luego lo asocias al cliente
            cliente.setUser(userEntity);
            
            // Ahora guardas el cliente
            clienteService.crearCliente(cliente);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Usuario creado correctamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al crear el cliente");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Cliente> actualizarCliente(@PathVariable Integer id, @RequestBody Cliente clienteActualizado) {
        Cliente clienteEditado = clienteService.editarCliente(id, clienteActualizado);
        return ResponseEntity.ok(clienteEditado);
    }
    

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Map<String, String>> eliminarCliente(@PathVariable Integer id) {
        Map<String, String> respuesta = clienteService.eliminarCliente(id);
        HttpStatus estado = respuesta.get("mensaje").contains("no encontrado") ? HttpStatus.NOT_FOUND : HttpStatus.OK;
        return new ResponseEntity<>(respuesta, estado);
    }
    


    @GetMapping("/idClientePorNombreUsuario/{nombreUsuario}")
public ResponseEntity<Integer> obtenerIdClientePorNombreUsuario(@PathVariable String nombreUsuario) {
    Integer idCliente = clienteService.obtenerIdClientePorNombreUsuario(nombreUsuario);
    if (idCliente != null) {
        return ResponseEntity.ok(idCliente);
    } else {
        return ResponseEntity.notFound().build();
    }
}
    @GetMapping("/buscar")
    public ResponseEntity<List<Cliente>> buscarPorNombre(@RequestParam String nombre) {
        List<Cliente> clientes = clienteService.buscarPorNombre(nombre);
        return ResponseEntity.ok(clientes);
    }
}