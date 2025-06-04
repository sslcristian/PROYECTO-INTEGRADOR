package data;

import model.Usuario;

import java.sql.*;
import java.util.ArrayList;

public class UsuarioDAO implements CRUD_Operation<Usuario, Long> {
    private Connection connection;

    public UsuarioDAO(Connection connection) {
        this.connection = connection;
    }

    public void save(Usuario usuario) {
        String call = "{ call proyecto343.SP_INSERTAR_USUARIO(?, ?, ?, ?, ?, ?, ?) }";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setLong(1, usuario.getCedula());
            cs.setString(2, usuario.getNombre());
            cs.setString(3, usuario.getCorreo());
            cs.setString(4, usuario.getTelefono());
            cs.setString(5, usuario.getTipoUsuario());
            cs.setString(6, usuario.getDepartamento());
            cs.setString(7, usuario.getContraseña());
            cs.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Usuario> fetch() {
        ArrayList<Usuario> usuarios = new ArrayList<>();
        String query = "SELECT * FROM proyecto343.TBL_USUARIO";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Usuario u = new Usuario(
                    rs.getLong("CEDULA"),
                    rs.getString("NOMBRE"),
                    rs.getString("CORREO"),
                    rs.getString("TELEFONO"),
                    rs.getString("TIPO_USUARIO"),
                    rs.getString("DEPARTAMENTO"),
                    rs.getString("CONTRASENA")
                );
                usuarios.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    public void update(Usuario usuario) {
        String call = "{ call proyecto343.SP_ACTUALIZAR_USUARIO(?, ?, ?, ?, ?, ?, ?) }";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setLong(1, usuario.getCedula());
            cs.setString(2, usuario.getNombre());
            cs.setString(3, usuario.getCorreo());
            cs.setString(4, usuario.getTelefono());
            cs.setString(5, usuario.getTipoUsuario());
            cs.setString(6, usuario.getDepartamento());
            cs.setString(7, usuario.getContraseña());
            cs.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(Long cedula) {
        String call = "{ call proyecto343.SP_ELIMINAR_USUARIO(?) }";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setLong(1, cedula);
            cs.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean authenticate(Long cedula) {
        String call = "{ ? = call proyecto343.FN_AUTENTICAR_USUARIO(?) }";
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
    public boolean correoExiste(String correo) {
        String call = "{ ? = call proyecto343.FN_CORREO_EXISTE(?) }";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.registerOutParameter(1, java.sql.Types.INTEGER);
            cs.setString(2, correo);
            cs.execute();
            return cs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean autenticar(long cedula, String contrasena) {
        String call = "{ ? = call proyecto343.FN_AUTENTICAR_USUARIO_LOGIN(?, ?) }";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.registerOutParameter(1, java.sql.Types.INTEGER);
            cs.setLong(2, cedula);
            cs.setString(3, contrasena);
            cs.execute();
            return cs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Usuario findByCedula(long cedula) {
        String sql = "SELECT * FROM proyecto343.TBL_USUARIO WHERE CEDULA = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, cedula);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Usuario(
                    rs.getLong("CEDULA"),
                    rs.getString("NOMBRE"),
                    rs.getString("CORREO"),
                    rs.getString("TELEFONO"),
                    rs.getString("TIPO_USUARIO"),
                    rs.getString("DEPARTAMENTO"),
                    rs.getString("CONTRASEÑA")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Usuario obtenerUsuarioPorCedula(long cedula) {
        Usuario usuario = null;
        String query = "SELECT * FROM proyecto343.TBL_USUARIO WHERE CEDULA = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, cedula);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                usuario = new Usuario(
                    rs.getLong("CEDULA"),
                    rs.getString("NOMBRE"),
                    rs.getString("CORREO"),
                    rs.getString("TELEFONO"),
                    rs.getString("TIPO_USUARIO"),
                    rs.getString("DEPARTAMENTO"),
                    rs.getString("CONTRASEÑA")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuario;
    }

    public boolean tieneSancionActiva(long cedula) {
        String call = "{ ? = call proyecto343.FN_TIENE_SANCION_ACTIVA(?) }";
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

    public double obtenerMontoSancionActiva(long cedula) {
        String call = "{ ? = call proyecto343.FN_MONTO_SANCION_ACTIVA(?) }";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.registerOutParameter(1, java.sql.Types.DOUBLE);
            cs.setLong(2, cedula);
            cs.execute();
            return cs.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    public boolean NotificacionReserva(long cedula) {
        String call = "{ ? = call proyecto343.FN_NOTIFICACION_RESERVA(?) }";
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

    public ArrayList<Usuario> fetchAllCedulaNombre() {
        ArrayList<Usuario> usuarios = new ArrayList<>();
        String query = "SELECT CEDULA, NOMBRE FROM proyecto343.TBL_USUARIO";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Usuario u = new Usuario(
                    rs.getLong("CEDULA"),
                    rs.getString("NOMBRE"),
                    null, // correo
                    null, // telefono
                    null, // tipoUsuario
                    null, // departamento
                    null  // contraseña
                );
                usuarios.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    public java.util.HashMap<Long, String> fetchCedulaNombreMap() {
        java.util.HashMap<Long, String> mapa = new java.util.HashMap<>();
        String query = "SELECT CEDULA, NOMBRE FROM proyecto343.TBL_USUARIO";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                mapa.put(rs.getLong("CEDULA"), rs.getString("NOMBRE"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mapa;
    }


}