package controller;

import application.Main;
import data.DBConnection;
import data.SalaPrestadaDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import model.SalaPrestada;
import java.sql.Connection;
import java.sql.Timestamp;

public class SalasReservadasController {

    // Referencias a los elementos FXML
    @FXML private TableView<SalaPrestada> tablaSalasReservadas;
    @FXML private TableColumn<SalaPrestada, String> colSala;
    @FXML private TableColumn<SalaPrestada, String> colUsuario;
    @FXML private TableColumn<SalaPrestada, String> colInicio;
    @FXML private TableColumn<SalaPrestada, String> colFin;
    @FXML private TableColumn<SalaPrestada, String> colEstado;
    @FXML private Button btnActualizar;
    @FXML private Button btnVolver;

    // Conexión a la base de datos y acceso a la DAO
    private final Connection connection = DBConnection.getInstance().getConnection();
    private final SalaPrestadaDAO salaPrestadaDAO = new SalaPrestadaDAO(connection);
    private final ObservableList<SalaPrestada> salaReservadaList = FXCollections.observableArrayList();

    // Inicialización del controlador
    @FXML
    public void initialize() {
        // Configuración de las columnas de la tabla
        colSala.setCellValueFactory(cellData -> cellData.getValue().idSalaProperty().asString());
        colUsuario.setCellValueFactory(cellData -> cellData.getValue().idSolicitudSProperty().asString());  // Usando un campo correcto para el usuario
        colInicio.setCellValueFactory(cellData -> cellData.getValue().fechaInicioProperty().asString());
        colFin.setCellValueFactory(cellData -> cellData.getValue().fechaFinProperty().asString());
        colEstado.setCellValueFactory(cellData -> new SimpleStringProperty(determineEstado(cellData.getValue())));  // Cálculo del estado

        // Cargar las salas reservadas al iniciar
        fetchSalasReservadas();

        // Escuchar evento de tecla ESC para limpiar la selección
        tablaSalasReservadas.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                tablaSalasReservadas.getSelectionModel().clearSelection();
            }
        });
    }

    // Método para actualizar la tabla con los datos más recientes
    @FXML
    public void actualizarTabla(ActionEvent event) {
        fetchSalasReservadas(); // Recarga la lista de salas reservadas
    }

    // Método para cargar las salas reservadas desde la base de datos
    @FXML
    public void fetchSalasReservadas() {
        try {
            salaReservadaList.setAll(salaPrestadaDAO.fetch());  // Obtener todas las salas desde la base de datos
            tablaSalasReservadas.setItems(salaReservadaList);  // Establecer los elementos en la tabla
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Hubo un problema al cargar los datos de las salas.");
        }
    }

    // Método para regresar al menú principal
    @FXML
    public void volverAlMenu(ActionEvent event) {
        Main.loadScene("/view/AdminMenu.fxml");
    }

    // Método para mostrar alertas
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Método para determinar el estado basado en las fechas
    private String determineEstado(SalaPrestada sala) {
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis()); // Obtener la fecha y hora actual

        // Si la fecha de inicio es futura, está reservada
        if (sala.getFechaInicio().after(currentTimestamp)) {
            return "Reservada";
        } 
        // Si la fecha de fin es anterior, ya terminó
        else if (sala.getFechaFin().before(currentTimestamp)) {
            return "Finalizada";
        } 
        // Si está en el rango actual, está disponible
        else {
            return "Disponible";
        }
    }
}
