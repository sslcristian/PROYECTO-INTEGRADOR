package controller;

import application.Main;
import data.DBConnection;
import data.EquipoAudiovisualDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.control.TableRow;
import model.EquipoAudiovisual;
import model.FXUtils;

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

    @FXML private TextField nombreField;
    @FXML private TextField tipoField;
    @FXML private TextField estadoField;
    @FXML private TextField ubicacionField;
    @FXML private TextField marcaField;
    @FXML private TextField modeloField;
    @FXML private DatePicker fechaAdquisicionPicker;

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

        fetchEquipos();

        equipoTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                nombreField.setText(newSel.getNombre());
                tipoField.setText(newSel.getTipo());
                estadoField.setText(newSel.getEstado());
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
        String nombre = nombreField.getText().trim();
        String tipo = tipoField.getText().trim();
        String estado = estadoField.getText().trim();
        String ubicacion = ubicacionField.getText().trim();
        String marca = marcaField.getText().trim();
        String modelo = modeloField.getText().trim();
        LocalDate fechaAdquisicionLocalDate = fechaAdquisicionPicker.getValue();  // Obtenemos la fecha de adquisición del DatePicker

        if (nombre.isEmpty() || tipo.isEmpty() || estado.isEmpty() || ubicacion.isEmpty() || marca.isEmpty() || modelo.isEmpty() || fechaAdquisicionLocalDate == null) {
            showAlert(Alert.AlertType.ERROR, "Campos vacíos", "Complete todos los campos.");
            return;
        }

        Date fechaAdquisicion = Date.valueOf(fechaAdquisicionLocalDate);  // Convertimos LocalDate a java.sql.Date

        // Aquí se está utilizando el constructor con los parámetros correctos
        EquipoAudiovisual equipo = new EquipoAudiovisual(nombre, tipo, estado, ubicacion, marca, modelo, fechaAdquisicion);

        // Guardar el equipo en la base de datos
        equipoDAO.save(equipo);  // Suponiendo que equipoDAO es una instancia de EquipoAudiovisualDAO

        fetchEquipos();  // Actualizamos la lista de equipos en la tabla
        clearFields();  // Limpiamos los campos de texto
    }


    @FXML
    public void updateEquipo(ActionEvent event) {
        EquipoAudiovisual selected = equipoTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Sin selección", "Seleccione un equipo de la tabla.");
            return;
        }

        String nombre = nombreField.getText().trim();
        String tipo = tipoField.getText().trim();
        String estado = estadoField.getText().trim();
        String ubicacion = ubicacionField.getText().trim();
        String marca = marcaField.getText().trim();
        String modelo = modeloField.getText().trim();
        java.sql.Date fechaAdquisicion = java.sql.Date.valueOf(fechaAdquisicionPicker.getValue());

        if (nombre.isEmpty() || tipo.isEmpty() || estado.isEmpty() || ubicacion.isEmpty() || marca.isEmpty() || modelo.isEmpty() || fechaAdquisicion == null) {
            showAlert(Alert.AlertType.ERROR, "Campos vacíos", "Complete todos los campos.");
            return;
        }

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
        nombreField.clear();
        tipoField.clear();
        estadoField.clear();
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

    @FXML
    public void goBackToMenu(ActionEvent event) {
        Main.loadScene("/view/AdminMenu.fxml");
    }
}
