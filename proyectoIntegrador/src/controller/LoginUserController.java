package controller;

import data.PrestamoDAO;
import data.UsuarioDAO;
import data.DBConnectionFactory;
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
import model.SolicitudInfo;

import java.sql.Connection;
import java.util.List;

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

            // Consultar el tipo de usuario antes de iniciar sesión
            String tipoUsuario = null;
            try {
                Connection tempConn = DBConnectionFactory.getConnectionByRole("usuario").getConnection();
                UsuarioDAO tempDao = new UsuarioDAO(tempConn);
                Usuario userTemp = tempDao.findByCedula(cedula);
                if (userTemp == null) {
                    showAlert("Error de autenticación", "Cédula o contraseña incorrecta.");
                    return;
                }
                tipoUsuario = userTemp.getTipoUsuario();
                tempConn.close();
            } catch (Exception e) {
                showAlert("Error", "No se pudo determinar el tipo de usuario: " + e.getMessage());
                return;
            }

            if (tipoUsuario == null) {
                showAlert("Error de autenticación", "No se pudo determinar el tipo de usuario.");
                return;
            }

            tipoUsuario = tipoUsuario.trim().toLowerCase();
            String rolConexion = tipoUsuario.equals("docente") ? "docente" : "usuario";

            // Inicializa la sesión y la conexión SOLO si no existe
            if (Session.getConnection() == null) {
                boolean sesionIniciada = Session.login(cedula, rolConexion);
                if (!sesionIniciada) {
                    showAlert("Error de autenticación", "Cédula o contraseña incorrecta.");
                    return;
                }
            }

            Connection conn = Session.getConnection();
            if (conn == null) {
                showAlert("Error de conexión", "No se pudo establecer conexión con la base de datos.");
                return;
            }

            UsuarioDAO usuarioDao = new UsuarioDAO(conn);
            PrestamoDAO prestamoDao = new PrestamoDAO(conn);
            Usuario usuario = null;

            // Obtiene solicitudes vigentes pero NO las muestra aquí
            List<SolicitudInfo> solicitudesVigentes = prestamoDao.obtenerSolicitudesVigentes(cedula);

            try {
                usuario = usuarioDao.autenticar(cedula, contrasena);
            } catch (IllegalStateException ex) {
                showAlert("Sanción Activa", ex.getMessage());
                return;
            }

            if (usuario != null) {
                Session.setUsuarioActual(usuario);

                // Cargar menú principal
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserMenu.fxml"));
                Parent userMenu = loader.load();

                // Pasa las solicitudes vigentes al UserMenuController
                controller.UserMenuController userMenuController = loader.getController();
                userMenuController.setSolicitudesVigentes(solicitudesVigentes, prestamoDao);

                Stage stage = (Stage) txtCedula.getScene().getWindow();
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