package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import model.Session; // Importa la clase Session para manejo de sesión admin

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;

public class GestionEquiposMenuController {

    @FXML private Button btnGestionarEquipos;
    @FXML private Button btnEquiposReservados;
    @FXML private Button btnHistorialEquipos;

    // Conexión a la base de datos (admin)
    private Connection connection;

    @FXML
    public void initialize() {
        // Usa la conexión de la sesión activa (admin)
        this.connection = Session.getConnection();
        if (this.connection == null) {
            System.err.println("❌ Error: No hay sesión activa de admin. Debes iniciar sesión como admin antes de usar este menú.");
        }
    }

    @FXML
    private void abrirGestionEquipos() {
        cargarEscena(btnGestionarEquipos, "/view/GestionarEquipos.fxml");
    }

    @FXML
    private void abrirEquiposReservados() {
        cargarEscena(btnEquiposReservados, "/view/VerEquiposReservados.fxml");
    }

    @FXML
    private void abrirHistorialEquipos() {
        cargarEscena(btnHistorialEquipos, "/view/HistorialEquipos.fxml");
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

            // Si necesitas pasar la conexión a los controladores hijos, puedes hacer algo como esto:
            /*
            if (fxmlPath.contains("GestionarEquipos.fxml")) {
                GestionarEquiposController controller = loader.getController();
                controller.init(connection);
            }
            if (fxmlPath.contains("VerEquiposReservados.fxml")) {
                VerEquiposReservadosController controller = loader.getController();
                controller.init(connection);
            }
            if (fxmlPath.contains("HistorialEquipos.fxml")) {
                HistorialEquiposController controller = loader.getController();
                controller.init(connection);
            }
            */

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