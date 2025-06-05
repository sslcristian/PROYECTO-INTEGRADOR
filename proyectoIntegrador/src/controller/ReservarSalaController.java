package controller;

import data.PrestamoDAO;
import data.SalaInformaticaDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import model.SalaInformatica;
import model.Session;
import model.SolicitudPrestamo;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class ReservarSalaController {

    @FXML private DatePicker fechaInicioPicker;
    @FXML private TextField horaInicioField;
    @FXML private DatePicker fechaFinPicker;
    @FXML private TextField horaFinField;
    @FXML private TextArea detalleRecursoArea;

    @FXML private TableView<SalaInformatica> salaTable;
    @FXML private TableColumn<SalaInformatica, Integer> idSalaColumn;
    @FXML private TableColumn<SalaInformatica, String> nombreSalaColumn;
    @FXML private TableColumn<SalaInformatica, Integer> capacidadColumn;
    @FXML private TableColumn<SalaInformatica, String> softwareColumn;
    @FXML private TableColumn<SalaInformatica, String> hardwareColumn;
    @FXML private TableColumn<SalaInformatica, String> ubicacionColumn;
    @FXML private TableColumn<SalaInformatica, String> estadoColumn;

    @FXML private Button btnReservar;

    private PrestamoDAO prestamoDAO;
    private SalaInformaticaDAO salaDAO;
    private ObservableList<SalaInformatica> listaSalas = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        try {
            Connection conn = Session.getConnection();
            prestamoDAO = new PrestamoDAO(conn);
            salaDAO = new SalaInformaticaDAO(conn);
            cargarSalasDisponibles();
            configurarColumnas();

           
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
        salaTable.setItems(listaSalas);
    }

    private void configurarColumnas() {
        idSalaColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getIdSala()).asObject());
        nombreSalaColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNombreSala()));
        capacidadColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getCapacidad()).asObject());
        softwareColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getSoftwareDisponible()));
        hardwareColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getHardwareEspecial()));
        ubicacionColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUbicacion()));
        estadoColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEstado()));
    }

    private void cargarSalasDisponibles() {
        try {
            ArrayList<SalaInformatica> salas = salaDAO.fetchDisponibles();
            listaSalas.setAll(salas);
        } catch (Exception e) {
            mostrarAlerta("Error al cargar salas: " + e.getMessage());
        }
    }

    @FXML
    private void reservarSala() {
        LocalDate fechaInicio = fechaInicioPicker.getValue();
        LocalDate fechaFin = fechaFinPicker.getValue();
        String horaInicioStr = horaInicioField.getText();
        String horaFinStr = horaFinField.getText();
        String detalle = detalleRecursoArea.getText();
        SalaInformatica seleccionada = salaTable.getSelectionModel().getSelectedItem();

        if (fechaInicio == null || fechaFin == null || horaInicioStr.isEmpty() || horaFinStr.isEmpty() || seleccionada == null) {
            mostrarAlerta("Por favor, completa todos los campos y selecciona una sala.");
            return;
        }

        // Solo un día permitido
        if (!fechaInicio.equals(fechaFin)) {
            mostrarAlerta("La reserva solo puede ser para un solo día. La fecha de inicio y fin deben ser iguales.");
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

        // Validación de que la fecha de inicio y fin no sean anteriores a la fecha de hoy
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

        if (!finDateTime.isAfter(inicioDateTime)) {
            mostrarAlerta("La fecha y hora de fin deben ser posteriores a la de inicio.");
            return;
        }


        Timestamp fechaHoraInicio = Timestamp.valueOf(inicioDateTime);
        Timestamp fechaHoraFin = Timestamp.valueOf(finDateTime);

        if (!salaDAO.salaDisponible(seleccionada.getIdSala(), fechaHoraInicio, fechaHoraFin)) {
            mostrarAlerta("Ya existe una reserva para esta sala en el rango de fecha y hora seleccionado.");
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
                seleccionada.getIdSala(),
                null // idEquipo null
        );

        try {
            prestamoDAO.save(solicitud);
            mostrarAlerta("¡Reserva de sala realizada correctamente!");
            limpiarCampos();
        } catch (Exception e) {
            mostrarAlerta("Error al reservar: " + e.getMessage());
        }
    }

    private boolean esHoraValida(LocalTime hora, DayOfWeek dia) {
        if (dia == DayOfWeek.SATURDAY) {
            return !hora.isBefore(LocalTime.of(7, 0)) && !hora.isAfter(LocalTime.of(14, 0));
        } else if (dia.getValue() >= 1 && dia.getValue() <= 5) { // Lunes a viernes
            boolean mañana = !hora.isBefore(LocalTime.of(7, 0)) && !hora.isAfter(LocalTime.of(12, 0));
            boolean tarde = !hora.isBefore(LocalTime.of(14, 0)) && !hora.isAfter(LocalTime.of(18, 0));
            return mañana || tarde;
        }
        return false;
    }

    private void limpiarCampos() {
        fechaInicioPicker.setValue(null);
        horaInicioField.clear();
        fechaFinPicker.setValue(null);
        horaFinField.clear();
        detalleRecursoArea.clear();
        salaTable.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}