package controller;

import data.UsuarioDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import model.Usuario;
import model.Session;

import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class RegisterUserController implements Initializable {

    @FXML private TextField txtCedula;
    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private ComboBox<String> cbTipoUsuario;
    @FXML private ComboBox<String> cbDepartamento;
    @FXML private PasswordField txtContrasena;

    private final Pattern emailPattern = Pattern.compile("^[\\w.-]+@udi\\.edu\\.co$");

    private static final String[] DEPARTAMENTOS = {
        "Administración de Empresas", "Comunicación Social", "Criminalística", "Derecho",
        "Diseño Gráfico", "Diseño Industrial", "Ingeniería Civil", "Ingeniería Electrónica",
        "Ingeniería Industrial", "Ingeniería de Sistemas", "Negocios Internacionales",
        "Psicología", "Publicidad y Marketing Digital", "Licenciatura en Educación Infantil"
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbTipoUsuario.setItems(FXCollections.observableArrayList("Docente", "Usuario"));
        cbDepartamento.setItems(FXCollections.observableArrayList(DEPARTAMENTOS));
    }

    @FXML
    private void registrarUsuario() {
        try {
            if (camposVacios()) {
                mostrarAlerta("Campos vacíos", "Todos los campos son obligatorios.");
                return;
            }

            if (!isNumeric(txtCedula.getText()) || txtCedula.getText().length() < 6 || txtCedula.getText().length() > 12) {
                mostrarAlerta("Cédula inválida", "La cédula debe contener solo números y tener entre 6 y 12 dígitos.");
                return;
            }

            if (!isNumeric(txtTelefono.getText()) || txtTelefono.getText().length() < 7) {
                mostrarAlerta("Teléfono inválido", "El teléfono debe contener solo números y al menos 7 dígitos.");
                return;
            }

            if (txtNombre.getText().trim().length() < 5) {
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

            if (cbTipoUsuario.getValue() == null || cbTipoUsuario.getValue().trim().isEmpty()) {
                mostrarAlerta("Tipo de usuario", "Debe seleccionar un tipo de usuario.");
                return;
            }

            if (cbDepartamento.getValue() == null || cbDepartamento.getValue().trim().isEmpty()) {
                mostrarAlerta("Departamento", "Debe seleccionar un departamento.");
                return;
            }

            Connection conn = Session.getConnection();
            UsuarioDAO dao = new UsuarioDAO(conn);

            if (dao.correoExiste(correo)) {
                mostrarAlerta("Correo duplicado", "Este correo ya está registrado en el sistema.");
                return;
            }

           
            String nombreEnMayusculas = txtNombre.getText().trim().toUpperCase();

            Usuario usuario = new Usuario(
                Long.parseLong(txtCedula.getText()),
                nombreEnMayusculas,
                correo,
                txtTelefono.getText().trim(),
                cbTipoUsuario.getValue().trim(),
                cbDepartamento.getValue().trim(),
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
               cbTipoUsuario.getValue() == null || cbDepartamento.getValue() == null ||
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
        cbTipoUsuario.setValue(null);
        cbDepartamento.setValue(null);
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