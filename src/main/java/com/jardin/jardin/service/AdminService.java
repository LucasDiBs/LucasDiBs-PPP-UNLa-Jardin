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
        String userEncriptado = passwordEncoder.encode(admin.getUserName());

        admin.setPassword(passwordEncriptada);

        admin.setUserName(userEncriptado);
        repository.save(admin);
    }


}
