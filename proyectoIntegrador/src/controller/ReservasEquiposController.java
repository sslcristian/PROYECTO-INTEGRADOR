package controller;

import application.Main;
import data.DBConnection;
import data.EquipoPrestadoDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.EquipoPrestado;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;

public class ReservasEquiposController {

    @FXML private TableView<EquipoPrestado> tablaHistorialReservas;
    @FXML private TableColumn<EquipoPrestado, String> colEquipo;
    @FXML private TableColumn<EquipoPrestado, String> colUsuario;
    @FXML private TableColumn<EquipoPrestado, String> colInicio;
    @FXML private TableColumn<EquipoPrestado, String> colFin;
    @FXML private TableColumn<EquipoPrestado, String> colEstado;

    @FXML private Button btnFiltrar;
    @FXML private Button btnMostrarTodo;
    @FXML private Button btnVolver;

    @FXML private DatePicker datePickerDesde;
    @FXML private DatePicker datePickerHasta;

    private final Connection connection = DBConnection.getInstance().getConnection();
    private final EquipoPrestadoDAO equipoPrestadoDAO = new EquipoPrestadoDAO(connection);
    private final ObservableList<EquipoPrestado> historialEquiposList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarColumnas();
        fetchHistorialEquipos(); // Cargar los datos iniciales al inicio
    }

    private void configurarColumnas() {
        colEquipo.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getIdEquipo())));
        colUsuario.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getIdSolicitudE())));
        colInicio.setCellValueFactory(cellData -> new SimpleStringProperty(formatDate(cellData.getValue().getFechaInicio())));
        colFin.setCellValueFactory(cellData -> new SimpleStringProperty(formatDate(cellData.getValue().getFechaFin())));
        colEstado.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getObservaciones()));
    }

    @FXML
    public void fetchHistorialEquipos() {
        try {
            historialEquiposList.setAll(equipoPrestadoDAO.obtenerHistorialEquipos());

            if (historialEquiposList.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "Sin registros", "No hay equipos prestados registrados.");
                return;
            }

            tablaHistorialReservas.setItems(historialEquiposList);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error en la BD", "No se pudo obtener el historial de equipos.");
            e.printStackTrace();
        }
    }

    @FXML
    public void filtrarPorFecha(ActionEvent event) {
        LocalDate desde = datePickerDesde.getValue();
        LocalDate hasta = datePickerHasta.getValue();

        if (historialEquiposList.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Datos vacíos", "No hay registros en el historial.");
            return;
        }

        if (desde == null || hasta == null) {
            showAlert(Alert.AlertType.WARNING, "Campos vacíos", "Selecciona ambas fechas.");
            return;
        }

        if (hasta.isBefore(desde)) {
            showAlert(Alert.AlertType.WARNING, "Fechas inválidas", "La fecha 'Hasta' no puede ser anterior a 'Desde'.");
            return;
        }

        LocalDate hoy = LocalDate.now();
        if (desde.isAfter(hoy) || hasta.isAfter(hoy)) {
            showAlert(Alert.AlertType.WARNING, "Fechas futuras no permitidas", "Solo puedes consultar el historial.");
            return;
        }

        Timestamp timestampDesde = Timestamp.valueOf(desde.atStartOfDay());
        Timestamp timestampHasta = Timestamp.valueOf(hasta.atTime(23, 59, 59)); 

        try {
            List<EquipoPrestado> historialFiltrado = equipoPrestadoDAO.obtenerHistorialEquiposPorFecha(timestampDesde, timestampHasta);

            if (historialFiltrado.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "Sin resultados", "No hay registros en ese período.");
                return;
            }

            tablaHistorialReservas.setItems(FXCollections.observableArrayList(historialFiltrado));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error en la BD", "Hubo un problema al filtrar los datos.");
            e.printStackTrace();
        }
    }

    @FXML
    public void mostrarTodo(ActionEvent event) {
        if (historialEquiposList.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Sin datos", "No hay registros de equipos prestados.");
            return;
        }
        tablaHistorialReservas.setItems(historialEquiposList);
    }

    @FXML
    public void volverAlMenu(ActionEvent event) {
        Main.loadScene("/view/AdminMenu.fxml");
    }

    private String formatDate(Timestamp timestamp) {
        return (timestamp != null) ? new SimpleDateFormat("dd/MM/yyyy HH:mm").format(timestamp) : "";
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
