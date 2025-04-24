package controller;

import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.Session;
import model.Usuario;
import javafx.scene.control.ButtonBar;

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

   
    @FXML
    private void initialize() {
        
        Usuario usuario = Session.getUsuarioActual();
        if (usuario != null) {
            lblNombreUsuario.setText("Bienvenido, " + usuario.getNombre());
        } else {
            lblNombreUsuario.setText("No hay usuario logueado.");
        }
    }

    
    @FXML
    private void verInformacion() {
        try {
            Usuario usuario = Session.getUsuarioActual();
            if (usuario == null) {
                System.out.println("No hay usuario logueado.");
                return;
            }

   
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserInfo.fxml"));
            Parent infoView = loader.load();

        
            UserInfoController controller = loader.getController();
            controller.setUsuario(usuario);

         
            stackPaneContenido.getChildren().setAll(infoView);
        } catch (Exception e) {
            e.printStackTrace();
        }
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



    @FXML
    private void reservarSala() {
        System.out.println("Reserva de sala activada.");
       
    }


    @FXML
    private void reservarEquipo() {
        System.out.println("Reserva de equipo activada.");
        
    }
}
