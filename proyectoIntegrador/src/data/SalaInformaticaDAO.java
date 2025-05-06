package data;

import java.sql.*;
import java.util.ArrayList;
import model.SalaInformatica;

public class SalaInformaticaDAO implements CRUD_Operation<SalaInformatica, Integer> {

    private final Connection connection;

    public SalaInformaticaDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(SalaInformatica sala) {
        String insertQuery = "INSERT INTO TBL_SALA_INFORMATICA (id_sala, nombre_sala, capacidad, software_disponible, hardware_especial, ubicacion, estado) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
            System.out.println("Intentando insertar sala: ID=" + sala.getIdSala() +
                               ", Nombre=" + sala.getNombreSala() +
                               ", Capacidad=" + sala.getCapacidad() +
                               ", Software=" + sala.getSoftwareDisponible() +
                               ", Hardware=" + sala.getHardwareEspecial() +
                               ", Ubicación=" + sala.getUbicacion() +
                               ", Estado=" + sala.getEstado()); // Depuración antes de insertar

            pstmt.setInt(1, sala.getIdSala());
            pstmt.setString(2, sala.getNombreSala());
            pstmt.setInt(3, sala.getCapacidad());
            pstmt.setString(4, sala.getSoftwareDisponible());
            pstmt.setString(5, sala.getHardwareEspecial());
            pstmt.setString(6, sala.getUbicacion());
            pstmt.setString(7, sala.getEstado());

            pstmt.executeUpdate();
            System.out.println("✅ Sala insertada correctamente.");
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("❌ Error: Violación de restricción al insertar sala. Verifica que la ID no esté duplicada.");
        } catch (SQLException e) {
            System.err.println("❌ Error al insertar sala: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<SalaInformatica> fetch() {
        ArrayList<SalaInformatica> salas = new ArrayList<>();
        String query = "SELECT * FROM TBL_SALA_INFORMATICA";

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
            System.err.println("❌ Error al obtener salas informáticas: " + e.getMessage());
            e.printStackTrace();
        }
        return salas;
    }


    @Override
    public void update(SalaInformatica sala) {
        String query = "UPDATE TBL_SALA_INFORMATICA SET nombre_sala=?, capacidad=?, software_disponible=?, hardware_especial=?, ubicacion=?, estado=? WHERE id_sala=?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, sala.getNombreSala());
            pstmt.setInt(2, sala.getCapacidad());
            pstmt.setString(3, sala.getSoftwareDisponible());
            pstmt.setString(4, sala.getHardwareEspecial());
            pstmt.setString(5, sala.getUbicacion());
            pstmt.setString(6, sala.getEstado());
            pstmt.setInt(7, sala.getIdSala());

            int rows = pstmt.executeUpdate();
            System.out.println(rows > 0 ? "✅ Sala actualizada correctamente." : "⚠️ No se encontró una sala con ID: " + sala.getIdSala());
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar la sala: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Integer idSala) {
        String query = "DELETE FROM TBL_SALA_INFORMATICA WHERE id_sala = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, idSala);
            int rows = pstmt.executeUpdate();
            System.out.println(rows > 0 ? "✅ Sala eliminada correctamente." : "⚠️ No se encontró una sala con ID: " + idSala);
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar la sala: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean authenticate(Integer idSala) {
        String query = "SELECT id_sala FROM TBL_SALA_INFORMATICA WHERE id_sala = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, idSala);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al autenticar la sala: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean exists(Integer idSala) {
        String query = "SELECT 1 FROM TBL_SALA_INFORMATICA WHERE id_sala = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, idSala);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al verificar existencia de la sala: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
