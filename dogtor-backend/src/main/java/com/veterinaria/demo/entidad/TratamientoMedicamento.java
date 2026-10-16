package com.veterinaria.demo.entidad;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tratamiento_medicamento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TratamientoMedicamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "idTratamiento", nullable = false)
    private Tratamiento tratamiento;

    @ManyToOne
    @JoinColumn(name = "idMedicamento", nullable = false)
    private Medicamento medicamento;

    @Column(nullable = false)
    private Integer cantidad;
}