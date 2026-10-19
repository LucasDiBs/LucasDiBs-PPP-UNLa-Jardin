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

    // ACÁ ESTÁ EL SERVICIO DE CORREOS QUE TE FALTABA
    @Autowired
    private EmailService emailService;

    @Scheduled(cron = "*/10 * * * * *")
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
            
            System.out.println("Alerta generada en BD para: " + alerta.getInfante().getNombre());

            // ACÁ SE DISPARA EL EMAIL REAL
            String emailPadre = alerta.getInfante().getEmailContacto();
            if (emailPadre != null && !emailPadre.trim().isEmpty()) {
                emailService.enviarRecordatorioVacuna(
                        emailPadre, 
                        alerta.getInfante().getNombre() + " " + alerta.getInfante().getApellido(), 
                        alerta.getVacuna().getNombre(), 
                        alerta.getFechaEstimada().toString()
                );
            }
        }
    }
}