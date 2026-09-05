package com.jardin.jardin.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Controller
@Scope("prototype")

public class ViewNabarController {

    @Autowired
    private ApplicationContext applicationContext;

    @FXML
    void irAInfantes(ActionEvent event) {
        cambiarVista(event, "/Views/ViewInfantes.fxml", "Gestión de Infantes");
    }

    @FXML
    void irAAdmins(ActionEvent event) {
        cambiarVista(event, "/Views/ViewListaAdmins.fxml", "Gestión de Administradores");
    }

    @FXML
    void irANuevoAdmin(ActionEvent event) {
        cambiarVista(event, "/Views/ViewAdminForm.fxml", "Nuevo Administrador");
    }

    // Método genérico para realizar el cambio de escena manteniendo Spring
    private void cambiarVista(ActionEvent event, String fxmlRuta, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlRuta));
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            System.out.println("¡Error al cambiar de vista desde el Navbar!");
            e.printStackTrace();
        }
    }
}
