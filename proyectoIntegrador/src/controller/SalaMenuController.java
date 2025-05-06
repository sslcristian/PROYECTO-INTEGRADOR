package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class SalaMenuController {

    @FXML
    private StackPane contentPane;

    @FXML
    private void abrirGestionSalas() {
        cargarVista("/views/GestionSalas.fxml");
    }

    @FXML
    private void abrirSalasReservadas() {
        cargarVista("/views/VerSalasReservadas.fxml");
    }

    @FXML
    private void abrirReservasSalas() {
        cargarVista("/views/VerReservasSalas.fxml");
    }

    private void cargarVista(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent vista = loader.load();
            contentPane.getChildren().setAll(vista);
        } catch (IOException e) {
            System.err.println("No se pudo cargar la vista: " + fxmlPath);
            e.printStackTrace();
        }
    }
}
