package controller;

import data.DBConnection;
import data.EquipoAudiovisualDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.control.TableRow;
import javafx.scene.control.cell.PropertyValueFactory;
import model.EquipoAudiovisual;
import model.FXUtils;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.time.LocalDate;

public class GestionarEquiposController {

    @FXML private TableView<EquipoAudiovisual> equipoTable;
    @FXML private TableColumn<EquipoAudiovisual, Integer> idColumn;
    @FXML private TableColumn<EquipoAudiovisual, String> nombreColumn;
    @FXML private TableColumn<EquipoAudiovisual, String> tipoColumn;
    @FXML private TableColumn<EquipoAudiovisual, String> estadoColumn;
    @FXML private TableColumn<EquipoAudiovisual, String> ubicacionColumn;
    @FXML private TableColumn<EquipoAudiovisual, String> marcaColumn;
    @FXML private TableColumn<EquipoAudiovisual, String> modeloColumn;
    @FXML private TableColumn<EquipoAudiovisual, String> fechaAdquisicionColumn;

    @FXML private TextField idField;
    @FXML private TextField nombreField;
    @FXML private TextField tipoField;
    @FXML private TextField estadoField;
    @FXML private TextField ubicacionField;
    @FXML private TextField marcaField;
    @FXML private TextField modeloField;
    @FXML private DatePicker fechaAdquisicionPicker;

    @FXML private ComboBox<String> tipoComboBox;
    @FXML private ComboBox<String> estadoComboBox;

    @FXML private Button btnAdd;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;
    @FXML private Button btnFetch;
    @FXML private Button btnBack;

    private final Connection connection = DBConnection.getInstance().getConnection();
    private final EquipoAudiovisualDAO equipoDAO = new EquipoAudiovisualDAO(connection);
    private final ObservableList<EquipoAudiovisual> equipoList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configuración de las columnas de la tabla
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idEquipo"));
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        tipoColumn.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        estadoColumn.setCellValueFactory(new PropertyValueFactory<>("estado"));
        ubicacionColumn.setCellValueFactory(new PropertyValueFactory<>("ubicacion"));
        marcaColumn.setCellValueFactory(new PropertyValueFactory<>("marca"));
        modeloColumn.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        fechaAdquisicionColumn.setCellValueFactory(new PropertyValueFactory<>("fechaAdquisicion"));

        // Inicialización de ComboBox
        tipoComboBox.setItems(FXCollections.observableArrayList("Proyector", "Pantalla", "Micrófono", "Otro"));
        estadoComboBox.setItems(FXCollections.observableArrayList("Disponible", "Mantenimiento", "Reservado"));

        fetchEquipos();

        equipoTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                idField.setText(String.valueOf(newSel.getIdEquipo()));
                nombreField.setText(newSel.getNombre());
                tipoComboBox.setValue(newSel.getTipo());
                estadoComboBox.setValue(newSel.getEstado());
                ubicacionField.setText(newSel.getUbicacion());
                marcaField.setText(newSel.getMarca());
                modeloField.setText(newSel.getModelo());
                fechaAdquisicionPicker.setValue(newSel.getFechaAdquisicion().toLocalDate());
            }
        });

        equipoTable.setRowFactory(tv -> {
            TableRow<EquipoAudiovisual> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 2) {
                    FXUtils.clearSelectionAndFieldsE(equipoTable, nombreField, tipoField, estadoField, ubicacionField, marcaField, modeloField, fechaAdquisicionPicker);
                }
            });
            return row;
        });

        equipoTable.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                FXUtils.clearSelectionAndFieldsE(equipoTable, nombreField, tipoField, estadoField, ubicacionField, marcaField, modeloField, fechaAdquisicionPicker);
            }
        });
    }

    @FXML
    public void addEquipo(ActionEvent event) {
        String idStr = idField.getText().trim();
        String nombre = nombreField.getText().trim();
        String tipo = tipoComboBox.getSelectionModel().getSelectedItem();
        String estado = estadoComboBox.getSelectionModel().getSelectedItem();
        String ubicacion = ubicacionField.getText().trim();
        String marca = marcaField.getText().trim();
        String modelo = modeloField.getText().trim();
        LocalDate fechaAdquisicionLocalDate = fechaAdquisicionPicker.getValue();

        if (idStr.isEmpty() || nombre.isEmpty() || tipo == null || estado == null || ubicacion.isEmpty() || marca.isEmpty() || modelo.isEmpty() || fechaAdquisicionLocalDate == null) {
            showAlert(Alert.AlertType.ERROR, "Campos vacíos", "Complete todos los campos.");
            return;
        }

        int idEquipo = Integer.parseInt(idStr);
        Date fechaAdquisicion = Date.valueOf(fechaAdquisicionLocalDate);

        EquipoAudiovisual equipo = new EquipoAudiovisual(idEquipo, nombre, tipo, estado, ubicacion, marca, modelo, fechaAdquisicion);
        equipoDAO.save(equipo);

        fetchEquipos();
        clearFields();
    }

    @FXML
    public void updateEquipo(ActionEvent event) {
        EquipoAudiovisual selected = equipoTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Sin selección", "Seleccione un equipo de la tabla.");
            return;
        }

        if (validateFields()) {
            String nombre = nombreField.getText().trim();
            String tipo = tipoComboBox.getSelectionModel().getSelectedItem();
            String estado = estadoComboBox.getSelectionModel().getSelectedItem();
            String ubicacion = ubicacionField.getText().trim();
            String marca = marcaField.getText().trim();
            String modelo = modeloField.getText().trim();
            Date fechaAdquisicion = Date.valueOf(fechaAdquisicionPicker.getValue());

            selected.setNombre(nombre);
            selected.setTipo(tipo);
            selected.setEstado(estado);
            selected.setUbicacion(ubicacion);
            selected.setMarca(marca);
            selected.setModelo(modelo);
            selected.setFechaAdquisicion(fechaAdquisicion);
            equipoDAO.update(selected);
            fetchEquipos();
            clearFields();
        }
    }

    @FXML
    public void deleteEquipo(ActionEvent event) {
        EquipoAudiovisual selected = equipoTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Sin selección", "Seleccione un equipo para eliminar.");
            return;
        }

        equipoDAO.delete(selected.getIdEquipo());
        fetchEquipos();
        clearFields();
    }

    @FXML
    public void fetchEquipos() {
        equipoList.setAll(equipoDAO.fetch());
        equipoTable.setItems(equipoList);
    }

    private void clearFields() {
        idField.clear();
        nombreField.clear();
        tipoComboBox.setValue(null);
        estadoComboBox.setValue(null);
        ubicacionField.clear();
        marcaField.clear();
        modeloField.clear();
        fechaAdquisicionPicker.setValue(null);
        equipoTable.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private boolean validateFields() {
        String idStr = idField.getText().trim();
        String nombre = nombreField.getText().trim();
        String tipo = tipoComboBox.getSelectionModel().getSelectedItem();
        String estado = estadoComboBox.getSelectionModel().getSelectedItem();
        String ubicacion = ubicacionField.getText().trim();
        String marca = marcaField.getText().trim();
        String modelo = modeloField.getText().trim();
        LocalDate fechaAdquisicionLocalDate = fechaAdquisicionPicker.getValue();

        if (idStr.isEmpty() || nombre.isEmpty() || tipo == null || estado == null || ubicacion.isEmpty() || 
            marca.isEmpty() || modelo.isEmpty() || fechaAdquisicionLocalDate == null) {
            showAlert(Alert.AlertType.ERROR, "Campos vacíos", "Complete todos los campos.");
            return false;
        }

        try {
            Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "ID inválido", "El ID debe ser un número válido.");
            return false;
        }

        return true;
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

            stage.setWidth(currentWidth);
            stage.setHeight(currentHeight);

            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo regresar al menú.");
        }
    }
}
