package com.veterinaria.demo.repositorio;

import com.veterinaria.demo.entidad.TratamientoMedicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface TratamientoMedicamentoRepository extends JpaRepository<TratamientoMedicamento, Long> {
    
    @Query("SELECT COALESCE(SUM(m.precioVenta * tm.cantidad), 0) " +
           "FROM TratamientoMedicamento tm " +
           "JOIN tm.medicamento m")
    BigDecimal sumPrecioVentaMedicamentos();

    @Query("SELECT COALESCE(SUM(m.precioCompra * tm.cantidad), 0) " +
        "FROM TratamientoMedicamento tm " +
        "JOIN tm.medicamento m")
    BigDecimal sumPrecioCompraMedicamentos();
}