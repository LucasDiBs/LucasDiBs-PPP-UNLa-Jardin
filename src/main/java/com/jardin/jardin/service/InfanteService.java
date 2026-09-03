package com.jardin.jardin.service;

import com.jardin.jardin.models.Infante;
import com.jardin.jardin.repository.InfanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class InfanteService {

    @Autowired
    private InfanteRepository repository;

    public Infante guardar(Infante infante) {
        return repository.save(infante);
    }

    public List<Infante> listarTodos() {
        return repository.findAll();
    }

    public Infante buscarPorId(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public void bajaLogica(Integer id) {
        Infante infante = buscarPorId(id);
        if (infante != null) {
            infante.setActivo(false);
            repository.save(infante);
        }
    }
}