package com.jardin.jardin.service;

import com.jardin.jardin.models.CalendarioInfante;
import com.jardin.jardin.models.Notificacion;
import com.jardin.jardin.repository.CalendarioInfanteRepository;
import com.jardin.jardin.repository.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlertaVacunasCron {

    @Autowired
    private CalendarioInfanteRepository calendarioRepository;

    @Autowired
    private NotificacionRepository notificacionRepository;

    // "0 0 9 * * *" significa que se ejecuta todos los días a las 9:00 AM.
    @Scheduled(cron = "0 0 9 * * *") //Esta linea deberia estar en el proyecto final
    //@Scheduled(cron = "*/10 * * * * *") //esta linea se ejecuta cada 10 segundos, la hice para probar que realmente se generaba el evento
    public void buscarVacunasProximas() {
        LocalDate fechaObjetivo = LocalDate.now().plusDays(30);

        List<CalendarioInfante> pendientes = calendarioRepository.findAll().stream()
                .filter(c -> !c.isAplicada())
                .filter(c -> c.getFechaEstimada().isEqual(fechaObjetivo))
                .toList();

        for (CalendarioInfante alerta : pendientes) {
            Notificacion aviso = new Notificacion();
            aviso.setMensaje("Atención: El infante " + alerta.getInfante().getNombre() + " " + 
                             alerta.getInfante().getApellido() + " debe recibir la vacuna " + 
                             alerta.getVacuna().getNombre() + " el día " + alerta.getFechaEstimada());
            aviso.setFechaEnvio(LocalDateTime.now());
            aviso.setEstado("Pendiente de Lectura");
            
            notificacionRepository.save(aviso);
            
            System.out.println("Alerta automática generada para el infante: " + alerta.getInfante().getNombre());
        }
    }
}