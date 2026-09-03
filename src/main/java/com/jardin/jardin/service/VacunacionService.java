package com.jardin.jardin.service;

import com.jardin.jardin.models.CalendarioInfante;
import com.jardin.jardin.models.Infante;
import com.jardin.jardin.models.Vacuna;
import com.jardin.jardin.repository.CalendarioInfanteRepository;
import com.jardin.jardin.repository.VacunaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class VacunacionService {

    @Autowired
    private VacunaRepository vacunaRepository;

    @Autowired
    private CalendarioInfanteRepository calendarioRepository;

    public void generarCalendarioParaInfante(Infante infante) {
        // Traemos el calendario nacional 2026 de la base
        List<Vacuna> catalogoOficial = vacunaRepository.findAll();

        for (Vacuna vacuna : catalogoOficial) {
            CalendarioInfante registro = new CalendarioInfante();
            registro.setInfante(infante);
            registro.setVacuna(vacuna);
            
            LocalDate fechaAplicacion = infante.getFechaNacimiento().plusMonths(vacuna.getMesesParaAplicacion());
            registro.setFechaEstimada(fechaAplicacion);
            
            registro.setAplicada(false); 

            calendarioRepository.save(registro);
        }
    }
}