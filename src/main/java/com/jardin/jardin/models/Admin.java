package com.jardin.jardin.models;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "admins")
@Getter
@Setter
@NoArgsConstructor
public class Admin  extends Persona {

    @Column(unique = true)
    private String userName;
    private String password;


}
