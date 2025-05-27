package controller;

import data.DevolucionDAO;
import data.PrestamoDAO; // El DAO de solicitudes (antes prestamoDAO)
import data.DBConnectionFactory;
import model.Devolucion;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class DevolucionController {
	@FXML private ComboBox<Integer> comboSolicitud;
    @FXML private DatePicker fechaDevolucion;
    @FXML private TextField horaDevolucion;
    @FXML private TextField estadoRecurso;
    @FXML private TextArea observaciones;
    @FXML private TableView<Devolucion> tablaDevoluciones;
    @FXML private TableColumn<Devolucion, Integer> colIdSolicitud;
    @FXML private TableColumn<Devolucion, Date> colFechaDevolucion;
    @FXML private TableColumn<Devolucion, Time> colHoraDevolucion;
    @FXML private TableColumn<Devolucion, String> colEstado;
    @FXML private TableColumn<Devolucion, String> colObservaciones;
    @FXML private Button btnVolver;
    @FXML private Button btnRegistrar;
    private DevolucionDAO devolucionDAO;
    private PrestamoDAO prestamoDAO;

    public void initialize() {
        try {
            // Obtener la conexión admin usando el factory
            Connection conn = DBConnectionFactory.getConnectionByRole("admin").getConnection();
            devolucionDAO = new DevolucionDAO(conn);
            prestamoDAO = new PrestamoDAO(conn);
            cargarSolicitudes();
            configurarTabla();
            cargarDevoluciones();
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo conectar a la base de datos: " + e.getMessage());
        }
    }

    private void cargarSolicitudes() {
        try {
            ArrayList<Integer> solicitudes = prestamoDAO.fetchIdsAceptadas();
            comboSolicitud.getItems().clear();
            comboSolicitud.getItems().addAll(solicitudes);
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudieron cargar las solicitudes aceptadas.");
            e.printStackTrace();
        }
    }
    private void configurarTabla() {
        colIdSolicitud.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getIdSolicitud()).asObject());
        colFechaDevolucion.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getFechaDevolucion()));
        colHoraDevolucion.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getHoraDevolucion()));
        colEstado.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEstadoRecurso()));
        colObservaciones.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getObservaciones()));
    }

    private void cargarDevoluciones() {
        List<Devolucion> devoluciones = devolucionDAO.fetch();
        tablaDevoluciones.setItems(FXCollections.observableArrayList(devoluciones));
    }

    @FXML
    private void registrarDevolucion() {
        Integer idSolicitud = comboSolicitud.getValue();
        String horaTxt = horaDevolucion.getText() != null ? horaDevolucion.getText().trim() : "";

        if (idSolicitud == null || fechaDevolucion.getValue() == null ||
            horaTxt.isEmpty() || estadoRecurso.getText().trim().isEmpty()) {
            mostrarAlerta("Campos obligatorios", "Todos los campos menos observaciones son obligatorios.");
            return;
        }

        String horaFormateada;
        if (horaTxt.matches("\\d{2}:\\d{2}")) {
            horaFormateada = horaTxt + ":00";
        } else if (horaTxt.matches("\\d{2}:\\d{2}:\\d{2}")) {
            horaFormateada = horaTxt;
        } else {
            mostrarAlerta("Formato incorrecto", "La hora debe tener formato HH:mm o HH:mm:ss");
            return;
        }

        try {
            Date fecha = Date.valueOf(fechaDevolucion.getValue());
            Time hora = Time.valueOf(horaFormateada);
            String estado = estadoRecurso.getText().trim();
            String obs = observaciones.getText() != null ? observaciones.getText().trim() : "";

            // idDevolucion se genera en el DAO usando la secuencia
            Devolucion devolucion = new Devolucion(
                0,
                idSolicitud,
                fecha,
                hora,
                estado,
                obs
            );
            devolucionDAO.save(devolucion);

            mostrarAlerta("Éxito", "Devolución registrada correctamente");
            cargarDevoluciones();
        } catch (IllegalArgumentException e) {
            mostrarAlerta("Error en la hora", "El formato de la hora es incorrecto: " + e.getMessage());
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al registrar la devolución: " + e.getMessage());
        }
    }

    @FXML
    private void volverAlMenu(ActionEvent event) {
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
        	mostrarAlerta("Error", "No se pudo regresar al menú.");
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