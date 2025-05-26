package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import model.Session; // Usando Session para mantener sesión admin

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;

public class ControlMantenimientoController {

    @FXML
    private Button irMantenimientoSala;

    @FXML
    private Button irMantenimientoEquipo;

    // Conexión a la base de datos para ADMIN
    private Connection connection;

    @FXML
    public void initialize() {
        // Usa la conexión de la sesión activa (admin)
        this.connection = Session.getConnection();
        if (this.connection == null) {
            System.err.println("❌ Error: No hay sesión activa de admin. Debes iniciar sesión como admin antes de usar este módulo.");
        }
    }

    @FXML
    private void irMantenimientoSala() {
        cargarEscena(irMantenimientoSala, "/view/MantenimientoSala.fxml");
    }

    @FXML
    private void irMantenimientoEquipo() {
        cargarEscena(irMantenimientoEquipo, "/view/MantenimientoEquipo.fxml");
    }

    private void cargarEscena(Button boton, String fxmlPath) {
        if (boton == null) {
            System.err.println("❌ Error: Botón es NULL. Verifica su `fx:id` en el FXML.");
            return;
        }

        if (connection == null) {
            System.err.println("❌ Error: La conexión a la base de datos es NULL o no se ha iniciado sesión como admin.");
            return;
        }

        URL fxmlLocation = getClass().getResource(fxmlPath);
        if (fxmlLocation == null) {
            System.err.println("❌ Error: Archivo FXML no encontrado en la ruta " + fxmlPath);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            if (fxmlPath.contains("MantenimientoEquipo.fxml")) {
                MantenimientoEquipoController controller = loader.getController();
                controller.init(connection); // Inyecta la conexión de admin
            }

            if (fxmlPath.contains("MantenimientoSala.fxml")) {
                MantenimientoSalaController controller = loader.getController();
                controller.init(connection); // Inyecta la conexión de admin
            }

            Stage stage = (Stage) boton.getScene().getWindow();
            double currentWidth = stage.getWidth();
            double currentHeight = stage.getHeight();
            stage.setScene(new Scene(root));
            stage.setWidth(currentWidth);
            stage.setHeight(currentHeight);
            stage.show();

        } catch (IOException e) {
            System.err.println("❌ Error al cargar la escena: " + fxmlPath);
            e.printStackTrace();
        }
    }
}