package data;

import model.EquipoAudiovisual;
import java.sql.*;
import java.util.ArrayList;

public class EquipoAudiovisualDAO implements CRUD_Operation<EquipoAudiovisual, Integer> {
    private Connection connection;

    public EquipoAudiovisualDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(EquipoAudiovisual equipo) {
        String query = "INSERT INTO TBL_EQUIPO (nombre, tipo, estado, ubicación, marca, modelo, fecha_adquisición) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, equipo.getNombre());
            pstmt.setString(2, equipo.getTipo());
            pstmt.setString(3, equipo.getEstado());
            pstmt.setString(4, equipo.getUbicacion());
            pstmt.setString(5, equipo.getMarca());
            pstmt.setString(6, equipo.getModelo());
            pstmt.setDate(7, equipo.getFechaAdquisicion());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Equipo audiovisual registrado correctamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<EquipoAudiovisual> fetch() {
        ArrayList<EquipoAudiovisual> equipos = new ArrayList<>();
        String query = "SELECT * FROM TBL_EQUIPO";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                EquipoAudiovisual equipo = new EquipoAudiovisual(
                        rs.getInt("id_equipo"),
                        rs.getString("nombre"),
                        rs.getString("tipo"),
                        rs.getString("estado"),
                        rs.getString("ubicación"),
                        rs.getString("marca"),
                        rs.getString("modelo"),
                        rs.getDate("fecha_adquisición")
                );
                equipos.add(equipo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return equipos;
    }

    @Override
    public void update(EquipoAudiovisual equipo) {
        String sql = "UPDATE TBL_EQUIPO SET nombre=?, tipo=?, estado=?, ubicación=?, marca=?, modelo=?, fecha_adquisición=? WHERE id_equipo=?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, equipo.getNombre());
            stmt.setString(2, equipo.getTipo());
            stmt.setString(3, equipo.getEstado());
            stmt.setString(4, equipo.getUbicacion());
            stmt.setString(5, equipo.getMarca());
            stmt.setString(6, equipo.getModelo());
            stmt.setDate(7, equipo.getFechaAdquisicion());
            stmt.setInt(8, equipo.getIdEquipo());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM TBL_EQUIPO WHERE id_equipo=?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Equipo con ID " + id + " eliminado correctamente.");
            } else {
                System.out.println("No se encontró un equipo con el ID: " + id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean authenticate(Integer id) {
        String sql = "SELECT id_equipo FROM TBL_EQUIPO WHERE id_equipo=?";

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
