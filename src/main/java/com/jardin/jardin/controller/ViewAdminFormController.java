package com.jardin.jardin.controller;

import com.jardin.jardin.models.Admin;
import com.jardin.jardin.service.AdminService;
import com.jardin.jardin.models.Infante;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Controller

public class ViewAdminFormController {

    @Autowired
    private AdminService adminService;



    @FXML
    private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtDni;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtDireccion;
    @FXML private DatePicker dpFechaNacimiento;
    @FXML private TextField txtUsuario;
    @FXML private TextField txtPassword;

    private ViewAdminFormController padreController;
    private Admin adminActual;

    public void setPadreController(ViewAdminFormController padreController) {
        this.padreController = padreController;
    }

    public void cargarDatos(Admin user) {
        this.adminActual = user;
        txtNombre.setText(user.getNombre());
        txtApellido.setText(user.getApellido());
        txtDni.setText(String.valueOf(user.getDni()));
        txtTelefono.setText(user.getTelefono());
        txtDireccion.setText(user.getDireccion());
        txtUsuario.setText(user.getUserName());
        txtPassword.setText(user.getPassword());
    }

    @FXML
    public void guardar(ActionEvent event) {
        try {
            boolean esNuevo = (adminActual == null);

            if (esNuevo) {
                adminActual = new Admin();
            }

            adminActual.setNombre(txtNombre.getText());
            adminActual.setApellido(txtApellido.getText());
            adminActual.setDni(Integer.parseInt(txtDni.getText()));
            adminActual.setTelefono(txtTelefono.getText());
            adminActual.setDireccion(txtDireccion.getText());
            adminActual.setUserName(txtUsuario.getText());
            adminActual.setPassword(txtPassword.getText());


            adminService.guardarAdmin(adminActual);

        } catch (Exception e) {
            System.out.println("Error al guardar: Verifica los datos ingresados.");
            e.printStackTrace();
        }
    }

    @FXML
    public void cancelar(ActionEvent event) {
        cerrarVentana(event);
    }

    private void cerrarVentana(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
