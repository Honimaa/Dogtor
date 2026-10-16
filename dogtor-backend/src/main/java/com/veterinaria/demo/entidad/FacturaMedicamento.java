package com.veterinaria.demo.entidad;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "factura_medicamento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacturaMedicamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(cascade = CascadeType.PERSIST,optional = true)
    @JoinColumn(name = "idFactura")
    private Factura factura;

    @ManyToOne(optional = true)
    @JoinColumn(name = "idMedicamento")
    private Medicamento medicamento;

    @Column(nullable = false)
    private Integer cantidad;
}