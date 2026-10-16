package com.veterinaria.demo.servicio;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.veterinaria.demo.entidad.Medicamento;
import com.veterinaria.demo.repositorio.MedicamentoRepository;

@Service
public class CargaMedicamentosService {

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    public void cargarMedicamentosDesdeCSV() {
        // Evita duplicar el catálogo cuando la base de datos ya tiene medicamentos
        if (medicamentoRepository.count() > 0) {
            return;
        }
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/medicamentos.csv");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String linea;
            boolean primeraLinea = true;

            while ((linea = reader.readLine()) != null) {
                if (primeraLinea) {
                    primeraLinea = false;
                    continue; // Saltar encabezado
                }

                String[] campos = linea.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                String nombre = campos[0];
                Float precioVenta = parsePrecio(campos[1]);
                Float precioCompra = parsePrecio(campos[2]);
                Integer unidadesDisponibles = Integer.parseInt(campos[3]);
                Integer unidadesVendidas = Integer.parseInt(campos[4]);

                Medicamento medicamento = new Medicamento();
                medicamento.setNombre(nombre);
                medicamento.setPrecioVenta(precioVenta);
                medicamento.setPrecioCompra(precioCompra);
                medicamento.setUnidadesDisponibles(unidadesDisponibles);
                medicamento.setUnidadesVendidas(unidadesVendidas);

                medicamentoRepository.save(medicamento);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Los precios del CSV están en pesos colombianos (p. ej. "$151,300")
    private Float parsePrecio(String texto) {
        return Float.parseFloat(texto.replace("$", "").replace(",", "").replace("\"", "").trim());
    }
}