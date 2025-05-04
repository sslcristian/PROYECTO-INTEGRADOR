package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class GestionEquiposMenuController {

    @FXML
    private StackPane contentPane;  // Panel donde se cargarán las vistas

    // Acción para abrir la interfaz de gestionar equipos
    @FXML
    private void abrirGestionEquipos() {
        cargarVista("/views/GestionarEquipos.fxml");
    }

    // Acción para abrir la interfaz de ver equipos reservados
    @FXML
    private void abrirEquiposReservados() {
        cargarVista("/views/VerEquiposReservados.fxml");
    }

    // Acción para abrir la interfaz de ver historial de equipos
    @FXML
    private void abrirHistorialEquipos() {
        cargarVista("/views/HistorialEquipos.fxml");
    }

    // Método que carga las vistas dentro del contentPane
    private void cargarVista(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent vista = loader.load();
            contentPane.getChildren().setAll(vista);  // Reemplaza el contenido del panel derecho
        } catch (IOException e) {
            System.err.println("No se pudo cargar la vista: " + fxmlPath);
            e.printStackTrace();
        }
    }
}
