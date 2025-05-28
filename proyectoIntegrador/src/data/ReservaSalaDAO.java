package data;

import model.ReservaSala;
import java.sql.*;
import java.util.ArrayList;

public class ReservaSalaDAO {
    private final Connection connection;

    public ReservaSalaDAO(Connection connection) {
        this.connection = connection;
    }

    // Trae todas las salas informáticas (TBL_SALA_INFORMATICA)
    public ArrayList<ReservaSala> fetchAll() throws SQLException {
        ArrayList<ReservaSala> salas = new ArrayList<>();
        String sql = "SELECT ID_SALA, NOMBRE_SALA, CAPACIDAD, SOFTWARE_DISPONIBLE, HARDWARE_ESPECIAL, UBICACION, ESTADO FROM proyecto343.TBL_SALA_INFORMATICA";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                ReservaSala sala = new ReservaSala(
                        rs.getInt("ID_SALA"),
                        rs.getString("NOMBRE_SALA"),
                        rs.getInt("CAPACIDAD"),
                        rs.getString("SOFTWARE_DISPONIBLE"),
                        rs.getString("HARDWARE_ESPECIAL"),
                        rs.getString("UBICACION"),
                        rs.getString("ESTADO")
                );
                salas.add(sala);
            }
        }
        return salas;
    }

    // Puedes agregar otros métodos según necesidades, por ejemplo, fetchDisponibles() si solo quieres mostrar salas disponibles
    public ArrayList<ReservaSala> fetchDisponibles() throws SQLException {
        ArrayList<ReservaSala> salas = new ArrayList<>();
        String sql = "SELECT ID_SALA, NOMBRE_SALA, CAPACIDAD, SOFTWARE_DISPONIBLE, HARDWARE_ESPECIAL, UBICACION, ESTADO " +
                     "FROM proyecto343.TBL_SALA_INFORMATICA WHERE ESTADO = 'Disponible'";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                ReservaSala sala = new ReservaSala(
                        rs.getInt("ID_SALA"),
                        rs.getString("NOMBRE_SALA"),
                        rs.getInt("CAPACIDAD"),
                        rs.getString("SOFTWARE_DISPONIBLE"),
                        rs.getString("HARDWARE_ESPECIAL"),
                        rs.getString("UBICACION"),
                        rs.getString("ESTADO")
                );
                salas.add(sala);
            }
        }
        return salas;
    }
}