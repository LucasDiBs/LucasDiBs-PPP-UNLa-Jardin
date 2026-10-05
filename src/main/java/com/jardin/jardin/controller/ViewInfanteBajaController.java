package com.jardin.jardin.controller;

import com.jardin.jardin.models.Infante;
import com.jardin.jardin.service.EmailService;
import com.jardin.jardin.service.InfanteService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Controller
@Scope("prototype")
public class ViewInfanteBajaController {

    @Autowired
    private InfanteService infanteService;

    @FXML
    private Label lblDatosInfante;

    @Autowired
    private EmailService emailService;

    private ViewInfantesController padreController;
    private Infante infanteSeleccionado;

    public void setPadreController(ViewInfantesController padreController) {
        this.padreController = padreController;
    }

    public void cargarInfante(Infante infante) {
        this.infanteSeleccionado = infante;
        lblDatosInfante.setText(infante.getNombre() + " " + infante.getApellido() + " - DNI: " + infante.getDni());
    }

    @FXML
    public void confirmarBaja(ActionEvent event) {
        infanteService.bajaLogica(infanteSeleccionado.getPersonaId());

        informeBaja(infanteSeleccionado);
        if (padreController != null) {
            padreController.cargarTabla();
        }
        cerrarVentana(event);
    }

    private void informeBaja(Infante infante){

        emailService.enviar(
                infante.getEmail(),
                "Cambio de estado del infante",
                "Hola,\n\n"
                        + "El estado del infante "
                        + infante.getNombre()
                        + " "
                        + infante.getApellido()
                        + " fue dado de baja en el jardin Azucena Villaflor "
                        + ".\n\n"
                        + "Saludos."
        );
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