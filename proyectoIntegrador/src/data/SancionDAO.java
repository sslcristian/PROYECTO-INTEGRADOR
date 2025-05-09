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
        String query = "INSERT INTO TBL_SANCION (cedula_usuario, monto, motivo, fecha, estado) " +
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
        String query = "SELECT * FROM TBL_SANCION";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String motivo = null;
                if (rs.getClob("MOTIVO") != null) {
                    motivo = rs.getClob("MOTIVO").getSubString(1, (int) rs.getClob("MOTIVO").length());
                }
                
                Sancion sancion = new Sancion(
                        rs.getInt("ID_SANCION"),
                        rs.getLong("CEDULA_USUARIO"),
                        rs.getDouble("MONTO"),
                        motivo,
                        rs.getDate("FECHA"),
                        rs.getString("ESTADO")
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
        String query = "UPDATE TBL_SANCION SET cedula_usuario=?, monto=?, motivo=?, fecha=?, estado=? " +
                       "WHERE id_sanción=?";

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
        String query = "DELETE FROM TBL_SANCION WHERE id_sancion=?";

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
        String query = "SELECT id_sanción FROM TBL_SANCION WHERE id_sancin=?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean isUsuarioExistente(Long cedula) {
        String query = "SELECT CEDULA FROM TBL_USUARIO WHERE CEDULA = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, cedula);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasActiveSancion(long cedulaUsuario) {
        String query = "SELECT COUNT(*) FROM TBL_SANCION WHERE cedula_usuario = ? AND estado = 'Activa'";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, cedulaUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    
}
