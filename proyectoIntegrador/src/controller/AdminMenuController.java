package controller;

import java.io.IOException;

import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import model.Session;
import model.Usuario;
import javafx.scene.control.ButtonBar;

public class AdminMenuController {

    @FXML
    private StackPane adminContentPane;
    
    @FXML
    private Label lblNombreAdmin;  // Label para mostrar el nombre del administrador
    
    @FXML 
    private javafx.scene.control.Button btnGestionarSalas;
    
    @FXML 
    private javafx.scene.control.Button btnGestionarEquipos;
    
    @FXML 
    private javafx.scene.control.Button btnControlMantenimiento;
    
    @FXML 
    private javafx.scene.control.Button btnDevolucion;
    
    @FXML 
    private javafx.scene.control.Button btnGenerarSancion;
    
    @FXML 
    private javafx.scene.control.Button btnCerrarSesion;

    @FXML
    private void initialize() {
        // Al iniciar el menú, obtenemos el usuario actual desde la sesión
        Usuario admin = Session.getUsuarioActual();
        if (admin != null) {
            lblNombreAdmin.setText("Bienvenido, " + admin.getNombre());
        } else {
            lblNombreAdmin.setText("No hay usuario logueado.");
        }
    }

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
                // Cerrar la sesión y redirigir al menú principal
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
