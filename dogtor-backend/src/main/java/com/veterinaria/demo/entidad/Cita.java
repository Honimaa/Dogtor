package com.veterinaria.demo.entidad;


import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "cita")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCita;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaHora;
    @Column(nullable = false)
    private String sede;

    @ManyToOne
    @JoinColumn(name = "idMascota", nullable = false)
    private Mascota mascota;

    @ManyToOne
    @JoinColumn(name = "idVeterinario")
    private Veterinario veterinario;

    @ManyToOne
    @JoinColumn(name = "idServicio")
    private Servicio servicio;

    @Column(nullable = false)
    private String estado;

    // Dogtor: tipo de consulta (CONTROL, VACUNACION, URGENCIA, CIRUGIA) y motivo
    @Column(nullable = true)
    private String tipoConsulta;

    @Column(nullable = true, length = 1000)
    private String motivo;

    public Cita(Integer idCita, Date fechaHora, String sede, Mascota mascota, Veterinario veterinario,
                Servicio servicio, String estado) {
        this(idCita, fechaHora, sede, mascota, veterinario, servicio, estado, null, null);
    }

    public Cita(Date fechaHora, String sede, String estado) {
        this.fechaHora = fechaHora;
        this.sede = sede;
        this.estado = estado;
    }
}