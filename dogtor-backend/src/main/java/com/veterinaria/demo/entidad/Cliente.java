package com.veterinaria.demo.entidad;

import jakarta.persistence.*;

import lombok.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "cliente")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCliente;
    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false)
    private String correo;
    @Column(nullable = false)
    private String celular;
    @Column(nullable = false, length = 1000)
    private String foto;

    @Column(unique = true, nullable = false)
    private String cedula;

    @Column(unique = true, nullable = false)
    private String nombreUsuario;

    @Transient
    //@Column(name = "contrasenha", nullable = false)
    private String contrasena;

    @OneToOne
    @JoinColumn(name = "user_id_user") // Esto asegura que el nombre de la columna coincida con la FK
    private UserEntity user;

    @JsonIgnore
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Mascota> mascotas;

    @JsonIgnore
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Testimonio> testimonios;

    @JsonIgnore
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Factura> facturas;

    public Cliente(String nombre, String correo, String celular, String foto, String cedula, String nombreUsuario, String contrasena) {
        this.nombre = nombre;
        this.correo = correo;
        this.celular = celular;
        this.foto = foto;
        this.cedula = cedula;
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
    }
}