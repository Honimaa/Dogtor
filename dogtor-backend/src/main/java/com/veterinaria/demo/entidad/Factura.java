package com.veterinaria.demo.entidad;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "factura")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Factura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idFactura;

    @Column(nullable = false)
    private Date fechaHora;

    @Column(nullable = false)
    private Float total;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean pagada;

    @Column(nullable = false)
    private String metododepago;

    @ManyToOne
    @JoinColumn(name = "idCliente", nullable = false)
    private Cliente cliente;

    @OneToOne
    @JoinColumn(name = "idTratamiento", nullable = true)
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.SET_NULL)
    private Tratamiento tratamiento;


    @ManyToOne
    @JoinColumn(name = "idServicio", nullable = true)
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.SET_NULL)
    private Servicio servicio;

    @JsonIgnore
    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL)
    private List<FacturaMedicamento> facturaMedicamentos;

    public Factura(Date fechaHora, Float total, Boolean pagada, String metododepago) {
        this.fechaHora = fechaHora;
        this.total = total;
        this.pagada = pagada;
        this.metododepago = metododepago;
    }
}