package com.jardin.jardin.controller;

import com.jardin.jardin.models.CalendarioInfante;
import com.jardin.jardin.service.AlertaVacunasCron;
import com.jardin.jardin.service.VacunacionService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class ViewGestionVacunasController {

    @Autowired
    private VacunacionService vacunacionService;
    @Autowired
    private AlertaVacunasCron alertaVacunasCron;

    @FXML
    private TextField txtBuscarInfante;
    @FXML
    private ComboBox<String> comboEstado;
    @FXML
    private TableView<CalendarioInfante> tablaAlertas;
    @FXML
    private TableColumn<CalendarioInfante, String> colInfante;
    @FXML
    private TableColumn<CalendarioInfante, String> colSala;
    @FXML
    private TableColumn<CalendarioInfante, String> colVacuna;
    @FXML
    private TableColumn<CalendarioInfante, String> colFechaEstimada;
    @FXML
    private TableColumn<CalendarioInfante, String> colEstado;
    @FXML
    private Button btnMarcarAplicada;
    @FXML
    private Button btnRevertirAplicacion;

    // Se reemplaza el viejo HeaderController por ViewNabarController
    // Debe coincidir con el fx:id="viewNabar" del include en el FXML
    @FXML
    private ViewNabarController viewNabarController;

    private ObservableList<CalendarioInfante> listaAlertas = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Cargar opciones en el Combo
        if (comboEstado != null) {
            comboEstado.setItems(FXCollections.observableArrayList(
                    "Pendientes / Vencidas",
                    "Aplicadas (Historial)",
                    "Todas"));
            comboEstado.getSelectionModel().selectFirst();
        }

        // Mapeo de columnas
        if (colInfante != null) {
            colInfante.setCellValueFactory(cell -> new SimpleStringProperty(
                    cell.getValue().getInfante().getApellido() + ", " + cell.getValue().getInfante().getNombre()));
            colSala.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getInfante().getSala()));
            colVacuna.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getVacuna().getNombre()));
            colFechaEstimada.setCellValueFactory(
                    cell -> new SimpleStringProperty(cell.getValue().getFechaEstimada().toString()));
            colEstado.setCellValueFactory(cell -> {
                boolean aplicada = cell.getValue().isAplicada();
                LocalDate fechaEst = cell.getValue().getFechaEstimada();
                if (aplicada)
                    return new SimpleStringProperty("Aplicada");
                if (fechaEst.isBefore(LocalDate.now()))
                    return new SimpleStringProperty("VENCIDA");
                return new SimpleStringProperty("Pendiente");
            });
        }

        cargarAlertas();
    }

    private void cargarAlertas() {
        if (comboEstado == null)
            return;

        String seleccion = comboEstado.getValue();
        List<CalendarioInfante> datos;

        if ("Aplicadas (Historial)".equals(seleccion)) {
            datos = vacunacionService.obtenerVacunasAplicadas();
            if (btnMarcarAplicada != null)
                btnMarcarAplicada.setDisable(true);
        } else if ("Todas".equals(seleccion)) {
            datos = vacunacionService.obtenerTodasLasVacunas();
            if (btnMarcarAplicada != null)
                btnMarcarAplicada.setDisable(false);
        } else {
            datos = vacunacionService.obtenerVacunasPendientesOVencidas();
            if (btnMarcarAplicada != null)
                btnMarcarAplicada.setDisable(false);
        }

        listaAlertas.setAll(datos);
        if (tablaAlertas != null) {
            tablaAlertas.setItems(listaAlertas);
        }
    }

    @FXML
    private void handleCambioFiltroEstado() {
        cargarAlertas();
    }

    @FXML
    private void handleBuscar() {
        String filtro = txtBuscarInfante.getText().toLowerCase().trim();
        if (filtro.isBlank()) {
            cargarAlertas();
            return;
        }
        ObservableList<CalendarioInfante> filtradas = listaAlertas
                .filtered(item -> item.getInfante().getApellido().toLowerCase().contains(filtro) ||
                        String.valueOf(item.getInfante().getDni()).contains(filtro));
        tablaAlertas.setItems(filtradas);
    }

    @FXML
    private void handleGenerarAlertas() {
        // 1. Corre el proceso que evalúa las vacunas y guarda las alertas
        alertaVacunasCron.buscarVacunasProximas();

        // 2. Refresca la tabla de la vista
        cargarAlertas();

        // 3. Notifica al ViewNabarController para recalcular la badge
        if (viewNabarController != null) {
            viewNabarController.actualizarBadgeNotificaciones();
        }
    }

    @FXML
    private void handleMarcarAplicada() {
        CalendarioInfante seleccion = tablaAlertas.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención", "Seleccioná una vacuna de la lista.");
            return;
        }

        if (seleccion.isAplicada()) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Aviso", "Esta vacuna ya figura como aplicada.");
            return;
        }

        vacunacionService.registrarAplicacionVacuna(seleccion.getId());
        cargarAlertas();
        mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "La vacuna se registró como aplicada.");
    }

    @FXML
    private void handleRevertirAplicacion() {
        CalendarioInfante seleccion = tablaAlertas.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención", "Seleccioná una vacuna de la lista para revertir.");
            return;
        }

        if (!seleccion.isAplicada()) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Aviso", "Esta vacuna ya figura como pendiente o vencida.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Reversión");
        confirmacion.setHeaderText("¿Estás seguro de que deseas revertir el estado?");
        confirmacion.setContentText("La vacuna '" + seleccion.getVacuna().getNombre() +
                "' registrada para " + seleccion.getInfante().getApellido() +
                " pasará nuevamente a estado PENDIENTE / VENCIDA.");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            vacunacionService.revertirAplicacionVacuna(seleccion.getId());
            cargarAlertas();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "El registro fue revertido a pendiente correctamente.");
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String msj) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msj);
        alert.showAndWait();
    }
}