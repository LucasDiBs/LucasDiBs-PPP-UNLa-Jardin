package com.jardin.jardin.controller;

import com.jardin.jardin.models.Admin;
import com.jardin.jardin.repository.AdminRepository;
import com.jardin.jardin.service.AdminService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

@Controller
public class ViewAdminUpdateController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private AdminRepository repository;
    @Autowired
    private ApplicationContext applicationContext;


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
        // Opcional: si querés que la contraseña no se obligue a reescribir o se limpie
        // txtPassword.setText(user.getPassword());
    }

    @FXML
    public void guardar(ActionEvent event) {
        try {
            boolean esNuevo = (adminActual == null);
            String nuevoUsuario = txtUsuario.getText().trim();

            // 1. Validar si el username ya existe en la base de datos
            Admin aux = repository.findByUserName(nuevoUsuario); // O repository.findByUserName(nuevoUsuario)

            if (aux != null) {
                // Si existe, verificamos si pertenece a OTRA persona (evita conflicto si edita y deja su propio nombre)
                if (esNuevo || !aux.getPersonaId().equals(adminActual.getPersonaId())) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Advertencia");
                    alert.setHeaderText(null);
                    alert.setContentText("El nombre de usuario ya está en uso por otro administrador.");
                    alert.showAndWait();
                    return; // Detenemos el guardado
                }
            }

            // 2. Si es nuevo, instanciamos. Si es edición, adminActual ya trae su ID.
            if (esNuevo) {
                adminActual = new Admin();
            }

            // 3. Seteamos todos los valores actualizados
            adminActual.setNombre(txtNombre.getText());
            adminActual.setApellido(txtApellido.getText());
            adminActual.setDni(Integer.parseInt(txtDni.getText()));
            adminActual.setTelefono(txtTelefono.getText());
            adminActual.setDireccion(txtDireccion.getText());
            adminActual.setUserName(nuevoUsuario);
            adminActual.setPassword(txtPassword.getText());

            // 4. Guardamos mediante el servicio
            adminService.guardarAdmin(adminActual);

            // 5. Refrescamos la tabla del padre si existe
            if (padreController != null) {
                padreController.cargarTabla();
            }

            cerrarVentana(event);

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de formato");
            alert.setHeaderText(null);
            alert.setContentText("El DNI debe ser un número entero válido.");
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


