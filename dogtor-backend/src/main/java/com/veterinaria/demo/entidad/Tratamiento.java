package com.veterinaria.demo.entidad;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "tratamiento")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tratamiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idTratamiento;

    @Column(nullable = false)
    private String codigo;



    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date fecha;

    @Column(nullable = false)
    private String detalles;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "idVeterinario", nullable = true)
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.SET_NULL)
    private Veterinario veterinario;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "idMascota", nullable = true)
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.SET_NULL)
    private Mascota mascota;

    @ManyToOne
    @JoinColumn(name = "idServicio", nullable = true)
    @JsonIgnore
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.SET_NULL)
    private Servicio servicio;

    @OneToMany(mappedBy = "tratamiento", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<TratamientoMedicamento> tratamientoMedicamentos;

    


    public Tratamiento(String codigo, Date fecha, String detalles) {
        this.codigo = codigo;
        this.fecha = fecha;
        this.detalles = detalles;
    }
}