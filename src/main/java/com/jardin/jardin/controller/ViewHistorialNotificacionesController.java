package com.jardin.jardin.controller;

import com.jardin.jardin.models.Notificacion;
import com.jardin.jardin.repository.NotificacionRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class ViewHistorialNotificacionesController {

    @FXML
    private ComboBox<String> cmbFiltroEstado;
    @FXML
    private TableView<Notificacion> tblNotificaciones;
    @FXML
    private TableColumn<Notificacion, String> colFecha;
    @FXML
    private TableColumn<Notificacion, String> colMensaje;
    @FXML
    private TableColumn<Notificacion, String> colEstado;
    @FXML
    private TableColumn<Notificacion, Void> colAcciones;

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private ApplicationContext springContext;

    private final ObservableList<Notificacion> listaObservable = FXCollections.observableArrayList();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        configurarComboFiltro();
        configurarTabla();
        cargarNotificaciones();
    }

    private void configurarComboFiltro() {
        cmbFiltroEstado.getItems().addAll("Todas", "Pendiente de Lectura", "LEIDA");
        cmbFiltroEstado.setValue("Todas");
    }

    private void configurarTabla() {
        colFecha.setCellValueFactory(cellData -> {
            var fecha = cellData.getValue().getFechaEnvio();
            return new SimpleStringProperty(fecha != null ? fecha.format(formatter) : "-");
        });

        colMensaje.setCellValueFactory(new PropertyValueFactory<>("mensaje"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnVer = new Button("👁 Ver");

            {
                btnVer.setStyle(
                        "-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 3 8 3 8;");
                btnVer.setOnAction(e -> {
                    Notificacion notificacion = getTableView().getItems().get(getIndex());
                    abrirDetalle(notificacion);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnVer);
                }
            }
        });

        tblNotificaciones.setItems(listaObservable);
    }

    @FXML
    private void handleFiltrar() {
        cargarNotificaciones();
    }

    public void cargarNotificaciones() {
        String seleccion = cmbFiltroEstado.getValue();
        List<Notificacion> resultado;

        if ("Todas".equals(seleccion) || seleccion == null) {
            resultado = notificacionRepository.findAll();
        } else {
            resultado = notificacionRepository.findByEstadoOrderByFechaEnvioDesc(seleccion);
        }

        listaObservable.setAll(resultado);
    }

    private void abrirDetalle(Notificacion notificacion) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ViewDetalleNotificacion.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            ViewDetalleNotificacionController controller = loader.getController();
            controller.setNotificacion(notificacion);

            Stage stage = new Stage();
            stage.setTitle("Detalle de la Notificación");
            stage.setScene(new Scene(root));

            stage.setOnHiding(e -> cargarNotificaciones());

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}