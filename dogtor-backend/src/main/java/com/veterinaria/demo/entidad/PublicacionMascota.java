package com.veterinaria.demo.entidad;

import java.util.Date;

import jakarta.persistence.*;
import lombok.*;

/**
 * Publicación de "Dogtor Encuentra".
 * tipo PERDIDA: el dueño busca a su mascota. tipo ENCONTRADA: alguien halló una mascota y busca a su dueño.
 */
@Entity
@Table(name = "publicacion_mascota")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicacionMascota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPublicacion;

    @Column(nullable = false)
    private String tipo;

    // Nombre de la mascota (si se conoce)
    private String nombreMascota;

    private String especie;

    @Column(length = 2000)
    private String descripcion;

    // Dónde se perdió o dónde fue encontrada
    @Column(nullable = false)
    private String lugar;

    private String ciudad;

    @Temporal(TemporalType.DATE)
    private Date fechaSuceso;

    // URL o imagen comprimida enviada desde el navegador
    @Column(length = 1000000)
    private String foto;

    @Column(nullable = false)
    private String contactoNombre;

    @Column(nullable = false)
    private String contactoTelefono;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaPublicacion;

    @Column(nullable = false)
    @Builder.Default
    private Boolean resuelta = false;
}
