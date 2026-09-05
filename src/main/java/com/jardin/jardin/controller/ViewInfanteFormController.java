package com.jardin.jardin.controller;

import com.jardin.jardin.models.Admin;
import com.jardin.jardin.service.VacunacionService;
import com.jardin.jardin.models.Infante;
import com.jardin.jardin.service.InfanteService;
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
        txtTelefono.setText(infante.getTelefono());
        dpFechaNacimiento.setValue(infante.getFechaNacimiento());
        txtSala.setText(infante.getSala());
    }

    @FXML
    public void guardar(ActionEvent event) {
        try {
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