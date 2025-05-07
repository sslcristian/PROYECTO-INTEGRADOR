package controller;

import data.AdminDAO;
import data.DBConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Admin;

import java.sql.Connection;

public class LoginAdminController {

    @FXML private TextField txtCedula;
    @FXML private PasswordField txtContrasena;

    @FXML
    private void iniciarSesionAdmin() {
        try {
            String cedulaTexto = txtCedula.getText().trim();
            if (cedulaTexto.isEmpty()) {
                mostrarAlerta("Campo vacío", "Por favor ingrese su cédula.");
                return;
            }

            long cedula = Long.parseLong(cedulaTexto);

            String contrasena = txtContrasena.getText().trim();
            if (contrasena.isEmpty()) {
                mostrarAlerta("Campo vacío", "Por favor ingrese su contraseña.");
                return;
            }

            Connection conn = DBConnection.getInstance().getConnection();
            AdminDAO dao = new AdminDAO(conn);

            Admin admin = dao.findByCedula(cedula);

            if (admin == null) {
                mostrarAlerta("Cédula no encontrada", "La cédula no está registrada en el sistema.");
            } else if (!admin.getContraseñaAdministrativo().equals(contrasena)) {
                mostrarAlerta("Contraseña incorrecta", "La contraseña ingresada no es correcta.");
            } else {
              

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AdminMenu.fxml"));
                Parent userMenu = loader.load();

                Stage stage = (Stage) txtCedula.getScene().getWindow();
                
                // Mantener el tamaño actual
                double anchoActual = stage.getWidth();
                double altoActual = stage.getHeight();

                Scene scene = new Scene(userMenu, anchoActual, altoActual);
                stage.setScene(scene);
                stage.show();

            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Formato inválido", "La cédula debe ser un número.");
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al iniciar sesión: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
