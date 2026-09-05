package com.jardin.jardin.controller;

import com.jardin.jardin.models.Admin;
import com.jardin.jardin.models.Infante;
import com.jardin.jardin.service.AdminService;
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
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ViewAdminListaController {

    @Autowired
    private AdminService service;

    static class ViewAdminListaControllerService {

    };

    @Autowired
    private ApplicationContext applicationContext;

    @FXML
    private TableView<Admin> tablaAdmins;

    @FXML
    private TableColumn<Admin, Void> colAcciones;



    @FXML
    public void initialize() {
        configurarColumnasAcciones();
        cargarTabla();
    }

    public void cargarTabla() {
        // Traemos TODOS los infantes
        List<Admin> lista = service.listarTodosAdmin();




        ObservableList<Admin> data = FXCollections.observableArrayList(lista);
        tablaAdmins.setItems(data);
    }


    private void configurarColumnasAcciones() {
        Callback<TableColumn<Admin, Void>, TableCell<Admin, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Admin, Void> call(final TableColumn<Admin, Void> param) {
                return new TableCell<>() {
                    private final Button btnModificar = new Button("Modificar");

                    private final HBox pane = new HBox(10, btnModificar);

                    {
                        btnModificar.setStyle("-fx-background-color: #FFC107; -fx-cursor: hand;");

                        btnModificar.setOnAction(event -> {
                            Admin admin = getTableView().getItems().get(getIndex());
                            abrirModalFormulario(admin);
                        });

                       }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            // Obtenemos el infante de esta fila
                            Admin admin = getTableView().getItems().get(getIndex());

                            // Si el infante ya está inactivo, desactivamos los botones


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

    private void abrirModalFormulario(Admin admin) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/ViewAdminUpdate.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();

            ViewAdminUpdateController formController = loader.getController();
            formController.setPadreController(this);

            if (admin != null) {
                formController.cargarDatos(admin);
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(admin == null ? "Alta de Nuevo Admin" : "Modificar Admin");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
