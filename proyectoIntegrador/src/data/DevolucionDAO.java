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
        // Usar el procedimiento almacenado para insertar devolución
        String call = "{call proyecto343.sp_insert_devolucion(?,?,?,?,?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setInt(1, devolucion.getIdSolicitud());
            cs.setDate(2, devolucion.getFechaDevolucion());
            cs.setString(3, devolucion.getHoraDevolucion().toString()); // O ajusta formato si necesario
            cs.setString(4, devolucion.getEstadoRecurso());
            cs.setString(5, devolucion.getObservaciones());
            cs.execute();
            System.out.println("Devolución registrada correctamente (procedimiento almacenado).");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
                        idDevolucion, idSolicitud, fechaDevolucion, horaDevolucion, estadoRecurso, observaciones);
                devoluciones.add(devolucion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return devoluciones;
    }

    @Override
    public void update(Devolucion devolucion) {
        // Usar procedimiento almacenado para actualizar devolución
        String call = "{call proyecto343.sp_update_devolucion(?,?,?,?,?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setInt(1, devolucion.getIdDevolucion());
            cs.setDate(2, devolucion.getFechaDevolucion());
            cs.setString(3, devolucion.getHoraDevolucion().toString()); // O ajusta formato si necesario
            cs.setString(4, devolucion.getEstadoRecurso());
            cs.setString(5, devolucion.getObservaciones());
            cs.execute();
            System.out.println("Devolución actualizada correctamente (procedimiento almacenado).");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Integer id) {
        // Usar procedimiento almacenado para eliminar devolución
        String call = "{call proyecto343.sp_delete_devolucion(?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setInt(1, id);
            cs.execute();
            System.out.println("Devolución eliminada correctamente (procedimiento almacenado).");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean authenticate(Integer id) {
        // Usar función almacenada para verificar existencia
        String call = "{? = call proyecto343.fn_devolucion_exists(?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.registerOutParameter(1, java.sql.Types.INTEGER);
            cs.setInt(2, id);
            cs.execute();
            return cs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Función adicional: verificar existencia por id_solicitud
    public boolean existePorSolicitud(int idSolicitud) {
        String call = "{? = call proyecto343.fn_dev_sol_exists(?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.registerOutParameter(1, java.sql.Types.INTEGER);
            cs.setInt(2, idSolicitud);
            cs.execute();
            return cs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Función adicional: obtener estado del recurso por id_devolucion
    public String obtenerEstadoPorId(int idDevolucion) {
        String call = "{? = call proyecto343.fn_dev_estado_by_id(?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.registerOutParameter(1, java.sql.Types.VARCHAR);
            cs.setInt(2, idDevolucion);
            cs.execute();
            return cs.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}