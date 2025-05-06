package data;

import model.SalaPrestada;
import java.sql.*;
import java.util.ArrayList;

public class SalaPrestadaDAO {

    private final Connection connection;

    public SalaPrestadaDAO(Connection connection) {
        this.connection = connection;
    }

    public void save(SalaPrestada salaPrestada) {
        String query = "INSERT INTO TBL_SALA_PRESTADA (id_solicitud_s, id_sala, fecha_inicio, fecha_fin, observaciones) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, salaPrestada.getIdSolicitudS());
            pstmt.setObject(2, salaPrestada.getIdSala() > 0 ? salaPrestada.getIdSala() : null, Types.INTEGER);
            pstmt.setDate(3, salaPrestada.getFechaInicio());
            pstmt.setDate(4, salaPrestada.getFechaFin());
            pstmt.setString(5, salaPrestada.getObservaciones());

            if (pstmt.executeUpdate() > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        salaPrestada.setIdPrestamoS(generatedKeys.getInt(1));
                    }
                }
                System.out.println("✅ Sala prestada registrada correctamente.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al guardar sala prestada: " + e.getMessage());
        }
    }

    public ArrayList<SalaPrestada> fetch() {
        ArrayList<SalaPrestada> salasPrestadas = new ArrayList<>();
        String query = "SELECT id_prestamo_s, id_solicitud_s, id_sala, fecha_inicio, fecha_fin, observaciones FROM TBL_SALA_PRESTADA";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                salasPrestadas.add(new SalaPrestada(
                    rs.getInt("id_prestamo_s"),
                    rs.getInt("id_solicitud_s"),
                    rs.getInt("id_sala"),
                    rs.getDate("fecha_inicio"),
                    rs.getDate("fecha_fin"),
                    rs.getString("observaciones")
                ));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener salas prestadas: " + e.getMessage());
        }

        return salasPrestadas;
    }

    public void update(SalaPrestada salaPrestada) {
        String query = "UPDATE TBL_SALA_PRESTADA SET id_solicitud_s=?, id_sala=?, fecha_inicio=?, fecha_fin=?, observaciones=? WHERE id_prestamo_s=?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, salaPrestada.getIdSolicitudS());
            pstmt.setObject(2, salaPrestada.getIdSala() > 0 ? salaPrestada.getIdSala() : null, Types.INTEGER);
            pstmt.setDate(3, salaPrestada.getFechaInicio());
            pstmt.setDate(4, salaPrestada.getFechaFin());
            pstmt.setString(5, salaPrestada.getObservaciones());
            pstmt.setInt(6, salaPrestada.getIdPrestamoS());

            if (pstmt.executeUpdate() > 0) {
                System.out.println("✅ Sala prestada actualizada correctamente.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar sala prestada: " + e.getMessage());
        }
    }

    public void delete(Integer id) {
        String query = "DELETE FROM TBL_SALA_PRESTADA WHERE id_prestamo_s=?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);

            if (pstmt.executeUpdate() > 0) {
                System.out.println("✅ Sala prestada eliminada correctamente.");
            } else {
                System.out.println("⚠️ No se encontró una sala prestada con ID: " + id);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar sala prestada: " + e.getMessage());
        }
    }
}
