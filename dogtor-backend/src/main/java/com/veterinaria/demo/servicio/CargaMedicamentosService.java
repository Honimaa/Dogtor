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

    // Tasa de referencia para convertir los precios del CSV (en pesos colombianos) a dólares
    private static final float COP_POR_USD = 4000f;

    private Float parsePrecio(String texto) {
        float pesos = Float.parseFloat(texto.replace("$", "").replace(",", "").replace("\"", "").trim());
        // Precio en dólares redondeado a valores comerciales (terminados en .49 o .99)
        float dolares = Math.max(0.99f, Math.round(pesos / COP_POR_USD * 2) / 2f - 0.01f);
        return Math.round(dolares * 100) / 100f;
    }
}