package controller;

import application.Main;
import data.DBConnection;
import data.SalaInformaticaDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.control.TableRow;
import model.SalaInformatica;
import model.FXUtils;

import java.sql.Connection;

public class GestionarSalasController {

    @FXML private TableView<SalaInformatica> salaTable;
    @FXML private TableColumn<SalaInformatica, Integer> idColumn;
    @FXML private TableColumn<SalaInformatica, String> nombreColumn;
    @FXML private TableColumn<SalaInformatica, Integer> capacidadColumn;
    @FXML private TableColumn<SalaInformatica, String> softwareColumn;
    @FXML private TableColumn<SalaInformatica, String> hardwareColumn;
    @FXML private TableColumn<SalaInformatica, String> ubicacionColumn;
    @FXML private TableColumn<SalaInformatica, String> estadoColumn;

    @FXML private TextField idSalaField;
    @FXML private TextField nombreField;
    @FXML private TextField capacidadField;
    @FXML private TextField softwareField;
    @FXML private TextField hardwareField;
    @FXML private TextField ubicacionField;
    @FXML private TextField estadoField;
    

    @FXML private Button btnAdd;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;
    @FXML private Button btnFetch;
    @FXML private Button btnBack;

    private final Connection connection = DBConnection.getInstance().getConnection();
    private final SalaInformaticaDAO salaDAO = new SalaInformaticaDAO(connection);
    private final ObservableList<SalaInformatica> salaList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
    	idColumn.setCellValueFactory(new PropertyValueFactory<>("idSala"));
    	nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombreSala"));
        capacidadColumn.setCellValueFactory(new PropertyValueFactory<>("capacidad"));
        softwareColumn.setCellValueFactory(new PropertyValueFactory<>("softwareDisponible"));
        hardwareColumn.setCellValueFactory(new PropertyValueFactory<>("hardwareEspecial"));
        ubicacionColumn.setCellValueFactory(new PropertyValueFactory<>("ubicacion"));
        estadoColumn.setCellValueFactory(new PropertyValueFactory<>("estado"));

        fetchSalas();

        salaTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                idSalaField.setText(String.valueOf(newSel.getIdSala()));
                nombreField.setText(newSel.getNombreSala());
                capacidadField.setText(String.valueOf(newSel.getCapacidad()));
                softwareField.setText(newSel.getSoftwareDisponible());
                hardwareField.setText(newSel.getHardwareEspecial());
                ubicacionField.setText(newSel.getUbicacion());
                estadoField.setText(newSel.getEstado());
                idSalaField.setDisable(true);
            }
        });

        salaTable.setRowFactory(tv -> {
            TableRow<SalaInformatica> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 2) {
                    FXUtils.clearSelectionAndFieldsS(salaTable, idSalaField, nombreField, capacidadField, softwareField, hardwareField, ubicacionField, estadoField);
                }
            });
            return row;
        });

        salaTable.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                FXUtils.clearSelectionAndFieldsS(salaTable, idSalaField, nombreField, capacidadField, softwareField, hardwareField, ubicacionField, estadoField);
            }
        });
    }

    @FXML
    public void addSala(ActionEvent event) {
        String idText = idSalaField.getText().trim();
        String nombre = nombreField.getText().trim();
        String capacidadText = capacidadField.getText().trim();
        String software = softwareField.getText().trim();
        String hardware = hardwareField.getText().trim();
        String ubicacion = ubicacionField.getText().trim();
        String estado = estadoField.getText().trim();

        if (idText.isEmpty() || nombre.isEmpty() || capacidadText.isEmpty() || software.isEmpty() || hardware.isEmpty() || ubicacion.isEmpty() || estado.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Campos vacíos", "Complete todos los campos.");
            return;
        }

        if (!idText.matches("\\d+") || !capacidadText.matches("\\d+")) {
            showAlert(Alert.AlertType.ERROR, "Formato inválido", "ID y Capacidad deben ser números enteros.");
            return;
        }

        int id = Integer.parseInt(idText);
        int capacidad = Integer.parseInt(capacidadText);

        if (salaDAO.exists(id)) {
            showAlert(Alert.AlertType.WARNING, "Sala duplicada", "Ya existe una sala con ese ID.");
            return;
        }

        SalaInformatica sala = new SalaInformatica(id, nombre, capacidad, software, hardware, ubicacion, estado);
        salaDAO.save(sala);
        fetchSalas();
        clearFields();
    }

    @FXML
    public void updateSala(ActionEvent event) {
        SalaInformatica selected = salaTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Sin selección", "Seleccione una sala de la tabla.");
            return;
        }

        String nombre = nombreField.getText().trim();
        String capacidadText = capacidadField.getText().trim();
        String software = softwareField.getText().trim();
        String hardware = hardwareField.getText().trim();
        String ubicacion = ubicacionField.getText().trim();
        String estado = estadoField.getText().trim();

        if (nombre.isEmpty() || capacidadText.isEmpty() || software.isEmpty() || hardware.isEmpty() || ubicacion.isEmpty() || estado.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Campos vacíos", "Complete todos los campos.");
            return;
        }

        if (!capacidadText.matches("\\d+")) {
            showAlert(Alert.AlertType.ERROR, "Capacidad inválida", "La capacidad debe ser un número entero.");
            return;
        }

        selected.setNombreSala(nombre);
        selected.setCapacidad(Integer.parseInt(capacidadText));
        selected.setSoftwareDisponible(software);
        selected.setHardwareEspecial(hardware);
        selected.setUbicacion(ubicacion);
        selected.setEstado(estado);
        salaDAO.update(selected);
        fetchSalas();
        clearFields();
    }

    @FXML
    public void deleteSala(ActionEvent event) {
        SalaInformatica selected = salaTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Sin selección", "Seleccione una sala para eliminar.");
            return;
        }

        salaDAO.delete(selected.getIdSala());
        fetchSalas();
        clearFields();
    }

    @FXML
    public void fetchSalas() {
        salaList.setAll(salaDAO.fetch());
        salaTable.setItems(salaList);
    }

    private void clearFields() {
        idSalaField.clear();
        nombreField.clear();
        capacidadField.clear();
        softwareField.clear();
        hardwareField.clear();
        ubicacionField.clear();
        estadoField.clear();
        idSalaField.setDisable(false);
        salaTable.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void goBackToMenu(ActionEvent event) {
        Main.loadScene("/view/AdminMenu.fxml");
    }
        

}


