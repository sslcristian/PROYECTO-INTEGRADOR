package controller;

import data.PrestamoDAO;
import data.EquipoAudiovisualDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.scene.control.*;

import model.EquipoAudiovisual;
import model.Session;
import model.SolicitudPrestamo;


import java.sql.Connection;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class ReservarEquipoController {

    @FXML private DatePicker fechaInicioPicker;
    @FXML private TextField horaInicioField;
    @FXML private DatePicker fechaFinPicker;
    @FXML private TextField horaFinField;
    @FXML private TextArea detalleRecursoArea;

    @FXML private TableView<EquipoAudiovisual> equipoTable;
    @FXML private TableColumn<EquipoAudiovisual, Integer> idEquipoColumn;
    @FXML private TableColumn<EquipoAudiovisual, String> nombreColumn;
    @FXML private TableColumn<EquipoAudiovisual, String> tipoColumn;
    @FXML private TableColumn<EquipoAudiovisual, String> estadoColumn;
    @FXML private TableColumn<EquipoAudiovisual, String> ubicacionColumn;
    @FXML private TableColumn<EquipoAudiovisual, String> marcaColumn;
    @FXML private TableColumn<EquipoAudiovisual, String> modeloColumn;
    @FXML private TableColumn<EquipoAudiovisual, java.sql.Date> fechaAdquisicionColumn;

    @FXML private Button btnReservar;
    

    private PrestamoDAO prestamoDAO;
    private EquipoAudiovisualDAO equipoDAO;
    private ObservableList<EquipoAudiovisual> listaEquipos = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        try {
            Connection conn = Session.getConnection();
            prestamoDAO = new PrestamoDAO(conn);
            equipoDAO = new EquipoAudiovisualDAO(conn);
            cargarEquiposDisponibles();
            configurarColumnas();
        } catch (Exception e) {
            mostrarAlerta("No se pudo conectar a la base de datos: " + e.getMessage());
        }
        equipoTable.setItems(listaEquipos);
    }

    private void configurarColumnas() {
        idEquipoColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getIdEquipo()).asObject());
        nombreColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNombre()));
        tipoColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTipo()));
        estadoColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEstado()));
        ubicacionColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUbicacion()));
        marcaColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getMarca()));
        modeloColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getModelo()));
        fechaAdquisicionColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getFechaAdquisicion()));
    }

    private void cargarEquiposDisponibles() {
        try {
            ArrayList<EquipoAudiovisual> equipos = equipoDAO.fetchDisponibles();
            // Si deseas filtrar solo equipos disponibles, puedes hacerlo aquí:
            // equipos.removeIf(eq -> !"Disponible".equalsIgnoreCase(eq.getEstado()));
            listaEquipos.setAll(equipos);
        } catch (Exception e) {
            mostrarAlerta("Error al cargar equipos: " + e.getMessage());
        }
    }

    @FXML
    private void reservarEquipo() {
        LocalDate fechaInicio = fechaInicioPicker.getValue();
        LocalDate fechaFin = fechaFinPicker.getValue();
        String horaInicioStr = horaInicioField.getText();
        String horaFinStr = horaFinField.getText();
        String detalle = detalleRecursoArea.getText();
        EquipoAudiovisual seleccionado = equipoTable.getSelectionModel().getSelectedItem();

        if (fechaInicio == null || fechaFin == null || horaInicioStr.isEmpty() || horaFinStr.isEmpty() || seleccionado == null) {
            mostrarAlerta("Por favor, completa todos los campos y selecciona un equipo.");
            return;
        }

        LocalTime horaInicio, horaFin;
        try {
            horaInicio = LocalTime.parse(horaInicioStr);
            horaFin = LocalTime.parse(horaFinStr);
        } catch (Exception e) {
            mostrarAlerta("Hora inválida. Usa el formato HH:mm (ejemplo: 14:30).");
            return;
        }

        LocalDateTime inicioDateTime = LocalDateTime.of(fechaInicio, horaInicio);
        LocalDateTime finDateTime = LocalDateTime.of(fechaFin, horaFin);

        Timestamp fechaHoraInicio = Timestamp.valueOf(inicioDateTime);
        Timestamp fechaHoraFin = Timestamp.valueOf(finDateTime);

        // Cédula desde el usuario actual en sesión
        long cedulaUsuario = Session.getUsuarioActual().getCedula();

        SolicitudPrestamo solicitud = new SolicitudPrestamo(
                0, // idSolicitud, lo pone la secuencia
                cedulaUsuario,
                detalle,
                fechaHoraInicio,
                fechaHoraFin,
                "Pendiente",     // Estado siempre "Pendiente"
                null,            // idSala NULL
                seleccionado.getIdEquipo()
        );

        try {
            prestamoDAO.save(solicitud);
            mostrarAlerta("¡Reserva realizada correctamente!");
            limpiarCampos();
        } catch (Exception e) {
            mostrarAlerta("Error al reservar: " + e.getMessage());
        }
    }

    

    private void limpiarCampos() {
        fechaInicioPicker.setValue(null);
        horaInicioField.clear();
        fechaFinPicker.setValue(null);
        horaFinField.clear();
        detalleRecursoArea.clear();
        equipoTable.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}