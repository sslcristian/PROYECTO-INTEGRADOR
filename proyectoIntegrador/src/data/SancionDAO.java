package data;

import model.Sancion;
import java.sql.*;
import java.util.ArrayList;

public class SancionDAO implements CRUD_Operation<Sancion, Integer> {
    private final Connection connection;

    public SancionDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Sancion sancion) {
        String query = "INSERT INTO TBL_SANCION (cedula_usuario, monto, motivo, fecha, estado) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, sancion.getCedulaUsuario());
            stmt.setDouble(2, sancion.getMonto());
            stmt.setString(3, sancion.getMotivo());
            stmt.setDate(4, sancion.getFecha());
            stmt.setString(5, sancion.getEstado());

            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                sancion.setIdSancion(keys.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Sancion> fetch() {
        ArrayList<Sancion> lista = new ArrayList<>();
        String query = "SELECT * FROM TBL_SANCION";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String motivo = rs.getClob("MOTIVO") != null
                    ? rs.getClob("MOTIVO").getSubString(1, (int) rs.getClob("MOTIVO").length())
                    : null;

                lista.add(new Sancion(
                    rs.getInt("ID_SANCION"),
                    rs.getLong("CEDULA_USUARIO"),
                    rs.getDouble("MONTO"),
                    motivo,
                    rs.getDate("FECHA"),
                    rs.getString("ESTADO")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    @Override
    public void update(Sancion sancion) {
        String query = "UPDATE TBL_SANCION SET cedula_usuario=?, monto=?, motivo=?, fecha=?, estado=? WHERE id_sancion=?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, sancion.getCedulaUsuario());
            stmt.setDouble(2, sancion.getMonto());
            stmt.setString(3, sancion.getMotivo());
            stmt.setDate(4, sancion.getFecha());
            stmt.setString(5, sancion.getEstado());
            stmt.setInt(6, sancion.getIdSancion());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Integer id) {
        String query = "DELETE FROM TBL_SANCION WHERE id_sancion=?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean authenticate(Integer id) {
        String query = "SELECT id_sancion FROM TBL_SANCION WHERE id_sancion=?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isUsuarioExistente(Long cedula) {
        String query = "SELECT CEDULA FROM TBL_USUARIO WHERE CEDULA = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, cedula);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasActiveSancion(long cedulaUsuario) {
        String query = "SELECT COUNT(*) FROM TBL_SANCION WHERE cedula_usuario = ? AND estado = 'Activa'";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, cedulaUsuario);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
