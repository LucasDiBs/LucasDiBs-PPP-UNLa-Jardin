package com.jardin.jardin.controller;

import com.jardin.jardin.models.Admin;
import com.jardin.jardin.service.EmailService;
import com.jardin.jardin.service.VacunacionService;
import com.jardin.jardin.models.Infante;
import com.jardin.jardin.service.InfanteService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Controller
@Scope("prototype")
public class ViewInfanteFormController {

    @Autowired
    private InfanteService infanteService;

    @Autowired
    private VacunacionService vacunacionService;

    @Autowired
    private EmailService emailService;

    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtDni;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtPapis;
    @FXML private TextField txtEmail;
    @FXML private DatePicker dpFechaNacimiento;
    @FXML private TextField txtEdadMeses;
    @FXML private TextField txtSala;

    private ViewInfantesController padreController;
    private Infante infanteActual; 

    @FXML
    public void initialize() {
        txtEdadMeses.setEditable(false);
        txtEdadMeses.setStyle("-fx-background-color: #e9ecef;");

        dpFechaNacimiento.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                long meses = ChronoUnit.MONTHS.between(newValue, LocalDate.now());
                if (meses < 0) meses = 0;
                txtEdadMeses.setText(String.valueOf(meses));
            } else {
                txtEdadMeses.clear();
            }
        });
    }

    public void setPadreController(ViewInfantesController padreController) {
        this.padreController = padreController;
    }

    public void cargarDatos(Infante infante) {
        this.infanteActual = infante;
        txtNombre.setText(infante.getNombre());
        txtApellido.setText(infante.getApellido());
        txtDni.setText(String.valueOf(infante.getDni()));
        txtDireccion.setText(infante.getDireccion());
        
        if (infante.getTelefono() != null) txtTelefono.setText(infante.getTelefono());
        if (infante.getPapis() != null && txtPapis != null) txtPapis.setText(infante.getPapis());
        if (infante.getEmail() != null && txtEmail != null) txtEmail.setText(infante.getEmail());
        
        dpFechaNacimiento.setValue(infante.getFechaNacimiento());
        txtSala.setText(infante.getSala());
    }

   @FXML
    public void guardar(ActionEvent event) {
        try {
            String dniStr = txtDni.getText();
            if (dniStr == null || !dniStr.matches("\\d{8}")) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Advertencia");
                alert.setHeaderText(null);
                alert.setContentText("El DNI debe contener exactamente 8 números.");
                alert.showAndWait();
                return; 
            }

            String emailStr = (txtEmail != null && !txtEmail.getText().isEmpty()) ? txtEmail.getText() : "";
            String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
            
            if (!emailStr.isEmpty() && !emailStr.matches(emailRegex)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Advertencia");
                alert.setHeaderText(null);
                alert.setContentText("Email debe tener un dominio válido.");
                alert.showAndWait();
                return; 
            }

            // Validar que se haya elegido una fecha para evitar errores de null
            if (dpFechaNacimiento.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Advertencia");
                alert.setHeaderText(null);
                alert.setContentText("Por favor, seleccione una fecha de nacimiento.");
                alert.showAndWait();
                return; 
            }

            boolean esNuevo = (infanteActual == null);

            if (esNuevo) {
                infanteActual = new Infante();
                infanteActual.setActivo(true);
            }

            infanteActual.setNombre(txtNombre.getText());
            infanteActual.setApellido(txtApellido.getText());
            infanteActual.setDni(Integer.parseInt(txtDni.getText()));
            infanteActual.setDireccion(txtDireccion.getText());
            infanteActual.setTelefono(txtTelefono.getText());
            
            if (txtPapis != null) infanteActual.setPapis(txtPapis.getText());
            if (txtEmail != null) infanteActual.setEmail(txtEmail.getText());

            infanteActual.setFechaNacimiento(dpFechaNacimiento.getValue());
            
            // Protección: Si la edad quedó vacía, le asignamos 0 por defecto para que no crashee
            int edad = 0;
            if (txtEdadMeses.getText() != null && !txtEdadMeses.getText().trim().isEmpty()) {
                edad = Integer.parseInt(txtEdadMeses.getText().trim());
            }
            infanteActual.setEdadEnMeses(edad);
            
            infanteActual.setSala(txtSala.getText());
            
            infanteService.guardar(infanteActual);

            if (esNuevo) {
                vacunacionService.generarCalendarioParaInfante(infanteActual);
                informeAlta(infanteActual);
            } else {
                informeModificacion(infanteActual);
            }

            if (padreController != null) {
                padreController.cargarTabla();
            }
            
            cerrarVentana(event);
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Interno");
            alert.setHeaderText("No se pudo guardar el infante");
            alert.setContentText("Causa: " + e.getMessage() + "\nRevisá la consola para más detalles.");
            alert.showAndWait();
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

    private void informeAlta(Infante infante){
        String correoDestino = infante.getEmail();
        if (correoDestino == null || correoDestino.isEmpty()) return;

        emailService.enviar(
                correoDestino,
                "Cambio de estado del infante",
                "Hola,\n\n"
                        + "El estado del infante "
                        + infante.getNombre()
                        + " "
                        + infante.getApellido()
                        + " fue dado de alta en el jardin Azucena Villaflor "
                        + ".\n\n"
                        + "Saludos."
        );
    }

    private void informeModificacion(Infante infante){
        String correoDestino = infante.getEmail();
        if (correoDestino == null || correoDestino.isEmpty()) return;

        emailService.enviar(
                correoDestino,
                "Datos actualizados",
                "Hola,\n\n"
                        + "Los datos del infante han sido actualizados:\n"
                        + "DNI: " + infante.getDni() + "\n"
                        + "Nombre: " + infante.getNombre() + " " + infante.getApellido() + "\n"
                        + "Nacimiento: " + infante.getFechaNacimiento() + "\n"
                        + "Sala: " + infante.getSala() + "\n"
                        + "Teléfono: " + infante.getTelefono() + "\n"
                        + "Dirección: " + infante.getDireccion() + "\n\n"
                        + "Saludos."
        );
    }
}