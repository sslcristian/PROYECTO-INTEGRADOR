package controller;

import data.PrestamoDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.SolicitudPrestamo;
import model.Session;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SolicitudesController {

    @FXML private TableView<SolicitudPrestamo> tablaPendientes;
    @FXML private TableColumn<SolicitudPrestamo, Integer> colIdPend;
    @FXML private TableColumn<SolicitudPrestamo, Long> colCedulaPend;
    @FXML private TableColumn<SolicitudPrestamo, String> colDetallePend;
    @FXML private TableColumn<SolicitudPrestamo, String> colInicioPend;
    @FXML private TableColumn<SolicitudPrestamo, String> colFinPend;
    @FXML private TableColumn<SolicitudPrestamo, String> colEstadoPend;
    @FXML private TableColumn<SolicitudPrestamo, String> colSalaPend;
    @FXML private TableColumn<SolicitudPrestamo, String> colEquipoPend;

    @FXML private TableView<SolicitudPrestamo> tablaAceptadas;
    @FXML private TableColumn<SolicitudPrestamo, Integer> colIdAcept;
    @FXML private TableColumn<SolicitudPrestamo, Long> colCedulaAcept;
    @FXML private TableColumn<SolicitudPrestamo, String> colDetalleAcept;
    @FXML private TableColumn<SolicitudPrestamo, String> colInicioAcept;
    @FXML private TableColumn<SolicitudPrestamo, String> colFinAcept;
    @FXML private TableColumn<SolicitudPrestamo, String> colEstadoAcept;
    @FXML private TableColumn<SolicitudPrestamo, String> colSalaAcept;
    @FXML private TableColumn<SolicitudPrestamo, String> colEquipoAcept;

    @FXML private Button btnAceptar;
    @FXML private Button btnRechazar;
    @FXML private Button btnActualizarPendientes;
    @FXML private Button btnActualizarAceptadas;
    @FXML private Button btnVolver;

    private PrestamoDAO prestamoDAO;
    private ObservableList<SolicitudPrestamo> solicitudesPendientes = FXCollections.observableArrayList();
    private ObservableList<SolicitudPrestamo> solicitudesAceptadas = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        try {
            Connection conn = Session.getConnection(); 
            prestamoDAO = new PrestamoDAO(conn);
        } catch (Exception e) {
            mostrarAlerta("No se pudo conectar a la base de datos: " + e.getMessage());
            return;
        }

     // Para tabla pendientes
        colIdPend.setCellValueFactory(new PropertyValueFactory<>("idSolicitud"));
        colCedulaPend.setCellValueFactory(new PropertyValueFactory<>("cedulaUsuario"));
        colDetallePend.setCellValueFactory(new PropertyValueFactory<>("detalleRecurso"));
        colInicioPend.setCellValueFactory(new PropertyValueFactory<>("horaInicioString"));
        colFinPend.setCellValueFactory(new PropertyValueFactory<>("horaFinString"));
        colEstadoPend.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colSalaPend.setCellValueFactory(new PropertyValueFactory<>("idSalaString"));
        colEquipoPend.setCellValueFactory(new PropertyValueFactory<>("idEquipoString"));
        tablaPendientes.setItems(solicitudesPendientes);

        // Para tabla aceptadas
        colIdAcept.setCellValueFactory(new PropertyValueFactory<>("idSolicitud"));
        colCedulaAcept.setCellValueFactory(new PropertyValueFactory<>("cedulaUsuario"));
        colDetalleAcept.setCellValueFactory(new PropertyValueFactory<>("detalleRecurso"));
        colInicioAcept.setCellValueFactory(new PropertyValueFactory<>("horaInicioString"));
        colFinAcept.setCellValueFactory(new PropertyValueFactory<>("horaFinString"));
        colEstadoAcept.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colSalaAcept.setCellValueFactory(new PropertyValueFactory<>("idSalaString"));
        colEquipoAcept.setCellValueFactory(new PropertyValueFactory<>("idEquipoString"));
        tablaAceptadas.setItems(solicitudesAceptadas);

        cargarSolicitudes();
    }

    private void cargarSolicitudes() {
        solicitudesPendientes.clear();
        solicitudesAceptadas.clear();
        try {
            List<SolicitudPrestamo> todas = prestamoDAO.fetchAll();
            for (SolicitudPrestamo s : todas) {
                if ("Pendiente".equalsIgnoreCase(s.getEstado())) {
                    solicitudesPendientes.add(s);
                } else if ("Aceptada".equalsIgnoreCase(s.getEstado())) {
                    solicitudesAceptadas.add(s);
                }
            }
            tablaPendientes.refresh();
            tablaAceptadas.refresh();
        } catch (SQLException e) {
            mostrarAlerta("Error cargando solicitudes: " + e.getMessage());
        }
    }

    @FXML
    private void aceptarSolicitud(ActionEvent event) {
        SolicitudPrestamo seleccionada = tablaPendientes.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            try {
                prestamoDAO.aceptarSolicitud(seleccionada.getIdSolicitud());
                mostrarAlerta("Solicitud aceptada correctamente.");
                cargarSolicitudes();
            } catch (SQLException e) {
                mostrarAlerta("Error al aceptar la solicitud: " + e.getMessage());
            }
        } else {
            mostrarAlerta("Por favor, selecciona una solicitud pendiente para aceptar.");
        }
    }

    @FXML
    private void rechazarSolicitud(ActionEvent event) {
        SolicitudPrestamo seleccionada = tablaPendientes.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            try {
                prestamoDAO.rechazarSolicitud(seleccionada.getIdSolicitud());
                mostrarAlerta("Solicitud rechazada correctamente.");
                cargarSolicitudes();
            } catch (SQLException e) {
                mostrarAlerta("Error al rechazar la solicitud: " + e.getMessage());
            }
        } else {
            mostrarAlerta("Por favor, selecciona una solicitud pendiente para rechazar.");
        }
    }

    @FXML
    private void actualizarPendientes(ActionEvent event) {
        cargarSolicitudes();
    }

    @FXML
    private void actualizarAceptadas(ActionEvent event) {
        cargarSolicitudes();
    }

    @FXML
    private void volverAlMenu(ActionEvent event)  {
        try {
      
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            double currentWidth = stage.getWidth();
            double currentHeight = stage.getHeight();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AdminMenu.fxml"));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.setWidth(currentWidth);
            stage.setHeight(currentHeight);
            stage.show();
        } catch (IOException e) {
            mostrarAlerta("No se pudo regresar al menú.");
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}