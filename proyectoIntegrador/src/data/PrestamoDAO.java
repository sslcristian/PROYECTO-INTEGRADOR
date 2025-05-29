package data;

import model.SolicitudPrestamo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.SolicitudInfo;
public class PrestamoDAO {
    private final Connection connection;

    public PrestamoDAO(Connection connection) {
        this.connection = connection;
    }

    // Guarda una nueva solicitud usando la secuencia SEQ_TBL_SOLICITUD para el id
    public void save(SolicitudPrestamo solicitud) throws SQLException {
        String sql = "INSERT INTO proyecto343.TBL_SOLICITUD (" +
                "id_solicitud, cedula_usuario, detalle_recurso, fecha_inicio, fecha_fin, estado, id_sala, id_equipo" +
                ") VALUES (proyecto343.SEQ_TBL_SOLICITUD.NEXTVAL, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, solicitud.getCedulaUsuario());
            ps.setString(2, solicitud.getDetalleRecurso());
            ps.setTimestamp(3, solicitud.getFechaInicio());
            ps.setTimestamp(4, solicitud.getFechaFin());
            ps.setString(5, solicitud.getEstado());
            if (solicitud.getIdSala() != null) {
                ps.setInt(6, solicitud.getIdSala());
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            if (solicitud.getIdEquipo() != null) {
                ps.setInt(7, solicitud.getIdEquipo());
            } else {
                ps.setNull(7, Types.INTEGER);
            }
            ps.executeUpdate();
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

    // Actualiza una solicitud existente
    public void update(SolicitudPrestamo solicitud) throws SQLException {
        String sql = "UPDATE proyecto343.TBL_SOLICITUD SET cedula_usuario=?, detalle_recurso=?, fecha_inicio=?, fecha_fin=?, estado=?, id_sala=?, id_equipo=? " +
                     "WHERE id_solicitud=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, solicitud.getCedulaUsuario());
            ps.setString(2, solicitud.getDetalleRecurso());
            ps.setTimestamp(3, solicitud.getFechaInicio());
            ps.setTimestamp(4, solicitud.getFechaFin());
            ps.setString(5, solicitud.getEstado());
            if (solicitud.getIdSala() != null) {
                ps.setInt(6, solicitud.getIdSala());
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            if (solicitud.getIdEquipo() != null) {
                ps.setInt(7, solicitud.getIdEquipo());
            } else {
                ps.setNull(7, Types.INTEGER);
            }
            ps.setInt(8, solicitud.getIdSolicitud());
            ps.executeUpdate();
        }
    }

    // Elimina una solicitud por id
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM proyecto343.TBL_SOLICITUD WHERE id_solicitud=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
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

    // Cambia el estado de una solicitud a "Aceptada".
    public void aceptarSolicitud(int idSolicitud) throws SQLException {
        String sql = "UPDATE proyecto343.TBL_SOLICITUD SET estado = 'Aceptada' WHERE id_solicitud = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idSolicitud);
            ps.executeUpdate();
        }
    }

    // Cambia el estado de una solicitud a "Rechazada".
    public void rechazarSolicitud(int idSolicitud) throws SQLException {
        String sql = "UPDATE proyecto343.TBL_SOLICITUD SET estado = 'Rechazada' WHERE id_solicitud = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idSolicitud);
            ps.executeUpdate();
        }
    }

    // Marca una solicitud como expirada
    public void marcarSolicitudComoExpirada(int idSolicitud) throws SQLException {
        String sql = "UPDATE proyecto343.TBL_SOLICITUD SET estado = 'Expirada' WHERE id_solicitud = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idSolicitud);
            stmt.executeUpdate();
        }
    }
    public boolean equipoDisponible(int idEquipo, Timestamp inicio, Timestamp fin) {
        String query = "SELECT COUNT(*) FROM proyecto343.TBL_SOLICITUD " +
                       "WHERE id_equipo = ? " +
                       "AND estado IN ('Pendiente', 'Aprobada') " +
                       "AND (? > fecha_inicio AND ? < fecha_fin)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idEquipo);
            ps.setTimestamp(2, fin);
            ps.setTimestamp(3, inicio);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0; // Si es 0, el equipo está disponible
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Si hay error, no disponible
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

    /**
     * Marca la solicitud como 'Finalizada' (aceptada por el usuario o docente).
     * Ya no se volverá a mostrar ni aparecerá como pendiente.
     */
    public boolean aceptarSolicitud(long idSolicitud) {
        String sql = "UPDATE proyecto343.TBL_SOLICITUD SET estado = 'Finalizada' WHERE id_solicitud = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, idSolicitud);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Elimina la solicitud de la base de datos si el usuario o docente la rechaza.
     */
    public boolean cancelarSolicitud(long idSolicitud) {
        String sql = "DELETE FROM proyecto343.TBL_SOLICITUD WHERE id_solicitud = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, idSolicitud);
            return stmt.executeUpdate() > 0;
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
