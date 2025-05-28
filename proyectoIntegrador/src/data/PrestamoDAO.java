package data;

import model.SolicitudPrestamo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
}