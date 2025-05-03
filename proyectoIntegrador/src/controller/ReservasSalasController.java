package controller;

import application.Main;
import data.DBConnection;
import data.SalaPrestadaDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.SalaPrestada;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.LocalDate;

public class ReservasSalasController {

    @FXML private TableView<SalaPrestada> tablaHistorialReservas;
    @FXML private TableColumn<SalaPrestada, String> colSala;
    @FXML private TableColumn<SalaPrestada, String> colUsuario;
    @FXML private TableColumn<SalaPrestada, String> colSolicitud;
    @FXML private TableColumn<SalaPrestada, String> colInicio;
    @FXML private TableColumn<SalaPrestada, String> colFin;
    @FXML private TableColumn<SalaPrestada, String> colEstado;

    @FXML private Button btnFiltrar;
    @FXML private Button btnMostrarTodo;
    @FXML private Button btnVolver;

    @FXML private DatePicker datePickerDesde;
    @FXML private DatePicker datePickerHasta;

    private final Connection connection = DBConnection.getInstance().getConnection();
    private final SalaPrestadaDAO salaPrestadaDAO = new SalaPrestadaDAO(connection);
    private final ObservableList<SalaPrestada> historialReservasList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colSala.setCellValueFactory(cellData -> cellData.getValue().idSalaProperty().asString());
        colUsuario.setCellValueFactory(cellData -> cellData.getValue().idSolicitudSProperty().asString());
        colSolicitud.setCellValueFactory(cellData -> {
            Timestamp fecha = cellData.getValue().getFechaInicio();
            return new SimpleStringProperty(fecha.toString());
        });
        colInicio.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFechaInicio().toString()));
        colFin.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFechaFin().toString()));
        colEstado.setCellValueFactory(cellData -> cellData.getValue().observacionesProperty());

        fetchHistorialReservas();
    }

    @FXML
    public void fetchHistorialReservas() {
        try {
            historialReservasList.setAll(salaPrestadaDAO.fetch());
            tablaHistorialReservas.setItems(historialReservasList);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Hubo un problema al cargar el historial de reservas.");
        }
    }

    @FXML
    public void filtrarPorFecha(ActionEvent event) {
        LocalDate desde = datePickerDesde.getValue();
        LocalDate hasta = datePickerHasta.getValue();

        if (desde == null || hasta == null) {
            showAlert(Alert.AlertType.WARNING, "Campos vacíos", "Por favor selecciona ambas fechas.");
            return;
        }

        if (hasta.isBefore(desde)) {
            showAlert(Alert.AlertType.WARNING, "Fechas inválidas", "La fecha 'Hasta' no puede ser anterior a 'Desde'.");
            return;
        }

        ObservableList<SalaPrestada> filtradas = FXCollections.observableArrayList();
        for (SalaPrestada reserva : historialReservasList) {
            LocalDate inicio = reserva.getFechaInicio().toLocalDateTime().toLocalDate();
            if ((inicio.isEqual(desde) || inicio.isAfter(desde)) && (inicio.isEqual(hasta) || inicio.isBefore(hasta))) {
                filtradas.add(reserva);
            }
        }

        tablaHistorialReservas.setItems(filtradas);
    }

    @FXML
    public void mostrarTodo(ActionEvent event) {
        tablaHistorialReservas.setItems(historialReservasList);
    }

    @FXML
    public void volverAlMenu(ActionEvent event) {
        Main.loadScene("/view/AdminMenu.fxml");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

