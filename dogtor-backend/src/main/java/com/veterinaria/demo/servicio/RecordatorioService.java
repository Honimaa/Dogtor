package com.veterinaria.demo.servicio;

import java.util.List;

import com.veterinaria.demo.dto.RecordatorioDTO;

public interface RecordatorioService {
    List<RecordatorioDTO> recordatoriosCliente(Integer idCliente);
    List<RecordatorioDTO> recordatoriosClinica();
}
