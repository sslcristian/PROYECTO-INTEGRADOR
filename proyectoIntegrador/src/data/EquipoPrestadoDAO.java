package data;

import model.EquipoPrestado;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipoPrestadoDAO implements CRUD_Operation<EquipoPrestado, Integer> {
    private Connection connection;

    // Constructor
    public EquipoPrestadoDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(EquipoPrestado equipoPrestado) {
        // Usando el procedimiento almacenado
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
        String query = "SELECT id_prestamo_e, id_solicitud_e, id_equipo, fecha_inicio, fecha_fin, observaciones FROM proyecto343.TBL_EQUIPO_PRESTADO";

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
        // Usando el procedimiento almacenado
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
        // Usando el procedimiento almacenado
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
        // Usando la función almacenada
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
        String query = "SELECT * FROM proyecto343.TBL_EQUIPO_PRESTADO ORDER BY fecha_inicio DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

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

        return historial;}
    public List<EquipoPrestado> obtenerHistorialEquiposPorFecha(Timestamp desde, Timestamp hasta) throws SQLException {
        List<EquipoPrestado> historialFiltrado = new ArrayList<>();
        String query = "SELECT * FROM proyecto343.TBL_EQUIPO_PRESTADO WHERE fecha_inicio BETWEEN ? AND ? ORDER BY fecha_inicio DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setTimestamp(1, desde);
            pstmt.setTimestamp(2, hasta);
            ResultSet rs = pstmt.executeQuery();

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

        return historialFiltrado;
    }
    public List<EquipoPrestado> fetchWithNames() {
        List<EquipoPrestado> lista = new ArrayList<>();
        String query = """
            SELECT 
                ep.id_prestamo_e,
                ep.id_solicitud_e,
                ep.id_equipo,
                ep.fecha_inicio,
                ep.fecha_fin,
                ep.observaciones,
                eq.nombre AS nombre_equipo,
                u.cedula AS cedula_usuario,
                u.nombre AS nombre_usuario
            FROM 
                proyecto343.TBL_EQUIPO_PRESTADO ep
            INNER JOIN 
                proyecto343.TBL_EQUIPO eq ON ep.id_equipo = eq.id_equipo
            INNER JOIN 
                proyecto343.TBL_SOLICITUD s ON ep.id_solicitud_e = s.id_solicitud
            INNER JOIN 
                proyecto343.TBL_USUARIO u ON s.cedula_usuario = u.cedula
            """;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
