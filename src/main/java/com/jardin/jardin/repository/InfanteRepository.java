package com.jardin.jardin.repository;

import com.jardin.jardin.models.Infante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfanteRepository extends JpaRepository<Infante, Integer> {
}