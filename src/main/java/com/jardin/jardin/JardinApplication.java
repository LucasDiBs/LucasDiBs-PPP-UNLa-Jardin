package com.jardin.jardin;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JardinApplication extends Application {

    private ConfigurableApplicationContext springContext;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        // Iniciar Spring Boot
        springContext = SpringApplication.run(JardinApplication.class);

        // 1. Corregimos la ruta apuntando a la carpeta Views y al archivo del CRUD
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Views/ViewInfantes.fxml"));

        // Le indicamos a JavaFX que los Controllers los cree Spring
        fxmlLoader.setControllerFactory(springContext::getBean);

        // 2. Cargamos la escena sin pasarle parámetros extra al load()
        Scene scene = new Scene(fxmlLoader.load());

        // Mostrar ventana
        stage.setScene(scene);
        stage.setTitle("Panel de Pruebas - Gestión de Infantes");
        stage.show();
    }

    @Override
    public void stop() {
        // Cerrar Spring cuando se cierre JavaFX
        if (springContext != null) {
            springContext.close();
        }
    }
}