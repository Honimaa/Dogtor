package com.veterinaria.demo.seguridad;

import com.veterinaria.demo.entidad.*;
import com.veterinaria.demo.repositorio.RoleRepository;
import com.veterinaria.demo.repositorio.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userDB = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("User " + username + " not found")
        );
        return new User(userDB.getUsername(), userDB.getPassword(), mapToGrantedAuthorities(userDB.getRoles()));
    }

    private Collection<GrantedAuthority> mapToGrantedAuthorities(List<Role> roles){
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }

    public  UserEntity clienteToUser(Cliente cliente){
        UserEntity user = new UserEntity();
        user.setUsername(cliente.getNombre());
        user.setPassword(passwordEncoder.encode(cliente.getContrasena()));

        Role role = roleRepository.findByName("CLIENTE").get();
        user.setRoles(List.of(role));
        return user;
    }

    public UserEntity veterinarioToUser(Veterinario veterinario){
        UserEntity user = new UserEntity();
        user.setUsername(veterinario.getNombre());
        user.setPassword(passwordEncoder.encode(veterinario.getContrasena()));

        Role role = roleRepository.findByName("VETERINARIO").get();
        user.setRoles(List.of(role));
        return user;
    }

    public UserEntity andminToUser(Administrador administrador){
        UserEntity user = new UserEntity();
        user.setUsername(administrador.getNombre());
        user.setPassword(passwordEncoder.encode(administrador.getContrasena()));
        Role role = roleRepository.findByName("ADMIN").get();
        user.setRoles(List.of(role));
        return user;
    }
}