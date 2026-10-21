package com.jardin.jardin.controller;

import com.jardin.jardin.models.Notificacion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
@Scope("prototype")
public class ViewDetalleNotificacionController {

    @FXML
    private Label lblMensaje;
    @FXML
    private Label lblInfante;
    @FXML
    private Label lblTutor;
    @FXML
    private Label lblFecha;
    @FXML
    private TextArea txtMensajeOpcional;
    @FXML
    private Button btnCerrar;
    @FXML
    private Button btnEnviarAlerta;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void setNotificacion(Notificacion notificacion) {
        if (notificacion != null) {
            lblMensaje.setText(notificacion.getMensaje() != null ? notificacion.getMensaje() : "-");
            lblFecha.setText(
                    notificacion.getFechaEnvio() != null ? notificacion.getFechaEnvio().format(formatter) : "-");

            // Si tenés relaciones a Infante o Tutor en el modelo Notificacion:
            // lblInfante.setText(notificacion.getInfante() != null ?
            // notificacion.getInfante().getNombreCompleto() : "-");
            // lblTutor.setText(notificacion.getTutor() != null ?
            // notificacion.getTutor().getNombreCompleto() : "-");
        }
    }

    @FXML
    private void handleCerrar(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleEnviarAlerta(ActionEvent event) {
        // Lógica para enviar el mail al tutor incorporando txtMensajeOpcional.getText()
    }
}