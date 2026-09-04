package com.jardin.jardin.service;
import com.jardin.jardin.models.Admin;
import com.jardin.jardin.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AdminService {

    @Autowired
    private AdminRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder; // <--- Inyectamos el codificador

    public void guardarAdmin(Admin admin) {
        // Encriptamos la contraseña y el user antes de asignarla al modelo


        String passwordEncriptada = passwordEncoder.encode(admin.getPassword());
               admin.setPassword(passwordEncriptada);

        repository.save(admin);
    }

    public Admin autenticar(String username, String passwordPlana) {
        Admin admin = repository.findByUserName(username);

        if (admin.getUserName().isEmpty()) {
            System.out.println("-> ¡El usuario '" + username + "' NO existe en la base de datos!");
            return null;
        }

        System.out.println("-> ¡Usuario encontrado! Contraseña en BD: " + admin.getPassword());
        if (admin != null) {
            // passwordEncoder.matches(ingresadaEnTextoPlano, encriptadaEnBaseDeDatos)
            if (passwordEncoder.matches(passwordPlana, admin.getPassword())) {
                System.out.println("-> ¡Contraseña correcta!");
                return admin; // Login exitoso
            }
            System.out.println("-> ¡Contraseña incorrecta!");
        }
        return null; // Credenciales inválidas
    }

}
