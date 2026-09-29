package com.jardin.jardin.controller;

import com.jardin.jardin.JardinApplication;
import com.jardin.jardin.models.Admin;
import com.jardin.jardin.service.AdminService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.swing.*;
import java.io.IOException;

@Controller
public class ViewLogInController {

    @Autowired
    private AdminService Service;

    @FXML
    private TextField txtUser;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Button botonLogin;

    @FXML
    void eventKey(KeyEvent event) {

        Object evt = event.getSource();

        if (evt.equals(txtUser)) {

            if (event.getCharacter().equals(" ")) {

                event.consume();
            }

        } else if (evt.equals(txtPassword)) {

            if (event.getCharacter().equals(" ")) {

                event.consume();
            }
        }
    }

    @FXML
    void eventAction(ActionEvent event) {
        String user = txtUser.getText().trim();
        String pass = txtPassword.getText().trim();

        System.out.println("1. Intentando iniciar sesión con usuario: " + user);
        // 1. Validar que los campos no estén vacíos
        if (!(user.isEmpty() || pass.isEmpty())){

            Admin adminAutenticado = Service.autenticar(user, pass);

            if (adminAutenticado != null) {
                System.out.println("2. ¡Credenciales correctas! Intentando cargar la vista ViewAdmin.fxml...");
                try {
                    // 3. Login exitoso: Cargar la ventana principal (ViewAdmin.fxml)
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/ViewInfantes.fxml"));
                    loader.setControllerFactory(JardinApplication.getSpringContext()::getBean);

                    Parent root = loader.load();
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Panel Principal - Jardín Maternal");
                    stage.centerOnScreen();
                    stage.show();

                } catch (IOException e) {
                    System.out.println("¡ERROR AL CARGAR EL FXML!");
                    e.printStackTrace();

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Error al cargar la siguiente vista.");
                    alert.showAndWait();}
            } else {
                // 4. Credenciales incorrectas
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Advertencia");
                alert.setHeaderText(null);
                alert.setContentText("Usuario o contraseña incorrectos.");
                alert.showAndWait();}
        }


    }
}
