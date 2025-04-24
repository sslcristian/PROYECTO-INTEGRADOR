package data;

import model.SalaPrestada;

import java.sql.*;
import java.util.ArrayList;

public class SalaPrestadaDAO implements CRUD_Operation<SalaPrestada, Integer> {
    private Connection connection;

    // Constructor
    public SalaPrestadaDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(SalaPrestada salaPrestada) {
        String query = "INSERT INTO TBL_SALA_PRESTADA (id_solicitud_s, id_sala, fecha_inicio, fecha_fin, observaciones) " +
                       "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, salaPrestada.getIdSolicitudS());
            pstmt.setInt(2, salaPrestada.getIdSala());
            pstmt.setTimestamp(3, salaPrestada.getFechaInicio());   
            pstmt.setTimestamp(4, salaPrestada.getFechaFin());       
            pstmt.setString(5, salaPrestada.getObservaciones());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        salaPrestada.setIdPrestamoS(generatedKeys.getInt(1));
                    }
                }
                System.out.println("Sala prestada registrada correctamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<SalaPrestada> fetch() {
        ArrayList<SalaPrestada> salasPrestadas = new ArrayList<>();
        String query = "SELECT * FROM TBL_SALA_PRESTADA";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int idPrestamoS = rs.getInt("id_prestamo_s");
                int idSolicitudS = rs.getInt("id_solicitud_s");
                int idSala = rs.getInt("id_sala");
                Timestamp fechaInicio = rs.getTimestamp("fecha_inicio");  
                Timestamp fechaFin = rs.getTimestamp("fecha_fin");         
                String observaciones = rs.getString("observaciones");

                SalaPrestada salaPrestada = new SalaPrestada(
                        idPrestamoS, idSolicitudS, idSala, fechaInicio, fechaFin, observaciones
                );

                salasPrestadas.add(salaPrestada);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return salasPrestadas;
    }

    @Override
    public void update(SalaPrestada salaPrestada) {
        String query = "UPDATE TBL_SALA_PRESTADA SET id_solicitud_s=?, id_sala=?, fecha_inicio=?, fecha_fin=?, observaciones=? " +
                       "WHERE id_prestamo_s=?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, salaPrestada.getIdSolicitudS());
            pstmt.setInt(2, salaPrestada.getIdSala());
            pstmt.setTimestamp(3, salaPrestada.getFechaInicio());  
            pstmt.setTimestamp(4, salaPrestada.getFechaFin());     
            pstmt.setString(5, salaPrestada.getObservaciones());
            pstmt.setInt(6, salaPrestada.getIdPrestamoS());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Sala prestada actualizada correctamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Integer id) {
        String query = "DELETE FROM TBL_SALA_PRESTADA WHERE id_prestamo_s=?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Sala prestada con ID " + id + " eliminada correctamente.");
            } else {
                System.out.println("No se encontró una sala prestada con el ID: " + id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean authenticate(Integer id) {
        String query = "SELECT id_prestamo_s FROM TBL_SALA_PRESTADA WHERE id_prestamo_s=?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
