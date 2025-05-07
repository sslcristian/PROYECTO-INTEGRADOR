package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class SalaMenuController {

    @FXML private Button btnGestionSalas;
    @FXML private Button btnSalasReservadas;
    @FXML private Button btnReservasSalas;

    @FXML
    private void abrirGestionSalas() {
        cargarEscena(btnGestionSalas, "/view/GestionSalas.fxml");
    }

    @FXML
    private void abrirSalasReservadas() {
        cargarEscena(btnSalasReservadas, "/view/SalasReservadasActivas.fxml");
    }

    @FXML
    private void abrirReservasSalas() {
        cargarEscena(btnReservasSalas, "/view/HistorialSalas.fxml");
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
            Stage stage = (Stage) boton.getScene().getWindow();
            double currentWidth = stage.getWidth();
            double currentHeight = stage.getHeight();

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();
            stage.setScene(new Scene(root));

            // Mantener el tamaño actual
            stage.setWidth(currentWidth);
            stage.setHeight(currentHeight);

            stage.show();
        } catch (IOException e) {
            System.err.println("❌ Error al cargar la escena: " + fxmlPath);
            e.printStackTrace();
        }
    }

}
