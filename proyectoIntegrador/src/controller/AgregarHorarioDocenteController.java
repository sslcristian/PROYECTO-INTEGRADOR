package controller;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;




import data.DBConnection;
import data.ExcelService;
import data.SalaPrestadaDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.SalaPrestada;

public class AgregarHorarioDocenteController {

    @FXML private Button botonCargar, botonEliminar, botonRegistrar, btnVolverMenu;

    @FXML private TableColumn<SalaPrestada, Integer>  columnIdSala1;
    @FXML private TableColumn<SalaPrestada, String>  columnObservaciones1;
    @FXML private TableColumn<SalaPrestada, Date> columnFechaInicio1, columnFechaFin1;
    @FXML private TableColumn<SalaPrestada, Integer> columnIdSala;
    @FXML private TableColumn<SalaPrestada, String>  columnObservaciones;
    @FXML private TableColumn<SalaPrestada, Date> columnFechaInicio, columnFechaFin;
    @FXML private TableView<SalaPrestada> tableProductos, tableTemplate;

    private final Connection connection = DBConnection.getInstance().getConnection();
    private final SalaPrestadaDAO salaPrestadaDAO = new SalaPrestadaDAO(connection);

    @FXML
    public void initialize() {
        cargarDatosTablaPrincipal();
        configurarColumnasTablaPrincipal();
        configurarColumnasTemplate();
        
    }

    // Configura columnas de la tabla principal (base de datos)
    private void configurarColumnasTablaPrincipal() {
        columnIdSala1.setCellValueFactory(new PropertyValueFactory<>("idSala"));
        columnObservaciones1.setCellValueFactory(new PropertyValueFactory<>("observaciones"));

        // Formato completo con hora
        SimpleDateFormat formatoCompleto = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        // Fecha Inicio
        columnFechaInicio1.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        columnFechaInicio1.setCellFactory(column -> {
            return new TableCell<SalaPrestada, Date>() {
                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText("");
                    } else {
                        setText(formatoCompleto.format(item));
                    }
                }
            };
        });

        // Fecha Fin
        columnFechaFin1.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));
        columnFechaFin1.setCellFactory(column -> {
            return new TableCell<SalaPrestada, Date>() {
                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText("");
                    } else {
                        setText(formatoCompleto.format(item));
                    }
                }
            };
        });
    }


    private void configurarColumnasTemplate() {
        columnIdSala.setCellValueFactory(new PropertyValueFactory<>("idSala"));
        columnObservaciones.setCellValueFactory(new PropertyValueFactory<>("observaciones"));

        // Asegúrate de tener este formato declarado aquí
        SimpleDateFormat formatoCompleto = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        // Fecha Inicio
        columnFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        columnFechaInicio.setCellFactory(column -> {
            return new TableCell<SalaPrestada, Date>() {
                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText("");
                    } else {
                        setText(formatoCompleto.format(item));
                    }
                }
            };
        });

        // Fecha Fin
        columnFechaFin.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));
        columnFechaFin.setCellFactory(column -> {
            return new TableCell<SalaPrestada, Date>() {
                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText("");
                    } else {
                        setText(formatoCompleto.format(item));
                    }
                }
            };
        });
    }





    // Carga los datos reales desde la base de datos
    private void cargarDatosTablaPrincipal() {
        ObservableList<SalaPrestada> salasPrestadas = FXCollections.observableArrayList(salaPrestadaDAO.fetch());
        tableProductos.setItems(salasPrestadas);
    }

    @FXML
    private void registrar() {
        int errores = 0;

        // Usar la tabla correcta (tableTemplate)
        for (SalaPrestada sp : tableTemplate.getItems()) {
            // Validar conflicto de horario antes de guardar
            boolean conflicto = salaPrestadaDAO.existeConflictoHorario(
                sp.getIdSala(), sp.getFechaInicio(), sp.getFechaFin());

            if (conflicto) {
                errores++;
                System.err.println("⚠️ Conflicto de horario para Sala ID: " + sp.getIdSala()
                    + " entre " + sp.getFechaInicio() + " y " + sp.getFechaFin());
                continue; // Saltar este registro
            }

            try {
                salaPrestadaDAO.save(sp); // Asumes que lanza excepciones si falla
                // (Opcional) actualizar estado de la sala
                // salaDAO.actualizarEstado(sp.getIdSala(), "Ocupada");
            } catch (Exception e) {
                errores++;
                System.err.println("❌ Error al guardar sala prestada: " + sp);
                e.printStackTrace();
            }
        }

        if (errores > 0) {
            mostrarAlerta("Registro incompleto", "Se registraron algunas salas, pero hubo " + errores + " errores.",Alert.AlertType.WARNING);
        } else {
            mostrarAlerta("Registro exitoso", "Todas las salas fueron registradas correctamente.",Alert.AlertType.WARNING);
        }

        // Limpiar la tabla temporal
        tableTemplate.getItems().clear();
        // Recargar la tabla principal desde la base de datos
        cargarDatosTablaPrincipal();
    }





    @FXML
    void eliminar(ActionEvent event) {
        SalaPrestada seleccionada = tableProductos.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            salaPrestadaDAO.delete(seleccionada.getIdPrestamoS());
            cargarDatosTablaPrincipal();
        } else {
            mostrarAlerta("Sin selección", "Debe seleccionar un registro de la tabla.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    void cargarExcel(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo de Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos Excel", "*.xlsx"));

        Stage stage = (Stage) botonCargar.getScene().getWindow();
        File archivo = fileChooser.showOpenDialog(stage);

        if (archivo != null) {
            // Limpiar la tabla antes de cargar nuevos datos
            tableTemplate.getItems().clear();

            ArrayList<SalaPrestada> salasExcel;
            try {
                salasExcel = ExcelService.fetchExcel(archivo);
                if (salasExcel != null && !salasExcel.isEmpty()) {
                    tableTemplate.getItems().setAll(salasExcel);
                    mostrarAlerta("Carga exitosa", "Archivo Excel cargado correctamente.", Alert.AlertType.INFORMATION);
                } else {
                    mostrarAlerta("Archivo vacío", "El archivo Excel no contiene datos válidos. Verifique el formato de las fechas (dd/MM/yyyy HH:mm) o (dd/MM/yyyy hh:mm a).", Alert.AlertType.WARNING);
                }
            } catch (Exception ex) {
                System.err.println("Error al cargar archivo Excel: " + ex.getMessage());
                mostrarAlerta("Error", "No se pudo leer el archivo Excel. Verifique que el formato sea correcto y que las fechas tengan el patrón dd/MM/yyyy HH:mm o dd/MM/yyyy hh:mm a.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    void cerrarSesion(ActionEvent event) {
        try {
            Stage stage = (Stage) btnVolverMenu.getScene().getWindow();
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
        	mostrarAlerta("No se pudo regresar al menú.", "Error", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void creacion(ActionEvent event) {
        ExcelService.createExcelFormat("HorarioSalas.xlsx");
        mostrarAlerta("Plantilla creada", "Se ha generado correctamente la plantilla Excel.", Alert.AlertType.INFORMATION);
    }
    

    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}