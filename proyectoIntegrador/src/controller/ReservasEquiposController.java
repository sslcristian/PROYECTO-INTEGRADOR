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
import java.util.Date;
import java.util.List;

public class ReservasEquiposController {

    @FXML private TableView<EquipoPrestado> tablaHistorialReservas;
    @FXML private TableColumn<EquipoPrestado, String> colEquipo;
    @FXML private TableColumn<EquipoPrestado, String> colUsuario;
    @FXML private TableColumn<EquipoPrestado, String> colSolicitud;
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
        // Configurar las columnas de la tabla
        colEquipo.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getIdEquipo())));
        colUsuario.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getIdSolicitudE())));
        colSolicitud.setCellValueFactory(cellData -> new SimpleStringProperty(formatDate(cellData.getValue().getFechaInicio())));
        colInicio.setCellValueFactory(cellData -> new SimpleStringProperty(formatDate(cellData.getValue().getFechaInicio())));
        colFin.setCellValueFactory(cellData -> new SimpleStringProperty(formatDate(cellData.getValue().getFechaFin())));
        colEstado.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getObservaciones()));

        fetchHistorialEquipos(); // Cargar los datos iniciales al inicio
    }

    // Obtener todo el historial de equipos prestados
    @FXML
    public void fetchHistorialEquipos() {
        try {
            historialEquiposList.setAll(equipoPrestadoDAO.obtenerHistorialEquipos());
            tablaHistorialReservas.setItems(historialEquiposList);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Hubo un problema al cargar el historial de equipos.");
        }
    }

    // Filtrar por fechas
    @FXML
    public void filtrarPorFecha(ActionEvent event) {
        LocalDate desde = datePickerDesde.getValue();
        LocalDate hasta = datePickerHasta.getValue();

        // Verificar si las fechas están seleccionadas
        if (desde == null || hasta == null) {
            showAlert(Alert.AlertType.WARNING, "Campos vacíos", "Por favor selecciona ambas fechas.");
            return;
        }

        // Verificar que la fecha 'hasta' no sea anterior a la fecha 'desde'
        if (hasta.isBefore(desde)) {
            showAlert(Alert.AlertType.WARNING, "Fechas inválidas", "La fecha 'Hasta' no puede ser anterior a 'Desde'.");
            return;
        }

        // Convertir LocalDate a Timestamp para la base de datos
        Timestamp timestampDesde = Timestamp.valueOf(desde.atStartOfDay());
        Timestamp timestampHasta = Timestamp.valueOf(hasta.atTime(23, 59, 59)); // Último segundo del día 'hasta'

        try {
            // Llamar al método para obtener el historial filtrado por fecha
            List<EquipoPrestado> historialEquiposFiltrados = equipoPrestadoDAO.obtenerHistorialEquiposPorFecha(timestampDesde, timestampHasta);
            
            // Convertir la lista filtrada a ObservableList y asignarla a la tabla
            ObservableList<EquipoPrestado> observableHistorial = FXCollections.observableArrayList(historialEquiposFiltrados);
            tablaHistorialReservas.setItems(observableHistorial);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Hubo un problema al cargar el historial de equipos filtrado.");
        }
    }

    // Mostrar todos los equipos prestados
    @FXML
    public void mostrarTodo(ActionEvent event) {
        tablaHistorialReservas.setItems(historialEquiposList);
    }

    // Volver al menú principal
    @FXML
    public void volverAlMenu(ActionEvent event) {
        Main.loadScene("/view/AdminMenu.fxml");
    }

    // Método para formatear las fechas a un formato legible
    private String formatDate(Timestamp timestamp) {
        if (timestamp != null) {
            Date date = new Date(timestamp.getTime());
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            return sdf.format(date);
        }
        return "";
    }

    // Mostrar alertas
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
