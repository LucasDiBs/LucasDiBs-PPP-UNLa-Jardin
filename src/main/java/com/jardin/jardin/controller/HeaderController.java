package com.jardin.jardin.controller;

import com.jardin.jardin.models.Notificacion;
import com.jardin.jardin.repository.NotificacionRepository;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class HeaderController {

    @FXML
    private Label lblTituloModulo;
    @FXML
    private Button btnNotificaciones;
    @FXML
    private Label lblBadgeNotificaciones;

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private ApplicationContext springContext;

    private Popup popup;

    @FXML
    public void initialize() {
        actualizarContadorNotificaciones();
    }

    public void setTituloModulo(String titulo) {
        lblTituloModulo.setText(titulo);
    }

    public void actualizarContadorNotificaciones() {
        List<Notificacion> noLeidas = notificacionRepository.findByEstadoOrderByFechaEnvioDesc("Pendiente de Lectura");
        int cantidad = noLeidas.size();

        if (cantidad > 0) {
            lblBadgeNotificaciones.setText(String.valueOf(cantidad));
            lblBadgeNotificaciones.setVisible(true);
        } else {
            lblBadgeNotificaciones.setVisible(false);
        }
    }

    @FXML
    private void mostrarPopupNotificaciones() {
        // Si el popup existe y está abierto, lo cerramos y destruimos la referencia
        if (popup != null && popup.isShowing()) {
            popup.hide();
            popup = null;
            return;
        }

        // Creamos una nueva instancia limpia de Popup para evitar bloqueos
        popup = new Popup();
        popup.setAutoHide(true);

        VBox contenedor = new VBox(10);
        contenedor.setPadding(new Insets(15));
        contenedor.setStyle(
                "-fx-background-color: #FFFFFF; -fx-border-color: #CBD5E1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 10, 0, 0, 5);");
        contenedor.setPrefWidth(390);

        Label lblTitulo = new Label("Notificaciones");
        lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #0F172A;");
        contenedor.getChildren().add(lblTitulo);

        List<Notificacion> listaNotificaciones = notificacionRepository
                .findByEstadoOrderByFechaEnvioDesc("Pendiente de Lectura");

        if (listaNotificaciones.isEmpty()) {
            Label lblVacio = new Label("No hay notificaciones pendientes.");
            lblVacio.setPadding(new Insets(10, 0, 10, 0));
            lblVacio.setStyle("-fx-text-fill: #64748B; -fx-font-style: italic;");
            contenedor.getChildren().add(lblVacio);
        } else {
            ListView<Notificacion> listView = new ListView<>();
            listView.getItems().addAll(listaNotificaciones);
            listView.setPrefHeight(260);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            listView.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Notificacion item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        VBox box = new VBox(4);

                        Label msj = new Label(item.getMensaje());
                        msj.setWrapText(true);
                        msj.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");

                        HBox filaInferior = new HBox(6);
                        filaInferior.setAlignment(Pos.CENTER_LEFT);

                        String fechaTexto = item.getFechaEnvio() != null ? item.getFechaEnvio().format(formatter) : "";
                        Label fecha = new Label(fechaTexto);
                        fecha.setStyle("-fx-font-size: 10px; -fx-text-fill: #64748B;");
                        HBox.setHgrow(fecha, Priority.ALWAYS);

                        Button btnVer = new Button("👁 Ver");
                        btnVer.setStyle(
                                "-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-size: 10px; -fx-cursor: hand; -fx-padding: 2 6 2 6;");
                        btnVer.setOnAction(e -> {
                            ocultarPopup();
                            abrirDetalleNotificacion(item);
                        });

                        Button btnMarcarIndividual = new Button("✔ Leída");
                        btnMarcarIndividual.setStyle(
                                "-fx-background-color: #E2E8F0; -fx-text-fill: #334155; -fx-font-size: 10px; -fx-cursor: hand; -fx-padding: 2 6 2 6;");
                        btnMarcarIndividual.setOnAction(e -> {
                            item.setEstado("LEIDA");
                            notificacionRepository.save(item);
                            actualizarContadorNotificaciones();
                            ocultarPopup();
                            mostrarPopupNotificaciones();
                        });

                        filaInferior.getChildren().addAll(fecha, btnVer, btnMarcarIndividual);

                        box.getChildren().addAll(msj, filaInferior);
                        box.setPadding(new Insets(4, 0, 4, 0));
                        setGraphic(box);
                    }
                }
            });

            contenedor.getChildren().add(listView);
        }

        // --- Barra de Acciones Inferior ---
        HBox barraAcciones = new HBox(8);
        barraAcciones.setAlignment(Pos.CENTER);

        // Botón: Ver Historial
        Button btnHistorial = new Button("📋 Historial");
        btnHistorial.setMaxWidth(Double.MAX_VALUE);
        btnHistorial.setStyle(
                "-fx-background-color: #64748B; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        HBox.setHgrow(btnHistorial, Priority.ALWAYS);
        btnHistorial.setOnAction(e -> {
            ocultarPopup();
            abrirHistorialNotificaciones();
        });

        // Botón: Marcar todas como leídas
        Button btnMarcarLeidas = new Button("✔ Marcar todas leídas");
        btnMarcarLeidas.setMaxWidth(Double.MAX_VALUE);
        btnMarcarLeidas.setStyle(
                "-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        HBox.setHgrow(btnMarcarLeidas, Priority.ALWAYS);
        btnMarcarLeidas.setOnAction(e -> {
            for (Notificacion n : listaNotificaciones) {
                n.setEstado("LEIDA");
            }
            notificacionRepository.saveAll(listaNotificaciones);
            actualizarContadorNotificaciones();
            ocultarPopup();
        });

        barraAcciones.getChildren().addAll(btnHistorial, btnMarcarLeidas);
        contenedor.getChildren().add(barraAcciones);

        popup.getContent().add(contenedor);

        // Obtener ventana actual
        Window ownerWindow = btnNotificaciones.getScene().getWindow();

        // Calcular posición en pantalla
        double x = btnNotificaciones.localToScreen(btnNotificaciones.getBoundsInLocal()).getMinX() - 320;
        double y = btnNotificaciones.localToScreen(btnNotificaciones.getBoundsInLocal()).getMaxY() + 5;

        popup.show(ownerWindow, x, y);
    }

    private void ocultarPopup() {
        if (popup != null) {
            popup.hide();
            popup = null;
        }
    }

    private void abrirDetalleNotificacion(Notificacion notificacion) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ViewDetalleNotificacion.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            ViewDetalleNotificacionController controller = loader.getController();
            controller.setNotificacion(notificacion);

            Stage stage = new Stage();
            stage.setTitle("Detalle de la Notificación");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void abrirHistorialNotificaciones() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ViewHistorialNotificaciones.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Historial de Notificaciones");
            stage.setScene(new Scene(root));

            // Al cerrar la ventana modal del historial, refrescamos el contador del header
            stage.setOnHiding(e -> actualizarContadorNotificaciones());

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}