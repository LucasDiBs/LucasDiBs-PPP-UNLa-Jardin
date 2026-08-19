package com.jardin.jardin.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class ViewController implements Initializable {

    @FXML
    public Label Hola;

    public void cambio(){

        Hola.setText("Cambio a Hola Mundo");
    }
    @Bean
    String titulo(){
        return "Hola Mundo";
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cambio();
    }
}

