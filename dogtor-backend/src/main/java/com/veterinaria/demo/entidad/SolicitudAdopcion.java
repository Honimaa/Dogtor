package com.veterinaria.demo.entidad;

import java.util.Date;

import jakarta.persistence.*;
import lombok.*;

/**
 * Solicitud enviada por un adoptante para una mascota publicada (REQ-ADO-02, 07).
 * Estados: PENDIENTE, EN_REVISION, APROBADA, RECHAZADA.
 */
@Entity
@Table(name = "solicitud_adopcion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudAdopcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idSolicitud;

    @Column(nullable = false)
    private String nombreSolicitante;

    @Column(nullable = false)
    private String correo;

    @Column(nullable = false)
    private String telefono;

    private String ciudad;

    @Column(length = 2000)
    private String mensaje;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;

    @Column(nullable = false)
    @Builder.Default
    private String estado = "PENDIENTE";

    @ManyToOne
    @JoinColumn(name = "idMascotaAdopcion", nullable = false)
    private MascotaAdopcion mascotaAdopcion;
}
