package com.jardin.jardin.service;
import com.jardin.jardin.models.Admin;
import com.jardin.jardin.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AdminService {

    @Autowired
    private AdminRepository repository;

    public Admin guardar(Admin admin) {
    return repository.save(admin);
}



}
