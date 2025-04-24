package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class SalaMenuController {

    private StackPane contentPane;

    public void setContentPane(StackPane contentPane) {
        this.contentPane = contentPane;
    }

    @FXML
    private void abrirGestionSalas() throws IOException {
        cargarVista("views/GestionarSalas.fxml");
    }

    @FXML
    private void abrirSalasReservadas() throws IOException {
        cargarVista("views/VerSalasReservadas.fxml");
    }

    @FXML
    private void abrirReservasSalas() throws IOException {
        cargarVista("views/VerReservasSalas.fxml");
    }

    private void cargarVista(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlPath));
        Parent root = loader.load();

        Object controller = loader.getController();
        if (controller instanceof GestionarSalasController c1) {
            c1.setContentPane(contentPane);
        } else if (controller instanceof VerSalasReservadasController c2) {
            c2.setContentPane(contentPane);
        } else if (controller instanceof VerReservasSalasController c3) {
            c3.setContentPane(contentPane);
        }

        contentPane.getChildren().setAll(root);
    }
}
