package com.jardin.jardin.repository;

import com.jardin.jardin.models.CalendarioInfante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarioInfanteRepository extends JpaRepository<CalendarioInfante, Integer> {
}