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

    public void save(Sancion sancion) {
        String call = "{ call proyecto343.SP_INSERTAR_SANCION(?, ?, ?, ?, ?, ?) }";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setLong(1, sancion.getCedulaUsuario());
            cs.setDouble(2, sancion.getMonto());
            cs.setString(3, sancion.getMotivo());
            cs.setDate(4, sancion.getFecha());
            cs.setString(5, sancion.getEstado());
            cs.registerOutParameter(6, java.sql.Types.INTEGER);
            cs.execute();
            sancion.setIdSancion(cs.getInt(6));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int eliminarInactivasPorMes(int mes, int anio) {
    	String call = "{ call proyecto343.SP_ELIMINAR_SANCION_INA(?, ?, ?) }";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setInt(1, mes);
            cs.setInt(2, anio);
            cs.registerOutParameter(3, java.sql.Types.INTEGER);
            cs.execute();
            return cs.getInt(3);
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

    public void update(Sancion sancion) {
        String call = "{ call proyecto343.SP_ACTUALIZAR_SANCION(?, ?, ?, ?, ?, ?) }";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setInt(1, sancion.getIdSancion());
            cs.setLong(2, sancion.getCedulaUsuario());
            cs.setDouble(3, sancion.getMonto());
            cs.setString(4, sancion.getMotivo());
            cs.setDate(5, sancion.getFecha());
            cs.setString(6, sancion.getEstado());
            cs.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void delete(Integer id) {
        String call = "{ call proyecto343.SP_ELIMINAR_SANCION(?) }";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setInt(1, id);
            cs.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean authenticate(Integer id) {
        String call = "{ ? = call proyecto343.FN_AUTENTICAR_SANCION(?) }";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.registerOutParameter(1, java.sql.Types.INTEGER);
            cs.setInt(2, id);
            cs.execute();
            return cs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isUsuarioExistente(Long cedula) {
        String call = "{ ? = call proyecto343.FN_USUARIO_EXISTENTE(?) }";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.registerOutParameter(1, java.sql.Types.INTEGER);
            cs.setLong(2, cedula);
            cs.execute();
            return cs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean hasActiveSancion(long cedulaUsuario) {
        String call = "{ ? = call proyecto343.FN_USUARIO_CON_SANCION_ACTIVA(?) }";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.registerOutParameter(1, java.sql.Types.INTEGER);
            cs.setLong(2, cedulaUsuario);
            cs.execute();
            return cs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
