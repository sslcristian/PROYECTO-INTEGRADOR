package data;

import model.SalaPrestada;

import java.sql.*;
import java.util.ArrayList;

public class SalaPrestadaDAO implements CRUD_Operation<SalaPrestada, Integer> {

    private final Connection connection;

    public SalaPrestadaDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(SalaPrestada salaPrestada) {
        String query = "INSERT INTO TBL_SALA_PRESTADA (id_solicitud_s, id_sala, fecha_inicio, fecha_fin, observaciones) " +
                       "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, salaPrestada.getIdSolicitudS());
            
            if (salaPrestada.getIdSala() != 0) {
                pstmt.setInt(2, salaPrestada.getIdSala());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            pstmt.setTimestamp(3, salaPrestada.getFechaInicio());
            pstmt.setTimestamp(4, salaPrestada.getFechaFin());
            pstmt.setString(5, salaPrestada.getObservaciones());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        salaPrestada.setIdPrestamoS(generatedKeys.getInt(1));
                    }
                }
                System.out.println("Sala prestada registrada correctamente.");
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar sala prestada: " + e.getMessage());
        }
    }

    @Override
    public ArrayList<SalaPrestada> fetch() {
        ArrayList<SalaPrestada> salasPrestadas = new ArrayList<>();
        String query = "SELECT * FROM TBL_SALA_PRESTADA";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int idPrestamoS = rs.getInt("id_prestamo_s");
                int idSolicitudS = rs.getInt("id_solicitud_s");
                int idSala = rs.getInt("id_sala");
                Timestamp fechaInicio = rs.getTimestamp("fecha_inicio");
                Timestamp fechaFin = rs.getTimestamp("fecha_fin");
                String observaciones = rs.getString("observaciones");

                SalaPrestada salaPrestada = new SalaPrestada(
                        idPrestamoS, idSolicitudS, idSala, fechaInicio, fechaFin, observaciones
                );

                salasPrestadas.add(salaPrestada);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener salas prestadas: " + e.getMessage());
        }

        return salasPrestadas;
    }

    @Override
    public void update(SalaPrestada salaPrestada) {
        String query = "UPDATE TBL_SALA_PRESTADA SET id_solicitud_s=?, id_sala=?, fecha_inicio=?, fecha_fin=?, observaciones=? " +
                       "WHERE id_prestamo_s=?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, salaPrestada.getIdSolicitudS());

            if (salaPrestada.getIdSala() != 0) {
                pstmt.setInt(2, salaPrestada.getIdSala());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            pstmt.setTimestamp(3, salaPrestada.getFechaInicio());
            pstmt.setTimestamp(4, salaPrestada.getFechaFin());
            pstmt.setString(5, salaPrestada.getObservaciones());
            pstmt.setInt(6, salaPrestada.getIdPrestamoS());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Sala prestada actualizada correctamente.");
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar sala prestada: " + e.getMessage());
        }
    }

    @Override
    public void delete(Integer id) {
        String query = "DELETE FROM TBL_SALA_PRESTADA WHERE id_prestamo_s=?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Sala prestada eliminada correctamente.");
            } else {
                System.out.println("No se encontró una sala prestada con ID: " + id);
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar sala prestada: " + e.getMessage());
        }
    }

    @Override
    public boolean authenticate(Integer id) {
        String query = "SELECT id_prestamo_s FROM TBL_SALA_PRESTADA WHERE id_prestamo_s=?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar existencia de sala prestada: " + e.getMessage());
        }
        return false;
    }
}
