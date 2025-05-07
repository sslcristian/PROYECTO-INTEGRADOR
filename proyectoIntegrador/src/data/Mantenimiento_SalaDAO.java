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
        String query = "INSERT INTO TBL_MANTENIMIENTO_S (id_mantenimiento, id_sala, fecha_mantenimiento, detalle, tecnico_responsable) " +
                       "VALUES (SEQ_MANTENIMIENTO_S.NEXTVAL, ?, ?, ?, ?)";
        String[] returnCols = { "id_mantenimiento" };

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

                // Actualizar estado de la sala a "mantenimiento"
                actualizarEstadoSala(mantenimientoSala.getIdSala(), "mantenimiento");
            }
        } catch (SQLException e) {
            System.err.println("Error al registrar el mantenimiento de la sala.");
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
            System.err.println("Error al obtener los mantenimientos de sala.");
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
            System.err.println("Error al actualizar el mantenimiento de la sala.");
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Integer id) {
        // Primero obtenemos el id de la sala asociada al mantenimiento a eliminar
        String query = "SELECT id_sala FROM TBL_MANTENIMIENTO_S WHERE id_mantenimiento = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int idSala = rs.getInt("id_sala");

                // Eliminamos el mantenimiento
                String deleteQuery = "DELETE FROM TBL_MANTENIMIENTO_S WHERE id_mantenimiento=?";
                try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
                    deleteStmt.setInt(1, id);
                    int rowsAffected = deleteStmt.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("Mantenimiento de sala con ID " + id + " eliminado correctamente.");

                        // Actualizamos el estado de la sala a "disponible"
                        actualizarEstadoSala(idSala, "disponible");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar el mantenimiento de la sala.");
            e.printStackTrace();
        }
    }

    @Override
    public boolean authenticate(Integer id) {
        String query = "SELECT id_mantenimiento FROM TBL_MANTENIMIENTO_S WHERE id_mantenimiento=?" ;

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Error al autenticar el mantenimiento de sala.");
            e.printStackTrace();
        }
        return false;
    }

    // Método para actualizar el estado de la sala
    private void actualizarEstadoSala(int idSala, String nuevoEstado) {
        String query = "UPDATE TBL_SALA_INFORMATICA SET estado = ? WHERE id_sala = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, nuevoEstado);
            pstmt.setInt(2, idSala);

            int filas = pstmt.executeUpdate();
            if (filas > 0) {
                System.out.println("Estado de la sala actualizado correctamente.");
            } else {
                System.out.println("No se encontró sala con el ID: " + idSala);
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar el estado de la sala.");
            e.printStackTrace();
        }
    }

    public ArrayList<Integer> obtenerSalasDisponibles() {
        ArrayList<Integer> salasDisponibles = new ArrayList<>();
        String query = "SELECT id_sala FROM TBL_SALA_INFORMATICA WHERE estado = 'disponible'";  // Consulta a salas disponibles

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                salasDisponibles.add(rs.getInt("id_sala"));  // Obtener los IDs de las salas
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener las salas disponibles.");
            e.printStackTrace();
        }

        return salasDisponibles;
    }
}
