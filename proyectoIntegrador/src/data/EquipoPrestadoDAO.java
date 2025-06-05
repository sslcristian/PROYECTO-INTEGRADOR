package data;

import model.EquipoPrestado;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipoPrestadoDAO implements CRUD_Operation<EquipoPrestado, Integer> {
    private Connection connection;


    public EquipoPrestadoDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(EquipoPrestado equipoPrestado) {
     
        String call = "{call proyecto343.SP_INSERT_EQUIPO_PRESTADO(?, ?, ?, ?, ?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setInt(1, equipoPrestado.getIdSolicitudE());
            cs.setInt(2, equipoPrestado.getIdEquipo());
            cs.setTimestamp(3, equipoPrestado.getFechaInicio());
            cs.setTimestamp(4, equipoPrestado.getFechaFin());
            cs.setString(5, equipoPrestado.getObservaciones());
            cs.execute();
            System.out.println("Equipo prestado registrado correctamente.");
        } catch (SQLException e) {
            System.err.println("Error al registrar el equipo prestado.");
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<EquipoPrestado> fetch() {
        ArrayList<EquipoPrestado> equipoPrestados = new ArrayList<>();
        String call = "{call proyecto343.sp_fetch_equipo_prestado(?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.registerOutParameter(1, oracle.jdbc.OracleTypes.CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
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
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener los equipos prestados.");
            e.printStackTrace();
        }
        return equipoPrestados;
    }

    @Override
    public void update(EquipoPrestado equipoPrestado) {
     
        String call = "{call proyecto343.SP_UPDATE_EQUIPO_PRESTADO(?, ?, ?, ?, ?, ?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setInt(1, equipoPrestado.getIdPrestamoE());
            cs.setInt(2, equipoPrestado.getIdSolicitudE());
            cs.setInt(3, equipoPrestado.getIdEquipo());
            cs.setTimestamp(4, equipoPrestado.getFechaInicio());
            cs.setTimestamp(5, equipoPrestado.getFechaFin());
            cs.setString(6, equipoPrestado.getObservaciones());
            cs.execute();
            System.out.println("Equipo prestado actualizado correctamente.");
        } catch (SQLException e) {
            System.err.println("Error al actualizar el equipo prestado.");
            e.printStackTrace();
        }
    }
    @Override
    public void delete(Integer id) {
       
        String call = "{call proyecto343.SP_DELETE_EQUIPO_PRESTADO(?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setInt(1, id);
            cs.execute();
            System.out.println("Equipo prestado con ID " + id + " eliminado correctamente.");
        } catch (SQLException e) {
            System.err.println("Error al eliminar el equipo prestado.");
            e.printStackTrace();
        }
    }


    @Override
    public boolean authenticate(Integer id) {
     
        String call = "{? = call proyecto343.FN_EQUIPO_PRESTADO_EXISTS(?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setInt(2, id);
            cs.execute();
            return cs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("Error al autenticar el equipo prestado.");
            e.printStackTrace();
        }
        return false;
    }

    public List<EquipoPrestado> obtenerHistorialEquipos() throws SQLException {
        List<EquipoPrestado> historial = new ArrayList<>();
        String call = "{call proyecto343.sp_historial_equipo_prestado(?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.registerOutParameter(1, oracle.jdbc.OracleTypes.CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    historial.add(new EquipoPrestado(
                        rs.getInt("id_prestamo_e"),
                        rs.getInt("id_solicitud_e"),
                        rs.getInt("id_equipo"),
                        rs.getTimestamp("fecha_inicio"),
                        rs.getTimestamp("fecha_fin"),
                        rs.getString("observaciones")
                    ));
                }
            }
        }
        return historial;
    }
    public List<EquipoPrestado> obtenerHistorialEquiposPorFecha(Timestamp desde, Timestamp hasta) throws SQLException {
        List<EquipoPrestado> historialFiltrado = new ArrayList<>();
        String call = "{call proyecto343.sp_historiale_por_fecha(?, ?, ?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setTimestamp(1, desde);
            cs.setTimestamp(2, hasta);
            cs.registerOutParameter(3, oracle.jdbc.OracleTypes.CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(3)) {
                while (rs.next()) {
                    historialFiltrado.add(new EquipoPrestado(
                        rs.getInt("id_prestamo_e"),
                        rs.getInt("id_solicitud_e"),
                        rs.getInt("id_equipo"),
                        rs.getTimestamp("fecha_inicio"),
                        rs.getTimestamp("fecha_fin"),
                        rs.getString("observaciones")
                    ));
                }
            }
        }
        return historialFiltrado;
    }
    public List<EquipoPrestado> fetchWithNames() {
        List<EquipoPrestado> lista = new ArrayList<>();
        String call = "{call proyecto343.sp_fetch_equipo_prestado_names(?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.registerOutParameter(1, oracle.jdbc.OracleTypes.CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    EquipoPrestado equipo = new EquipoPrestado(
                        rs.getInt("id_prestamo_e"),
                        rs.getInt("id_solicitud_e"),
                        rs.getInt("id_equipo"),
                        rs.getTimestamp("fecha_inicio"),
                        rs.getTimestamp("fecha_fin"),
                        rs.getString("observaciones"),
                        rs.getString("nombre_equipo"),
                        rs.getString("cedula_usuario"),
                        rs.getString("nombre_usuario")
                    );
                    lista.add(equipo);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
