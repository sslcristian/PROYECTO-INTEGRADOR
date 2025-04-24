package data;

import model.EquipoPrestado;
import java.sql.*;
import java.util.ArrayList;

public class prestamoDAO implements CRUD_Operation<EquipoPrestado, Integer> {
    private Connection connection;

    public prestamoDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(EquipoPrestado equipoPrestado) {
        String query = "INSERT INTO equipo_prestado (id_solicitud_e, id_equipo, fecha_inicio, fecha_fin, observaciones) " +
                       "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, equipoPrestado.getIdSolicitudE());
            pstmt.setInt(2, equipoPrestado.getIdEquipo());
            pstmt.setTimestamp(3, equipoPrestado.getFechaInicio());
            pstmt.setTimestamp(4, equipoPrestado.getFechaFin());
            pstmt.setString(5, equipoPrestado.getObservaciones());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
               
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1); 
                        equipoPrestado.setIdPrestamoE(generatedId);  
                        System.out.println("Equipo prestado registrado correctamente con ID: " + generatedId);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar el equipo prestado.");
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<EquipoPrestado> fetch() {
        ArrayList<EquipoPrestado> equipoPrestados = new ArrayList<>();
        String query = "SELECT id_prestamo_e, id_solicitud_e, id_equipo, fecha_inicio, fecha_fin, observaciones FROM equipo_prestado";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int idPrestamoE = rs.getInt("id_prestamo_e");
                int idSolicitudE = rs.getInt("id_solicitud_e");
                int idEquipo = rs.getInt("id_equipo");
                Timestamp fechaInicio = rs.getTimestamp("fecha_inicio");
                Timestamp fechaFin = rs.getTimestamp("fecha_fin");
                String observaciones = rs.getString("observaciones");

                EquipoPrestado equipoPrestado = new EquipoPrestado(
                        idPrestamoE, idSolicitudE, idEquipo, fechaInicio, fechaFin, observaciones
                );
                equipoPrestados.add(equipoPrestado);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener los equipos prestados.");
            e.printStackTrace();
        }

        return equipoPrestados;
    }

    @Override
    public void update(EquipoPrestado equipoPrestado) {
        String query = "UPDATE equipo_prestado SET id_solicitud_e=?, id_equipo=?, fecha_inicio=?, fecha_fin=?, observaciones=? " +
                       "WHERE id_prestamo_e=?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, equipoPrestado.getIdSolicitudE());
            pstmt.setInt(2, equipoPrestado.getIdEquipo());
            pstmt.setTimestamp(3, equipoPrestado.getFechaInicio());
            pstmt.setTimestamp(4, equipoPrestado.getFechaFin());
            pstmt.setString(5, equipoPrestado.getObservaciones());
            pstmt.setInt(6, equipoPrestado.getIdPrestamoE());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Equipo prestado actualizado correctamente.");
            } else {
                System.out.println("No se encontró un equipo prestado con el ID: " + equipoPrestado.getIdPrestamoE());
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar el equipo prestado.");
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Integer id) {
        String query = "DELETE FROM equipo_prestado WHERE id_prestamo_e=?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Equipo prestado con ID " + id + " eliminado correctamente.");
            } else {
                System.out.println("No se encontró un equipo prestado con el ID: " + id);
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar el equipo prestado.");
            e.printStackTrace();
        }
    }

    @Override
    public boolean authenticate(Integer id) {
        String query = "SELECT id_prestamo_e FROM equipo_prestado WHERE id_prestamo_e=?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Error al autenticar el equipo prestado.");
            e.printStackTrace();
        }
        return false;
    }
}
