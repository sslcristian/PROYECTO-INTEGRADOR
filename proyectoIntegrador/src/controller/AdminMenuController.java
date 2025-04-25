package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import model.Session;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.io.IOException;

import application.Main;

public class AdminMenuController {

    @FXML private StackPane adminContentPane;


    @FXML private Button btnGestionarSalas;
    @FXML private Button btnGestionarEquipos;
    @FXML private Button btnControlMantenimiento;
    @FXML private Button btnDevolucion;
    @FXML private Button btnGenerarSancion;
    @FXML private Button btnCerrarSesion;

  
    @FXML
    private void gestionarSalas() {
        cargarVista("/view/SalaMenu.fxml");
    }

    @FXML
    private void gestionarEquipos() {
        cargarVista("/view/GestionEquipos.fxml");
    }

    @FXML
    private void controlMantenimiento() {
        cargarVista("/view/ControlMantenimiento.fxml");
    }

    @FXML
    private void controlDevolucion() {
        cargarVista("/view/ControlDevolucion.fxml");
    }

    @FXML
    private void generarSancion() {
        cargarVista("/view/GenerarSancion.fxml");
    }

    @FXML
    private void cerrarSesion() {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Cerrar sesión");
        alerta.setHeaderText("¿Deseas cerrar sesión?");
        alerta.setContentText("Serás redirigido al menú principal.");

        ButtonType confirmar = new ButtonType("Sí");
        ButtonType cancelar = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        alerta.getButtonTypes().setAll(confirmar, cancelar);

        alerta.showAndWait().ifPresent(respuesta -> {
            if (respuesta == confirmar) {
               
                Session.cerrarSesion();

              
                Main.loadScene("/view/MainMenu.fxml");
            }
        });
    }

    // Método para cargar la vista en el panel central
    private void cargarVista(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent vista = loader.load();
            adminContentPane.getChildren().setAll(vista);
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo cargar la vista: " + fxmlPath);
            e.printStackTrace();
        }
    }

    // Método para mostrar alertas de error
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
