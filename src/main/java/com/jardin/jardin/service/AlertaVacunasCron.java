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

    // Ejecuta todos los días a las 9:00 AM (descomentar la línea de 10s para
    // testing local)
    @Scheduled(cron = "0 0 9 * * *")
    // @Scheduled(cron = "*/10 * * * * *")
    public void buscarVacunasProximas() {
        List<CalendarioInfante> pendientes = calendarioRepository.findByAplicadaFalse();
        LocalDate hoy = LocalDate.now();

        for (CalendarioInfante alerta : pendientes) {
            LocalDate fechaEst = alerta.getFechaEstimada();
            String infanteNombre = alerta.getInfante().getNombre() + " " + alerta.getInfante().getApellido();
            String vacunaNombre = alerta.getVacuna().getNombre();

            String mensaje = null;

            // 1. Caso VENCIDA
            // Si pasó a estar vencida, busca si ya existe una alerta de tipo 'VENCIDA' sin
            // leer
            if (fechaEst.isBefore(hoy)) {
                String msjVencida = "ALERTA: Vacuna " + vacunaNombre + " VENCIDA para " + infanteNombre;
                if (!notificacionRepository.existsByMensajeAndEstado(msjVencida, "Pendiente de Lectura")) {
                    // Genera la alerta crítica de vencimiento
                }
            }
            // 2. Caso PROXIMO VENCIMIENTO (faltan 30 días o menos)
            else if (!fechaEst.isAfter(hoy.plusDays(30))) {
                mensaje = "Atención: El infante " + infanteNombre + " debe recibir la vacuna " + vacunaNombre
                        + " el día " + fechaEst;
            }

            // Guardar solo si se generó un mensaje y no existe una notificación activa
            // idéntica
            if (mensaje != null && !notificacionRepository.existsByMensajeAndEstado(mensaje, "Pendiente de Lectura")) {
                Notificacion aviso = new Notificacion();
                aviso.setMensaje(mensaje);
                aviso.setFechaEnvio(LocalDateTime.now());
                aviso.setEstado("Pendiente de Lectura");

                notificacionRepository.save(aviso);
                System.out.println("Alerta automática generada: " + mensaje);
            }
        }
    }
}