package com.veterinaria.demo.entidad;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "administrador")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class    Administrador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idAdministrador;
    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false)
    private String correo;
    @Column(nullable = false, length = 1000000)
    private String foto;

    @Column(unique = true, nullable = false)
    private String cedula;

    @Column(unique = true, nullable = false)
    private String nombreUsuario;

    @Transient
    @Column(name = "contrasenha", unique = true, nullable = false)
    private String contrasena;

    @OneToOne(cascade = CascadeType.ALL)
    private UserEntity user;

    public Administrador(String nombre, String correo, String foto, String cedula, String nombreUsuario, String contrasena) {
        this.nombre = nombre;
        this.correo = correo;
        this.foto = foto;
        this.cedula = cedula;
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
    }
}