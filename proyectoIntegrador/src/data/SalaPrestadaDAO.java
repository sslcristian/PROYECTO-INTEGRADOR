package data;

import model.SalaPrestada;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalaPrestadaDAO implements CRUD_Operation<SalaPrestada, Integer> {
    private Connection connection;

    public SalaPrestadaDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(SalaPrestada salaPrestada) {
        String query = "INSERT INTO TBL_SALA_PRESTADA " +
                       "(id_prestamo_s, id_solicitud_s, id_sala, fecha_inicio, fecha_fin, observaciones) " +
                       "VALUES (seq_id_prestamo_s.NEXTVAL, seq_id_solicitud_s.NEXTVAL, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, salaPrestada.getIdSala());
            pstmt.setDate(2, salaPrestada.getFechaInicio());
            pstmt.setDate(3, salaPrestada.getFechaFin());
            pstmt.setString(4, salaPrestada.getObservaciones());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Sala prestada registrada correctamente.");
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
        // Recuperar los valores del ResultSet
        int idPrestamo = resultSet.getInt("id_prestamo_s");
        int idSolicitud = resultSet.getInt("id_solicitud_s");
        int idSala = resultSet.getInt("id_sala");
        Date fechaInicio = resultSet.getDate("fecha_inicio");
        Date fechaFin = resultSet.getDate("fecha_fin");
        String observaciones = resultSet.getString("observaciones");

        // Verificar si las fechas son null, en cuyo caso asignar la fecha actual o algún valor por defecto
        fechaInicio = (fechaInicio != null) ? fechaInicio : new Date(System.currentTimeMillis());  // Si es null, se usa la fecha actual
        fechaFin = (fechaFin != null) ? fechaFin : new Date(System.currentTimeMillis());  // Lo mismo para la fecha de fin

        // Crear el objeto SalaPrestada con los valores obtenidos
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
                int idPrestamo = rs.getInt("id_prestamo_s");
                int idSolicitud = rs.getInt("id_solicitud_s");
                int idSala = rs.getInt("id_sala");
                Date inicio = rs.getDate("fecha_inicio");
                Date fin = rs.getDate("fecha_fin");
                String observaciones = rs.getString("observaciones");

                SalaPrestada salaPrestada = new SalaPrestada(idPrestamo, idSolicitud, idSala, inicio, fin, observaciones);
                historialSalas.add(salaPrestada);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return historialSalas;
    }

}
