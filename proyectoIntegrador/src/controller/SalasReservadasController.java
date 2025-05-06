package controller;

import data.DBConnection;
import data.SalaPrestadaDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.SalaPrestada;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

public class SalasReservadasController {

    @FXML private TableView<SalaPrestada> tablaSalasReservadas;
    @FXML private TableColumn<SalaPrestada, String> colSala;
    @FXML private TableColumn<SalaPrestada, String> colUsuario;
    @FXML private TableColumn<SalaPrestada, String> colInicio;
    @FXML private TableColumn<SalaPrestada, String> colFin;
    @FXML private TableColumn<SalaPrestada, String> colEstado;
    @FXML private Button btnActualizar, btnVolver;

    private final SalaPrestadaDAO salaPrestadaDAO;
    private final ObservableList<SalaPrestada> salasList = FXCollections.observableArrayList();

    public SalasReservadasController() {
        Connection connection = DBConnection.getInstance().getConnection();
        if (connection == null) throw new IllegalStateException("❌ Error: No hay conexión con la base de datos.");
        this.salaPrestadaDAO = new SalaPrestadaDAO(connection);
    }

    @FXML
    public void initialize() {
        colSala.setCellValueFactory(new PropertyValueFactory<>("idSala"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("idSolicitudS"));
        colInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        colFin.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("observaciones"));

        actualizarTabla();
    }

    @FXML
    private void actualizarTabla() {
        salasList.setAll(salaPrestadaDAO.fetch());
        tablaSalasReservadas.setItems(salasList);
    }

    @FXML
    private void volverAlMenu() {
        cargarEscena("/view/AdminMenu.fxml", btnVolver);
    }

    private void cargarEscena(String fxmlPath, Button boton) {
        if (boton == null) {
            System.err.println("❌ Error: Botón es NULL. Verifica su fx:id en el FXML.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) boton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo cargar la escena.");
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
