package controller;

import data.SancionDAO;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Sancion;
import model.Session;
import java.sql.Connection;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class SancionController {

    // Usa la conexión de la sesión admin
    private final Connection connection = Session.getConnection();
    private final SancionDAO sancionDAO = new SancionDAO(connection);
    private final ObservableList<Sancion> sancionList = FXCollections.observableArrayList();

    @FXML private TextField cedulaUsuarioField, montoField, motivoField;
    @FXML private DatePicker fechaPicker;
    @FXML private ComboBox<String> estadoComboBox;
    @FXML private TableView<Sancion> sancionTable;
    @FXML private TableColumn<Sancion, Long> cedulaColumn;
    @FXML private TableColumn<Sancion, Double> montoColumn;
    @FXML private TableColumn<Sancion, String> motivoColumn;
    @FXML private TableColumn<Sancion, Date> fechaColumn;
    @FXML private TableColumn<Sancion, String> estadoColumn;
    @FXML private Button btnAdd, btnUpdate, btnDelete, btnFetch;
    @FXML private Button btnEliminarInactivasMes;
    @FXML private Button btnFiltrarUsuario;

    @FXML
    public void initialize() {
        estadoComboBox.setItems(FXCollections.observableArrayList("Activa", "Inactiva"));
        estadoComboBox.getSelectionModel().selectFirst();

        cedulaColumn.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getCedulaUsuario()).asObject());
        montoColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getMonto()).asObject());
        motivoColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMotivo()));
        fechaColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getFecha()));
        estadoColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEstado()));

        sancionTable.setItems(sancionList);
        fetchSanciones();

        sancionTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> onSancionSelected());

        // No permitir fechas anteriores al día de hoy 
        fechaPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.isBefore(today));
            }
        });
        fechaPicker.setEditable(false);
    }

    @FXML
    public void onSancionSelected() {
        Sancion sancion = sancionTable.getSelectionModel().getSelectedItem();
        if (sancion == null) {
            clearFields();
            return;
        }

        cedulaUsuarioField.setText(String.valueOf(sancion.getCedulaUsuario()));
        montoField.setText(String.valueOf(sancion.getMonto()));
        motivoField.setText(sancion.getMotivo());
        fechaPicker.setValue(sancion.getFecha().toLocalDate());
        estadoComboBox.setValue(sancion.getEstado());
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
                showAlert(Alert.AlertType.WARNING, "Usuario no encontrado", "La cédula ingresada no existe.");
                return;
            }

            if (sancionDAO.hasActiveSancion(cedula)) {
                showAlert(Alert.AlertType.WARNING, "Sanción activa existente", "El usuario ya tiene una sanción activa.");
                return;
            }

            double monto = Double.parseDouble(montoField.getText());
            if (monto < 0) {
                showAlert(Alert.AlertType.WARNING, "Monto inválido", "El monto no puede ser negativo.");
                return;
            }

            String motivo = motivoField.getText().trim();
            if (motivo.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Campo vacío", "El motivo no puede estar vacío.");
                return;
            }
            if (fechaPicker.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Fecha requerida", "Debes seleccionar una fecha.");
                return;
            }
            Date fecha = Date.valueOf(fechaPicker.getValue());
            String estado = estadoComboBox.getValue();

            Sancion nueva = new Sancion(0, cedula, monto, motivo, fecha, estado);
            sancionDAO.save(nueva);
            fetchSanciones();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Éxito", "Sanción agregada correctamente.");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error de formato", "Cédula y Monto deben ser numéricos.");
        }
    }

    @FXML
    private void updateSancion(ActionEvent event) {
        Sancion seleccionada = sancionTable.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            showAlert(Alert.AlertType.WARNING, "Selecciona una sanción", "Debes seleccionar una sanción para actualizar.");
            return;
        }

        try {
            long cedula = Long.parseLong(cedulaUsuarioField.getText().trim());
            double monto = Double.parseDouble(montoField.getText().trim());
            if (monto < 0) {
                showAlert(Alert.AlertType.WARNING, "Monto inválido", "El monto no puede ser negativo.");
                return;
            }
            String motivo = motivoField.getText().trim();
            if (motivo.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Campo vacío", "El motivo no puede estar vacío.");
                return;
            }
            if (fechaPicker.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Fecha requerida", "Debes seleccionar una fecha.");
                return;
            }
            Date fecha = Date.valueOf(fechaPicker.getValue());
            String estado = estadoComboBox.getValue();

            if (estado == null || estado.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Campos vacíos", "Todos los campos deben estar completos.");
                return;
            }

            Sancion actualizada = new Sancion(
                seleccionada.getIdSancion(),
                cedula,
                monto,
                motivo,
                fecha,
                estado
            );

            sancionDAO.update(actualizada);
            fetchSanciones();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Éxito", "Sanción actualizada correctamente.");

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error de formato", "Cédula y Monto deben ser válidos.");
        }
    }

    @FXML
    public void deleteSancion(ActionEvent event) {
        Sancion seleccionada = sancionTable.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            showAlert(Alert.AlertType.WARNING, "Selecciona una sanción", "Debes seleccionar una sanción para eliminar.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setContentText("¿Deseas eliminar esta sanción?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                sancionDAO.delete(seleccionada.getIdSancion());
                fetchSanciones();
                clearFields();
                showAlert(Alert.AlertType.INFORMATION, "Eliminada", "Sanción eliminada correctamente.");
            }
        });
    }

   

    @FXML
    public void eliminarSancionesInactivasDelMes(ActionEvent event) {
        try {
            LocalDate now = LocalDate.now();
            int mes = now.getMonthValue();
            int anio = now.getYear();
            int eliminadas = sancionDAO.eliminarInactivasPorMes(mes, anio);
            fetchSanciones();
            showAlert(Alert.AlertType.INFORMATION, "Eliminación completada", eliminadas + " sanciones inactivas eliminadas para este mes.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudieron eliminar las sanciones inactivas.");
        }
    }

    @FXML
    public void filtrarPorUsuario(ActionEvent event) {
        try {
            String cedulaText = cedulaUsuarioField.getText().trim();
            if (cedulaText.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Campo vacío", "Ingrese una cédula para filtrar.");
                return;
            }
            long cedula = Long.parseLong(cedulaText);
            List<Sancion> sanciones = sancionDAO.buscarPorCedula(cedula);
            if (sanciones.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "Sin resultados", "No se encontraron sanciones para la cédula ingresada.");
            }
            sancionList.setAll(sanciones);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error de formato", "La cédula debe ser numérica.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Ocurrió un error al filtrar las sanciones.");
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