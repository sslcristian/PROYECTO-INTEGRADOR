package controller;

import data.Mantenimiento_EquipoDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;

import model.Mantenimiento_Equipo;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.input.KeyCode;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Optional;

public class MantenimientoEquipoController {
    @FXML
    private ComboBox<Integer> comboEquipo;
    @FXML
    private DatePicker fechaMantenimiento;
    @FXML
    private TextArea detalleMantenimiento;
    @FXML
    private TextField tecnicoResponsable;
    @FXML
    private TableView<Mantenimiento_Equipo> tablaMantenimientoEquipo;
    @FXML
    private TableColumn<Mantenimiento_Equipo, Integer> colIdEquipo;
    @FXML
    private TableColumn<Mantenimiento_Equipo, Date> colFechaMantenimiento;
    @FXML
    private TableColumn<Mantenimiento_Equipo, String> colDetalles;
    @FXML
    private TableColumn<Mantenimiento_Equipo, String> colTecnicoResponsable;
    @FXML
    private BorderPane rootLayout;

    private Mantenimiento_EquipoDAO dao;

    public MantenimientoEquipoController(Connection connection) {
        this.dao = new Mantenimiento_EquipoDAO(connection);
    }

    @FXML
    public void initialize() {
        cargarEquipos();
        cargarMantenimientos();
        
        // Interacción con TableView mejorada
        tablaMantenimientoEquipo.setRowFactory(tv -> {
            TableRow<Mantenimiento_Equipo> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    if (event.getClickCount() == 2) { // Doble clic para deseleccionar
                        tablaMantenimientoEquipo.getSelectionModel().clearSelection();
                        limpiarCampos();
                    } else {
                        seleccionarMantenimiento();
                    }
                }
            });
            return row;
        });

        tablaMantenimientoEquipo.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) { // Escape para deseleccionar
                tablaMantenimientoEquipo.getSelectionModel().clearSelection();
                limpiarCampos();
            }
        });
    }

    private void seleccionarMantenimiento() {
        Mantenimiento_Equipo seleccionado = tablaMantenimientoEquipo.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            comboEquipo.setValue(seleccionado.getIdEquipo());
            fechaMantenimiento.setValue(seleccionado.getFechaMantenimiento().toLocalDate());
            detalleMantenimiento.setText(seleccionado.getDetalle());
            tecnicoResponsable.setText(seleccionado.getTecnicoResponsable());
        }
    }

    private void limpiarCampos() {
        comboEquipo.setValue(null);
        fechaMantenimiento.setValue(null);
        detalleMantenimiento.clear();
        tecnicoResponsable.clear();
    }

    private void cargarEquipos() {
        ArrayList<Integer> equiposDisponibles = dao.obtenerEquiposDisponibles();
        comboEquipo.setItems(FXCollections.observableArrayList(equiposDisponibles));
    }

    private void cargarMantenimientos() {
        ArrayList<Mantenimiento_Equipo> lista = dao.fetch();
        tablaMantenimientoEquipo.setItems(FXCollections.observableArrayList(lista));
    }

    public void guardarMantenimientoEquipo() {
        if (validarCampos()) {
            int idEquipo = comboEquipo.getValue();
            Date fecha = Date.valueOf(fechaMantenimiento.getValue());
            String detalle = detalleMantenimiento.getText();
            String tecnico = tecnicoResponsable.getText();

            Mantenimiento_Equipo mantenimiento = new Mantenimiento_Equipo(0, idEquipo, fecha, detalle, tecnico);
            dao.save(mantenimiento);
            dao.actualizarEstadoEquipo(idEquipo, "mantenimiento"); // Actualiza el estado en la base de datos

            mostrarAlerta("Registro exitoso", "El mantenimiento ha sido registrado correctamente.", Alert.AlertType.INFORMATION);
            cargarMantenimientos();
        }
    }

    public void actualizarMantenimientoEquipo() {
        Mantenimiento_Equipo seleccionado = tablaMantenimientoEquipo.getSelectionModel().getSelectedItem();
        if (seleccionado != null && validarCampos()) {
            seleccionado.setFechaMantenimiento(Date.valueOf(fechaMantenimiento.getValue()));
            seleccionado.setDetalle(detalleMantenimiento.getText());
            seleccionado.setTecnicoResponsable(tecnicoResponsable.getText());

            dao.update(seleccionado);
            mostrarAlerta("Actualización exitosa", "El mantenimiento ha sido actualizado correctamente.", Alert.AlertType.INFORMATION);
            cargarMantenimientos();
        }
    }

    public void borrarMantenimientoEquipo() {
        Mantenimiento_Equipo seleccionado = tablaMantenimientoEquipo.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            Optional<ButtonType> resultado = mostrarConfirmacion("¿Eliminar?", "¿Estás seguro de eliminar este mantenimiento?");
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                dao.delete(seleccionado.getIdMantenimiento());
                dao.actualizarEstadoEquipo(seleccionado.getIdEquipo(), "disponible"); // Reinicia el estado del equipo

                mostrarAlerta("Eliminado", "El mantenimiento ha sido eliminado correctamente.", Alert.AlertType.INFORMATION);
                cargarMantenimientos();
            }
        }
    }

    public void volverAlMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ControlMantenimiento.fxml"));
            Parent menuView = loader.load();
            rootLayout.setCenter(menuView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean validarCampos() {
        if (comboEquipo.getValue() == null || fechaMantenimiento.getValue() == null ||
                detalleMantenimiento.getText().trim().isEmpty() || tecnicoResponsable.getText().trim().isEmpty()) {
            mostrarAlerta("Error", "Todos los campos deben estar completos.", Alert.AlertType.ERROR);
            return false;
        }
        return true;
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
}
