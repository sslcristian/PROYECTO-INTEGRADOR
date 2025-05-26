package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.ReservaSala;

import java.time.LocalDate;

public class ReservaSalaController {

    @FXML private TextField cedulaField;
    @FXML private ComboBox<String> salaComboBox;
    @FXML private DatePicker datePicker;
    @FXML private Spinner<Integer> spinnerHoraInicio;
    @FXML private Spinner<Integer> spinnerDuracion;

    @FXML private TableView<ReservaSala> tablaSalas;
    @FXML private TableColumn<ReservaSala, Integer> colId;
    @FXML private TableColumn<ReservaSala, String> colSala;
    @FXML private TableColumn<ReservaSala, String> colEstado;
    @FXML private TableColumn<ReservaSala, String> colUsuario;
    @FXML private TableColumn<ReservaSala, String> colFecha;
    @FXML private TableColumn<ReservaSala, String> colHoraInicio;
    @FXML private TableColumn<ReservaSala, String> colDuracion;

    private final ObservableList<ReservaSala> listaReservas = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Inicializa el ComboBox con opciones
        salaComboBox.setItems(FXCollections.observableArrayList("Sala A", "Sala B", "Sala C"));

        // Spinner para hora inicio entre 0 y 23, default 8
        spinnerHoraInicio.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 8));
        spinnerHoraInicio.setEditable(true);

        // Spinner para duración entre 1 y 6 horas, default 2
        spinnerDuracion.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 6, 2));
        spinnerDuracion.setEditable(true);

        // Configura las columnas
        colId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        colSala.setCellValueFactory(cellData -> cellData.getValue().salaProperty());
        colEstado.setCellValueFactory(cellData -> cellData.getValue().estadoProperty());
        colUsuario.setCellValueFactory(cellData -> cellData.getValue().usuarioProperty());
        colFecha.setCellValueFactory(cellData -> cellData.getValue().fechaProperty());
        colHoraInicio.setCellValueFactory(cellData -> cellData.getValue().horaInicioProperty());
        colDuracion.setCellValueFactory(cellData -> cellData.getValue().duracionProperty());

        tablaSalas.setItems(listaReservas);
    }

    @FXML
    public void mostrarDisponibles() {
        listaReservas.clear();
        listaReservas.add(new ReservaSala(1, "Sala A", "Disponible", "", LocalDate.now().toString(), "08:00", "2"));
        listaReservas.add(new ReservaSala(2, "Sala B", "Disponible", "", LocalDate.now().toString(), "10:00", "1"));
    }

    @FXML
    public void reservarSala() {
        if (cedulaField.getText().isEmpty() || salaComboBox.getValue() == null || datePicker.getValue() == null) {
            mostrarAlerta("Error", "Debe llenar todos los campos.");
            return;
        }

        ReservaSala nueva = new ReservaSala(
                listaReservas.size() + 1,
                salaComboBox.getValue(),
                "Reservada",
                cedulaField.getText(),
                datePicker.getValue().toString(),
                String.format("%02d:00", spinnerHoraInicio.getValue()),
                spinnerDuracion.getValue().toString()
        );

        listaReservas.add(nueva);
    }

    @FXML
    public void devolverSala() {
        ReservaSala seleccionada = tablaSalas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            seleccionada.setEstado("Disponible");
            tablaSalas.refresh();
        } else {
            mostrarAlerta("Aviso", "Seleccione una reserva para devolver.");
        }
    }

    @FXML
    public void volverMenu() {
        System.out.println("Volver al menú...");
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
