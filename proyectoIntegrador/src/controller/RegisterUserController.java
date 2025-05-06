package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Usuario;
import data.UsuarioDAO;


public class RegisterUserController {

    @FXML private TextField txtCedula;
    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private ComboBox<String> tipoUsuarioCombo;
    @FXML private TextField txtDepartamento;
    @FXML private PasswordField txtContrasena;
    @FXML private Button btnRegistrar;

    private UsuarioDAO usuarioDAO;

    @FXML
    public void registrarUsuario() {
        if (camposVacios()) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        String correo = txtCorreo.getText().trim();
        String tipoUsuario = tipoUsuarioCombo.getValue();
        Usuario usuario = new Usuario(
                Long.parseLong(txtCedula.getText()),
                txtNombre.getText().trim(),
                correo,
                txtTelefono.getText().trim(),
                tipoUsuario,
                txtDepartamento.getText().trim(),
                txtContrasena.getText()
        );

        usuarioDAO.save(usuario);

        limpiarCampos();

        mostrarAlerta("Éxito", "Usuario registrado exitosamente.");
    }

    private boolean camposVacios() {
        return txtCedula.getText().isEmpty() || txtNombre.getText().isEmpty() ||
                txtCorreo.getText().isEmpty() || txtTelefono.getText().isEmpty() ||
                tipoUsuarioCombo.getValue() == null || txtDepartamento.getText().isEmpty() ||
                txtContrasena.getText().isEmpty();
    }

    private void limpiarCampos() {
        txtCedula.clear();
        txtNombre.clear();
        txtCorreo.clear();
        txtTelefono.clear();
        tipoUsuarioCombo.getSelectionModel().clearSelection();
        txtDepartamento.clear();
        txtContrasena.clear();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    public void initialize() {
        tipoUsuarioCombo.getItems().addAll("ESTUDIANTE", "DOCENTE");
    }
}
