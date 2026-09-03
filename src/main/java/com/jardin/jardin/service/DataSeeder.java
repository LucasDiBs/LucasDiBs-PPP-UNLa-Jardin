package com.jardin.jardin.service;

import com.jardin.jardin.models.Vacuna;
import com.jardin.jardin.repository.VacunaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private VacunaRepository vacunaRepository;

    @Override
    public void run(String... args) throws Exception {
        if (vacunaRepository.count() == 0) {
            Vacuna v1 = new Vacuna(); v1.setNombre("BCG (Tuberculosis)"); v1.setMesesParaAplicacion(0);
            Vacuna v2 = new Vacuna(); v2.setNombre("Hepatitis B"); v2.setMesesParaAplicacion(0);
            Vacuna v3 = new Vacuna(); v3.setNombre("Quíntuple (Pentavalente)"); v3.setMesesParaAplicacion(2);
            Vacuna v4 = new Vacuna(); v4.setNombre("Neumococo Conjugada"); v4.setMesesParaAplicacion(2);
            Vacuna v5 = new Vacuna(); v5.setNombre("Triple Viral (SRP)"); v5.setMesesParaAplicacion(12);

            vacunaRepository.saveAll(List.of(v1, v2, v3, v4, v5));
            System.out.println("Catálogo oficial de vacunas cargado en MySQL.");
        }
    }
}