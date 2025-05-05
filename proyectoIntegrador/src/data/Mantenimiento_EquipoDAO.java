package data;

import model.Mantenimiento_Equipo;

import java.sql.*;
import java.util.ArrayList;

public class Mantenimiento_EquipoDAO implements CRUD_Operation<Mantenimiento_Equipo, Integer> {
    private Connection connection;

    public Mantenimiento_EquipoDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Mantenimiento_Equipo mantenimientoEquipo) {
        String query = "INSERT INTO TBL_MANTENIMIENTO_E (id_equipo, fecha_mantenimiento, detalle, tecnico_responsable) " +
                       "VALUES (?, ?, ?, ?)";
        String[] returnCols = { "id_mantenimiento" };

        // Actualización del estado del equipo a 'mantenimiento'
        String updateEquipoEstado = "UPDATE TBL_EQUIPO SET estado = 'mantenimiento' WHERE id_equipo = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query, returnCols)) {
            pstmt.setInt(1, mantenimientoEquipo.getIdEquipo());
            pstmt.setDate(2, mantenimientoEquipo.getFechaMantenimiento());
            pstmt.setString(3, mantenimientoEquipo.getDetalle());
            pstmt.setString(4, mantenimientoEquipo.getTecnicoResponsable());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        mantenimientoEquipo.setIdMantenimiento(generatedKeys.getInt(1)); // Recupera ID asignado por el trigger
                    }
                }
                System.out.println("Mantenimiento de equipo registrado correctamente.");

                try (PreparedStatement updateStmt = connection.prepareStatement(updateEquipoEstado)) {
                    updateStmt.setInt(1, mantenimientoEquipo.getIdEquipo());
                    updateStmt.executeUpdate();
                    System.out.println("Estado del equipo actualizado a 'mantenimiento'.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public ArrayList<Mantenimiento_Equipo> fetch() {
        ArrayList<Mantenimiento_Equipo> mantenimientos = new ArrayList<>();
        String query = "SELECT * FROM TBL_MANTENIMIENTO_E";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Mantenimiento_Equipo mantenimiento = new Mantenimiento_Equipo(
                        rs.getInt("id_mantenimiento"),
                        rs.getInt("id_equipo"),
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
    public void update(Mantenimiento_Equipo mantenimientoEquipo) {
        String query = "UPDATE TBL_MANTENIMIENTO_E SET id_equipo=?, fecha_mantenimiento=?, detalle=?, tecnico_responsable=? " +
                       "WHERE id_mantenimiento=?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, mantenimientoEquipo.getIdEquipo());
            pstmt.setDate(2, mantenimientoEquipo.getFechaMantenimiento());
            pstmt.setString(3, mantenimientoEquipo.getDetalle());
            pstmt.setString(4, mantenimientoEquipo.getTecnicoResponsable());
            pstmt.setInt(5, mantenimientoEquipo.getIdMantenimiento());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Mantenimiento de equipo actualizado correctamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Integer id) {
        String query = "DELETE FROM TBL_MANTENIMIENTO_E WHERE id_mantenimiento=?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Mantenimiento de equipo con ID " + id + " eliminado correctamente.");
            } else {
                System.out.println("No se encontró mantenimiento con el ID: " + id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean authenticate(Integer id) {
        String query = "SELECT id_mantenimiento FROM TBL_MANTENIMIENTO_E WHERE id_mantenimiento=?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    // Método para eliminar el mantenimiento por ID de equipo
    public void deleteByEquipoId(int idEquipo) {
        String sql = "DELETE FROM TBL_MANTENIMIENTO_E WHERE id_equipo = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            // Establecer el ID del equipo que queremos eliminar
            statement.setInt(1, idEquipo);
            
            // Ejecutar la consulta de eliminación
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Mantenimiento(s) del equipo con ID " + idEquipo + " eliminado(s) correctamente.");
            } else {
                System.out.println("No se encontraron mantenimientos para el equipo con ID " + idEquipo + ".");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al eliminar el mantenimiento del equipo: " + e.getMessage());
        }
    }
}

