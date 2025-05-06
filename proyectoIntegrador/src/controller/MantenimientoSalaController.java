package controller;

import data.Mantenimiento_SalaDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;

import model.Mantenimiento_Sala;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Optional;

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
    private BorderPane rootLayout; // Para mantener AdminMenu y cambiar solo el contenido principal

    private Mantenimiento_SalaDAO dao;

    public MantenimientoSalaController(Connection connection) {
        this.dao = new Mantenimiento_SalaDAO(connection);
    }

    @FXML
    public void initialize() {
        cargarSalas();
        cargarMantenimientos();
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
            dao.actualizarEstadoSala(idSala, "mantenimiento"); // Actualiza el estado en la base de datos

            mostrarAlerta("Registro exitoso", "El mantenimiento ha sido registrado correctamente.", Alert.AlertType.INFORMATION);
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
            mostrarAlerta("Actualización exitosa", "El mantenimiento ha sido actualizado correctamente.", Alert.AlertType.INFORMATION);
            cargarMantenimientos();
        }
    }

    public void borrarMantenimientoSala() {
        Mantenimiento_Sala seleccionado = tablaMantenimientoSala.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            Optional<ButtonType> resultado = mostrarConfirmacion("¿Eliminar?", "¿Estás seguro de eliminar este mantenimiento?");
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                dao.delete(seleccionado.getIdMantenimiento());
                dao.actualizarEstadoSala(seleccionado.getIdSala(), "disponible"); // Reinicia el estado de la sala

                mostrarAlerta("Eliminado", "El mantenimiento ha sido eliminado correctamente.", Alert.AlertType.INFORMATION);
                cargarMantenimientos();
            }
        }
    }

    public void volverAlMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ControlMantenimiento.fxml"));
            Parent menuView = loader.load();

            rootLayout.setCenter(menuView); // Mantiene AdminMenu sin cambios
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean validarCampos() {
        if (comboSala.getValue() == null || fechaMantenimiento.getValue() == null ||
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
