package com.veterinaria.demo.entidad;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

/**
 * Refugio o fundación que publica mascotas en adopción.
 * Debe ser validado por un administrador de Dogtor antes de publicar (REQ-ADO-04).
 */
@Entity
@Table(name = "refugio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Refugio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRefugio;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String ciudad;

    private String telefono;

    private String correo;

    @Column(length = 1000)
    private String descripcion;

    private String logo;

    @Column(nullable = false)
    @Builder.Default
    private Boolean validado = false;

    @JsonIgnore
    @OneToMany(mappedBy = "refugio")
    private List<MascotaAdopcion> mascotas;
}
