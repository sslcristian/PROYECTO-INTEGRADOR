package controller;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;

import application.Main;
import data.DBConnection;
import data.ExcelService;
import data.SalaPrestadaDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.SalaPrestada;

public class AgregarHorarioDocenteController {

    @FXML
    private Button botonCargar, botonEliminar, botonRegistrar, btnVolverMenu;

    @FXML
    private TableColumn<SalaPrestada, Integer> columnIdPrestamo1, columnIdSolicitud1, columnIdSala1, columnIdSala;

    @FXML
    private TableColumn<SalaPrestada, String> columnFechaInicio1, columnFechaFin1, columnObservaciones1;

    @FXML
    private TableColumn<SalaPrestada, String> columnFechaInicio, columnFechaFin, columnObservaciones;

    @FXML
    private TableView<SalaPrestada> tableProductos, tableTemplate;

    private Connection connection = DBConnection.getInstance().getConnection();
    private SalaPrestadaDAO salaPrestadaDAO = new SalaPrestadaDAO(connection);

    @FXML
    public void initialize() {
        ObservableList<SalaPrestada> salasPrestadas = FXCollections.observableArrayList(salaPrestadaDAO.fetch());

        // Configurar columnas de la tabla principal
        columnIdPrestamo1.setCellValueFactory(new PropertyValueFactory<>("idPrestamo"));
        columnIdSolicitud1.setCellValueFactory(new PropertyValueFactory<>("idSolicitud"));
        columnIdSala1.setCellValueFactory(new PropertyValueFactory<>("idSala"));
        columnFechaInicio1.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        columnFechaFin1.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));
        columnObservaciones1.setCellValueFactory(new PropertyValueFactory<>("observaciones"));

        tableProductos.setItems(salasPrestadas);

        // Configurar columnas de la tabla resumen
        columnIdSala.setCellValueFactory(new PropertyValueFactory<>("idSala"));
        columnFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        columnFechaFin.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));
        columnObservaciones.setCellValueFactory(new PropertyValueFactory<>("observaciones"));

        tableTemplate.setItems(salasPrestadas);
    }

    @FXML
    void registrar(ActionEvent event) {
        mostrarAlerta("Registro", "Funcionalidad de registro pendiente de implementación.", Alert.AlertType.INFORMATION);
    }

    @FXML
    void eliminar(ActionEvent event) {
        if (!tableProductos.getSelectionModel().isEmpty()) {
            SalaPrestada sala = tableProductos.getSelectionModel().getSelectedItem();
            salaPrestadaDAO.delete(sala.getIdSala()); // Asegurar que el método y el campo son correctos
            initialize();
        } else {
            mostrarAlerta("Seleccione un registro", "Debe seleccionar un dato de la tabla", Alert.AlertType.WARNING);
        }
    }

    @FXML
    void cargarExcel(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo de Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos Excel", "*.xlsx"));

        Stage stage = (Stage) botonCargar.getScene().getWindow();
        File archivo = fileChooser.showOpenDialog(stage);

        if (archivo != null) {
            ArrayList<SalaPrestada> salasExcel = ExcelService.fetchExcel(archivo);
            tableProductos.getItems().setAll(salasExcel);
        }
    }

    @FXML
    void cerrarSesion(ActionEvent event) {
        Main.loadView("/view/Login.fxml");
    }

    @FXML
    void creacion(ActionEvent event) {
        crearExcel("HorarioSalas.xlsx");
    }

    private void crearExcel(String nombreArchivo) {
        ExcelService.createExcelFormat(nombreArchivo);
        mostrarAlerta("Plantilla creada", "Se ha generado el archivo Excel correctamente.", Alert.AlertType.INFORMATION);
    }

    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setContentText(contenido);
        alert.show();
    }
}
