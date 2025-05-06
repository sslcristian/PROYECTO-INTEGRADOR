package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class GestionEquiposMenuController {

    @FXML private Button btnGestionarEquipos;
    @FXML private Button btnEquiposReservados;
    @FXML private Button btnHistorialEquipos;

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

        URL fxmlLocation = getClass().getResource(fxmlPath);
        if (fxmlLocation == null) {
            System.err.println("❌ Error: Archivo FXML no encontrado en la ruta " + fxmlPath);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();
            Stage stage = (Stage) boton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("❌ Error al cargar la escena: " + fxmlPath);
            e.printStackTrace();
        }
    }
}
