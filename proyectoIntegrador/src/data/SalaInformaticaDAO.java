package data;


import java.sql.*;
import java.util.ArrayList;

import model.SalaInformatica;

public class SalaInformaticaDAO implements CRUD_Operation<SalaInformatica, Long> {

    private Connection connection;

    public SalaInformaticaDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(SalaInformatica sala) {
        String query = "INSERT INTO SALA_INFORMATICA (id_sala, nombre_sala, capacidad, software_disponible, hardware_especial, ubicacion, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, sala.getIdSala());
            pstmt.setString(2, sala.getNombreSala());
            pstmt.setInt(3, sala.getCapacidad());
            pstmt.setString(4, sala.getSoftwareDisponible());
            pstmt.setString(5, sala.getHardwareEspecial());
            pstmt.setString(6, sala.getUbicacion());
            pstmt.setString(7, sala.getEstado());

            pstmt.executeUpdate();
            System.out.println("Sala insertada correctamente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<SalaInformatica> fetch() {
        ArrayList<SalaInformatica> salas = new ArrayList<>();
        String query = "SELECT * FROM SALA_INFORMATICA";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("id_sala");
                String nombre = rs.getString("nombre_sala");
                int capacidad = rs.getInt("capacidad");
                String software = rs.getString("software_disponible");
                String hardware = rs.getString("hardware_especial");
                String ubicacion = rs.getString("ubicacion");
                String estado = rs.getString("estado");

                SalaInformatica sala = new SalaInformatica(id, nombre, capacidad, software, hardware, ubicacion, estado);

                salas.add(sala);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return salas;
    }

    @Override
    public void update(SalaInformatica sala) {
        String query = "UPDATE SALA_INFORMATICA SET nombre_sala=?, capacidad=?, software_disponible=?, hardware_especial=?, ubicacion=?, estado=? WHERE id_sala=?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, sala.getNombreSala());
            pstmt.setInt(2, sala.getCapacidad());
            pstmt.setString(3, sala.getSoftwareDisponible());
            pstmt.setString(4, sala.getHardwareEspecial());
            pstmt.setString(5, sala.getUbicacion());
            pstmt.setString(6, sala.getEstado());
            pstmt.setLong(7, sala.getIdSala());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Long idSala) {
        String query = "DELETE FROM SALA_INFORMATICA WHERE id_sala = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, idSala);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean authenticate(Long idSala) {
        String query = "SELECT id_sala FROM SALA_INFORMATICA WHERE id_sala = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, idSala);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
