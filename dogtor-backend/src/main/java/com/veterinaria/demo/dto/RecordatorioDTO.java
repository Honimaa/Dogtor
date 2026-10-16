package com.veterinaria.demo.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Recordatorio de cita, vacuna o control periódico (REQ-REC-01..03).
 * tipo: CITA, VACUNA, CONTROL
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordatorioDTO {
    private String tipo;
    private String titulo;
    private String mensaje;
    private Date fecha;
    private Integer idMascota;
    private String nombreMascota;
    private String nombrePropietario;
    private String correoPropietario;
    private boolean urgente;
}
