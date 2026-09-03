package com.jardin.jardin.controller;

import com.jardin.jardin.models.Infante;
import com.jardin.jardin.service.InfanteService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import java.util.List;

@Controller
public class ViewInfantesController {

    @Autowired
    private InfanteService infanteService;

    @Autowired
    private ApplicationContext applicationContext;

    @FXML
    private TableView<Infante> tablaInfantes;
    
    @FXML
    private TableColumn<Infante, Void> colAcciones;

    @FXML
    private TableColumn<Infante, Boolean> colEstado;

    @FXML
    public void initialize() {
        configurarColumnaEstado();
        configurarColumnasAcciones();
        cargarTabla();
    }

    public void cargarTabla() {
        // Traemos TODOS los infantes
        List<Infante> lista = infanteService.listarTodos();

        // Ordenamos: Primero los activos, despues por sala
        lista.sort((i1, i2) -> {
            // Comparamos el estado (true viene antes que false)
            int comparacionEstado = Boolean.compare(i2.isActivo(), i1.isActivo());
            if (comparacionEstado != 0) {
                return comparacionEstado;
            }
            // Si tienen el mismo estado, ordenamos por sala
            String sala1 = i1.getSala() != null ? i1.getSala() : "";
            String sala2 = i2.getSala() != null ? i2.getSala() : "";
            return sala1.compareToIgnoreCase(sala2);
        });

        ObservableList<Infante> data = FXCollections.observableArrayList(lista);
        tablaInfantes.setItems(data);
    }

    private void configurarColumnaEstado() {
        colEstado.setCellValueFactory(new PropertyValueFactory<>("activo"));
        
        // Transformamos el "true/false" en texto "Activo/Inactivo" con colores
        colEstado.setCellFactory(column -> new TableCell<Infante, Boolean>() {
            @Override
            protected void updateItem(Boolean activo, boolean empty) {
                super.updateItem(activo, empty);
                if (empty || activo == null) {
                    setText(null);
                    setStyle("");
                } else {
                    if (activo) {
                        setText("Activo");
                        setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;"); // Verde
                    } else {
                        setText("Inactivo");
                        setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold;"); // Rojo
                    }
                }
            }
        });
    }

    private void configurarColumnasAcciones() {
        Callback<TableColumn<Infante, Void>, TableCell<Infante, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Infante, Void> call(final TableColumn<Infante, Void> param) {
                return new TableCell<>() {
                    private final Button btnModificar = new Button("Modificar");
                    private final Button btnBaja = new Button("Dar de baja");
                    private final HBox pane = new HBox(10, btnModificar, btnBaja);

                    {
                        btnModificar.setStyle("-fx-background-color: #FFC107; -fx-cursor: hand;");
                        btnBaja.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-cursor: hand;");

                        btnModificar.setOnAction(event -> {
                            Infante infante = getTableView().getItems().get(getIndex());
                            abrirModalFormulario(infante);
                        });

                        btnBaja.setOnAction(event -> {
                            Infante infante = getTableView().getItems().get(getIndex());
                            abrirModalBaja(infante);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            // Obtenemos el infante de esta fila
                            Infante infante = getTableView().getItems().get(getIndex());
                            
                            // Si el infante ya está inactivo, desactivamos los botones
                            btnModificar.setDisable(!infante.isActivo());
                            btnBaja.setDisable(!infante.isActivo());
                            
                            setGraphic(pane);
                        }
                    }
                };
            }
        };
        colAcciones.setCellFactory(cellFactory);
    }

    @FXML
    public void abrirFormulario(ActionEvent event) {
        abrirModalFormulario(null);
    }

    private void abrirModalFormulario(Infante infante) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/ViewInfanteForm.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();
            
            ViewInfanteFormController formController = loader.getController();
            formController.setPadreController(this);
            
            if (infante != null) {
                formController.cargarDatos(infante);
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(infante == null ? "Alta de Nuevo Infante" : "Modificar Infante");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void abrirModalBaja(Infante infante) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/ViewInfanteBaja.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();
            
            ViewInfanteBajaController bajaController = loader.getController();
            bajaController.setPadreController(this);
            bajaController.cargarInfante(infante);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Confirmar Baja");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}