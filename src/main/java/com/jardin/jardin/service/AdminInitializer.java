package com.jardin.jardin.service;
import com.jardin.jardin.repository.AdminRepository;
import com.jardin.jardin.models.Admin;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements Runnable {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @EventListener(ApplicationReadyEvent.class) // Se ejecuta automáticamente cuando Spring Boot arranca por completo
    public void run() {
        // Verificamos si la tabla de administradores está vacía
        if (adminRepository.count() == 0) {



               Admin admin = new Admin();
               admin.setUserName("admin");
               admin.setPassword(passwordEncoder.encode("admin")); // Contraseña cifrada
               admin.setTelefono("ADMIN");
               admin.setNombre("ADMIN");
               admin.setApellido("ADMIN");
               admin.setDireccion("ADMIN");
               adminRepository.save(admin);
               System.out.println(">>> Administrador por defecto creado exitosamente.");

        }
    }
}