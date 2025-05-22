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

    @FXML private Button botonCargar, botonEliminar, botonRegistrar, btnVolverMenu;

    @FXML private TableColumn<SalaPrestada, Integer> columnIdPrestamo1, columnIdSolicitud1, columnIdSala1;
    @FXML private TableColumn<SalaPrestada, String> columnFechaInicio1, columnFechaFin1, columnObservaciones1;

    @FXML private TableColumn<SalaPrestada, Integer> columnIdSala;
    @FXML private TableColumn<SalaPrestada, String> columnFechaInicio, columnFechaFin, columnObservaciones;

    @FXML private TableView<SalaPrestada> tableProductos, tableTemplate;

    private final Connection connection = DBConnection.getInstance().getConnection();
    private final SalaPrestadaDAO salaPrestadaDAO = new SalaPrestadaDAO(connection);

    @FXML
    public void initialize() {
        cargarDatosTablaPrincipal();
        configurarColumnasTablaPrincipal();
        configurarColumnasTemplate();
    }

    // Configura columnas de la tabla principal (base de datos)
    private void configurarColumnasTablaPrincipal() {
        columnIdPrestamo1.setCellValueFactory(new PropertyValueFactory<>("idPrestamoS"));
        columnIdSolicitud1.setCellValueFactory(new PropertyValueFactory<>("idSolicitudS"));
        columnIdSala1.setCellValueFactory(new PropertyValueFactory<>("idSala"));
        columnFechaInicio1.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        columnFechaFin1.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));
        columnObservaciones1.setCellValueFactory(new PropertyValueFactory<>("observaciones"));
    }

    // Configura columnas de la tabla de plantilla (desde Excel)
    private void configurarColumnasTemplate() {
        columnIdSala.setCellValueFactory(new PropertyValueFactory<>("idSala"));
        columnFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        columnFechaFin.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));
        columnObservaciones.setCellValueFactory(new PropertyValueFactory<>("observaciones"));
    }

    // Carga los datos reales desde la base de datos
    private void cargarDatosTablaPrincipal() {
        ObservableList<SalaPrestada> salasPrestadas = FXCollections.observableArrayList(salaPrestadaDAO.fetch());
        tableProductos.setItems(salasPrestadas);
    }

    @FXML
    void registrar(ActionEvent event) {
        ObservableList<SalaPrestada> listaPlantilla = tableTemplate.getItems();

        if (listaPlantilla == null || listaPlantilla.isEmpty()) {
            mostrarAlerta("Sin datos", "Debe cargar primero la plantilla con datos desde Excel.", Alert.AlertType.WARNING);
            return;
        }

        int errores = 0;
        for (SalaPrestada sp : listaPlantilla) {
            try {
                salaPrestadaDAO.save(sp);
            } catch (Exception e) {
                errores++;
                System.err.println("Error al guardar registro: " + sp + " - " + e.getMessage());
            }
        }

        if (errores == 0) {
            mostrarAlerta("Registro exitoso", "Todos los horarios de sala fueron registrados correctamente.", Alert.AlertType.INFORMATION);
        } else {
            mostrarAlerta("Registro parcial", "Algunos registros no pudieron guardarse. Revise el log para más detalles.", Alert.AlertType.WARNING);
        }
        cargarDatosTablaPrincipal(); // Refrescar tabla con datos nuevos
        tableTemplate.getItems().clear(); // Limpiar la tabla de plantilla
    }

    @FXML
    void eliminar(ActionEvent event) {
        SalaPrestada seleccionada = tableProductos.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            salaPrestadaDAO.delete(seleccionada.getIdPrestamoS());
            cargarDatosTablaPrincipal();
        } else {
            mostrarAlerta("Sin selección", "Debe seleccionar un registro de la tabla.", Alert.AlertType.WARNING);
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
            // Limpiar la tabla antes de cargar nuevos datos
            tableTemplate.getItems().clear();

            ArrayList<SalaPrestada> salasExcel;
            try {
                salasExcel = ExcelService.fetchExcel(archivo);
                if (salasExcel != null && !salasExcel.isEmpty()) {
                    tableTemplate.getItems().setAll(salasExcel);
                    mostrarAlerta("Carga exitosa", "Archivo Excel cargado correctamente.", Alert.AlertType.INFORMATION);
                } else {
                    mostrarAlerta("Archivo vacío", "El archivo Excel no contiene datos válidos. Verifique el formato de las fechas (dd/MM/yyyy HH:mm) o (dd/MM/yyyy hh:mm a).", Alert.AlertType.WARNING);
                }
            } catch (Exception ex) {
                System.err.println("Error al cargar archivo Excel: " + ex.getMessage());
                mostrarAlerta("Error", "No se pudo leer el archivo Excel. Verifique que el formato sea correcto y que las fechas tengan el patrón dd/MM/yyyy HH:mm o dd/MM/yyyy hh:mm a.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    void cerrarSesion(ActionEvent event) {
        Main.loadView("/view/AdminMenu.fxml");
    }

    @FXML
    void creacion(ActionEvent event) {
        ExcelService.createExcelFormat("HorarioSalas.xlsx");
        mostrarAlerta("Plantilla creada", "Se ha generado correctamente la plantilla Excel.", Alert.AlertType.INFORMATION);
    }

    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}