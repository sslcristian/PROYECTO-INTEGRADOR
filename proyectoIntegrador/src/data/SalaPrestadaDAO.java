package data;

import model.SalaPrestada;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;

public class SalaPrestadaDAO implements CRUD_Operation<SalaPrestada, Integer> {
    private Connection connection;

    public SalaPrestadaDAO(Connection connection) {
        this.connection = connection;
    }

    public void save(SalaPrestada salaPrestada) {
        String query = "INSERT INTO TBL_SALA_PRESTADA " +
                       "(id_prestamo_s, id_solicitud_s, id_sala, fecha_inicio, fecha_fin, observaciones) " +
                       "VALUES (seq_id_prestamo_s.NEXTVAL, NULL, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, salaPrestada.getIdSala());

            pstmt.setTimestamp(2, new Timestamp(salaPrestada.getFechaInicio().getTime()));
            pstmt.setTimestamp(3, new Timestamp(salaPrestada.getFechaFin().getTime()));

            pstmt.setString(4, salaPrestada.getObservaciones());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Sala prestada registrada correctamente.");
            } else {
                System.err.println("No se pudo registrar la sala prestada.");
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar la sala prestada.");
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<SalaPrestada> fetch() {
        ArrayList<SalaPrestada> salasPrestadas = new ArrayList<>();
        String query = "SELECT * FROM TBL_SALA_PRESTADA";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                SalaPrestada salaPrestada = new SalaPrestada(
                    rs.getInt("id_prestamo_s"),
                    rs.getInt("id_solicitud_s"),
                    rs.getInt("id_sala"),
                    rs.getDate("fecha_inicio"),
                    rs.getDate("fecha_fin"),
                    rs.getString("observaciones")
                );
                salasPrestadas.add(salaPrestada);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener las salas prestadas: " + e.getMessage());
            e.printStackTrace();
        }

        return salasPrestadas;
    }

    @Override
    public void update(SalaPrestada salaPrestada) {
        String query = "UPDATE TBL_SALA_PRESTADA SET id_solicitud_s=?, id_sala=?, fecha_inicio=?, fecha_fin=?, observaciones=? WHERE id_prestamo_s=?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, salaPrestada.getIdSolicitudS());
            pstmt.setInt(2, salaPrestada.getIdSala());
            pstmt.setDate(3, salaPrestada.getFechaInicio());
            pstmt.setDate(4, salaPrestada.getFechaFin());
            pstmt.setString(5, salaPrestada.getObservaciones());
            pstmt.setInt(6, salaPrestada.getIdPrestamoS());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Sala prestada actualizada correctamente.");
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar la sala prestada.");
            e.printStackTrace();
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
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar la sala prestada.");
            e.printStackTrace();
        }
    }

    @Override
    public boolean authenticate(Integer id) {
        String query = "SELECT id_prestamo_s FROM TBL_SALA_PRESTADA WHERE id_prestamo_s=?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Error al autenticar la sala prestada.");
            e.printStackTrace();
        }
        return false;
    }

    public List<SalaPrestada> obtenerHistorialSalas() throws SQLException {
        List<SalaPrestada> historial = new ArrayList<>();
        String query = "SELECT * FROM TBL_SALA_PRESTADA ORDER BY fecha_inicio DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                historial.add(new SalaPrestada(
                    rs.getInt("id_prestamo_s"),
                    rs.getInt("id_solicitud_s"),
                    rs.getInt("id_sala"),
                    rs.getDate("fecha_inicio"),
                    rs.getDate("fecha_fin"),
                    rs.getString("observaciones")
                ));
            }
        }
        return historial;
    }

    public SalaPrestada obtenerSalaPrestadaFromResultSet(ResultSet resultSet) throws SQLException {
        int idPrestamo = resultSet.getInt("id_prestamo_s");
        int idSolicitud = resultSet.getInt("id_solicitud_s");
        int idSala = resultSet.getInt("id_sala");
        Date fechaInicio = resultSet.getDate("fecha_inicio");
        Date fechaFin = resultSet.getDate("fecha_fin");
        String observaciones = resultSet.getString("observaciones");

        fechaInicio = (fechaInicio != null) ? fechaInicio : new Date(System.currentTimeMillis());
        fechaFin = (fechaFin != null) ? fechaFin : new Date(System.currentTimeMillis());

        return new SalaPrestada(idPrestamo, idSolicitud, idSala, fechaInicio, fechaFin, observaciones);
    }

    public List<SalaPrestada> obtenerHistorialSalasPorFecha(Date fechaInicio, Date fechaFin) {
        List<SalaPrestada> historialSalas = new ArrayList<>();
        String sql = "SELECT id_prestamo_s, id_solicitud_s, id_sala, fecha_inicio, fecha_fin, observaciones " +
                     "FROM TBL_SALA_PRESTADA WHERE fecha_inicio >= ? AND fecha_fin <= ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, fechaInicio);
            ps.setDate(2, fechaFin);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                SalaPrestada salaPrestada = new SalaPrestada(
                    rs.getInt("id_prestamo_s"),
                    rs.getInt("id_solicitud_s"),
                    rs.getInt("id_sala"),
                    rs.getDate("fecha_inicio"),
                    rs.getDate("fecha_fin"),
                    rs.getString("observaciones")
                );
                historialSalas.add(salaPrestada);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return historialSalas;
    }

    public boolean existeConflictoHorario(int idSala, Date nuevoFechaInicio, Date nuevoFechaFin) {
        String sql = "SELECT COUNT(*) FROM TBL_SALA_PRESTADA "
                   + "WHERE id_sala = ? "
                   + "AND fecha_inicio < ? "  // inicio existente < fin nuevo
                   + "AND fecha_fin > ?";     // fin existente > inicio nuevo

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idSala);
            ps.setTimestamp(2, new Timestamp(nuevoFechaFin.getTime()));
            ps.setTimestamp(3, new Timestamp(nuevoFechaInicio.getTime()));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean existeConflictoEnTablaTemporal(SalaPrestada nueva, ObservableList<SalaPrestada> lista) {
        for (SalaPrestada existente : lista) {
            if (existente.getIdSala() == nueva.getIdSala()) {
                boolean solapa = nueva.getFechaInicio().before(existente.getFechaFin()) &&
                                 nueva.getFechaFin().after(existente.getFechaInicio());
                if (solapa) return true;
            }
        }
        return false;
    }
}
