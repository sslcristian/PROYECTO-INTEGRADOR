package controller;

import data.DBConnection;
import data.ReservaSalaDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.ReservaSala;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;

public class ReservaSalaController {

    @FXML private TableView<ReservaSala> salaTable;
    @FXML private TableColumn<ReservaSala, Integer> idSalaColumn;
    @FXML private TableColumn<ReservaSala, String> nombreSalaColumn;
    @FXML private TableColumn<ReservaSala, Integer> capacidadColumn;
    @FXML private TableColumn<ReservaSala, String> softwareDisponibleColumn;
    @FXML private TableColumn<ReservaSala, String> hardwareEspecialColumn;
    @FXML private TableColumn<ReservaSala, String> ubicacionColumn;
    @FXML private TableColumn<ReservaSala, String> estadoColumn;

    @FXML private DatePicker fechaInicioPicker;
    @FXML private TextField horaInicioField;
    @FXML private DatePicker fechaFinPicker;
    @FXML private TextField horaFinField;
    @FXML private TextArea detalleRecursoArea;
    @FXML private Button btnReservar;

    private ReservaSalaDAO reservaSalaDAO;
    private ObservableList<ReservaSala> listaSalas = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            reservaSalaDAO = new ReservaSalaDAO(conn);
            cargarSalas();
            configurarColumnas();
        } catch (Exception e) {
            mostrarAlerta("No se pudo conectar a la base de datos: " + e.getMessage());
        }
        salaTable.setItems(listaSalas);
    }

    private void configurarColumnas() {
        idSalaColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getIdSala()).asObject());
        nombreSalaColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNombreSala()));
        capacidadColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getCapacidad()).asObject());
        softwareDisponibleColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getSoftwareDisponible()));
        hardwareEspecialColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getHardwareEspecial()));
        ubicacionColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUbicacion()));
        estadoColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEstado()));
    }

    private void cargarSalas() {
        try {
            ArrayList<ReservaSala> salas = reservaSalaDAO.fetchAllSalas();
            listaSalas.setAll(salas);
        } catch (Exception e) {
            mostrarAlerta("Error al cargar salas: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    public void reservarSala() {
        try {
            LocalDate fechaInicio = fechaInicioPicker.getValue();
            LocalDate fechaFin = fechaFinPicker.getValue();
            String horaInicio = horaInicioField.getText();
            String horaFin = horaFinField.getText();
            String detalle = detalleRecursoArea.getText();

            ReservaSala salaSeleccionada = salaTable.getSelectionModel().getSelectedItem();
            if (salaSeleccionada == null) {
                mostrarAlerta("Selecciona una sala para reservar.");
                return;
            }
            if (fechaInicio == null || fechaFin == null ||
                    horaInicio == null || horaInicio.isEmpty() ||
                    horaFin == null || horaFin.isEmpty() ||
                    detalle == null || detalle.isEmpty()) {
                mostrarAlerta("Completa todos los campos obligatorios.");
                return;
            }

            ReservaSala reserva = new ReservaSala();
            reserva.setIdSala(salaSeleccionada.getIdSala());
            reserva.setFechaInicio(fechaInicio);
            reserva.setFechaFin(fechaFin);
            reserva.setObservaciones("Hora inicio: " + horaInicio + ", Hora fin: " + horaFin + ". Detalle: " + detalle);

            reservaSalaDAO.insertReserva(reserva);

            mostrarAlerta("¡Reserva registrada correctamente! El administrador la podrá ver en su panel.");
        } catch (Exception e) {
            mostrarAlerta("Error al registrar la reserva: " + e.getMessage());
        }
    }
}