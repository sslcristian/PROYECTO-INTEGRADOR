package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class ControlMantenimientoController {

    @FXML
    private BorderPane rootLayout; // Debe estar en tu FXML principal

    // Ir a Mantenimiento de Sala sin afectar el menú lateral
    @FXML
    private void irMantenimientoSala(ActionEvent event) {
        cambiarVista("/views/MantenimientoSala.fxml");
    }

    // Ir a Mantenimiento de Equipo sin afectar el menú lateral
    @FXML
    private void irMantenimientoEquipo(ActionEvent event) {
        cambiarVista("/views/MantenimientoEquipo.fxml");
    }

    private void cambiarVista(String rutaFXML) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent nuevaVista = loader.load();
            
            rootLayout.setCenter(nuevaVista); // Cambia solo la parte central, dejando AdminMenu intacto
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
