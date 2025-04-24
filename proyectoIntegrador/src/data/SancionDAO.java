package data;

import model.Sancion;

import java.sql.*;
import java.util.ArrayList;

public class SancionDAO implements CRUD_Operation<Sancion, Integer> {
    private Connection connection;

   
    public SancionDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Sancion sancion) {
        String query = "INSERT INTO sancion (cedula_usuario, monto, motivo, fecha, estado) " +
                       "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
         
            pstmt.setLong(1, sancion.getCedulaUsuario());
            pstmt.setDouble(2, sancion.getMonto());
            pstmt.setString(3, sancion.getMotivo());
            pstmt.setDate(4, sancion.getFecha());
            pstmt.setString(5, sancion.getEstado());

            
            int rowsAffected = pstmt.executeUpdate();

            
            if (rowsAffected > 0) {
               
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                       
                        sancion.setIdSancion(generatedKeys.getInt(1));
                    }
                }
                System.out.println("Sanción registrada correctamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Sancion> fetch() {
        ArrayList<Sancion> sanciones = new ArrayList<>();
        String query = "SELECT * FROM sancion";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                
                Sancion sancion = new Sancion(
                        rs.getInt("id_sancion"),
                        rs.getLong("cedula_usuario"),
                        rs.getDouble("monto"),
                        rs.getString("motivo"),
                        rs.getDate("fecha"),
                        rs.getString("estado")
                );
                sanciones.add(sancion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sanciones;
    }

    @Override
    public void update(Sancion sancion) {
        String query = "UPDATE sancion SET cedula_usuario=?, monto=?, motivo=?, fecha=?, estado=? " +
                       "WHERE id_sancion=?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, sancion.getCedulaUsuario());
            pstmt.setDouble(2, sancion.getMonto());
            pstmt.setString(3, sancion.getMotivo());
            pstmt.setDate(4, sancion.getFecha());
            pstmt.setString(5, sancion.getEstado());
            pstmt.setInt(6, sancion.getIdSancion());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Sanción actualizada correctamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Integer id) {
        String query = "DELETE FROM sancion WHERE id_sancion=?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Sanción con ID " + id + " eliminada correctamente.");
            } else {
                System.out.println("No se encontró una sanción con el ID: " + id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean authenticate(Integer id) {
        String query = "SELECT id_sancion FROM sancion WHERE id_sancion=?";

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
