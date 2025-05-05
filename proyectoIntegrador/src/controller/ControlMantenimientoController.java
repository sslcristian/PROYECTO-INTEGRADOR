package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class ControlMantenimientoController {

    // Acción para abrir la vista de Mantenimiento de Sala
    @FXML
    private void irMantenimientoSala(ActionEvent event) {
        try {
            // Cargar la interfaz MantenimientoSala.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MantenimientoSala.fxml"));
            Parent root = loader.load();

            // Obtener la escena actual y establecer la nueva interfaz en el StackPane
            Stage stage = (Stage) ((StackPane) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   
    @FXML
    private void irMantenimientoEquipo(ActionEvent event) {
        try {
            // Cargar la interfaz MantenimientoEquipo.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MantenimientoEquipo.fxml"));
            Parent root = loader.load();

            // Obtener la escena actual y establecer la nueva interfaz en el StackPane
            Stage stage = (Stage) ((StackPane) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
