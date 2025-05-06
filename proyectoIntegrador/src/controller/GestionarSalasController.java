package controller;

import data.DBConnection;
import data.SalaInformaticaDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.SalaInformatica;
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

    @FXML private TextField nombreField, capacidadField, softwareField, hardwareField, ubicacionField, estadoField;
    @FXML private Button btnAdd, btnUpdate, btnDelete, btnFetch, btnBack;

    private final Connection connection;
    private final SalaInformaticaDAO salaDAO;
    private final ObservableList<SalaInformatica> salaList = FXCollections.observableArrayList();

    public GestionarSalasController() {
        this.connection = DBConnection.getInstance().getConnection();
        if (connection == null) {
            throw new IllegalStateException("❌ Error: No se pudo establecer la conexión con la base de datos.");
        }
        this.salaDAO = new SalaInformaticaDAO(connection);
    }

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idSala"));
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombreSala"));
        capacidadColumn.setCellValueFactory(new PropertyValueFactory<>("capacidad"));
        softwareColumn.setCellValueFactory(new PropertyValueFactory<>("softwareDisponible"));
        hardwareColumn.setCellValueFactory(new PropertyValueFactory<>("hardwareEspecial"));
        ubicacionColumn.setCellValueFactory(new PropertyValueFactory<>("ubicacion"));
        estadoColumn.setCellValueFactory(new PropertyValueFactory<>("estado"));

        fetchSalas();
    }

    @FXML
    public void fetchSalas() {
        try {
            salaList.setAll(salaDAO.fetch());
            salaTable.setItems(salaList);
        } catch (Exception e) {
            System.err.println("❌ Error al cargar la tabla de salas: " + e.getMessage());
        }
    }

    @FXML
    public void addSala() {
        try {
            String nombre = nombreField.getText();
            int capacidad = Integer.parseInt(capacidadField.getText());
            String software = softwareField.getText();
            String hardware = hardwareField.getText();
            String ubicacion = ubicacionField.getText();
            String estado = estadoField.getText();

            if (nombre.isEmpty() || estado.isEmpty()) {
                System.err.println("⚠️ Error: Los campos requeridos están vacíos.");
                return;
            }

            SalaInformatica nuevaSala = new SalaInformatica(0, nombre, capacidad, software, hardware, ubicacion, estado);
            salaDAO.save(nuevaSala);
            fetchSalas();
        } catch (NumberFormatException e) {
            System.err.println("❌ Error: Capacidad debe ser un número entero válido.");
        }
    }

    @FXML
    public void updateSala() {
        SalaInformatica salaSeleccionada = salaTable.getSelectionModel().getSelectedItem();
        if (salaSeleccionada == null) {
            System.err.println("⚠️ Error: No se ha seleccionado ninguna sala.");
            return;
        }

        try {
            salaSeleccionada.setNombreSala(nombreField.getText());
            salaSeleccionada.setCapacidad(Integer.parseInt(capacidadField.getText()));
            salaSeleccionada.setSoftwareDisponible(softwareField.getText());
            salaSeleccionada.setHardwareEspecial(hardwareField.getText());
            salaSeleccionada.setUbicacion(ubicacionField.getText());
            salaSeleccionada.setEstado(estadoField.getText());

            salaDAO.update(salaSeleccionada);
            fetchSalas();
        } catch (NumberFormatException e) {
            System.err.println("❌ Error: Capacidad debe ser un número entero válido.");
        }
    }

    @FXML
    public void deleteSala() {
        SalaInformatica salaSeleccionada = salaTable.getSelectionModel().getSelectedItem();
        if (salaSeleccionada == null) {
            System.err.println("⚠️ Error: No se ha seleccionado ninguna sala.");
            return;
        }

        salaDAO.delete(salaSeleccionada.getIdSala());
        fetchSalas();
    }

    @FXML
    public void goBackToMenu() {
        try {
            if (btnBack == null) {
                System.err.println("❌ Error: btnBack es NULL. Verifica su `fx:id` en el FXML.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AdminMenu.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("❌ Error al regresar al menú: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
