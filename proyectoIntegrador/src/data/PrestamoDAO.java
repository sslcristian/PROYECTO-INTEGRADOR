package data;

import model.SolicitudPrestamo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import oracle.jdbc.OracleTypes; 
import model.SolicitudInfo;
public class PrestamoDAO {
    private final Connection connection;

    public PrestamoDAO(Connection connection) {
        this.connection = connection;
    }

    public void save(SolicitudPrestamo solicitud) throws SQLException {
        String call = "{call proyecto343.SP_INSERT_SOLICITUD(?, ?, ?, ?, ?, ?, ?, ?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setLong(1, solicitud.getCedulaUsuario());
            cs.setString(2, solicitud.getDetalleRecurso());
            cs.setTimestamp(3, solicitud.getFechaInicio());
            cs.setTimestamp(4, solicitud.getFechaFin());
            cs.setString(5, solicitud.getEstado());
            if (solicitud.getIdSala() != null) cs.setInt(6, solicitud.getIdSala());
            else cs.setNull(6, Types.INTEGER);
            if (solicitud.getIdEquipo() != null) cs.setInt(7, solicitud.getIdEquipo());
            else cs.setNull(7, Types.INTEGER);
            cs.registerOutParameter(8, Types.INTEGER);
            cs.execute();
            solicitud.setIdSolicitud(cs.getInt(8)); // Recupera el id generado
        }
    }

    // Lista todas las solicitudes
    public List<SolicitudPrestamo> fetchAll() throws SQLException {
        List<SolicitudPrestamo> solicitudes = new ArrayList<>();
        String sql = "SELECT id_solicitud, cedula_usuario, detalle_recurso, fecha_inicio, fecha_fin, estado, id_sala, id_equipo " +
                     "FROM proyecto343.TBL_SOLICITUD";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                SolicitudPrestamo solicitud = new SolicitudPrestamo(
                        rs.getInt("id_solicitud"),
                        rs.getLong("cedula_usuario"),
                        rs.getString("detalle_recurso"),
                        rs.getTimestamp("fecha_inicio"),
                        rs.getTimestamp("fecha_fin"),
                        rs.getString("estado"),
                        rs.getObject("id_sala") == null ? null : rs.getInt("id_sala"),
                        rs.getObject("id_equipo") == null ? null : rs.getInt("id_equipo")
                );
                solicitudes.add(solicitud);
            }
        }
        return solicitudes;
    }

    // Busca una solicitud por id
    public SolicitudPrestamo fetchById(int id) throws SQLException {
        String sql = "SELECT id_solicitud, cedula_usuario, detalle_recurso, fecha_inicio, fecha_fin, estado, id_sala, id_equipo " +
                     "FROM proyecto343.TBL_SOLICITUD WHERE id_solicitud = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new SolicitudPrestamo(
                        rs.getInt("id_solicitud"),
                        rs.getLong("cedula_usuario"),
                        rs.getString("detalle_recurso"),
                        rs.getTimestamp("fecha_inicio"),
                        rs.getTimestamp("fecha_fin"),
                        rs.getString("estado"),
                        rs.getObject("id_sala") == null ? null : rs.getInt("id_sala"),
                        rs.getObject("id_equipo") == null ? null : rs.getInt("id_equipo")
                    );
                }
            }
        }
        return null;
    }

    public void update(SolicitudPrestamo solicitud) throws SQLException {
        String call = "{call proyecto343.SP_UPDATE_SOLICITUD(?, ?, ?, ?, ?, ?, ?, ?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setInt(1, solicitud.getIdSolicitud());
            cs.setLong(2, solicitud.getCedulaUsuario());
            cs.setString(3, solicitud.getDetalleRecurso());
            cs.setTimestamp(4, solicitud.getFechaInicio());
            cs.setTimestamp(5, solicitud.getFechaFin());
            cs.setString(6, solicitud.getEstado());
            if (solicitud.getIdSala() != null) cs.setInt(7, solicitud.getIdSala());
            else cs.setNull(7, Types.INTEGER);
            if (solicitud.getIdEquipo() != null) cs.setInt(8, solicitud.getIdEquipo());
            else cs.setNull(8, Types.INTEGER);
            cs.execute();
        }
    }

    public void delete(int id) throws SQLException {
        String call = "{call proyecto343.SP_DELETE_SOLICITUD(?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setInt(1, id);
            cs.execute();
        }
    }

    public ArrayList<model.SolicitudComboDTO> fetchSolicitudesAceptadas() throws SQLException {
        ArrayList<model.SolicitudComboDTO> lista = new ArrayList<>();
        String sql = "SELECT id_solicitud, cedula_usuario FROM proyecto343.TBL_SOLICITUD WHERE estado = 'Aceptada'";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new model.SolicitudComboDTO(
                    rs.getInt("id_solicitud"),
                    rs.getInt("cedula_usuario")
                ));
            }
        }
        return lista;
    }

    public void aceptarSolicitud(int idSolicitud) throws SQLException {
        String call = "{call proyecto343.SP_ACEPTAR_SOLICITUD(?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setInt(1, idSolicitud);
            cs.execute();
        }
    }

    // Cambia el estado de una solicitud a "Rechazada".
    public void rechazarSolicitud(int idSolicitud) throws SQLException {
        String call = "{call proyecto343.SP_RECHAZAR_SOLICITUD(?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setInt(1, idSolicitud);
            cs.execute();
        }
    }

    // Marca una solicitud como expirada
    public void marcarSolicitudComoExpirada(int idSolicitud) throws SQLException {
        String call = "{call proyecto343.SP_EXPIRAR_SOLICITUD(?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setInt(1, idSolicitud);
            cs.execute();
        }
    }
    public boolean equipoDisponible(int idEquipo, Timestamp inicio, Timestamp fin) {
        String call = "{? = call proyecto343.FN_EQUIPO_DISPONIBLE(?, ?, ?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.registerOutParameter(1, java.sql.Types.INTEGER);
            cs.setInt(2, idEquipo);
            cs.setTimestamp(3, inicio);
            cs.setTimestamp(4, fin);
            cs.execute();
            return cs.getInt(1) == 1; // 1=disponible, 0=no disponible
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    

    public List<SolicitudInfo> obtenerSolicitudesVigentes(long cedulaUsuario) {
        List<SolicitudInfo> solicitudes = new ArrayList<>();
        String sql =
            "SELECT s.id_solicitud, s.fecha_inicio, s.fecha_fin, s.estado, " +
            "sal.nombre_sala, sal.ubicacion as ubicacion_sala, " +
            "eq.nombre as nombre_equipo, eq.tipo as tipo_equipo, eq.ubicacion as ubicacion_equipo " +
            "FROM proyecto343.TBL_SOLICITUD s " +
            "LEFT JOIN proyecto343.TBL_SALA_INFORMATICA sal ON s.id_sala = sal.id_sala " +
            "LEFT JOIN proyecto343.TBL_EQUIPO eq ON s.id_equipo = eq.id_equipo " +
            "WHERE s.cedula_usuario = ? " +
            "AND s.estado = 'Aceptada' " +
            "AND s.fecha_fin >= TRUNC(SYSDATE)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, cedulaUsuario);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                SolicitudInfo info = new SolicitudInfo();
                info.setIdSolicitud(rs.getLong("id_solicitud"));
                info.setFechaInicio(rs.getDate("fecha_inicio"));
                info.setFechaFin(rs.getDate("fecha_fin"));
                info.setEstado(rs.getString("estado"));
                info.setNombreSala(rs.getString("nombre_sala"));
                info.setUbicacionSala(rs.getString("ubicacion_sala"));
                info.setNombreEquipo(rs.getString("nombre_equipo"));
                info.setTipoEquipo(rs.getString("tipo_equipo"));
                info.setUbicacionEquipo(rs.getString("ubicacion_equipo"));
                solicitudes.add(info);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return solicitudes;
    }

 public boolean aceptarSolicitud(long idSolicitud) {
	    String call = "{call proyecto343.SP_ACEPTAR_SOLICITUD(?)}";
	    try (CallableStatement cs = connection.prepareCall(call)) {
	        cs.setLong(1, idSolicitud);
	        cs.execute();
	        return true;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}

    // Elimina la solicitud de la base de datos si el usuario o docente la rechaza.
    public boolean cancelarSolicitud(long idSolicitud) {
        String call = "{call proyecto343.SP_CANCELAR_SOLICITUD(?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setLong(1, idSolicitud);
            cs.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public List<String[]> obtenerDetallesPrestamosVigentes(long cedulaUsuario) {
        List<String[]> prestamos = new ArrayList<>();
        String sql =
            "SELECT " +
            "  CASE WHEN s.id_sala IS NOT NULL THEN sal.nombre_sala ELSE eq.nombre END AS nombre, " +
            "  CASE WHEN s.id_sala IS NOT NULL THEN sal.ubicacion ELSE eq.ubicacion END AS ubicacion, " +
            "  TO_CHAR(s.fecha_inicio, 'YYYY-MM-DD') AS fecha_inicio, " +
            "  TO_CHAR(s.fecha_fin, 'YYYY-MM-DD') AS fecha_fin " +
            "FROM proyecto343.TBL_SOLICITUD s " +
            "LEFT JOIN proyecto343.TBL_SALA_INFORMATICA sal ON s.id_sala = sal.id_sala " +
            "LEFT JOIN proyecto343.TBL_EQUIPO eq ON s.id_equipo = eq.id_equipo " +
            "WHERE s.cedula_usuario = ? " +
            "  AND s.estado = 'Aceptada' " +
            "  AND s.fecha_fin >= TRUNC(SYSDATE)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, cedulaUsuario);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String[] info = new String[4];
                info[0] = rs.getString("nombre");
                info[1] = rs.getString("ubicacion");
                info[2] = rs.getString("fecha_inicio");
                info[3] = rs.getString("fecha_fin");
                prestamos.add(info);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prestamos;
    }
    }
