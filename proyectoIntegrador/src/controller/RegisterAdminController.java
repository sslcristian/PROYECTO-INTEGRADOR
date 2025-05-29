package controller;

import data.AdminDAO;
import data.DBConnectionFactory;
import data.DBConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.Admin;
import model.Session;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;

import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;

public class RegisterAdminController implements Initializable {

    @FXML private TextField txtCedula;
    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private PasswordField txtContrasenaAdmin;
    @FXML private ComboBox<String> cbDepartamento;
    @FXML private PasswordField txtContrasenaAdministrativo;

    private static final String CONTRASENA_ADMIN_VALIDA = "C12282025";

    private static final String[] DEPARTAMENTOS = {
        "Ingeniería",
        "Administración",
        "Ciencias Básicas",
        "Humanidades",
        "Sistemas",
        "Finanzas"
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbDepartamento.setItems(FXCollections.observableArrayList(DEPARTAMENTOS));
    }

    @FXML
    private void registrarAdmin() {
        try {
            if (txtCedula.getText().isEmpty() || txtNombre.getText().isEmpty() || txtCorreo.getText().isEmpty()
                    || txtTelefono.getText().isEmpty() || txtContrasenaAdmin.getText().isEmpty()
                    || cbDepartamento.getValue() == null || txtContrasenaAdministrativo.getText().isEmpty()) {
                showAlert("Campos incompletos", "Por favor, complete todos los campos.");
                return;
            }
            String cedulaTexto = txtCedula.getText();

            if (cedulaTexto.length() > 12) {
                showAlert("Cédula inválida", "La cédula no puede tener más de 12 dígitos.");
                return;
            }

            long cedula;
            try {
                cedula = Long.parseLong(txtCedula.getText());
            } catch (NumberFormatException e) {
                showAlert("Cédula inválida", "La cédula debe ser un número.");
                return;
            }

            String telefono = txtTelefono.getText();
            if (!telefono.matches("\\d{7,15}")) {
                showAlert("Teléfono inválido", "El número de teléfono debe contener entre 7 y 15 dígitos.");
                return;
            }

            String correo = txtCorreo.getText();
            if (!correo.matches("^[\\w-.]+@udi\\.edu\\.co$")) {
                showAlert("Correo inválido", "El correo debe ser institucional y terminar en @udi.edu.co.");
                return;
            }

            String contrasenaAdmin = txtContrasenaAdmin.getText();
            if (!CONTRASENA_ADMIN_VALIDA.equals(contrasenaAdmin)) {
                showAlert("Error", "Contraseña de administradores incorrecta.");
                return;
            }

            String nombre = txtNombre.getText();
            String departamento = cbDepartamento.getValue();
            String contrasenaAdministrativo = txtContrasenaAdministrativo.getText();

            // Obtener conexión como admin SIEMPRE, aunque no haya sesion activa
            Connection conn = Session.getConnection();
            if (conn == null) {
                DBConnection dbConnAdmin = DBConnectionFactory.getConnectionByRole("admin");
                if (dbConnAdmin != null) {
                    conn = dbConnAdmin.getConnection();
                }
            }
            if (conn == null) {
                showAlert("Error de conexión", "No se pudo establecer una conexión como administrador.");
                return;
            }

            AdminDAO dao = new AdminDAO(conn);

            if (dao.exists(cedula)) {
                showAlert("Error", "La cédula ya está registrada.");
                return;
            }

            if (dao.correoExiste(correo)) {
                showAlert("Error", "El correo ya está registrado.");
                return;
            }

            Admin admin = new Admin(cedula, nombre, correo, telefono, contrasenaAdmin, departamento, contrasenaAdministrativo);
            dao.save(admin);

            showAlert("Registro exitoso", "Administrador registrado correctamente.");
            limpiarCampos();

        } catch (Exception e) {
            showAlert("Error", "Ocurrió un error al registrar: " + e.getMessage());
        }
    }

    private void limpiarCampos() {
        txtCedula.clear();
        txtNombre.clear();
        txtCorreo.clear();
        txtTelefono.clear();
        txtContrasenaAdmin.clear();
        cbDepartamento.setValue(null);
        txtContrasenaAdministrativo.clear();
    }

    private void showAlert(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}