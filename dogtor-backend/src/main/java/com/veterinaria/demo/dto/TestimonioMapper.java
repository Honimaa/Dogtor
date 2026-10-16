package com.veterinaria.demo.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.veterinaria.demo.entidad.Testimonio;
@Mapper
public interface TestimonioMapper {
    TestimonioMapper INSTANCE = Mappers.getMapper(TestimonioMapper.class);
    @Mapping(target = "cliente", source = "cliente.nombre")
    @Mapping(target = "imagenCliente", source = "cliente.foto")
    @Mapping(target = "servicio", source = "servicio.nombre")
    TestimonioDTO convert(Testimonio testimonio);
}