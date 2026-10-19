package com.jardin.jardin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarRecordatorioVacuna(String destinatario, String nombreInfante, String nombreVacuna, String fecha) {
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            // Tiene que coincidir con el correo que pusiste en application.yaml
            mensaje.setFrom("jardin.unla.notificaciones@gmail.com"); 
            mensaje.setTo(destinatario);
            mensaje.setSubject("Aviso de Vacunación - Jardín UNLa");
            mensaje.setText("Estimada familia,\n\n" +
                    "Nos contactamos desde el Jardín para recordarles que el/la infante " + nombreInfante + 
                    " debe recibir la vacuna '" + nombreVacuna + "' el día " + fecha + ".\n\n" +
                    "Por favor, tengan en cuenta esta fecha para mantener el calendario al día.\n\n" +
                    "Saludos cordiales,\nAdministración del Jardín.");

            mailSender.send(mensaje);
            System.out.println("Correo enviado con éxito a: " + destinatario);
        } catch (Exception e) {
            System.out.println("Error al intentar enviar el correo a " + destinatario + ": " + e.getMessage());
        }
    }
}