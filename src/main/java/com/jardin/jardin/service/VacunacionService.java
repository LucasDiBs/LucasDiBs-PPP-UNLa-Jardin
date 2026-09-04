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

    // Devuelve la lista de vacunas no aplicadas para la tabla FXML
    public List<CalendarioInfante> obtenerVacunasPendientesOVencidas() {
        return calendarioRepository.findByAplicadaFalse();
    }

    // Cambia el estado de la vacuna a aplicada (1)
    public void registrarAplicacionVacuna(Integer calendarioId) {
        calendarioRepository.findById(calendarioId).ifPresent(registro -> {
            registro.setAplicada(true);
            calendarioRepository.save(registro);
        });
    }

    // Método para simular el refresco o generación de notificaciones
    public void generarNotificacionesDiarias() {
        // En una etapa posterior acá podés crear los registros en la tabla
        // 'notificaciones'
    }

    public List<CalendarioInfante> obtenerVacunasAplicadas() {
        return calendarioRepository.findByAplicadaTrue();
    }

    public List<CalendarioInfante> obtenerTodasLasVacunas() {
        return calendarioRepository.findAll();
    }

    // Revierte el estado de una vacuna a no aplicada (0)
    public void revertirAplicacionVacuna(Integer calendarioId) {
        calendarioRepository.findById(calendarioId).ifPresent(registro -> {
            registro.setAplicada(false);
            calendarioRepository.save(registro);
        });
    }
}