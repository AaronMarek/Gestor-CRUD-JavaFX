package com.ejem.ejem;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Main extends Application {

	public static ConfigurableApplicationContext context;

	public static void main(String[] args) {
		launch();
	}

	@Override
	public void start(Stage stage) throws Exception {
		// Inicializar el contexto de Spring
		context = SpringApplication.run(Main.class);

		// Cargar la interfaz gráfica
		FXMLLoader fxml = new FXMLLoader(getClass().getResource("/com/java/fx/main.fxml"));
		fxml.setControllerFactory(context::getBean);

		Scene scene = new Scene(fxml.load());
		stage.setTitle("Crud proyecto");
		stage.setScene(scene);

		// Agregar manejador para confirmar cierre
		stage.setOnCloseRequest(event -> {
			event.consume(); // Consumir el evento para manejarlo manualmente
			confirmClose(stage);
		});

		stage.show();
	}

	private void confirmClose(Stage stage) {
		// Crear un cuadro de diálogo de confirmación
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Confirmación de cierre");
		alert.setHeaderText("¿Está seguro que desea cerrar la aplicación?");
		alert.setContentText("Si cierra, los cambios no guardados se perderán.");

		// Manejar la respuesta del usuario
		ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);
		if (result == ButtonType.OK) {
			stop(); // Llama al método stop para cerrar todo correctamente
			stage.close(); // Cierra la ventana principal
		}
	}

	@Override
	public void stop() {
		if (context != null) {
			context.close(); // Cierra el contexto de Spring Boot
		}
		Platform.exit(); // Finaliza la aplicación JavaFX
	}
}
