package data;

import model.Devolucion;

import java.sql.*;
import java.util.ArrayList;

public class DevolucionDAO implements CRUD_Operation<Devolucion, Integer> {
    private Connection connection;

    public DevolucionDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Guarda una devolución en la base de datos usando la secuencia para id_devolucion.
     */
    @Override
    public void save(Devolucion devolucion) {
        int nextId = 0;
        // Paso 1: Obtener el siguiente valor de la secuencia
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT proyecto343.seq_id_devolucion.NEXTVAL FROM dual")) {
            if (rs.next()) {
                nextId = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        // Paso 2: Insertar el registro usando ese id
        String query = "INSERT INTO proyecto343.tbl_devolucion " +
                "(id_devolucion, id_solicitud, fecha_devolucion, hora_devolucion, estado_recurso, observaciones) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, nextId);
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

    /**
     * Lista todas las devoluciones de la base de datos.
     */
    @Override
    public ArrayList<Devolucion> fetch() {
        ArrayList<Devolucion> devoluciones = new ArrayList<>();
        String query = "SELECT * FROM proyecto343.tbl_devolucion";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int idDevolucion = rs.getInt("id_devolucion");
                int idSolicitud = rs.getInt("id_solicitud");
                Date fechaDevolucion = rs.getDate("fecha_devolucion");
                Time horaDevolucion = rs.getTime("hora_devolucion");
                String estadoRecurso = rs.getString("estado_recurso");
                String observaciones = rs.getString("observaciones");

                Devolucion devolucion = new Devolucion(
                        idDevolucion, idSolicitud, fechaDevolucion, horaDevolucion, estadoRecurso, observaciones
                );
                devoluciones.add(devolucion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return devoluciones;
    }

    /**
     * Actualiza una devolución existente.
     */
    @Override
    public void update(Devolucion devolucion) {
        String sql = "UPDATE proyecto343.tbl_devolucion " +
                "SET fecha_devolucion=?, hora_devolucion=?, estado_recurso=?, observaciones=? " +
                "WHERE id_devolucion=?";
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

    /**
     * Elimina una devolución por su ID.
     */
    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM proyecto343.tbl_devolucion WHERE id_devolucion=?";
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

    /**
     * Verifica si existe una devolución con el ID dado.
     */
    @Override
    public boolean authenticate(Integer id) {
        String sql = "SELECT id_devolucion FROM proyecto343.tbl_devolucion WHERE id_devolucion=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Método adicional para listar devoluciones (útil para JavaFX TableView o conveniencia).
     */
    public ArrayList<Devolucion> listarDevoluciones() {
        return fetch();
    }

    /**
     * Método adicional para registrar devolución (más expresivo para tu controlador).
     */
    public void registrarDevolucion(Devolucion devolucion) {
        save(devolucion);
    }
}