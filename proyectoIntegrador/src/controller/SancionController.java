package controller;

import data.SancionDAO;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Sancion;
import data.DBConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;

public class SancionController {

    private final Connection connection = DBConnection.getInstance().getConnection();
    private final SancionDAO sancionDAO = new SancionDAO(connection);
    private final ObservableList<Sancion> sancionList = FXCollections.observableArrayList();

    @FXML
    private TextField cedulaUsuarioField, montoField, motivoField;
    @FXML
    private DatePicker fechaPicker;
    @FXML
    private ComboBox<String> estadoComboBox;
    @FXML
    private TableView<Sancion> sancionTable;
    @FXML
    private TableColumn<Sancion, Integer> idColumn;
    @FXML
    private TableColumn<Sancion, Long> cedulaColumn;
    @FXML
    private TableColumn<Sancion, Double> montoColumn;
    @FXML
    private TableColumn<Sancion, String> motivoColumn;
    @FXML
    private TableColumn<Sancion, Date> fechaColumn;
    @FXML
    private TableColumn<Sancion, String> estadoColumn;
    @FXML private Button btnAdd, btnUpdate, btnDelete, btnFetch, btnBack;

    @FXML
    public void onSancionSelected() {
        Sancion sancionSeleccionada = sancionTable.getSelectionModel().getSelectedItem();
        
        if (sancionSeleccionada == null) {
            clearFields();
            return;
        }

       
        cedulaUsuarioField.setText(String.valueOf(sancionSeleccionada.getCedulaUsuario()));
        montoField.setText(String.valueOf(sancionSeleccionada.getMonto()));
        motivoField.setText(sancionSeleccionada.getMotivo());
        fechaPicker.setValue(sancionSeleccionada.getFecha().toLocalDate());
        estadoComboBox.setValue(sancionSeleccionada.getEstado());
    }

    @FXML
    public void initialize() {
        estadoComboBox.setItems(FXCollections.observableArrayList("Activa", "Inactiva"));
        estadoComboBox.getSelectionModel().selectFirst();

        idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getIdSancion()).asObject());
        cedulaColumn.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getCedulaUsuario()).asObject());
        montoColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getMonto()).asObject());
        motivoColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMotivo()));
        fechaColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getFecha()));
        estadoColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEstado()));

        sancionTable.setItems(sancionList);
        fetchSanciones();

        sancionTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> onSancionSelected());
    }

    @FXML
    private void fetchSanciones() {
        try {
            sancionList.setAll(sancionDAO.fetch());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error al cargar sanciones", "No se pudieron cargar las sanciones.");
        }
    }

    @FXML
    public void addSancion(ActionEvent event) {
        try {
            long cedula = Long.parseLong(cedulaUsuarioField.getText());

            if (!sancionDAO.isUsuarioExistente(cedula)) {
                showAlert(Alert.AlertType.WARNING, "Usuario no encontrado", "La cédula ingresada no corresponde a ningún usuario registrado.");
                return;
            }

            if (sancionDAO.hasActiveSancion(cedula)) {
                showAlert(Alert.AlertType.WARNING, "Sanción activa existente", "El usuario ya tiene una sanción activa y no se le puede asignar otra.");
                return;
            }

            double monto = Double.parseDouble(montoField.getText());
            String motivo = motivoField.getText().trim();
            Date fecha = Date.valueOf(fechaPicker.getValue());
            String estado = estadoComboBox.getValue();

            Sancion nuevaSancion = new Sancion(0, cedula, monto, motivo, fecha, estado);
            sancionDAO.save(nuevaSancion);
            fetchSanciones();
            clearFields();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error de formato", "Cédula y Monto deben ser numéricos.");
        }
    }

    @FXML
    private void updateSancion(ActionEvent event) {
        // Obtener la sanción seleccionada de la tabla
        Sancion sancionSeleccionada = sancionTable.getSelectionModel().getSelectedItem();
        
        // Verificar si se seleccionó una sanción
        if (sancionSeleccionada == null) {
            showAlert(Alert.AlertType.WARNING, "Ninguna sanción seleccionada", "Debes seleccionar una sanción para actualizar.");
            return;
        }

        try {
            // Obtener y validar la cédula del usuario
            long cedulaUsuario = Long.parseLong(cedulaUsuarioField.getText().trim());

            // Verificar si el usuario tiene una sanción activa
            if (!sancionDAO.hasActiveSancion(cedulaUsuario)) {
                showAlert(Alert.AlertType.WARNING, "No tiene sanción activa", "El usuario no tiene una sanción activa para actualizar.");
                return;
            }

            // Obtener los valores de los campos
            double monto = Double.parseDouble(montoField.getText().trim());
            String motivo = motivoField.getText().trim();
            String estado = estadoComboBox.getValue();

            // Verificar que todos los campos estén completos
            if (motivo.isEmpty() || estado == null || estado.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Campos vacíos", "Todos los campos deben estar completos.");
                return;
            }

            // Crear una nueva sanción con los datos actualizados
            Sancion sancionActualizada = new Sancion(0, cedulaUsuario, monto, motivo, new Date(System.currentTimeMillis()), estado);

            // Actualizar la sanción en la base de datos
            sancionDAO.update(sancionActualizada);

            // Actualizar la vista de sanciones y limpiar los campos
            fetchSanciones();
            clearFields();

            // Mostrar mensaje de éxito
            showAlert(Alert.AlertType.INFORMATION, "Actualización exitosa", "La sanción ha sido actualizada correctamente.");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error de formato", "Cédula y Monto deben ser números válidos.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error al actualizar", "No se pudo actualizar la sanción.");
            e.printStackTrace();  // Opcional: Imprime la traza del error para depuración.
        }
    }


    @FXML
    public void deleteSancion(ActionEvent event) {
        Sancion sancionSeleccionada = sancionTable.getSelectionModel().getSelectedItem();

        if (sancionSeleccionada == null) {
            showAlert(Alert.AlertType.WARNING, "Ninguna sanción seleccionada", "Debes seleccionar una sanción para eliminar.");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmar eliminación");
        confirmDialog.setContentText("¿Estás seguro de que deseas eliminar esta sanción?");
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                
                    sancionDAO.delete(sancionSeleccionada.getIdSancion());
                    fetchSanciones();
                    clearFields();
                    showAlert(Alert.AlertType.INFORMATION, "Eliminación exitosa", "La sanción ha sido eliminada correctamente.");
               
            }
        });
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

    private void clearFields() {
       
        cedulaUsuarioField.clear();
        montoField.clear();
        motivoField.clear();
        fechaPicker.setValue(null);
        estadoComboBox.getSelectionModel().selectFirst();
        sancionTable.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}