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
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;

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
    @FXML
    private Button btnBack;

    private Mantenimiento_EquipoDAO dao;

    public MantenimientoEquipoController() {
        // Constructor vacío requerido por FXMLLoader
    }

    public void init(Connection connection) {
        this.dao = new Mantenimiento_EquipoDAO(connection);
        System.out.println("Conexión establecida: " + connection);  // Verificar conexión
        cargarEquipos();
        cargarMantenimientos();
    }



    @FXML
    public void initialize() {
        // Configuración de las columnas
        colIdEquipo.setCellValueFactory(cellData -> cellData.getValue().idEquipoProperty().asObject());
        colFechaMantenimiento.setCellValueFactory(cellData -> cellData.getValue().fechaMantenimientoProperty());
        colDetalles.setCellValueFactory(cellData -> cellData.getValue().detalleProperty());
        colTecnicoResponsable.setCellValueFactory(cellData -> cellData.getValue().tecnicoResponsableProperty());

        tablaMantenimientoEquipo.setRowFactory(tv -> {
            TableRow<Mantenimiento_Equipo> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    if (event.getClickCount() == 2) {
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
            if (event.getCode() == KeyCode.ESCAPE) {
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
        
        System.out.println("Equipos obtenidos del DAO: " + equiposDisponibles);  // Verificar los equipos obtenidos

        comboEquipo.getItems().clear();  
        comboEquipo.getItems().addAll(equiposDisponibles);

        System.out.println("Items en el ComboBox: " + comboEquipo.getItems());  // Verificar los items en el ComboBox
    }



    private void cargarMantenimientos() {
        ArrayList<Mantenimiento_Equipo> lista = dao.fetch();
        tablaMantenimientoEquipo.setItems(FXCollections.observableArrayList(lista));
    }

    public void guardarMantenimientoEquipo() {
        if (validarCampos()) {
            int idEquipo = comboEquipo.getValue();
            
            // Verificar si el equipo ya está en mantenimiento
            if (dao.estaEnMantenimiento(idEquipo)) {
                mostrarAlerta("Error", "Este equipo ya está registrado en mantenimiento.", Alert.AlertType.ERROR);
                return;
            }

            Date fecha = Date.valueOf(fechaMantenimiento.getValue());
            String detalle = detalleMantenimiento.getText();
            String tecnico = tecnicoResponsable.getText();

            // Crear un nuevo objeto Mantenimiento_Equipo
            Mantenimiento_Equipo mantenimiento = new Mantenimiento_Equipo(0, idEquipo, fecha, detalle, tecnico);

            // Guardar el mantenimiento
            dao.save(mantenimiento);
            
            // Actualizar el estado del equipo a 'mantenimiento'
            dao.actualizarEstadoEquipo(idEquipo, "mantenimiento");

            // Mostrar una alerta de éxito
            mostrarAlerta("Registro exitoso", "El mantenimiento ha sido registrado correctamente.", Alert.AlertType.INFORMATION);

            // Recargar la lista de mantenimientos
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
                dao.actualizarEstadoEquipo(seleccionado.getIdEquipo(), "disponible");

                mostrarAlerta("Eliminado", "El mantenimiento ha sido eliminado correctamente.", Alert.AlertType.INFORMATION);
                cargarMantenimientos();
            }
        }
    }

    @FXML
    public void volverAlMenu() {
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
            mostrarAlerta("No se pudo regresar al menú.", "Error", Alert.AlertType.ERROR);
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
