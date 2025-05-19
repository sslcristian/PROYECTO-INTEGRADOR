package data;

import model.Usuario;

import java.sql.*;
import java.util.ArrayList;

public class UsuarioDAO implements CRUD_Operation<Usuario, Long> {
    private Connection connection;

    public UsuarioDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Usuario usuario) {
        String query = "INSERT INTO TBL_USUARIO (cedula, nombre, correo, telefono, tipo_usuario, departamento, contraseña) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, usuario.getCedula());
            pstmt.setString(2, usuario.getNombre());
            pstmt.setString(3, usuario.getCorreo());
            pstmt.setString(4, usuario.getTelefono());
            pstmt.setString(5, usuario.getTipoUsuario());
            pstmt.setString(6, usuario.getDepartamento());
            pstmt.setString(7, usuario.getContraseña());

            pstmt.executeUpdate();
            System.out.println("Usuario guardado exitosamente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Usuario> fetch() {
        ArrayList<Usuario> usuarios = new ArrayList<>();
        String query = "SELECT * FROM TBL_USUARIO";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Usuario u = new Usuario(
                    rs.getLong("cedula"),
                    rs.getString("nombre"),
                    rs.getString("correo"),
                    rs.getString("telefono"),
                    rs.getString("tipo_usuario"),
                    rs.getString("departamento"),
                    rs.getString("contraseña")
                );
                usuarios.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return usuarios;
    }

    @Override
    public void update(Usuario usuario) {
        String query = "UPDATE TBL_USUARIO SET nombre=?, correo=?, telefono=?, tipo_usuario=?, departamento=?, contraseña=? WHERE cedula=?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, usuario.getNombre());
            pstmt.setString(2, usuario.getCorreo());
            pstmt.setString(3, usuario.getTelefono());
            pstmt.setString(4, usuario.getTipoUsuario());
            pstmt.setString(5, usuario.getDepartamento());
            pstmt.setString(6, usuario.getContraseña());
            pstmt.setLong(7, usuario.getCedula());

            pstmt.executeUpdate();
            System.out.println("Usuario actualizado correctamente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Long cedula) {
        String query = "DELETE FROM TBL_USUARIO WHERE cedula=?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, cedula);
            pstmt.executeUpdate();
            System.out.println("Usuario eliminado correctamente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean authenticate(Long cedula) {
        String query = "SELECT cedula FROM TBL_USUARIO WHERE cedula=?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, cedula);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean correoExiste(String correo) {
        String sql = "SELECT COUNT(*) FROM TBL_USUARIO WHERE correo = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, correo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Usuario autenticar(long cedula, String contrasena) {
        String sql = "SELECT * FROM TBL_USUARIO WHERE cedula = ? AND contraseña = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, cedula);
            stmt.setString(2, contrasena);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                if (tieneSancionActiva(cedula)) {
                    double monto = obtenerMontoSancionActiva(cedula);
                    throw new IllegalStateException("Tienes una sanción activa comunicate con los administradores  Monto: COP" + monto);
                }

                return new Usuario(
                    rs.getLong("cedula"),
                    rs.getString("nombre"),
                    rs.getString("correo"),
                    rs.getString("telefono"),
                    rs.getString("tipo_usuario"),
                    rs.getString("departamento"),
                    rs.getString("contraseña")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public Usuario findByCedula(long cedula) {
        String sql = "SELECT * FROM TBL_USUARIO WHERE cedula = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, cedula);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Usuario(
                    rs.getLong("cedula"),
                    rs.getString("nombre"),
                    rs.getString("correo"),
                    rs.getString("telefono"),
                    rs.getString("tipo_usuario"),
                    rs.getString("departamento"),
                    rs.getString("contraseña")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Usuario obtenerUsuarioPorCedula(long cedula) {
        Usuario usuario = null;
        String query = "SELECT * FROM usuarios WHERE cedula = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, cedula);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
              
                usuario = new Usuario(
                    rs.getLong("cedula"),
                    rs.getString("nombre"),
                    rs.getString("correo"),
                    rs.getString("telefono"),
                    rs.getString("tipoUsuario"),
                    rs.getString("departamento"),
                    rs.getString("contraseña")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return usuario;
    }
    public boolean tieneSancionActiva(long cedula) {
        String sql = "SELECT COUNT(*) FROM TBL_SANCION WHERE cedula_usuario = ? AND estado = 'Activa'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, cedula);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public double obtenerMontoSancionActiva(long cedula) {
        String sql = "SELECT monto FROM TBL_SANCION WHERE cedula_usuario = ? AND estado = 'Activa'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, cedula);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("monto");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }


}
