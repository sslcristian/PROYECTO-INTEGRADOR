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
        String query = "INSERT INTO TBL_MANTENIMIENTO_S (id_sala, fecha_mantenimiento, detalle, tecnico_responsable) VALUES (?, ?, ?, ?)";
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
        String query = "SELECT m.*, s.estado FROM TBL_MANTENIMIENTO_S m JOIN TBL_SALA_INFORMATICA s ON m.id_sala = s.id_sala";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

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
        String query = "UPDATE TBL_MANTENIMIENTO_S SET id_sala=?, fecha_mantenimiento=?, detalle=?, tecnico_responsable=? WHERE id_mantenimiento=?";

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
        String getSalaQuery = "SELECT id_sala FROM TBL_MANTENIMIENTO_S WHERE id_mantenimiento=?";
        String deleteQuery = "DELETE FROM TBL_MANTENIMIENTO_S WHERE id_mantenimiento=?";
        String updateSalaQuery = "UPDATE TBL_SALA_INFORMATICA SET estado = 'disponible' WHERE id_sala = ?";

        try (PreparedStatement getSalaStmt = connection.prepareStatement(getSalaQuery)) {
            getSalaStmt.setInt(1, id);
            ResultSet rs = getSalaStmt.executeQuery();
            if (rs.next()) {
                int idSala = rs.getInt("id_sala");

                try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
                    deleteStmt.setInt(1, id);
                    int rowsAffected = deleteStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Mantenimiento de sala eliminado correctamente.");

                        try (PreparedStatement updateStmt = connection.prepareStatement(updateSalaQuery)) {
                            updateStmt.setInt(1, idSala);
                            updateStmt.executeUpdate();
                            System.out.println("Estado de la sala restaurado a 'disponible'.");
                        }
                    }
                }
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

    public ArrayList<Integer> obtenerSalasDisponibles() {
        ArrayList<Integer> salasDisponibles = new ArrayList<>();
        String query = "SELECT id_sala FROM TBL_SALA_INFORMATICA WHERE estado = 'disponible'";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                salasDisponibles.add(rs.getInt("id_sala"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return salasDisponibles;
    }

    public void actualizarEstadoSala(int idSala, String nuevoEstado) {
        String query = "UPDATE TBL_SALA_INFORMATICA SET estado = ? WHERE id_sala = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, nuevoEstado);
            pstmt.setInt(2, idSala);

            int filasAfectadas = pstmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Estado de la sala actualizado a '" + nuevoEstado + "'.");
            } else {
                System.out.println("No se encontró una sala con el ID " + idSala);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
