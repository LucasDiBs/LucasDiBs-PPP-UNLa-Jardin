package com.jardin.jardin.controller;

import com.jardin.jardin.models.Admin;
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
        txtPapis.setText(infante.getPapis());
        txtEmail.setText(infante.getEmail());
        txtTelefono.setText(infante.getTelefono());
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
                // Detiene la ejecución si no es válido
            }

            // 2. Validar Email con dominio válido
            String emailStr = txtEmail.getText();
            // Regex estándar para correos con formato texto@dominio.extensión (ej: 2 a más letras en la extensión)
            String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
            if (emailStr == null || !emailStr.matches(emailRegex)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Advertencia");
                alert.setHeaderText(null);
                alert.setContentText("Email debe tener un dominio valido");
                alert.showAndWait();
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
            infanteActual.setPapis(txtPapis.getText());
            infanteActual.setEmail(txtEmail.getText());
            infanteActual.setFechaNacimiento(dpFechaNacimiento.getValue());
            infanteActual.setEdadEnMeses(Integer.parseInt(txtEdadMeses.getText()));
            infanteActual.setSala(txtSala.getText());

            infanteService.guardar(infanteActual);

            if (esNuevo) {
                vacunacionService.generarCalendarioParaInfante(infanteActual);
            }

            if (padreController != null) {
                padreController.cargarTabla();
            }
            cerrarVentana(event);
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