package data;

import model.SalaInformatica;

import java.sql.*;
import java.util.ArrayList;

public class SalaInformaticaDAO implements CRUD_Operation<SalaInformatica, Integer> {

    private final Connection connection;

    public SalaInformaticaDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(SalaInformatica sala) {
        if (exists(sala.getIdSala())) {
            System.err.println("⚠️ No se pudo guardar: ya existe una sala con ID " + sala.getIdSala());
            return;
        }

        String insertQuery = "INSERT INTO proyecto343.TBL_SALA_INFORMATICA " +
                "(id_sala, nombre_sala, capacidad, software_disponible, hardware_especial, ubicacion, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
            pstmt.setInt(1, sala.getIdSala());
            pstmt.setString(2, sala.getNombreSala());
            pstmt.setInt(3, sala.getCapacidad());
            pstmt.setString(4, sala.getSoftwareDisponible());
            pstmt.setString(5, sala.getHardwareEspecial());
            pstmt.setString(6, sala.getUbicacion());
            pstmt.setString(7, sala.getEstado());

            pstmt.executeUpdate();
            System.out.println("✅ Sala insertada correctamente.");
        } catch (SQLException e) {
            System.err.println("❌ Error al insertar sala: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void actualizarEstadoSegunReservas(int idSala) throws SQLException {
        String sql = "SELECT COUNT(*) FROM proyecto343.TBL_SALA_PRESTADA "
                   + "WHERE id_sala = ? "
                   + "AND SYSDATE BETWEEN fecha_inicio AND fecha_fin";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idSala);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    System.out.println("Reservas activas para sala " + idSala + ": " + count);
                    if (count > 0) {
                        actualizarEstadoSala(idSala, "Ocupada");
                    } else {
                        actualizarEstadoSala(idSala, "Disponible");
                    }
                }
            }
        }
    }


    public boolean actualizarEstadoSala(int idSala, String estado) {
        String sql = "UPDATE proyecto343.TBL_SALA_INFORMATICA SET estado = ? WHERE id_sala = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, idSala);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar estado de sala: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ArrayList<SalaInformatica> fetch() {
        ArrayList<SalaInformatica> salas = new ArrayList<>();
        String query = "SELECT * FROM proyecto343.TBL_SALA_INFORMATICA";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                salas.add(new SalaInformatica(
                        rs.getInt("id_sala"),
                        rs.getString("nombre_sala"),
                        rs.getInt("capacidad"),
                        rs.getString("software_disponible"),
                        rs.getString("hardware_especial"),
                        rs.getString("ubicacion"),
                        rs.getString("estado")
                ));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener salas: " + e.getMessage());
            e.printStackTrace();
        }

        return salas;
    }

    @Override
    public void update(SalaInformatica sala) {
        String query = "UPDATE proyecto343.TBL_SALA_INFORMATICA SET nombre_sala=?, capacidad=?, software_disponible=?, hardware_especial=?, ubicacion=?, estado=? WHERE id_sala=?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, sala.getNombreSala());
            pstmt.setInt(2, sala.getCapacidad());
            pstmt.setString(3, sala.getSoftwareDisponible());
            pstmt.setString(4, sala.getHardwareEspecial());
            pstmt.setString(5, sala.getUbicacion());
            pstmt.setString(6, sala.getEstado());
            pstmt.setInt(7, sala.getIdSala());

            int rows = pstmt.executeUpdate();
            System.out.println(rows > 0
                    ? "✅ Sala actualizada correctamente."
                    : "⚠️ No se encontró una sala con ID: " + sala.getIdSala());
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar sala: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Integer idSala) {
        String query = "DELETE FROM proyecto343.TBL_SALA_INFORMATICA WHERE id_sala = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, idSala);
            int rows = pstmt.executeUpdate();
            System.out.println(rows > 0
                    ? "✅ Sala eliminada correctamente."
                    : "⚠️ No se encontró una sala con ID: " + idSala);
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar sala: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean authenticate(Integer idSala) {
        throw new UnsupportedOperationException("El método authenticate no aplica para SalaInformaticaDAO");
    }

    public boolean exists(Integer idSala) {
        String query = "SELECT 1 FROM proyecto343.TBL_SALA_INFORMATICA WHERE id_sala = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, idSala);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Retorna true si hay algún registro
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al verificar existencia de sala: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public SalaInformatica findById(int idSala) {
        String query = "SELECT * FROM proyecto343.TBL_SALA_INFORMATICA WHERE id_sala = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, idSala);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new SalaInformatica(
                        rs.getInt("id_sala"),
                        rs.getString("nombre_sala"),
                        rs.getInt("capacidad"),
                        rs.getString("software_disponible"),
                        rs.getString("hardware_especial"),
                        rs.getString("ubicacion"),
                        rs.getString("estado")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar sala por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    public ArrayList<SalaInformatica> fetchDisponibles() throws SQLException {
        ArrayList<SalaInformatica> salas = new ArrayList<>();
        String query = "SELECT * FROM proyecto343.TBL_SALA_INFORMATICA WHERE estado = 'disponible'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                salas.add(new SalaInformatica(
                        rs.getInt("id_sala"),
                        rs.getString("nombre_sala"),
                        rs.getInt("capacidad"),
                        rs.getString("software_disponible"),
                        rs.getString("hardware_especial"),
                        rs.getString("ubicacion"),
                        rs.getString("estado")
                ));
            }
        }
        return salas;
    }
    public boolean salaDisponible(int idSala, Timestamp inicio, Timestamp fin) {
        String query = "SELECT COUNT(*) FROM proyecto343.TBL_SOLICITUD_PRESTAMO " +
                       "WHERE id_sala = ? " +
                       "AND estado IN ('Pendiente', 'Aprobada') " +
                       "AND (? > fecha_inicio AND ? < fecha_fin)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idSala);
            ps.setTimestamp(2, fin);
            ps.setTimestamp(3, inicio);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0; // Si es 0, la sala está disponible
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Si hay error, no disponible
    }
    public ArrayList<SalaInformatica> fetchAll() throws SQLException {
        ArrayList<SalaInformatica> salas = new ArrayList<>();
        String query = "SELECT * FROM proyecto343.TBL_SALA_INFORMATICA";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                salas.add(new SalaInformatica(
                        rs.getInt("id_sala"),
                        rs.getString("nombre_sala"),
                        rs.getInt("capacidad"),
                        rs.getString("software_disponible"),
                        rs.getString("hardware_especial"),
                        rs.getString("ubicacion"),
                        rs.getString("estado")
                ));
            }
        }
        return salas;
    }
}

