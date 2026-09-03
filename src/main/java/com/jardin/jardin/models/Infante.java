package com.jardin.jardin.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "infantes")
@Getter
@Setter
@NoArgsConstructor
public class Infante extends Persona {

    @Column(nullable = false)
    private int edadEnMeses;

    @Column(nullable = false)
    private LocalDate fechaNacimiento;

    private String sala;

    private boolean activo = true;
}