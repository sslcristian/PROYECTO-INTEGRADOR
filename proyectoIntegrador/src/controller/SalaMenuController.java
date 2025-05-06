package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class SalaMenuController {

    @FXML private Button btnGestionSalas; // Botón para referenciar el Stage correctamente

    @FXML
    private void abrirGestionSalas() {
        System.out.println("Estado de btnGestionSalas: " + btnGestionSalas); // Depuración
        if (btnGestionSalas == null) {
            System.err.println("❌ Error: btnGestionSalas es NULL. Verifica su `fx:id` en el FXML.");
            return;
        }

        cargarEscena("/view/GestionSalas.fxml");
    }


    @FXML
    private void abrirSalasReservadas() {
        cargarEscena("/view/VerSalasReservadas.fxml");
    }

    @FXML
    private void abrirReservasSalas() {
        cargarEscena("/view/VerReservasdeSalas.fxml");
    }

    private void cargarEscena(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            if (btnGestionSalas == null) {
                System.err.println("❌ Error: btnGestionSalas es NULL. Revisa el FXML y el controlador.");
                return;
            }

            Stage stage = (Stage) btnGestionSalas.getScene().getWindow(); // Se obtiene correctamente el Stage
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("❌ Error al cargar la escena: " + fxmlPath);
            e.printStackTrace();
        }
    }
}
