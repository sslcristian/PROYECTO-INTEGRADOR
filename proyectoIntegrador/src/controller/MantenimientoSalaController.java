package controller;

import data.Mantenimiento_SalaDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import model.Mantenimiento_Sala;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.input.KeyCode;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Optional;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;

public class MantenimientoSalaController {

    @FXML
    private ComboBox<Integer> comboSala;
    @FXML
    private DatePicker fechaMantenimiento;
    @FXML
    private TextArea detalleMantenimiento;
    @FXML
    private TextField tecnicoResponsable;
    @FXML
    private TableView<Mantenimiento_Sala> tablaMantenimientoSala;
    @FXML
    private TableColumn<Mantenimiento_Sala, Integer> colIdSala;
    @FXML
    private TableColumn<Mantenimiento_Sala, Date> colFechaMantenimiento;
    @FXML
    private TableColumn<Mantenimiento_Sala, String> colDetalles;
    @FXML
    private TableColumn<Mantenimiento_Sala, String> colTecnicoResponsable;
    @FXML
    private Button btnVolver;

    private Mantenimiento_SalaDAO dao;

    public MantenimientoSalaController() {}

    public void init(Connection connection) {
        this.dao = new Mantenimiento_SalaDAO(connection);
        cargarSalas();
        cargarMantenimientos();
    }

    @FXML
    public void initialize() {
        tablaMantenimientoSala.setRowFactory(tv -> {
            TableRow<Mantenimiento_Sala> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    seleccionarMantenimiento();
                }
            });
            return row;
        });
    }

    private void cargarSalas() {
        ArrayList<Integer> salasDisponibles = dao.obtenerSalasDisponibles();
        comboSala.setItems(FXCollections.observableArrayList(salasDisponibles));
    }

    private void cargarMantenimientos() {
        ArrayList<Mantenimiento_Sala> lista = dao.fetch();
        tablaMantenimientoSala.setItems(FXCollections.observableArrayList(lista));
    }

    public void guardarMantenimientoSala() {
        if (validarCampos()) {
            int idSala = comboSala.getValue();
            Date fecha = Date.valueOf(fechaMantenimiento.getValue());
            String detalle = detalleMantenimiento.getText();
            String tecnico = tecnicoResponsable.getText();

            Mantenimiento_Sala mantenimiento = new Mantenimiento_Sala(0, idSala, fecha, detalle, tecnico);
            dao.save(mantenimiento);
            cargarMantenimientos();
        }
    }

    public void actualizarMantenimientoSala() {
        Mantenimiento_Sala seleccionado = tablaMantenimientoSala.getSelectionModel().getSelectedItem();
        if (seleccionado != null && validarCampos()) {
            seleccionado.setFechaMantenimiento(Date.valueOf(fechaMantenimiento.getValue()));
            seleccionado.setDetalle(detalleMantenimiento.getText());
            seleccionado.setTecnicoResponsable(tecnicoResponsable.getText());

            dao.update(seleccionado);
            cargarMantenimientos();
        }
    }

    public void borrarMantenimientoSala() {
        Mantenimiento_Sala seleccionado = tablaMantenimientoSala.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            Optional<ButtonType> resultado = mostrarConfirmacion("¿Eliminar?", "¿Estás seguro de eliminar este mantenimiento?");
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                dao.delete(seleccionado.getIdMantenimiento());
                cargarMantenimientos();
            }
        }
    }

    @FXML
    public void volverAlMenu() {
        try {
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AdminMenu.fxml"));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo regresar al menú.", Alert.AlertType.ERROR);
        }
    }

    private boolean validarCampos() {
        return comboSala.getValue() != null && fechaMantenimiento.getValue() != null &&
               !detalleMantenimiento.getText().trim().isEmpty() && !tecnicoResponsable.getText().trim().isEmpty();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private Optional<ButtonType> mostrarConfirmacion(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        return alerta.showAndWait();
    }

    private void seleccionarMantenimiento() {
        Mantenimiento_Sala seleccionado = tablaMantenimientoSala.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            comboSala.setValue(seleccionado.getIdSala());
            fechaMantenimiento.setValue(seleccionado.getFechaMantenimiento().toLocalDate());
            detalleMantenimiento.setText(seleccionado.getDetalle());
            tecnicoResponsable.setText(seleccionado.getTecnicoResponsable());
        }
    }
}
