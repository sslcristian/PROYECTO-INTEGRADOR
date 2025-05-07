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
import java.sql.Date;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;

public class ReservasSalasController {

    @FXML private TableView<SalaPrestada> tablaHistorialReservas;
    @FXML private TableColumn<SalaPrestada, String> colSala;
    @FXML private TableColumn<SalaPrestada, String> colUsuario;
    @FXML private TableColumn<SalaPrestada, String> colSolicitud;
    @FXML private TableColumn<SalaPrestada, String> colInicio;
    @FXML private TableColumn<SalaPrestada, String> colFin;
    @FXML private TableColumn<SalaPrestada, String> colEstado;
    
    @FXML private DatePicker datePickerDesde;
    @FXML private DatePicker datePickerHasta;

    @FXML private Button btnFiltrar;
    @FXML private Button btnMostrarTodo;
    @FXML private Button btnVolver;

    private final Connection connection = DBConnection.getInstance().getConnection();
    private final SalaPrestadaDAO salaPrestadaDAO = new SalaPrestadaDAO(connection);
    private final ObservableList<SalaPrestada> historialSalasList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarColumnas();
        fetchHistorialSalas();
    }

    private void configurarColumnas() {
        colSala.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getIdSala())));
        colUsuario.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getIdSolicitudS())));
        colSolicitud.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getIdPrestamoS())));
        colInicio.setCellValueFactory(cellData -> new SimpleStringProperty(formatDate(cellData.getValue().getFechaInicio())));
        colFin.setCellValueFactory(cellData -> new SimpleStringProperty(formatDate(cellData.getValue().getFechaFin())));
        colEstado.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getObservaciones()));
    }

    @FXML
    public void fetchHistorialSalas() {
        historialSalasList.setAll(salaPrestadaDAO.fetch());
		tablaHistorialReservas.setItems(historialSalasList);
    }


    @FXML
    public void filtrarPorFecha(ActionEvent event) {
        LocalDate desde = datePickerDesde.getValue();
        LocalDate hasta = datePickerHasta.getValue();

        if (desde == null || hasta == null) {
            showAlert(Alert.AlertType.WARNING, "Campos vacíos", "Selecciona ambas fechas.");
            return;
        }

        if (hasta.isBefore(desde)) {
            showAlert(Alert.AlertType.WARNING, "Fechas inválidas", "La fecha 'Hasta' no puede ser anterior a 'Desde'.");
            return;
        }

        List<SalaPrestada> filtradas = salaPrestadaDAO.obtenerHistorialSalasPorFecha(Date.valueOf(desde), Date.valueOf(hasta));
		tablaHistorialReservas.setItems(FXCollections.observableArrayList(filtradas));
    }

    @FXML
    public void mostrarTodo(ActionEvent event) {
        tablaHistorialReservas.setItems(historialSalasList);
    }

    @FXML
    public void volverAlMenu(ActionEvent event) {
        Main.loadScene("/view/AdminMenu.fxml");
    }

    private String formatDate(Date date) {
        return (date != null) ? new SimpleDateFormat("dd/MM/yyyy").format(date) : "";
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
