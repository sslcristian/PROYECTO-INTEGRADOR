package data;

import java.sql.*;
import java.util.ArrayList;
import model.Admin;

public class AdminDAO implements CRUD_Operation<Admin, Long> {

    private final Connection connection;

    public AdminDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Admin admin) {
        if (admin == null) {
            System.out.println("El objeto Admin es nulo. No se puede guardar.");
            return;
        }
        // Verificar si la cédula ya existe en la base de datos
        if (exists(admin.getCedula())) {
            System.out.println("La cédula ya está registrada en la base de datos.");
            return;
        }
        // Usar procedimiento almacenado en esquema proyecto343
        String call = "{call proyecto343.sp_insert_admin(?,?,?,?,?,?,?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setLong(1, admin.getCedula());
            cs.setString(2, admin.getNombre());
            cs.setString(3, admin.getCorreo());
            cs.setString(4, admin.getContraseñaAdmin());
            cs.setString(5, admin.getDepartamento());
            cs.setString(6, admin.getContraseñaAdministrativo());
            cs.setString(7, admin.getTelefono());
            cs.execute();
            System.out.println("Admin insertado exitosamente (procedimiento almacenado).");
        } catch (SQLException e) {
            System.out.println("Error al insertar el admin: " + e.getMessage());
        }
    }

    public ArrayList<Admin> fetch() {
        ArrayList<Admin> admins = new ArrayList<>();
        String query = "SELECT * FROM proyecto343.TBL_ADMIN";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Admin admin = new Admin(
                    rs.getLong("CEDULA"),
                    rs.getString("NOMBRE"),
                    rs.getString("CORREO"),
                    rs.getString("TELEFONO"),
                    rs.getString("CONTRASEÑA_ADMIN"),
                    rs.getString("DEPARTAMENTO"),
                    rs.getString("CONTRASEÑA_ADMVO")
                );
                admins.add(admin);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener admins: " + e.getMessage());
        }
        return admins;
    }

    @Override
    public void update(Admin admin) {
        if (admin == null) {
            System.out.println("El objeto Admin es nulo. No se puede actualizar.");
            return;
        }
        // Usar procedimiento almacenado en esquema proyecto343
        String call = "{call proyecto343.sp_update_admin(?,?,?,?,?,?,?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setLong(1, admin.getCedula());
            cs.setString(2, admin.getNombre());
            cs.setString(3, admin.getCorreo());
            cs.setString(4, admin.getContraseñaAdmin());
            cs.setString(5, admin.getDepartamento());
            cs.setString(6, admin.getContraseñaAdministrativo());
            cs.setString(7, admin.getTelefono());
            cs.execute();
            System.out.println("Admin actualizado correctamente (procedimiento almacenado).");
        } catch (SQLException e) {
            System.out.println("Error al actualizar admin: " + e.getMessage());
        }
    }

    @Override
    public void delete(Long cedula) {
        // Usar procedimiento almacenado en esquema proyecto343
        String call = "{call proyecto343.sp_delete_admin(?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.setLong(1, cedula);
            cs.execute();
            System.out.println("Admin eliminado correctamente (procedimiento almacenado).");
        } catch (SQLException e) {
            System.out.println("Error al eliminar admin: " + e.getMessage());
        }
    }

    @Override
    public boolean authenticate(Long cedula) {
        String sql = "SELECT CEDULA FROM proyecto343.TBL_ADMIN WHERE CEDULA = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, cedula);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error en authenticate: " + e.getMessage());
        }
        return false;
    }

    public boolean exists(Long cedula) {
        // Usar función almacenada en esquema proyecto343
        String call = "{? = call proyecto343.fn_admin_exists(?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setLong(2, cedula);
            cs.execute();
            return cs.getInt(1) > 0;
        } catch (SQLException e) {
            System.out.println("Error en exists: " + e.getMessage());
        }
        return false;
    }

    public boolean correoExiste(String correo) {
        String call = "{? = call proyecto343.fn_admin_correo_exists(?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setString(2, correo);
            cs.execute();
            return cs.getInt(1) > 0;
        } catch (SQLException e) {
            System.out.println("Error en correoExiste: " + e.getMessage());
        }
        return false;
    }

    public boolean isCorreoUnico(String correo) {
        String call = "{? = call proyecto343.fn_admin_correo_unico(?)}";
        try (CallableStatement cs = connection.prepareCall(call)) {
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setString(2, correo);
            cs.execute();
            return cs.getInt(1) == 1;
        } catch (SQLException e) {
            System.out.println("Error en isCorreoUnico: " + e.getMessage());
        }
        return false;
    }

    public Admin findByCedula(Long cedula) {
        String query = "SELECT * FROM proyecto343.TBL_ADMIN WHERE CEDULA = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, cedula);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Admin(
                        rs.getLong("CEDULA"),
                        rs.getString("NOMBRE"),
                        rs.getString("CORREO"),
                        rs.getString("TELEFONO"),
                        rs.getString("CONTRASEÑA_ADMIN"),
                        rs.getString("DEPARTAMENTO"),
                        rs.getString("CONTRASEÑA_ADMVO")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en findByCedula: " + e.getMessage());
        }
        return null;
    }
}