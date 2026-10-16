package com.veterinaria.demo.entidad;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

/**
 * Mascota publicada en el catálogo de adopción (REQ-ADO-01, 03, 05, 06).
 * Estados: DISPONIBLE, EN_PROCESO, ADOPTADA.
 */
@Entity
@Table(name = "mascota_adopcion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MascotaAdopcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idMascotaAdopcion;

    @Column(nullable = false)
    private String nombre;

    // PERRO, GATO, OTRO
    @Column(nullable = false)
    private String especie;

    private String raza;

    private Integer edadMeses;

    // PEQUENO, MEDIANO, GRANDE
    private String tamano;

    private String sexo;

    @Column(nullable = false)
    private String ciudad;

    @Column(length = 2000)
    private String descripcion;

    @Column(length = 1000)
    private String estadoSalud;

    @Column(nullable = false)
    private String foto;

    @Column(nullable = false)
    @Builder.Default
    private String estado = "DISPONIBLE";

    @Temporal(TemporalType.DATE)
    private Date fechaPublicacion;

    @ManyToOne
    @JoinColumn(name = "idRefugio", nullable = false)
    private Refugio refugio;

    @JsonIgnore
    @OneToMany(mappedBy = "mascotaAdopcion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SolicitudAdopcion> solicitudes;
}
