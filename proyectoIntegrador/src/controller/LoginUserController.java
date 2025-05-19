package controller;

import data.DBConnection;
import data.UsuarioDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Session;
import model.Usuario;

import java.sql.Connection;

public class LoginUserController {

    @FXML private TextField txtCedula;
    @FXML private PasswordField txtContrasena;

    @FXML
    private void iniciarSesion() {
        try {
            if (txtCedula.getText().isEmpty() || txtContrasena.getText().isEmpty()) {
                showAlert("Campos vacíos", "Por favor, complete todos los campos.");
                return;
            }

            if (!txtCedula.getText().matches("\\d+")) {
                showAlert("Cédula inválida", "La cédula debe contener solo números.");
                return;
            }

            long cedula = Long.parseLong(txtCedula.getText());
            String contrasena = txtContrasena.getText();

            Connection conn = DBConnection.getInstance().getConnection();
            UsuarioDAO dao = new UsuarioDAO(conn);
            Usuario usuario = null;

            try {
                usuario = dao.autenticar(cedula, contrasena);
            } catch (IllegalStateException ex) {
                showAlert("Sanción Activa", ex.getMessage());
                return;
            }

            if (usuario != null) {
                Session.setUsuarioActual(usuario);

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserMenu.fxml"));
                Parent userMenu = loader.load();

                Stage stage = (Stage) txtCedula.getScene().getWindow();
                
                // Mantener el tamaño actual
                double anchoActual = stage.getWidth();
                double altoActual = stage.getHeight();

                Scene scene = new Scene(userMenu, anchoActual, altoActual);
                stage.setScene(scene);
                stage.show();

            } else {
                showAlert("Error de autenticación", "Cédula o contraseña incorrecta.");
            }

        } catch (Exception e) {
            showAlert("Error", "Ocurrió un error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

