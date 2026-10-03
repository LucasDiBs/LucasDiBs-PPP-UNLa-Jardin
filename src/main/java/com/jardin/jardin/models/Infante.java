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

    @Column(nullable = false)
    private String Papis;

    @Column(nullable = false)
    private String Email;

    private String sala;

    private boolean activo = true;

    public Infante(String direccion, String apellido, String nombre, int dni, String telefono, LocalDate fechaNacimiento, String papis, String email, String sala, boolean activo, int edadEnMeses) {
        super(direccion, apellido, nombre, dni, telefono);
        this.fechaNacimiento = fechaNacimiento;
        Papis = papis;
        Email = email;
        this.sala = sala;
        this.activo = activo;
        this.edadEnMeses = edadEnMeses;
    }
}
