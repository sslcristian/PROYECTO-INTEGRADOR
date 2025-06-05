package controller;

import java.io.IOException;
import java.text.SimpleDateFormat;

import java.util.List;
import java.util.Optional;

import application.Main;
import data.PrestamoDAO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import model.Session;
import model.Usuario;
import model.SolicitudInfo;

public class UserMenuController {

    @FXML
    private StackPane stackPaneContenido;

    @FXML
    private Label lblNombreUsuario;

    @FXML 
    private javafx.scene.control.Button btnVerInfo;

    @FXML 
    private javafx.scene.control.Button btnReservarSala;

    @FXML 
    private javafx.scene.control.Button btnReservarEquipo;

    @FXML 
    private javafx.scene.control.Button btnCerrarSesion;

   
    private List<SolicitudInfo> solicitudesVigentes;
    private PrestamoDAO prestamoDao;

 
    public void setSolicitudesVigentes(List<SolicitudInfo> solicitudes, PrestamoDAO dao) {
        this.solicitudesVigentes = solicitudes;
        this.prestamoDao = dao;
       
        Platform.runLater(this::mostrarResumenSolicitudesVigentes);
    }

    @FXML
    private void initialize() {
        Usuario usuario = Session.getUsuarioActual();
        if (usuario != null) {
            lblNombreUsuario.setText("Bienvenido, " + usuario.getNombre());
            String tipoUsuario = usuario.getTipoUsuario();
            if (tipoUsuario == null || !tipoUsuario.trim().equalsIgnoreCase("docente")) {
                btnReservarEquipo.setVisible(false);
                btnReservarEquipo.setManaged(false);
            }
        } else {
            lblNombreUsuario.setText("No hay usuario logueado.");
            btnReservarEquipo.setVisible(false);
            btnReservarEquipo.setManaged(false);
        }
    }

    @FXML
    private void verInformacion() {
        try {
            Usuario usuario = Session.getUsuarioActual();
            if (usuario == null) {
                System.out.println("No hay usuario logueado.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserInfo.fxml"));
            Parent infoView = loader.load();

            UserInfoController controller = loader.getController();
            controller.setUsuario(usuario);

            stackPaneContenido.getChildren().setAll(infoView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cerrarSesion() {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Cerrar sesión");
        alerta.setHeaderText("¿Deseas cerrar sesión?");
        alerta.setContentText("Serás redirigido al menú principal.");

        ButtonType confirmar = new ButtonType("Sí");
        ButtonType cancelar = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        alerta.getButtonTypes().setAll(confirmar, cancelar);

        alerta.showAndWait().ifPresent(respuesta -> {
            if (respuesta == confirmar) {
                Session.cerrarSesion();
                Main.loadScene("/view/MainMenu.fxml");
            }
        });
    }

    @FXML
    private void reservarSala() {
        cargarVista("/view/ReservarSala.fxml");
    }

    @FXML
    private void reservarEquipo() {
        cargarVista("/view/ReservarEquipo.fxml");
    }

    private void cargarVista(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent vista = loader.load();
            stackPaneContenido.getChildren().setAll(vista);
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo cargar la vista: " + fxmlPath);
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

   
    private void mostrarResumenSolicitudesVigentes() {
        if (solicitudesVigentes != null && !solicitudesVigentes.isEmpty()) {
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            for (SolicitudInfo solicitud : solicitudesVigentes) {
                String fechaInicioStr = solicitud.getFechaInicio() != null ? formato.format(solicitud.getFechaInicio()) : "";
                String fechaFinStr = solicitud.getFechaFin() != null ? formato.format(solicitud.getFechaFin()) : "";

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
                        if (prestamoDao != null && prestamoDao.aceptarSolicitud(solicitud.getIdSolicitud())) {
                            mostrarAlertaInfo("Préstamo", "Solicitud aceptada exitosamente.");
                        } else {
                            mostrarAlertaInfo("Error", "No se pudo aceptar la solicitud.");
                        }
                    } else if (result.get() == btnRechazar) {
                        if (prestamoDao != null && prestamoDao.cancelarSolicitud(solicitud.getIdSolicitud())) {
                            mostrarAlertaInfo("Préstamo", "Solicitud rechazada exitosamente.");
                        } else {
                            mostrarAlertaInfo("Error", "No se pudo rechazar la solicitud.");
                        }
                    }
                }
            }
        }
    }

    private void mostrarAlertaInfo(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}