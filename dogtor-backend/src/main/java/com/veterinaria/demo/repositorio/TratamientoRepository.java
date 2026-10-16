package com.veterinaria.demo.repositorio;

import java.util.List;
import java.util.Date;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.veterinaria.demo.entidad.Mascota;
import com.veterinaria.demo.entidad.Tratamiento;

@Repository
public interface TratamientoRepository extends JpaRepository<Tratamiento, Integer> {
        @Query("SELECT DISTINCT t FROM Tratamiento t " +
                        "LEFT JOIN FETCH t.servicio " +
                        "LEFT JOIN FETCH t.tratamientoMedicamentos tm " +
                        "LEFT JOIN FETCH tm.medicamento " +
                        "WHERE t.servicio.nombre IN :nombresServicios")
        List<Tratamiento> findByServicioNombreIn(@Param("nombresServicios") List<String> nombresServicios);

        @Query("SELECT t.mascota FROM Tratamiento t WHERE t.veterinario.idVeterinario = :idVeterinario")
        List<Mascota> findMascotasByVeterinarioId(@Param("idVeterinario") Integer idVeterinario);

        List<Tratamiento> findByMascota(Mascota mascota);

        @Query("SELECT COUNT(t) FROM Tratamiento t WHERE t.fecha BETWEEN :fechaInicio AND :fechaFin")
        long countByFechaBetween(@Param("fechaInicio") Date fechaInicio,
                        @Param("fechaFin") Date fechaFin);

        @Query("SELECT t FROM Tratamiento t WHERE t.fecha BETWEEN :fechaInicio AND :fechaFin")
        List<Tratamiento> findByFechaBetween(@Param("fechaInicio") Date fechaInicio,
                        @Param("fechaFin") Date fechaFin);

        @Query("SELECT tm.medicamento.nombre as medicamento, SUM(tm.cantidad) as total " +
                        "FROM Tratamiento t JOIN t.tratamientoMedicamentos tm " +
                        "WHERE t.fecha BETWEEN :fechaInicio AND :fechaFin " +
                        "GROUP BY tm.medicamento.nombre " +
                        "ORDER BY total DESC")
        List<Object[]> findMedicamentosMasUsados(@Param("fechaInicio") Date fechaInicio,
                        @Param("fechaFin") Date fechaFin);

        @Query("SELECT m.nombre as medicamento, SUM(tm.cantidad) as total " +
                        "FROM TratamientoMedicamento tm " +
                        "JOIN tm.medicamento m " +
                        "GROUP BY m.nombre " +
                        "ORDER BY total DESC " +
                        "LIMIT 3")
        List<Map<String, Object>> findTop3MedicamentosMasVendidos();

        @Query("SELECT t FROM Tratamiento t WHERE t.veterinario.idVeterinario = :idVeterinario")
        List<Tratamiento> findByVeterinarioId(@Param("idVeterinario") Integer idVeterinario);

        List<Tratamiento> findByMascota_IdMascota(Integer idMascota);

        List<Tratamiento> findByCodigo(String codigo);
}