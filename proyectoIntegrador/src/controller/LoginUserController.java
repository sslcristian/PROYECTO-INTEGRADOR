package controller;

import data.PrestamoDAO;
import data.UsuarioDAO;
import data.DBConnectionFactory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Session;
import model.Usuario;
import model.SolicitudInfo;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.text.SimpleDateFormat;
import java.util.Date;

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

            // --- NUEVO: Verifica si hay una solicitud aceptada vigente antes de autenticar completamente ---
            List<SolicitudInfo> solicitudesVigentes = prestamoDao.obtenerSolicitudesVigentes(cedula);
            mostrarResumenSolicitudesVigentes(solicitudesVigentes, prestamoDao);

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

    // Método para mostrar resumen de solicitudes vigentes, cada una con Aceptar y Rechazar
    private void mostrarResumenSolicitudesVigentes(List<SolicitudInfo> solicitudesVigentes, PrestamoDAO prestamoDao) {
        if (solicitudesVigentes != null && !solicitudesVigentes.isEmpty()) {
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            for (SolicitudInfo solicitud : solicitudesVigentes) {
                String fechaInicioStr = "";
                String fechaFinStr = "";
                Date fechaInicio = solicitud.getFechaInicio();
                Date fechaFin = solicitud.getFechaFin();
                if (fechaInicio != null) {
                    fechaInicioStr = formato.format(fechaInicio);
                }
                if (fechaFin != null) {
                    fechaFinStr = formato.format(fechaFin);
                }

                String resumen = "Nombre: " + 
                        (solicitud.getNombreSala() != null ? solicitud.getNombreSala() : solicitud.getNombreEquipo()) + "\n" +
                        "Ubicación: " + 
                        (solicitud.getUbicacionSala() != null ? solicitud.getUbicacionSala() : solicitud.getUbicacionEquipo()) + "\n" +
                        "Fecha Inicio: " + fechaInicioStr + "\n" +
                        "Fecha Fin: " + fechaFinStr;
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Solicitud Vigente");
                alert.setHeaderText("Resumen de tu solicitud");
                alert.setContentText(resumen);

                ButtonType btnAceptar = new ButtonType("Aceptar");
                ButtonType btnRechazar = new ButtonType("Rechazar");
                ButtonType btnCerrar = new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(btnAceptar, btnRechazar, btnCerrar);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent()) {
                    if (result.get() == btnAceptar) {
                        if (prestamoDao.aceptarSolicitud(solicitud.getIdSolicitud())) {
                            showAlert("Préstamo", "Solicitud aceptada exitosamente.");
                        } else {
                            showAlert("Error", "No se pudo aceptar la solicitud.");
                        }
                    } else if (result.get() == btnRechazar) {
                        if (prestamoDao.cancelarSolicitud(solicitud.getIdSolicitud())) {
                            showAlert("Préstamo", "Solicitud rechazada exitosamente.");
                        } else {
                            showAlert("Error", "No se pudo rechazar la solicitud.");
                        }
                    }
                }
            }
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