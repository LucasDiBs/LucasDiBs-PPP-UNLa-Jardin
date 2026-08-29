package com.jardin.jardin.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class Persona {
    private int personaId;
    private int dni;
    private String nombre;
    private String apellido;
    private String direccion;

    public Persona(String direccion, String apellido, String nombre, int dni) {
        this.direccion = direccion;
        this.apellido = apellido;
        this.nombre = nombre;
        this.dni = dni;
    }
}
