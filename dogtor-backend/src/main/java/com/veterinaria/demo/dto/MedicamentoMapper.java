package com.veterinaria.demo.dto;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.veterinaria.demo.entidad.Medicamento;

@Mapper
public interface MedicamentoMapper {
    MedicamentoMapper INSTANCE = Mappers.getMapper(MedicamentoMapper.class);
    MedicamentoDTO convert(Medicamento medicamento);
}