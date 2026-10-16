package com.veterinaria.demo.seguridad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthEntryPoint jwtAuthEntryPoint;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.
                csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/auth/inicio_sesion").permitAll()
                        .requestMatchers("/api/testimonios").permitAll()
                        .requestMatchers("/api/servicios").permitAll()
                        .requestMatchers("/factura/crear/medicamentos").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Dogtor: adopción (catálogo público, administración solo ADMIN)
                        .requestMatchers("/adopcion/admin/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/adopcion", "/adopcion/*").permitAll()
                        .requestMatchers(HttpMethod.POST, "/adopcion/*/solicitudes").permitAll()
                        // Dogtor: "Dogtor Encuentra" se consulta sin cuenta; publicar y resolver requiere sesión
                        .requestMatchers(HttpMethod.GET, "/publicaciones").permitAll()
                        // Dogtor: sedes con urgencias 24h visibles para cualquier propietario
                        .requestMatchers(HttpMethod.GET, "/veterinario/urgencias").permitAll()
                        // Dogtor: solo doctores autorizados modifican la historia clínica (REQ-HCL-07)
                        .requestMatchers(HttpMethod.POST, "/historia-clinica/**").hasAnyAuthority("VETERINARIO", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/historia-clinica/**").hasAnyAuthority("VETERINARIO", "ADMIN")
                        .requestMatchers("/medicamento/*/inventario").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/medicamento").hasAuthority("ADMIN")
                        .anyRequest().authenticated())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthEntryPoint));

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JWTAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        return new JWTAuthenticationFilter();
    }
}