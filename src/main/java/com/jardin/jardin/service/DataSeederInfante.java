package com.jardin.jardin.service;

import com.jardin.jardin.models.Infante;
import com.jardin.jardin.models.Vacuna;
import com.jardin.jardin.repository.InfanteRepository;
import com.jardin.jardin.repository.VacunaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataSeederInfante implements CommandLineRunner {

    @Autowired
    private InfanteRepository repository;

    @Override
    public void run(String... args) throws Exception {
        if (repository.count() <= 3) {
            Infante I1 = new Infante( "direccion 0", "apellido 0", "Nombre 0", 22222220, "119581010", LocalDate.of(2026, 07, 28), "papis 0", "lucas_dibenedetto@hootmail.com", "Azul", true, 3);
            Infante I2 = new Infante( "direccion 1", "apellido 1", "Nombre 1", 22222221, "119581011", LocalDate.of(2024, 10, 1), "papis 1", "papis1@Unla.com", "Verde", true, 18);
            Infante I3 = new Infante( "direccion 2", "apellido 2", "Nombre 2", 22222222, "119581012", LocalDate.of(2026, 02, 28), "papis 2", "papis2@Unla.com", "Roja", true, 7);
            Infante I4 = new Infante( "direccion 3", "apellido 3", "Nombre 3", 22222223, "119581013", LocalDate.of(2026, 07, 28), "papis 3", "papis3@Unla.com", "Amarilla", true, 3);
            Infante I5 = new Infante( "direccion 4", "apellido 4", "Nombre 4", 22222224, "119581014", LocalDate.of(2026, 04, 28), "papis 4", "papis4@Unla.com", "Azul", true, 5);

            repository.saveAll(List.of(I1, I2, I3, I4, I5));
            System.out.println("Infantes cargados en MySQL.");
        }
    }
}
