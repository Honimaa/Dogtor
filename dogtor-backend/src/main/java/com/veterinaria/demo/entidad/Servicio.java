package com.veterinaria.demo.entidad;

import jakarta.persistence.*;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

@Entity
@Table(name = "servicio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Servicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idServicio;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    private String descripcion;
    
    @Column(nullable = false)
    private Float precioBase;
    
    @Column(nullable = false)
    private String imagenFrontal;
    
    @Column(nullable = false)
    private String imagenTrasera;

    @JsonIgnore
    @OneToMany(mappedBy = "servicio", cascade = CascadeType.ALL)
    private List<Testimonio> testimonios;

    @JsonIgnore
    @OneToMany(mappedBy = "servicio", cascade = CascadeType.ALL)
    private List<Tratamiento> tratamientos;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "servicioFactura",
            joinColumns = @JoinColumn(name = "idServicio"),
            inverseJoinColumns = @JoinColumn(name = "idFactura")
    )
    private List<Factura> facturas;

    public Servicio(String nombre, String descripcion, Float precioBase, String imagenFrontal, String imagenTrasera) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioBase = precioBase;
        this.imagenFrontal = imagenFrontal;
        this.imagenTrasera = imagenTrasera;
    }
}