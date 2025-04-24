package controller;

import data.AdminDAO;
import data.DBConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.Admin;

import java.sql.Connection;

public class RegisterAdminController {

    @FXML private TextField txtCedula;
    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private PasswordField txtContrasenaAdmin;
    @FXML private TextField txtDepartamento;
    @FXML private PasswordField txtContrasenaAdministrativo;

    private static final String CONTRASENA_ADMIN_VALIDA = "C12282025";

    @FXML
    private void registrarAdmin() {
        try {
            if (txtCedula.getText().isEmpty() || txtNombre.getText().isEmpty() || txtCorreo.getText().isEmpty()
                    || txtTelefono.getText().isEmpty() || txtContrasenaAdmin.getText().isEmpty()
                    || txtDepartamento.getText().isEmpty() || txtContrasenaAdministrativo.getText().isEmpty()) {
                showAlert("Campos incompletos", "Por favor, complete todos los campos.");
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
            String departamento = txtDepartamento.getText();
            String contrasenaAdministrativo = txtContrasenaAdministrativo.getText();

            Connection conn = DBConnection.getInstance().getConnection();
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
        txtDepartamento.clear();
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
