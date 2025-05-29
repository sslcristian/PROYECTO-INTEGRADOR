package data;

import model.Sancion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SancionDAO implements CRUD_Operation<Sancion, Integer> {
    private final Connection connection;

    public SancionDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Sancion sancion) {
        try {
            // 1. Obtener el siguiente valor de la secuencia
            String sqlSeq = "SELECT proyecto343.SANCION_SEQ.NEXTVAL FROM DUAL";
            int nextId = -1;
            try (PreparedStatement seqStmt = connection.prepareStatement(sqlSeq)) {
                ResultSet rs = seqStmt.executeQuery();
                if (rs.next()) {
                    nextId = rs.getInt(1);
                }
            }
            sancion.setIdSancion(nextId);

            // 2. Insertar el registro con el id previamente obtenido
            String query = "INSERT INTO proyecto343.TBL_SANCION (id_sancion, cedula_usuario, monto, motivo, fecha, estado) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, nextId);
                stmt.setLong(2, sancion.getCedulaUsuario());
                stmt.setDouble(3, sancion.getMonto());
                stmt.setString(4, sancion.getMotivo());
                stmt.setDate(5, sancion.getFecha());
                stmt.setString(6, sancion.getEstado());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int eliminarInactivasPorMes(int mes, int anio) {
        String query = "DELETE FROM proyecto343.TBL_SANCION WHERE estado = 'Inactiva' AND EXTRACT(MONTH FROM fecha) = ? AND EXTRACT(YEAR FROM fecha) = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, mes);
            stmt.setInt(2, anio);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    public List<Sancion> buscarPorCedula(long cedula) {
        List<Sancion> lista = new ArrayList<>();
        String query = "SELECT * FROM proyecto343.TBL_SANCION WHERE cedula_usuario = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, cedula);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Sancion s = new Sancion(
                    rs.getInt("id_sancion"),
                    rs.getLong("cedula_usuario"),
                    rs.getDouble("monto"),
                    rs.getString("motivo"),
                    rs.getDate("fecha"),
                    rs.getString("estado")
                );
                lista.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    @Override
    public ArrayList<Sancion> fetch() {
        ArrayList<Sancion> lista = new ArrayList<>();
        String query = "SELECT * FROM proyecto343.TBL_SANCION";

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
        String query = "UPDATE proyecto343.TBL_SANCION SET cedula_usuario=?, monto=?, motivo=?, fecha=?, estado=? WHERE id_sancion=?";

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
        String query = "DELETE FROM proyecto343.TBL_SANCION WHERE id_sancion=?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean authenticate(Integer id) {
        String query = "SELECT id_sancion FROM proyecto343.TBL_SANCION WHERE id_sancion=?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isUsuarioExistente(Long cedula) {
        String query = "SELECT CEDULA FROM proyecto343.TBL_USUARIO WHERE CEDULA = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, cedula);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasActiveSancion(long cedulaUsuario) {
        String query = "SELECT COUNT(*) FROM proyecto343.TBL_SANCION WHERE cedula_usuario = ? AND estado = 'Activa'";
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
