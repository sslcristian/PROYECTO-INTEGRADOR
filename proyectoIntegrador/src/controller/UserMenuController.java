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
    @FXML private javafx.scene.control.Button btnVerInfo;
    @FXML private javafx.scene.control.Button btnReservarSala;
    @FXML private javafx.scene.control.Button btnReservarEquipo;
    @FXML private javafx.scene.control.Button btnCerrarSesion;

    @FXML
    private void initialize() {
        Usuario usuario = Session.getUsuarioActual();
        if (usuario != null) {
            lblNombreUsuario.setText("Bienvenido, " + usuario.getNombre());
        }
    }

    @FXML
    private void verInformacion() {
        try {
            Usuario usuario = Session.getUsuarioActual();
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
    private void reservarSala() {
        try {
            Parent view = FXMLLoader.load(getClass().getResource("/view/ReservarSala.fxml"));
            stackPaneContenido.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void reservarEquipo() {
        try {
            Parent view = FXMLLoader.load(getClass().getResource("/view/ReservarEquipo.fxml"));
            stackPaneContenido.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cerrarSesion() {
        Session.cerrarSesion();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainMenu.fxml"));
            Parent mainMenu = loader.load();
            Stage stage = (Stage) stackPaneContenido.getScene().getWindow();
            stage.setScene(new Scene(mainMenu));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
