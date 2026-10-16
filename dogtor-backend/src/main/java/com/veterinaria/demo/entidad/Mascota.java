package com.veterinaria.demo.entidad;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mascota")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mascota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idMascota;
    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false)
    private String raza;
    @Column(nullable = true)
    private Integer edad;
    @Column(nullable = true)
    private Float peso;
    @Column(nullable = true)
    private String enfermedad;
    // Admite URL o imagen subida por el propietario (data URL comprimida)
    @Column(nullable = false, length = 1000000)
    private String foto;

    @Temporal(TemporalType.DATE)
    @Column(nullable = true)
    private Date fechaNacimiento;

    @Temporal(TemporalType.DATE)
    @Column(nullable = true)
    private Date fechaIngreso;

    @Temporal(TemporalType.DATE)
    private Date fechaSalida;
    @Column(nullable = true)
    private Integer estado;

    @ManyToOne
    @JoinColumn(name = "idCliente", nullable = false)
    private Cliente cliente;

    @JsonIgnore
    @OneToMany(mappedBy = "mascota", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cita> citas;


    public Mascota(String nombre, String raza, Integer edad, Float peso, String enfermedad, String foto,
                Date fechaNacimiento, Date fechaIngreso, Date fechaSalida, Integer estado) {
        this.nombre = nombre;
        this.raza = raza;
        this.edad = edad;
        this.peso = peso;
        this.enfermedad = enfermedad;
        this.foto = foto;
        this.fechaNacimiento = fechaNacimiento;
        this.fechaIngreso = fechaIngreso;
        this.fechaSalida = fechaSalida;
        this.estado = estado;
    }
}