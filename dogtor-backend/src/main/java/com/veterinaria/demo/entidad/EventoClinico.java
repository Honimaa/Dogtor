package com.veterinaria.demo.entidad;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.*;

/**
 * Evento de la historia clínica digital de una mascota (REQ-HCL-01..07).
 * Tipos: VACUNA, DIAGNOSTICO, TRATAMIENTO, CIRUGIA, ALERGIA.
 */
@Entity
@Table(name = "evento_clinico")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoClinico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEvento;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private String titulo;

    @Column(length = 2000)
    private String descripcion;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date fecha;

    // Próxima dosis o control (usado por los recordatorios automáticos)
    @Temporal(TemporalType.DATE)
    private Date fechaProxima;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaRegistro;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "idMascota", nullable = false)
    private Mascota mascota;

    @ManyToOne
    @JoinColumn(name = "idVeterinario")
    @JsonIgnoreProperties({"user", "contrasena", "citas", "tratamientos", "nombreUsuario", "cedula"})
    private Veterinario veterinario;
}
