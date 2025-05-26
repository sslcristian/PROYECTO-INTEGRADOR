package controller;

import data.AdminDAO;
import data.DBConnectionFactory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Admin;
import model.Session;

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

            // 1. Usa la conexión de admin para validar el admin
            Connection conn = DBConnectionFactory.getConnectionByRole("admin").getConnection();
            AdminDAO dao = new AdminDAO(conn);

            Admin admin = dao.findByCedula(cedula);

            if (admin == null) {
                mostrarAlerta("Cédula no encontrada", "La cédula no está registrada en el sistema.");
            } else if (!admin.getContraseñaAdministrativo().equals(contrasena)) {
                mostrarAlerta("Contraseña incorrecta", "La contraseña ingresada no es correcta.");
            } else {
                // 2. Si las credenciales son correctas, inicializa la sesión de admin
                Session.login(cedula, "admin");

                // 3. Cambia de pantalla
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AdminMenu.fxml"));
                Parent adminMenu = loader.load();

                Stage stage = (Stage) txtCedula.getScene().getWindow();

                // Mantener el tamaño actual
                double anchoActual = stage.getWidth();
                double altoActual = stage.getHeight();

                Scene scene = new Scene(adminMenu, anchoActual, altoActual);
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