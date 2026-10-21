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

    @Autowired
    private EmailService emailService;

    //Descomentar la linea de abajo para probar localmente si se envia mail
    @Scheduled(cron = "*/10 * * * * *")
    // Ejecuta todos los días a las 9:00 AM
    //@Scheduled(cron = "0 0 9 * * *")
    public void buscarVacunasProximas() {
        List<CalendarioInfante> pendientes = calendarioRepository.findByAplicadaFalse();
        LocalDate hoy = LocalDate.now();

        for (CalendarioInfante alerta : pendientes) {
            LocalDate fechaEst = alerta.getFechaEstimada();
            String infanteNombre = alerta.getInfante().getNombre() + " " + alerta.getInfante().getApellido();
            String vacunaNombre = alerta.getVacuna().getNombre();
            
            String mensaje = null;
            boolean esVencida = false;

            // 1. Caso VENCIDA
            if (fechaEst.isBefore(hoy)) {
                mensaje = "ALERTA: Vacuna " + vacunaNombre + " VENCIDA para " + infanteNombre;
                esVencida = true;
            }
            // 2. Caso PROXIMO VENCIMIENTO (faltan 30 días o menos)
            else if (!fechaEst.isAfter(hoy.plusDays(30))) {
                mensaje = "Atención: El infante " + infanteNombre + " debe recibir la vacuna " + vacunaNombre
                        + " el día " + fechaEst;
            }

            // Guardar en BD y enviar email SOLO si la notificación no existe previamente
            if (mensaje != null && !notificacionRepository.existsByMensajeAndEstado(mensaje, "Pendiente de Lectura")) {
                Notificacion aviso = new Notificacion();
                aviso.setMensaje(mensaje);
                aviso.setFechaEnvio(LocalDateTime.now());
                aviso.setEstado("Pendiente de Lectura");

                notificacionRepository.save(aviso);
                System.out.println("Alerta automática generada: " + mensaje);

                // Disparamos el email real
                String emailPadre = alerta.getInfante().getEmail(); 
                if (emailPadre != null && !emailPadre.trim().isEmpty()) {
                    if (esVencida) {
                        // Usa el método genérico que creó tu compañero para alertas urgentes
                        emailService.enviar(emailPadre, "URGENTE: Vacuna Vencida - Jardín UNLa", 
                                "Estimada familia, les informamos que la vacuna " + vacunaNombre + 
                                " de " + infanteNombre + " se encuentra VENCIDA. Por favor, regularicen la situación.");
                    } else {
                        // Usa tu método específico para el recordatorio normal
                        emailService.enviarRecordatorioVacuna(
                                emailPadre, 
                                infanteNombre, 
                                vacunaNombre, 
                                fechaEst.toString()
                        );
                    }
                }
            }
        }
    }
}