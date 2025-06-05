package controller;

import application.Main;
import data.SalaPrestadaDAO;
import data.SalaInformaticaDAO;
import data.UsuarioDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import model.SalaPrestada;
import model.SalaPrestadaConCedula;
import model.Session;
import model.SalaInformatica;
import model.Usuario;

import java.sql.Connection;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

public class SalasReservadasController {

    // Tabla de salas reservadas por usuarios (con cédula)
    @FXML private TableView<SalaPrestadaConCedula> tablaSalasReservadas;
    @FXML private TableColumn<SalaPrestadaConCedula, String> colSala;
    @FXML private TableColumn<SalaPrestadaConCedula, String> colNombreSala;
    @FXML private TableColumn<SalaPrestadaConCedula, String> colUsuario;
    @FXML private TableColumn<SalaPrestadaConCedula, String> colNombreUsuario;
    @FXML private TableColumn<SalaPrestadaConCedula, String> colInicio;
    @FXML private TableColumn<SalaPrestadaConCedula, String> colFin;
    @FXML private TableColumn<SalaPrestadaConCedula, String> colEstado;
    @FXML private Button btnActualizar;

    // Tabla de salas reservadas por admin (sin cédula)
    @FXML private TableView<SalaPrestada> tablaSalasReservadasAdmin;
    @FXML private TableColumn<SalaPrestada, String> colSalaAdmin;
    @FXML private TableColumn<SalaPrestada, String> colNombreSalaAdmin;
    @FXML private TableColumn<SalaPrestada, String> colInicioAdmin;
    @FXML private TableColumn<SalaPrestada, String> colFinAdmin;
    @FXML private TableColumn<SalaPrestada, String> colEstadoAdmin;
    @FXML private Button btnActualizarAdmin;

    @FXML private Button btnVolver;

    // Usa la conexión de la sesión admin
    private final Connection connection = Session.getConnection();
    private final SalaPrestadaDAO salaPrestadaDAO = new SalaPrestadaDAO(connection);
    private final SalaInformaticaDAO salaInformaticaDAO = new SalaInformaticaDAO(connection);
    private final UsuarioDAO usuarioDAO = new UsuarioDAO(connection);

    private final ObservableList<SalaPrestadaConCedula> salasUsuariosList = FXCollections.observableArrayList();
    private final ObservableList<SalaPrestada> salasAdminList = FXCollections.observableArrayList();

    private Map<Integer, String> mapaSalas = new HashMap<>();
    private Map<Long, String> mapaUsuarios = new HashMap<>();

    @FXML
    public void initialize() {
    
        SimpleDateFormat formatoCompleto = new SimpleDateFormat("dd/MM/yyyy HH:mm");

       
        cargarNombresSalas();
        cargarNombresUsuarios();

        
        colSala.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getIdSala())));
        colNombreSala.setCellValueFactory(cellData -> new SimpleStringProperty(getNombreSala(cellData.getValue().getIdSala())));
        colUsuario.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getCedulaUsuario())));
        colNombreUsuario.setCellValueFactory(cellData -> new SimpleStringProperty(getNombreUsuario(cellData.getValue().getCedulaUsuario())));
        colInicio.setCellValueFactory(cellData -> {
            java.util.Date fecha = cellData.getValue().getFechaInicio();
            return new SimpleStringProperty(formatoCompleto.format(fecha));
        });
        colFin.setCellValueFactory(cellData -> {
            java.util.Date fecha = cellData.getValue().getFechaFin();
            return new SimpleStringProperty(formatoCompleto.format(fecha));
        });
        colEstado.setCellValueFactory(cellData -> new SimpleStringProperty(determineEstado(cellData.getValue())));
        
        colSalaAdmin.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getIdSala())));
        colNombreSalaAdmin.setCellValueFactory(cellData -> new SimpleStringProperty(getNombreSala(cellData.getValue().getIdSala())));
        colInicioAdmin.setCellValueFactory(cellData -> {
            java.util.Date fecha = cellData.getValue().getFechaInicio();
            return new SimpleStringProperty(formatoCompleto.format(fecha));
        });
        colFinAdmin.setCellValueFactory(cellData -> {
            java.util.Date fecha = cellData.getValue().getFechaFin();
            return new SimpleStringProperty(formatoCompleto.format(fecha));
        });
        colEstadoAdmin.setCellValueFactory(cellData -> new SimpleStringProperty(determineEstado(cellData.getValue())));

        fetchSalasReservadas();
        fetchSalasReservadasAdmin();

        tablaSalasReservadas.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                tablaSalasReservadas.getSelectionModel().clearSelection();
            }
        });
        tablaSalasReservadasAdmin.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                tablaSalasReservadasAdmin.getSelectionModel().clearSelection();
            }
        });
    }

    private void cargarNombresSalas() {
        try {
            List<SalaInformatica> salas = salaInformaticaDAO.fetchAll();
            for (SalaInformatica s : salas) {
                mapaSalas.put(s.getIdSala(), s.getNombreSala());
            }
        } catch (Exception e) {
            
        }
    }

    private void cargarNombresUsuarios() {
        try {
            List<Usuario> usuarios = usuarioDAO.fetchAllCedulaNombre();
            for (Usuario u : usuarios) {
                mapaUsuarios.put(u.getCedula(), u.getNombre());
            }
        } catch (Exception e) {
          
        }
    }

    private String getNombreSala(int idSala) {
        return mapaSalas.getOrDefault(idSala, String.valueOf(idSala));
    }

    private String getNombreUsuario(long cedula) {
        return mapaUsuarios.getOrDefault(cedula, String.valueOf(cedula));
    }

    @FXML
    public void actualizarTabla(ActionEvent event) {
        fetchSalasReservadas();
    }

    @FXML
    public void actualizarTablaAdmin(ActionEvent event) {
        fetchSalasReservadasAdmin();
    }

    @FXML
    public void fetchSalasReservadas() {
        try {
            List<SalaPrestadaConCedula> lista = salaPrestadaDAO.fetchConCedulaUsuario();
            salasUsuariosList.setAll(lista);
            tablaSalasReservadas.setItems(salasUsuariosList);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Hubo un problema al cargar los datos de las salas (usuarios).");
        }
    }

    @FXML
    public void fetchSalasReservadasAdmin() {
        try {
            List<SalaPrestada> lista = salaPrestadaDAO.fetch();
          
            salasAdminList.setAll(lista.stream()
                    .filter(s -> s.getIdSolicitudS() == 0)
                    .collect(Collectors.toList()));
            tablaSalasReservadasAdmin.setItems(salasAdminList);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Hubo un problema al cargar los datos de las salas (admin).");
        }
    }

    @FXML
    public void volverAlMenu(ActionEvent event) {
        Main.loadScene("/view/AdminMenu.fxml");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private String determineEstado(SalaPrestada sala) {
        Date now = new Date(System.currentTimeMillis());
        if (sala.getFechaInicio().after(now)) {
            return "Reservada";
        } else if (sala.getFechaFin().before(now)) {
            return "Finalizada";
        } else {
            return "En uso";
        }
    }
}