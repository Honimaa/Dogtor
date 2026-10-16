package com.veterinaria.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.Order;

import com.veterinaria.demo.servicio.CargaMedicamentosService;

@SpringBootApplication
@Order(0)
public class DogtorApplication implements CommandLineRunner {

    @Autowired
    private CargaMedicamentosService cargaMedicamentosService;

    public static void main(String[] args) {
        SpringApplication.run(DogtorApplication.class, args);
    }

    // false en el perfil ddl, que solo genera el script de la base de datos
    @Value("${dogtor.datos-ejemplo:true}")
    private boolean cargarDatosEjemplo;

    @Override
    public void run(String... args) throws Exception {
        if (!cargarDatosEjemplo) {
            return;
        }
        cargaMedicamentosService.cargarMedicamentosDesdeCSV();
        System.out.println("Medicamentos cargados desde el CSV.");
    }
}