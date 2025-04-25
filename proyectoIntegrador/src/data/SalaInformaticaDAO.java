package data;

import java.sql.*;
import java.util.ArrayList;
import model.SalaInformatica;

public class SalaInformaticaDAO implements CRUD_Operation<SalaInformatica, Integer> {

    private Connection connection;

    public SalaInformaticaDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(SalaInformatica sala) {
        String getIdQuery = "SELECT SEQ_SALA_ID.NEXTVAL FROM dual";
        String insertQuery = "INSERT INTO TBL_SALA_INFORMATICA (id_sala, nombre_sala, capacidad, software_disponible, hardware_especial, ubicación, estado) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (
            PreparedStatement getIdStmt = connection.prepareStatement(getIdQuery);
            PreparedStatement pstmt = connection.prepareStatement(insertQuery)
        ) {
            ResultSet rs = getIdStmt.executeQuery();
            if (rs.next()) {
                int newId = rs.getInt(1);
                sala.setIdSala(newId);

                pstmt.setInt(1, newId);
                pstmt.setString(2, sala.getNombreSala());
                pstmt.setInt(3, sala.getCapacidad());
                pstmt.setString(4, sala.getSoftwareDisponible());
                pstmt.setString(5, sala.getHardwareEspecial());
                pstmt.setString(6, sala.getUbicacion());
                pstmt.setString(7, sala.getEstado());

                pstmt.executeUpdate();
                System.out.println("✅ Sala insertada correctamente con ID: " + newId);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al insertar sala informática.");
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<SalaInformatica> fetch() {
        ArrayList<SalaInformatica> salas = new ArrayList<>();
        String query = "SELECT * FROM TBL_SALA_INFORMATICA";

        try (
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query)
        ) {
            while (rs.next()) {
                int id = rs.getInt("id_sala");
                String nombre = rs.getString("nombre_sala");
                int capacidad = rs.getInt("capacidad");
                String software = rs.getString("software_disponible");
                String hardware = rs.getString("hardware_especial");
                String ubicacion = rs.getString("ubicación");
                String estado = rs.getString("estado");

                SalaInformatica sala = new SalaInformatica(id, nombre, capacidad, software, hardware, ubicacion, estado);
                salas.add(sala);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener salas informáticas.");
            e.printStackTrace();
        }

        return salas;
    }

    @Override
    public void update(SalaInformatica sala) {
        String query = "UPDATE TBL_SALA_INFORMATICA SET nombre_sala=?, capacidad=?, software_disponible=?, hardware_especial=?, ubicación=?, estado=? WHERE id_sala=?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, sala.getNombreSala());
            pstmt.setInt(2, sala.getCapacidad());
            pstmt.setString(3, sala.getSoftwareDisponible());
            pstmt.setString(4, sala.getHardwareEspecial());
            pstmt.setString(5, sala.getUbicacion());
            pstmt.setString(6, sala.getEstado());
            pstmt.setInt(7, sala.getIdSala());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Sala actualizada correctamente.");
            } else {
                System.out.println("⚠️ No se encontró una sala con ID: " + sala.getIdSala());
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar la sala.");
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Integer idSala) {
        String query = "DELETE FROM TBL_SALA_INFORMATICA WHERE id_sala = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, idSala);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Sala eliminada correctamente.");
            } else {
                System.out.println("⚠️ No se encontró una sala con ID: " + idSala);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar la sala.");
            e.printStackTrace();
        }
    }

    @Override
    public boolean authenticate(Integer idSala) {
        String query = "SELECT id_sala FROM TBL_SALA_INFORMATICA WHERE id_sala = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, idSala);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("❌ Error al autenticar la sala.");
            e.printStackTrace();
        }

        return false;
    }
    public boolean exists(Integer idSala) {
        String query = "SELECT 1 FROM TBL_SALA_INFORMATICA WHERE id_sala = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, idSala);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("❌ Error al verificar existencia de la sala.");
            e.printStackTrace();
        }

        return false;
    }

}
