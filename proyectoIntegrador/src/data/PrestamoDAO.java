package data;

import model.SolicitudPrestamo;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
                "id_solicitud, cedula_usuario, fecha_solicitud, tipo_recurso, detalle_recurso, " +
                "fecha_uso, hora_inicio, hora_fin, estado" +
                ") VALUES (proyecto343.SEQ_TBL_SOLICITUD.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, solicitud.getCedulaUsuario());
            ps.setDate(2, solicitud.getFechaSolicitud());
            ps.setString(3, solicitud.getTipoRecurso());
            ps.setString(4, solicitud.getDetalleRecurso());
            ps.setDate(5, solicitud.getFechaUso());
            ps.setTimestamp(6, Timestamp.valueOf(solicitud.getHoraInicio()));
            ps.setTimestamp(7, Timestamp.valueOf(solicitud.getHoraFin()));
            ps.setString(8, solicitud.getEstado());
            ps.executeUpdate();
        }
    }

    // Lista todas las solicitudes
    public List<SolicitudPrestamo> fetchAll() throws SQLException {
        List<SolicitudPrestamo> solicitudes = new ArrayList<>();
        String sql = "SELECT id_solicitud, cedula_usuario, fecha_solicitud, tipo_recurso, detalle_recurso, fecha_uso, hora_inicio, hora_fin, estado FROM proyecto343.TBL_SOLICITUD";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                SolicitudPrestamo solicitud = new SolicitudPrestamo(
                        rs.getInt("id_solicitud"),
                        rs.getLong("cedula_usuario"),
                        rs.getDate("fecha_solicitud"),
                        rs.getString("tipo_recurso"),
                        rs.getString("detalle_recurso"),
                        rs.getDate("fecha_uso"),
                        toLocalDateTime(rs.getTimestamp("hora_inicio")),
                        toLocalDateTime(rs.getTimestamp("hora_fin")),
                        rs.getString("estado")
                );
                solicitudes.add(solicitud);
            }
        }
        return solicitudes;
    }

    // Busca una solicitud por id
    public SolicitudPrestamo fetchById(int id) throws SQLException {
        String sql = "SELECT id_solicitud, cedula_usuario, fecha_solicitud, tipo_recurso, detalle_recurso, fecha_uso, hora_inicio, hora_fin, estado " +
                     "FROM proyecto343.TBL_SOLICITUD WHERE id_solicitud = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new SolicitudPrestamo(
                        rs.getInt("id_solicitud"),
                        rs.getLong("cedula_usuario"),
                        rs.getDate("fecha_solicitud"),
                        rs.getString("tipo_recurso"),
                        rs.getString("detalle_recurso"),
                        rs.getDate("fecha_uso"),
                        toLocalDateTime(rs.getTimestamp("hora_inicio")),
                        toLocalDateTime(rs.getTimestamp("hora_fin")),
                        rs.getString("estado")
                    );
                }
            }
        }
        return null;
    }

    // Actualiza una solicitud existente
    public void update(SolicitudPrestamo solicitud) throws SQLException {
        String sql = "UPDATE proyecto343.TBL_SOLICITUD SET cedula_usuario=?, fecha_solicitud=?, tipo_recurso=?, detalle_recurso=?, fecha_uso=?, hora_inicio=?, hora_fin=?, estado=? " +
                     "WHERE id_solicitud=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, solicitud.getCedulaUsuario());
            ps.setDate(2, solicitud.getFechaSolicitud());
            ps.setString(3, solicitud.getTipoRecurso());
            ps.setString(4, solicitud.getDetalleRecurso());
            ps.setDate(5, solicitud.getFechaUso());
            ps.setTimestamp(6, Timestamp.valueOf(solicitud.getHoraInicio()));
            ps.setTimestamp(7, Timestamp.valueOf(solicitud.getHoraFin()));
            ps.setString(8, solicitud.getEstado());
            ps.setInt(9, solicitud.getIdSolicitud());
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
    // Utilidad para convertir Timestamp a LocalDateTime de forma segura
    private static LocalDateTime toLocalDateTime(Timestamp ts) {
        if (ts == null) return null;
        return ts.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
    
}