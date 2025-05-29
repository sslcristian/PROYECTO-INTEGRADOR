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
import java.time.*;
import java.time.format.DateTimeParseException;
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

            // Agregado: no permitir seleccionar fechas pasadas en los DatePicker
            fechaInicioPicker.setDayCellFactory(picker -> new DateCell() {
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    setDisable(empty || date.isBefore(LocalDate.now()));
                }
            });
            fechaFinPicker.setDayCellFactory(picker -> new DateCell() {
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    setDisable(empty || date.isBefore(LocalDate.now()));
                }
            });

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

        if (!horaInicioStr.matches("\\d{2}:\\d{2}") || !horaFinStr.matches("\\d{2}:\\d{2}")) {
            mostrarAlerta("Formato de hora inválido. Usa el formato 24 horas HH:mm (ejemplo: 07:00, 14:30, 18:00). No se acepta formato AM/PM.");
            return;
        }

        LocalTime horaInicio, horaFin;
        try {
            horaInicio = LocalTime.parse(horaInicioStr);
            horaFin = LocalTime.parse(horaFinStr);
        } catch (DateTimeParseException e) {
            mostrarAlerta("Hora inválida. Usa el formato HH:mm (ejemplo: 07:00, 14:30, 18:00).");
            return;
        }

        // Agregado: Validación de que la fecha de inicio y fin no sean anteriores a la fecha de hoy
        LocalDate hoy = LocalDate.now();
        if (fechaInicio.isBefore(hoy) || fechaFin.isBefore(hoy)) {
            mostrarAlerta("No puedes reservar para fechas que ya pasaron.");
            return;
        }

        // Validación de rangos de horario
        if (!esHoraValida(horaInicio, fechaInicio.getDayOfWeek()) || !esHoraValida(horaFin, fechaFin.getDayOfWeek())) {
            mostrarAlerta("Las horas ingresadas no están permitidas para el día seleccionado.\n" +
                    "Lunes a viernes: 07:00-12:00 o 14:00-18:00\n" +
                    "Sábados: 07:00-14:00\n" +
                    "Domingos: No se permiten reservas.");
            return;
        }

        LocalDateTime inicioDateTime = LocalDateTime.of(fechaInicio, horaInicio);
        LocalDateTime finDateTime = LocalDateTime.of(fechaFin, horaFin);

        // Validar que la fecha y hora de inicio sea antes que la de fin
        if (!finDateTime.isAfter(inicioDateTime)) {
            mostrarAlerta("La fecha y hora de fin deben ser posteriores a la de inicio.");
            return;
        }

        // Validar traslape de reservas
        Timestamp fechaHoraInicio = Timestamp.valueOf(inicioDateTime);
        Timestamp fechaHoraFin = Timestamp.valueOf(finDateTime);

        if (!prestamoDAO.equipoDisponible(seleccionado.getIdEquipo(), fechaHoraInicio, fechaHoraFin)) {
            mostrarAlerta("Ya existe una reserva para este equipo en el rango de fecha y hora seleccionado.");
            return;
        }

        // Cédula desde el usuario actual en sesión
        long cedulaUsuario = Session.getUsuarioActual().getCedula();

        SolicitudPrestamo solicitud = new SolicitudPrestamo(
                0,
                cedulaUsuario,
                detalle,
                fechaHoraInicio,
                fechaHoraFin,
                "Pendiente",
                null,
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

    // Valida si la hora es permitida según el día
    private boolean esHoraValida(LocalTime hora, DayOfWeek dia) {
        if (dia == DayOfWeek.SATURDAY) {
            return !hora.isBefore(LocalTime.of(7, 0)) && !hora.isAfter(LocalTime.of(14, 0));
        } else if (dia.getValue() >= 1 && dia.getValue() <= 5) { // Lunes a viernes
            boolean mañana = !hora.isBefore(LocalTime.of(7, 0)) && !hora.isAfter(LocalTime.of(12, 0));
            boolean tarde = !hora.isBefore(LocalTime.of(14, 0)) && !hora.isAfter(LocalTime.of(18, 0));
            return mañana || tarde;
        }
        return false; // Domingo no permitido
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