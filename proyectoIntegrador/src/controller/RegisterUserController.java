
package controller;

import data.DBConnection;
import data.UsuarioDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.Usuario;

import java.sql.Connection;
import java.util.regex.Pattern;

public class RegisterUserController {

    @FXML private TextField txtCedula;
    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtTipoUsuario;
    @FXML private TextField txtDepartamento;
    @FXML private PasswordField txtContrasena;

    private final Pattern emailPattern = Pattern.compile("^[\\w.-]+@udi\\.edu\\.co$");

    @FXML
    private void registrarUsuario() {
        try {
            if (camposVacios()) {
                mostrarAlerta("Campos vacíos", "Todos los campos son obligatorios.");
                return;
            }

            if (!isNumeric(txtCedula.getText()) || txtCedula.getText().length() < 6) {
                mostrarAlerta("Cédula inválida", "La cédula debe contener solo números y tener al menos 6 dígitos.");
                return;
            }

            if (!isNumeric(txtTelefono.getText()) || txtTelefono.getText().length() < 7) {
                mostrarAlerta("Teléfono inválido", "El teléfono debe contener solo números y al menos 7 dígitos.");
                return;
            }

            if (txtNombre.getText().trim().length() < 3) {
                mostrarAlerta("Nombre muy corto", "El nombre debe tener al menos 3 caracteres.");
                return;
            }

            String correo = txtCorreo.getText().trim().toLowerCase();
            if (!correo.endsWith("@udi.edu.co") || !emailPattern.matcher(correo).matches()) {
                mostrarAlerta("Correo inválido", "Debe ingresar un correo institucional que termine en @udi.edu.co.");
                return;
            }

            if (txtContrasena.getText().length() < 6) {
                mostrarAlerta("Contraseña débil", "La contraseña debe tener al menos 6 caracteres.");
                return;
            }

            Connection conn = DBConnection.getInstance().getConnection();
            UsuarioDAO dao = new UsuarioDAO(conn);

            if (dao.correoExiste(correo)) {
                mostrarAlerta("Correo duplicado", "Este correo ya está registrado en el sistema.");
                return;
            }

            Usuario usuario = new Usuario(
                Long.parseLong(txtCedula.getText()),
                txtNombre.getText().trim(),
                correo,
                txtTelefono.getText().trim(),
                txtTipoUsuario.getText().trim(),
                txtDepartamento.getText().trim(),
                txtContrasena.getText()
            );

            dao.save(usuario);
            mostrarAlerta("Registro exitoso", "Usuario registrado correctamente.");
            limpiarCampos();

        } catch (Exception e) {
            mostrarAlerta("Error", "Ocurrió un error inesperado: " + e.getMessage());
        }
    }

    private boolean camposVacios() {
        return txtCedula.getText().isEmpty() || txtNombre.getText().isEmpty() ||
               txtCorreo.getText().isEmpty() || txtTelefono.getText().isEmpty() ||
               txtTipoUsuario.getText().isEmpty() || txtDepartamento.getText().isEmpty() ||
               txtContrasena.getText().isEmpty();
    }

    private boolean isNumeric(String texto) {
        return texto.matches("\\d+");
    }

    private void limpiarCampos() {
        txtCedula.clear();
        txtNombre.clear();
        txtCorreo.clear();
        txtTelefono.clear();
        txtTipoUsuario.clear();
        txtDepartamento.clear();
        txtContrasena.clear();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
