package controller;

import data.DBConnection;
import data.SalaInformaticaDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import model.SalaInformatica;
import model.FXUtils;

import java.io.IOException;
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
    @FXML private ComboBox<String> ubicacionComboBox;
    @FXML private TextField idSalaField, nombreField, capacidadField, softwareField, hardwareField;
    @FXML private ComboBox<String> estadoComboBox; // Cambio de TextField a ComboBox
    @FXML private Button btnAdd, btnUpdate, btnDelete, btnFetch, btnBack;

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

        salaTable.setItems(salaList);
        fetchSalas();

        // Configuración del ComboBox para el estado
        estadoComboBox.setItems(FXCollections.observableArrayList("Disponible", "Ocupada", "Mantenimiento"));
        estadoComboBox.getSelectionModel().selectFirst();  // Seleccionar "Disponible" por defecto
        ubicacionComboBox.setItems(FXCollections.observableArrayList("JLB", "EF", "SB", "DC", "CLLE", "ES", "DB"));
        ubicacionComboBox.getSelectionModel().selectFirst();
        // Mostrar los datos de la sala seleccionada en los campos de texto
        salaTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                idSalaField.setText(String.valueOf(newSel.getIdSala()));
                nombreField.setText(newSel.getNombreSala());
                capacidadField.setText(String.valueOf(newSel.getCapacidad()));
                softwareField.setText(newSel.getSoftwareDisponible());
                hardwareField.setText(newSel.getHardwareEspecial());
                ubicacionComboBox.setValue(newSel.getUbicacion());
                estadoComboBox.setValue(newSel.getEstado());  // Asignar el estado desde la sala seleccionada
             
            }
        });

        // Doble clic para deseleccionar fila
        salaTable.setRowFactory(tv -> {
            TableRow<SalaInformatica> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    FXUtils.clearSelectionAndFields(salaTable, idSalaField, nombreField, capacidadField, softwareField, hardwareField, ubicacionComboBox, estadoComboBox);
                }
            });
            return row;
        });

        // Permitir deselección con la tecla ESC
        salaTable.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                FXUtils.clearSelectionAndFields(salaTable, idSalaField, nombreField, capacidadField, softwareField, hardwareField, ubicacionComboBox, estadoComboBox);
            }
        });
    }

    @FXML
    public void fetchSalas() {
        try {
            salaList.setAll(salaDAO.fetch());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error al cargar datos", "No se pudieron cargar las salas.");
        }
    }

    @FXML
    public void addSala(ActionEvent event) {
        try {
            int idSala = Integer.parseInt(idSalaField.getText());
            String nombre = nombreField.getText().trim();
            int capacidad = Integer.parseInt(capacidadField.getText());
            String software = softwareField.getText().trim();
            String hardware = hardwareField.getText().trim();
            String ubicacion = ubicacionComboBox.getValue();  // sin toLowerCase


            String estado = estadoComboBox.getValue().toLowerCase();  // Convertir a minúsculas


            // Validar que el estado sea uno de los valores permitidos
            if (estado == null || estado.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Estado inválido", "El estado debe ser 'Disponible', 'Ocupada' o 'Mantenimiento'.");
                return;
            }

            if (nombre.isEmpty() || estado.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Campos vacíos", "Nombre y estado son obligatorios.");
                return;
            }

            if (salaDAO.exists(idSala)) {
                showAlert(Alert.AlertType.WARNING, "ID duplicado", "Ya existe una sala con ese ID.");
                return;
            }

            SalaInformatica nuevaSala = new SalaInformatica(idSala, nombre, capacidad, software, hardware, ubicacion, estado);
            salaDAO.save(nuevaSala);
            fetchSalas();
            clearFields();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error de formato", "ID y Capacidad deben ser números enteros válidos.");
        }
    }

    @FXML
    public void updateSala(ActionEvent event) {
        SalaInformatica salaSeleccionada = salaTable.getSelectionModel().getSelectedItem();
        if (salaSeleccionada == null) {
            showAlert(Alert.AlertType.WARNING, "Ninguna sala seleccionada", "Debes seleccionar una sala para actualizar.");
            return;
        }

        try {
            salaSeleccionada.setNombreSala(nombreField.getText().trim());
            salaSeleccionada.setCapacidad(Integer.parseInt(capacidadField.getText()));
            salaSeleccionada.setSoftwareDisponible(softwareField.getText().trim());
            salaSeleccionada.setHardwareEspecial(hardwareField.getText().trim());
            salaSeleccionada.setUbicacion(ubicacionComboBox.getValue());
            salaSeleccionada.setEstado(estadoComboBox.getValue().toLowerCase());


            salaDAO.update(salaSeleccionada);
            fetchSalas();
            clearFields();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error de formato", "Capacidad debe ser un número entero válido.");
        }
    }

    @FXML
    public void deleteSala(ActionEvent event) {
        SalaInformatica salaSeleccionada = salaTable.getSelectionModel().getSelectedItem();
        if (salaSeleccionada == null) {
            showAlert(Alert.AlertType.WARNING, "Ninguna sala seleccionada", "Selecciona una sala para eliminar.");
            return;
        }

        salaDAO.delete(salaSeleccionada.getIdSala());
        fetchSalas();
        clearFields();
    }

    @FXML
    public void goBackToMenu(ActionEvent event) {
        try {
            Stage stage = (Stage) btnBack.getScene().getWindow();
            double currentWidth = stage.getWidth();
            double currentHeight = stage.getHeight();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AdminMenu.fxml"));
            Parent root = loader.load();
            stage.setScene(new Scene(root));

            // Mantener el tamaño anterior
            stage.setWidth(currentWidth);
            stage.setHeight(currentHeight);

            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo regresar al menú.");
        }
    }

    private void clearFields() {
        idSalaField.clear();
        nombreField.clear();
        capacidadField.clear();
        softwareField.clear();
        hardwareField.clear();
        ubicacionComboBox.getSelectionModel().selectFirst();
        estadoComboBox.getSelectionModel().selectFirst();  // Restablecer a "Disponible"
        idSalaField.setDisable(false);  // Habilitar el campo ID para nuevas salas
        salaTable.getSelectionModel().clearSelection();  // Limpiar la selección de la tabla
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
