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
import java.sql.Date;


public class SalasReservadasController {

    @FXML private TableView<SalaPrestada> tablaSalasReservadas;
    @FXML private TableColumn<SalaPrestada, String> colSala;
    @FXML private TableColumn<SalaPrestada, String> colUsuario;
    @FXML private TableColumn<SalaPrestada, String> colInicio;
    @FXML private TableColumn<SalaPrestada, String> colFin;
    @FXML private TableColumn<SalaPrestada, String> colEstado;
    @FXML private Button btnActualizar;
    @FXML private Button btnVolver;

    private final Connection connection = DBConnection.getInstance().getConnection();
    private final SalaPrestadaDAO salaPrestadaDAO = new SalaPrestadaDAO(connection);
    private final ObservableList<SalaPrestada> salasList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colSala.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getIdSala())));
        colUsuario.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getIdSolicitudS())));
        colInicio.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getFechaInicio())));
        colFin.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getFechaFin())));
        colEstado.setCellValueFactory(cellData -> new SimpleStringProperty(determineEstado(cellData.getValue())));

        fetchSalasReservadas();

        tablaSalasReservadas.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                tablaSalasReservadas.getSelectionModel().clearSelection();
            }
        });
    }

    @FXML
    public void actualizarTabla(ActionEvent event) {
        fetchSalasReservadas();
    }

    @FXML
    public void fetchSalasReservadas() {
        try {
            salasList.setAll(salaPrestadaDAO.fetch());
            tablaSalasReservadas.setItems(salasList);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Hubo un problema al cargar los datos de las salas.");
        }
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

    private String determineEstado(SalaPrestada sala) {
        Date now = new Date(System.currentTimeMillis());

        if (sala.getFechaInicio().after(now)) {
            return "Reservada";
        } else if (sala.getFechaFin().before(now)) {
            return "Finalizada";
        } else {
            return "En uso";
        }
    }
}
