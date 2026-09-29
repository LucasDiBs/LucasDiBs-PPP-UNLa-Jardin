package com.jardin.jardin.controller;

import com.jardin.jardin.models.Admin;
import com.jardin.jardin.repository.AdminRepository;
import com.jardin.jardin.service.AdminService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

@Controller

public class ViewAdminFormController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private AdminRepository repository;


    @FXML
    private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtDni;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtUsuario;
    @FXML private TextField txtPassword;

    private ViewAdminListaController padreController;
    private Admin adminActual;

    public void setPadreController(ViewAdminListaController padreController) {
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
        }

    @FXML
    public void guardar(ActionEvent event) {
        try {
            boolean esNuevo = (adminActual == null);

            if (esNuevo) {
                adminActual = new Admin();
            }

            String nuevoUsuario = txtUsuario.getText();

            // Verificamos si el nombre de usuario ya le pertenece a OTRO administrador
            Admin aux = repository.findByUserName(nuevoUsuario);
            if (aux != null && (esNuevo || !aux.getPersonaId().equals(adminActual.getPersonaId()))) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Advertencia");
                alert.setHeaderText(null);
                alert.setContentText("El nombre de usuario ya está en uso por otro administrador.");
                alert.showAndWait();
                return; // Cortamos la ejecución para que no guarde
            }

            // Seteamos los datos en el objeto
            adminActual.setNombre(txtNombre.getText());
            adminActual.setApellido(txtApellido.getText());
            adminActual.setDni(Integer.parseInt(txtDni.getText()));
            adminActual.setTelefono(txtTelefono.getText());
            adminActual.setDireccion(txtDireccion.getText());
            adminActual.setUserName(nuevoUsuario);
            adminActual.setPassword(txtPassword.getText());

            // Guardamos en la base de datos
            adminService.guardarAdmin(adminActual);

            // Si tenés referencia al controlador principal, refrescamos la tabla
            if (padreController != null) {
                padreController.cargarTabla();
            }
            

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de formato");
            alert.setContentText("El DNI debe ser un número válido.");
            alert.showAndWait();
        } catch (Exception e) {
            System.out.println("Error al guardar: Verifica los datos ingresados.");
            e.printStackTrace();
        }
    }
    private void cerrarVentana(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
