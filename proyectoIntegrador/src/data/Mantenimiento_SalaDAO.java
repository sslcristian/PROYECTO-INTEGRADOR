package data;

import model.Mantenimiento_Sala;

import java.sql.*;
import java.util.ArrayList;

public class Mantenimiento_SalaDAO implements CRUD_Operation<Mantenimiento_Sala, Integer> {
    private Connection connection;

    public Mantenimiento_SalaDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Mantenimiento_Sala mantenimientoSala) {
        String query = "INSERT INTO TBL_MANTENIMIENTO_S (id_sala, fecha_mantenimiento, detalle, tecnico_responsable) " +
                       "VALUES (?, ?, ?, ?)";
        String[] returnCols = { "id_mantenimiento" };

        String updateSalaEstado = "UPDATE TBL_SALA_INFORMATICA SET estado = 'mantenimiento' WHERE id_sala = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query, returnCols)) {
            pstmt.setInt(1, mantenimientoSala.getIdSala());
            pstmt.setDate(2, mantenimientoSala.getFechaMantenimiento());
            pstmt.setString(3, mantenimientoSala.getDetalle());
            pstmt.setString(4, mantenimientoSala.getTecnicoResponsable());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        mantenimientoSala.setIdMantenimiento(generatedKeys.getInt(1));
                    }
                }
                System.out.println("Mantenimiento de sala registrado correctamente.");

                try (PreparedStatement updateStmt = connection.prepareStatement(updateSalaEstado)) {
                    updateStmt.setInt(1, mantenimientoSala.getIdSala());
                    updateStmt.executeUpdate();
                    System.out.println("Estado de la sala actualizado a 'mantenimiento'.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public ArrayList<Mantenimiento_Sala> fetch() {
        ArrayList<Mantenimiento_Sala> mantenimientos = new ArrayList<>();
        String query = "SELECT * FROM TBL_MANTENIMIENTO_S";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Mantenimiento_Sala mantenimiento = new Mantenimiento_Sala(
                        rs.getInt("id_mantenimiento"),
                        rs.getInt("id_sala"),
                        rs.getDate("fecha_mantenimiento"),
                        rs.getString("detalle"),
                        rs.getString("tecnico_responsable")
                );
                mantenimientos.add(mantenimiento);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return mantenimientos;
    }

    @Override
    public void update(Mantenimiento_Sala mantenimientoSala) {
        String query = "UPDATE TBL_MANTENIMIENTO_S SET id_sala=?, fecha_mantenimiento=?, detalle=?, tecnico_responsable=? " +
                       "WHERE id_mantenimiento=?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, mantenimientoSala.getIdSala());
            pstmt.setDate(2, mantenimientoSala.getFechaMantenimiento());
            pstmt.setString(3, mantenimientoSala.getDetalle());
            pstmt.setString(4, mantenimientoSala.getTecnicoResponsable());
            pstmt.setInt(5, mantenimientoSala.getIdMantenimiento());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Mantenimiento de sala actualizado correctamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Integer id) {
        String query = "DELETE FROM TBL_MANTENIMIENTO_S WHERE id_mantenimiento=?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Mantenimiento de sala con ID " + id + " eliminado correctamente.");
            } else {
                System.out.println("No se encontró mantenimiento con el ID: " + id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean authenticate(Integer id) {
        String query = "SELECT id_mantenimiento FROM TBL_MANTENIMIENTO_S WHERE id_mantenimiento=?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
