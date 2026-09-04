package com.jardin.jardin.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public abstract class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer personaId;

    @Column(unique = true, nullable = false)
    private int dni;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    private String direccion;

    private String telefono;

    public Persona(String direccion, String apellido, String nombre, int dni, String telefono) {
        this.direccion = direccion;
        this.apellido = apellido;
        this.nombre = nombre;
        this.dni = dni;
        this.telefono = telefono;
    }
}