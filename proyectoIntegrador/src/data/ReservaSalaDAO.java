package data;

import model.ReservaSala;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class ReservaSalaDAO {
    private Connection conn;

    public ReservaSalaDAO(Connection conn) {
        this.conn = conn;
    }

    // Listar todas las salas de informática
    public ArrayList<ReservaSala> fetchAllSalas() throws SQLException {
        String sql = "SELECT ID_SALA, NOMBRE_SALA, CAPACIDAD, SOFTWARE_DISPONIBLE, HARDWARE_ESPECIAL, UBICACION, ESTADO FROM TBL_SALA_INFORMATICA";
        ArrayList<ReservaSala> lista = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ReservaSala sala = new ReservaSala();
                sala.setIdSala(rs.getInt("ID_SALA"));
                sala.setNombreSala(rs.getString("NOMBRE_SALA"));
                sala.setCapacidad(rs.getInt("CAPACIDAD"));
                sala.setSoftwareDisponible(rs.getString("SOFTWARE_DISPONIBLE"));
                sala.setHardwareEspecial(rs.getString("HARDWARE_ESPECIAL"));
                sala.setUbicacion(rs.getString("UBICACION"));
                sala.setEstado(rs.getString("ESTADO"));
                lista.add(sala);
            }
        }
        return lista;
    }

    // Crear una nueva reserva (préstamo) de sala
    public void insertReserva(ReservaSala reserva) throws SQLException {
        String sql = "INSERT INTO TBL_SALA_PRESTADA (ID_SALA, FECHA_INICIO, FECHA_FIN, OBSERVACIONES) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reserva.getIdSala());
            stmt.setDate(2, Date.valueOf(reserva.getFechaInicio()));
            stmt.setDate(3, Date.valueOf(reserva.getFechaFin()));
            stmt.setString(4, reserva.getObservaciones());
            stmt.executeUpdate();
        }
    }

    // Listar todas las reservas (préstamos) de sala, mostrando datos de la sala asociada
    public ArrayList<ReservaSala> fetchAllReservas() throws SQLException {
        String sql = "SELECT p.ID_PRESTAMO_S, p.ID_SALA, p.FECHA_INICIO, p.FECHA_FIN, p.OBSERVACIONES, " +
                     "s.NOMBRE_SALA, s.CAPACIDAD, s.SOFTWARE_DISPONIBLE, s.HARDWARE_ESPECIAL, s.UBICACION, s.ESTADO " +
                     "FROM TBL_SALA_PRESTADA p " +
                     "JOIN TBL_SALA_INFORMATICA s ON p.ID_SALA = s.ID_SALA";
        ArrayList<ReservaSala> lista = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ReservaSala reserva = new ReservaSala();
                reserva.setIdPrestamoS(rs.getInt("ID_PRESTAMO_S"));
                reserva.setIdSala(rs.getInt("ID_SALA"));
                reserva.setFechaInicio(rs.getDate("FECHA_INICIO").toLocalDate());
                reserva.setFechaFin(rs.getDate("FECHA_FIN").toLocalDate());
                reserva.setObservaciones(rs.getString("OBSERVACIONES"));
                reserva.setNombreSala(rs.getString("NOMBRE_SALA"));
                reserva.setCapacidad(rs.getInt("CAPACIDAD"));
                reserva.setSoftwareDisponible(rs.getString("SOFTWARE_DISPONIBLE"));
                reserva.setHardwareEspecial(rs.getString("HARDWARE_ESPECIAL"));
                reserva.setUbicacion(rs.getString("UBICACION"));
                reserva.setEstado(rs.getString("ESTADO"));
                lista.add(reserva);
            }
        }
        return lista;
    }
}