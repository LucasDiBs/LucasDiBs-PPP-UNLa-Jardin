package com.jardin.jardin;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class JardinApplication extends Application {

	private ConfigurableApplicationContext springContext;

	public static void main(String[] args) {

		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {

		// Iniciar Spring Boot
		springContext = SpringApplication.run(JardinApplication.class);

		// Cargar la interfaz FXML
		FXMLLoader fxmlLoader =
				new FXMLLoader(getClass().getResource("/Home.fxml"));

		// Le indicamos a JavaFX que los Controllers los cree Spring
		fxmlLoader.setControllerFactory(springContext::getBean);

		String titulo = springContext.getBean("titulo", String.class);
		// Crear la escena
		Scene scene = new Scene(fxmlLoader.load());

		// Mostrar ventana
		stage.setScene(scene);
		stage.setTitle(titulo);
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
