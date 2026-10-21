package com.jardin.jardin.controller;

import com.jardin.jardin.models.Notificacion;
import com.jardin.jardin.repository.NotificacionRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@Scope("prototype")
public class ViewNabarController {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private NotificacionRepository notificacionRepository;

    @FXML
    private Button btnNotificaciones;

    @FXML
    private Label lblBadgeNotificaciones;

    private Popup popup;

    @FXML
    public void initialize() {
        actualizarBadgeNotificaciones();
    }

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

    @FXML
    void irANotificaciones(ActionEvent event) {
        cambiarVista(event, "/Views/ViewGestionVacunas.fxml", "Centro de Notificaciones y Recordatorios");
    }

    /**
     * Consulta las notificaciones no leídas y actualiza el contador sobre la
     * campanita.
     */
    public void actualizarBadgeNotificaciones() {
        List<Notificacion> noLeidas = notificacionRepository.findByEstadoOrderByFechaEnvioDesc("Pendiente de Lectura");
        int cantidadAlertas = noLeidas.size();

        if (cantidadAlertas > 0) {
            lblBadgeNotificaciones.setText(String.valueOf(cantidadAlertas));
            lblBadgeNotificaciones.setVisible(true);
        } else {
            lblBadgeNotificaciones.setVisible(false);
        }
    }

    /**
     * Despliega el menú flotante desplegable al hacer clic sobre la campanita.
     */
    @FXML
    private void mostrarPopupNotificaciones() {
        if (popup != null && popup.isShowing()) {
            popup.hide();
            popup = null;
            return;
        }

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
                            actualizarBadgeNotificaciones();
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

        HBox barraAcciones = new HBox(8);
        barraAcciones.setAlignment(Pos.CENTER);

        Button btnHistorial = new Button("📋 Historial");
        btnHistorial.setMaxWidth(Double.MAX_VALUE);
        btnHistorial.setStyle(
                "-fx-background-color: #64748B; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        HBox.setHgrow(btnHistorial, Priority.ALWAYS);
        btnHistorial.setOnAction(e -> {
            ocultarPopup();
            abrirHistorialNotificaciones();
        });

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
            actualizarBadgeNotificaciones();
            ocultarPopup();
        });

        barraAcciones.getChildren().addAll(btnHistorial, btnMarcarLeidas);
        contenedor.getChildren().add(barraAcciones);

        popup.getContent().add(contenedor);

        Window ownerWindow = btnNotificaciones.getScene().getWindow();
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/ViewDetalleNotificacion.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/ViewHistorialNotificaciones.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Historial de Notificaciones");
            stage.setScene(new Scene(root));
            stage.setOnHiding(e -> actualizarBadgeNotificaciones());

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cambiarVista(ActionEvent event, String fxmlRuta, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlRuta));
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();

            // Obtener el Stage actual desde el evento
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Obtener la escena existente en lugar de instanciar una nueva
            Scene currentScene = stage.getScene();

            if (currentScene != null) {
                // Reemplazamos únicamente el contenido de la ventana
                currentScene.setRoot(root);
            } else {
                // Respaldamos la creación inicial si por alguna razón la escena fuera nula
                stage.setScene(new Scene(root));
            }

            stage.setTitle(titulo);
            stage.show();

        } catch (Exception e) {
            System.out.println("¡Error al cambiar de vista desde el Navbar!");
            e.printStackTrace();
        }
    }
}