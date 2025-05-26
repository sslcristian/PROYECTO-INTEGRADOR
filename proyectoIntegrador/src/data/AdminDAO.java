package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

        String query = "INSERT INTO proyecto343.TBL_ADMIN "
                + "(cedula, nombre, correo, telefono, contraseña_admin, departamento, contraseña_del_administrativo) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, admin.getCedula());
            pstmt.setString(2, admin.getNombre());
            pstmt.setString(3, admin.getCorreo());
            pstmt.setString(4, admin.getTelefono());
            pstmt.setString(5, admin.getContraseñaAdmin());
            pstmt.setString(6, admin.getDepartamento());
            pstmt.setString(7, admin.getContraseñaAdministrativo());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Admin inserted successfully.");
            } else {
                System.out.println("No rows affected. La inserción no fue exitosa.");
            }
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
                    rs.getLong("cedula"),
                    rs.getString("nombre"),
                    rs.getString("correo"),
                    rs.getString("telefono"),
                    rs.getString("contraseña_admin"),
                    rs.getString("departamento"),
                    rs.getString("contraseña_del_administrativo")
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

        String sql = "UPDATE proyecto343.TBL_ADMIN SET nombre=?, correo=?, telefono=?, contraseña_admin=?, departamento=?, contraseña_del_administrativo=? WHERE cedula=?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, admin.getNombre());
            stmt.setString(2, admin.getCorreo());
            stmt.setString(3, admin.getTelefono());
            stmt.setString(4, admin.getContraseñaAdmin());
            stmt.setString(5, admin.getDepartamento());
            stmt.setString(6, admin.getContraseñaAdministrativo());
            stmt.setLong(7, admin.getCedula());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Admin actualizado correctamente.");
            } else {
                System.out.println("No se encontró el admin para actualizar.");
            }
        } catch (SQLException e) {
            System.out.println("Error al actualizar admin: " + e.getMessage());
        }
    }

    @Override
    public void delete(Long cedula) {
        String sql = "DELETE FROM proyecto343.TBL_ADMIN WHERE cedula=?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, cedula);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Admin eliminado correctamente.");
            } else {
                System.out.println("No se encontró el admin para eliminar.");
            }
        } catch (SQLException e) {
            System.out.println("Error al eliminar admin: " + e.getMessage());
        }
    }

    @Override
    public boolean authenticate(Long cedula) {
        String sql = "SELECT cedula FROM proyecto343.TBL_ADMIN WHERE cedula=?";

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
        String query = "SELECT COUNT(*) FROM proyecto343.TBL_ADMIN WHERE cedula = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, cedula);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error en exists: " + e.getMessage());
        }
        return false;
    }

    public boolean correoExiste(String correo) {
        String sql = "SELECT COUNT(*) FROM proyecto343.TBL_ADMIN WHERE correo = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, correo);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error en correoExiste: " + e.getMessage());
        }
        return false;
    }

    public boolean isCorreoUnico(String correo) {
        String query = "SELECT COUNT(*) FROM proyecto343.TBL_ADMIN WHERE correo = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, correo);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            System.out.println("Error en isCorreoUnico: " + e.getMessage());
        }
        return false;
    }

    public Admin findByCedula(Long cedula) {
        String query = "SELECT * FROM proyecto343.TBL_ADMIN WHERE cedula = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, cedula);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Admin(
                        rs.getLong("cedula"),
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getString("telefono"),
                        rs.getString("contraseña_admin"),
                        rs.getString("departamento"),
                        rs.getString("contraseña_del_administrativo")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en findByCedula: " + e.getMessage());
        }
        return null;
    }
}