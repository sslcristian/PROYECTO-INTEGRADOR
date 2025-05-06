package controller;

import application.Main;
import data.DBConnection;
import data.EquipoPrestadoDAO;
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
import model.EquipoPrestado;

import java.sql.Connection;
import java.sql.Timestamp;

public class VerEquiposReservadosController {

    @FXML private TableView<EquipoPrestado> tablaEquiposReservados;
    @FXML private TableColumn<EquipoPrestado, String> colEquipo;
    @FXML private TableColumn<EquipoPrestado, String> colUsuario;
    @FXML private TableColumn<EquipoPrestado, String> colInicio;
    @FXML private TableColumn<EquipoPrestado, String> colFin;
    @FXML private TableColumn<EquipoPrestado, String> colEstado;
    @FXML private Button btnActualizar;
    @FXML private Button btnVolver;

    private final Connection connection = DBConnection.getInstance().getConnection();
    private final EquipoPrestadoDAO equipoPrestadoDAO = new EquipoPrestadoDAO(connection);
    private final ObservableList<EquipoPrestado> equipoReservadoList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
    	colEquipo.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getIdEquipo())));
    	colUsuario.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getIdSolicitudE())));
    	colInicio.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getFechaInicio())));
    	colFin.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getFechaFin())));
    	colEstado.setCellValueFactory(cellData -> new SimpleStringProperty(determineEstado(cellData.getValue())));


        fetchEquiposReservados();

        tablaEquiposReservados.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                tablaEquiposReservados.getSelectionModel().clearSelection();
            }
        });
    }

    @FXML
    public void actualizarTabla(ActionEvent event) {
        fetchEquiposReservados();
    }

    @FXML
    public void fetchEquiposReservados() {
        try {
            equipoReservadoList.setAll(equipoPrestadoDAO.fetch());
            tablaEquiposReservados.setItems(equipoReservadoList);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Hubo un problema al cargar los datos de los equipos.");
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

    private String determineEstado(EquipoPrestado equipo) {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        if (equipo.getFechaInicio().after(now)) {
            return "Reservado";
        } else if (equipo.getFechaFin().before(now)) {
            return "Finalizado";
        } else {
            return "En uso";
        }
    }
}
