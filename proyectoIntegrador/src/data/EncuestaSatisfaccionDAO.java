package data;

import model.EncuestaSatisfaccion;
import java.sql.*;
import java.util.ArrayList;

public class EncuestaSatisfaccionDAO implements CRUD_Operation<EncuestaSatisfaccion, Integer> {
    private Connection connection;

    public EncuestaSatisfaccionDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(EncuestaSatisfaccion encuesta) {
        String query = "INSERT INTO TBL_ENCUESTA_SATISFACCION (id_usuario, id_solicitud, calificacion, comentarios, fecha_respuesta) " +
                       "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, encuesta.getIdUsuario());
            pstmt.setInt(2, encuesta.getIdSolicitud());
            pstmt.setInt(3, encuesta.getCalificacion());
            pstmt.setString(4, encuesta.getComentarios());
            pstmt.setDate(5, encuesta.getFechaRespuesta());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Encuesta de satisfacción registrada correctamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<EncuestaSatisfaccion> fetch() {
        ArrayList<EncuestaSatisfaccion> encuestas = new ArrayList<>();
        String query = "SELECT * FROM TBL_ENCUESTA_SATISFACCION";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                EncuestaSatisfaccion encuesta = new EncuestaSatisfaccion(
                        rs.getInt("id_encuesta"),
                        rs.getLong("id_usuario"),
                        rs.getInt("id_solicitud"),
                        rs.getInt("calificacion"),
                        rs.getString("comentarios"),
                        rs.getDate("fecha_respuesta")
                );
                encuestas.add(encuesta);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return encuestas;
    }

    @Override
    public void update(EncuestaSatisfaccion encuesta) {
        String sql = "UPDATE TBL_ENCUESTA_SATISFACCION SET id_usuario=?, id_solicitud=?, calificacion=?, comentarios=?, fecha_respuesta=? WHERE id_encuesta=?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, encuesta.getIdUsuario());
            stmt.setInt(2, encuesta.getIdSolicitud());
            stmt.setInt(3, encuesta.getCalificacion());
            stmt.setString(4, encuesta.getComentarios());
            stmt.setDate(5, encuesta.getFechaRespuesta());
            stmt.setInt(6, encuesta.getIdEncuesta());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM TBL_ENCUESTA_SATISFACCION WHERE id_encuesta=?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Encuesta con ID " + id + " eliminada correctamente.");
            } else {
                System.out.println("No se encontró una encuesta con el ID: " + id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean authenticate(Integer id) {
        String sql = "SELECT id_encuesta FROM TBL_ENCUESTA_SATISFACCION WHERE id_encuesta=?";

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
