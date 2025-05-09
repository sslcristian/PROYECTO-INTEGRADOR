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
        String query = "INSERT INTO TBL_MANTENIMIENTO_E (id_mantenimiento, id_equipo, fecha_mantenimiento, detalle, tecnico_responsable) " +
                       "VALUES (SEQ_MANTENIMIENTO_E.NEXTVAL, ?, ?, ?, ?)";

        String[] returnCols = { "id_mantenimiento" };

        try (PreparedStatement pstmt = connection.prepareStatement(query, returnCols)) {
            pstmt.setInt(1, mantenimientoEquipo.getIdEquipo());
            pstmt.setDate(2, mantenimientoEquipo.getFechaMantenimiento());
            pstmt.setString(3, mantenimientoEquipo.getDetalle());
            pstmt.setString(4, mantenimientoEquipo.getTecnicoResponsable());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        mantenimientoEquipo.setIdMantenimiento(generatedKeys.getInt(1));
                    }
                }
                System.out.println("Mantenimiento de equipo registrado correctamente.");
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
 // Método para actualizar el estado de un equipo
    public void actualizarEstadoEquipo(int idEquipo, String estado) {
        String sql = "UPDATE TBL_EQUIPO SET estado = ? WHERE id_equipo = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, estado);  // Establecer el nuevo estado del equipo
            pstmt.setInt(2, idEquipo);   // Establecer el ID del equipo a actualizar
            pstmt.executeUpdate();       // Ejecutar la actualización en la base de datos
        } catch (SQLException e) {
            e.printStackTrace();         // Manejo de la excepción si ocurre un error
        }
    }

    // Método para obtener los equipos disponibles
    public ArrayList<Integer> obtenerEquiposDisponibles() {
        ArrayList<Integer> equiposDisponibles = new ArrayList<>();
        String query = "SELECT id_equipo FROM TBL_EQUIPO WHERE estado = 'disponible'";  // Consulta a equipos disponibles

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                equiposDisponibles.add(rs.getInt("id_equipo"));  // Obtener los IDs de los equipos disponibles
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener los equipos disponibles.");
            e.printStackTrace();
        }

        return equiposDisponibles;
    }
    public boolean estaEnMantenimiento(int idEquipo) {
        String query = "SELECT estado FROM TBL_EQUIPO WHERE id_equipo = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idEquipo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String estado = rs.getString("estado");
                return "mantenimiento".equals(estado); // Si el estado es "mantenimiento", no se puede asignar a un nuevo mantenimiento
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }






}
