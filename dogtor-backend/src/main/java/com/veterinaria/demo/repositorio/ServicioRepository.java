package com.veterinaria.demo.repositorio;

import com.veterinaria.demo.entidad.Servicio;
import java.util.List;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Integer> {
    List<Servicio> findAllByOrderByNombreAsc();
    
    @Query("SELECT s FROM Servicio s WHERE s.nombre IN :nombres")
    List<Servicio> findByNombreIn(@Param("nombres") List<String> nombres);

    @Query("SELECT COALESCE(SUM(s.precioBase), 0) FROM Servicio s")
    BigDecimal sumPrecioBaseAllServicios();
}