package com.jardin.jardin.repository;

import com.jardin.jardin.models.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {
    // Trae alertas no leídas ordenadas por la más reciente
    List<Notificacion> findByEstadoOrderByFechaEnvioDesc(String estado);

    // Permite saber si ya existe una alerta activa con ese mensaje exacto para no
    // duplicar
    boolean existsByMensajeAndEstado(String mensaje, String estado);

    // Cuenta cuántas no leídas hay para el número del globo en la campanita
    long countByEstado(String estado);
}