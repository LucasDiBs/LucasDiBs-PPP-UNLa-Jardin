package com.jardin.jardin.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class EmailService {

    // Método que fuerza la configuración de Gmail ignorando el application.yaml
    private JavaMailSenderImpl getMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        //Estos dos campos deben modificarse con los correctos para poder probar el sistema de avisos por mail
        mailSender.setUsername("jardin.unla.notificaciones@gmail.com");
        mailSender.setPassword("ohdkhozrtgkdvqtd"); 

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        
        return mailSender;
    }

    public void enviar(String destinatario, String asunto, String mensaje) {
        try {
            JavaMailSenderImpl mailSender = getMailSender();
            
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(destinatario);
            email.setSubject(asunto);
            email.setText(mensaje);
            
            mailSender.send(email);
            System.out.println("Correo enviado con éxito a: " + destinatario);
        } catch (Exception e) {
            System.out.println("Error al intentar enviar correo a " + destinatario + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void enviarRecordatorioVacuna(String destinatario, String nombreInfante, String nombreVacuna, String fecha) {
        try {
            JavaMailSenderImpl mailSender = getMailSender();
            
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom("jardin.unla.notificaciones@gmail.com");
            mensaje.setTo(destinatario);
            mensaje.setSubject("Aviso de Vacunación - Jardín UNLa");
            mensaje.setText("Estimada familia,\n\n" +
                    "Nos contactamos desde el Jardín para recordarles que el/la infante " + nombreInfante + 
                    " debe recibir la vacuna '" + nombreVacuna + "' el día " + fecha + ".\n\n" +
                    "Por favor, tengan en cuenta esta fecha para mantener el calendario al día.\n\n" +
                    "Saludos cordiales,\nAdministración del Jardín.");

            mailSender.send(mensaje);
            System.out.println("Correo de vacuna enviado con éxito a: " + destinatario);
        } catch (Exception e) {
            System.out.println("Error al intentar enviar el correo de vacuna a " + destinatario + ": " + e.getMessage());
        }
    }
}