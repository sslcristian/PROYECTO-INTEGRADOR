package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.Session;
import model.Usuario;

public class UserMenuController {

    @FXML
    private StackPane stackPaneContenido;
    
    @FXML
    private Label lblNombreUsuario;
    
    @FXML 
    private javafx.scene.control.Button btnVerInfo;
    
    @FXML 
    private javafx.scene.control.Button btnReservarSala;
    
    @FXML 
    private javafx.scene.control.Button btnReservarEquipo;
    
    @FXML 
    private javafx.scene.control.Button btnCerrarSesion;

    // Este método se ejecuta al inicializar el controlador
    @FXML
    private void initialize() {
        // Verificar si hay un usuario logueado
        Usuario usuario = Session.getUsuarioActual();
        if (usuario != null) {
            lblNombreUsuario.setText("Bienvenido, " + usuario.getNombre());
        } else {
            lblNombreUsuario.setText("No hay usuario logueado.");
        }
    }

    // Método para mostrar la información del usuario
    @FXML
    private void verInformacion() {
        try {
            Usuario usuario = Session.getUsuarioActual();
            if (usuario == null) {
                System.out.println("No hay usuario logueado.");
                return;
            }

            // Cargar la vista de información del usuario
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserInfo.fxml"));
            Parent infoView = loader.load();

            // Obtener el controlador de la vista cargada y pasarle el usuario
            UserInfoController controller = loader.getController();
            controller.setUsuario(usuario);

            // Mostrar la nueva vista en el StackPane
            stackPaneContenido.getChildren().setAll(infoView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para cerrar sesión
    @FXML
    private void cerrarSesion() {
        // Cerrar la sesión (limpiar los datos del usuario)
        Session.cerrarSesion();

        try {
            // Cargar la vista de inicio
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainMenu.fxml"));
            Parent mainMenu = loader.load();
            Stage stage = (Stage) stackPaneContenido.getScene().getWindow();
            stage.setScene(new Scene(mainMenu));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para reservar sala
    @FXML
    private void reservarSala() {
        System.out.println("Reserva de sala activada.");
        // Agrega la lógica para reservar la sala aquí si es necesario
    }

    // Método para reservar equipo
    @FXML
    private void reservarEquipo() {
        System.out.println("Reserva de equipo activada.");
        // Agrega la lógica para reservar el equipo aquí si es necesario
    }
}
