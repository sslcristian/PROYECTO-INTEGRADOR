package data;

import model.Devolucion;
import java.sql.*;
import java.util.ArrayList;

public class DevolucionDAO implements CRUD_Operation<Devolucion, Integer> {
    private Connection connection;

    public DevolucionDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Devolucion devolucion) {
        String query = "INSERT INTO TBL_DEVOLUCION (id_devolucion, id_solicitud, fecha_devolucion, hora_devolucion, estado_recurso, observaciones) " +
                       "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, devolucion.getIdDevolucion());
            pstmt.setInt(2, devolucion.getIdSolicitud());
            pstmt.setDate(3, devolucion.getFechaDevolucion());
            pstmt.setTime(4, devolucion.getHoraDevolucion());
            pstmt.setString(5, devolucion.getEstadoRecurso());
            pstmt.setString(6, devolucion.getObservaciones());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Devolución registrada correctamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Devolucion> fetch() {
        ArrayList<Devolucion> devoluciones = new ArrayList<>();
        String query = "SELECT * FROM TBL_DEVOLUCION";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int idDevolucion = rs.getInt("id_devolucion");
                int idSolicitud = rs.getInt("id_solicitud");
                Date fechaDevolucion = rs.getDate("fecha_devolucion");
                Time horaDevolucion = rs.getTime("hora_devolucion");
                String estadoRecurso = rs.getString("estado_recurso");
                String observaciones = rs.getString("observaciones");

                Devolucion devolucion = new Devolucion(idDevolucion, idSolicitud, fechaDevolucion, horaDevolucion, estadoRecurso, observaciones);
                devoluciones.add(devolucion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return devoluciones;
    }

    @Override
    public void update(Devolucion devolucion) {
        String sql = "UPDATE TBL_DEVOLUCION SET fecha_devolucion=?, hora_devolucion=?, estado_recurso=?, observaciones=? WHERE id_devolucion=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, devolucion.getFechaDevolucion());
            stmt.setTime(2, devolucion.getHoraDevolucion());
            stmt.setString(3, devolucion.getEstadoRecurso());
            stmt.setString(4, devolucion.getObservaciones());
            stmt.setInt(5, devolucion.getIdDevolucion());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM TBL_DEVOLUCION WHERE id_devolucion=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Devolución con ID " + id + " eliminada correctamente.");
            } else {
                System.out.println("No se encontró una devolución con el ID: " + id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean authenticate(Integer id) {
        String sql = "SELECT id_devolucion FROM TBL_DEVOLUCION WHERE id_devolucion=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
