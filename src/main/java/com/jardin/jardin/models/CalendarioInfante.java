package com.jardin.jardin.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "calendarios_infantes")
@Getter
@Setter
@NoArgsConstructor
public class CalendarioInfante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "infante_id", nullable = false)
    private Infante infante;

    @ManyToOne
    @JoinColumn(name = "vacuna_id", nullable = false)
    private Vacuna vacuna;

    @Column(nullable = false)
    private LocalDate fechaEstimada;

    @Column(nullable = false)
    private boolean aplicada = false;
}