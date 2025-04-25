package data;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import model.Admin;

public class AdminDAO implements CRUD_Operation<Admin, Long> {

    private Connection connection;

    public AdminDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Admin admin) {
        String query = "INSERT INTO TBL_ADMIN (cedula, nombre, correo, telefono, contraseña_admin, departamento, contraseña_del_administrativo) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        // Comprobamos si los parámetros son correctos antes de intentar insertar
        if (admin == null) {
            System.out.println("El objeto Admin es nulo. No se puede guardar.");
            return;
        }
        
        // Agregamos log para los datos antes de intentar la inserción
        System.out.println("Intentando insertar Admin con datos: ");
        System.out.println("Cedula: " + admin.getCedula());
        System.out.println("Nombre: " + admin.getNombre());
        System.out.println("Correo: " + admin.getCorreo());
        System.out.println("Telefono: " + admin.getTelefono());
        System.out.println("Contraseña Admin: " + admin.getContraseñaAdmin());
        System.out.println("Departamento: " + admin.getDepartamento());
        System.out.println("Contraseña Administrativo: " + admin.getContraseñaAdministrativo());

        // Verificar si la cédula ya existe en la base de datos
        if (exists(admin.getCedula())) {
            System.out.println("La cédula ya está registrada en la base de datos.");
            return;
        }
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, admin.getCedula());
            pstmt.setString(2, admin.getNombre());
            pstmt.setString(3, admin.getCorreo());
            pstmt.setString(4, admin.getTelefono());
            pstmt.setString(5, admin.getContraseñaAdmin());
            pstmt.setString(6, admin.getDepartamento());
            pstmt.setString(7, admin.getContraseñaAdministrativo());

            // Intentamos ejecutar la inserción
            int rowsAffected = pstmt.executeUpdate();
            
            // Verificamos si la inserción fue exitosa
            if (rowsAffected > 0) {
                System.out.println("Admin inserted successfully.");
            } else {
                System.out.println("No rows affected. La inserción no fue exitosa.");
            }
        } catch (SQLException e) {
            System.out.println("Error al insertar el admin: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public ArrayList<Admin> fetch() {
        ArrayList<Admin> admins = new ArrayList<>();
        String query = "SELECT * FROM TBL_ADMIN";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                long cedula = rs.getLong("cedula");
                String nombre = rs.getString("nombre");
                String correo = rs.getString("correo");
                String telefono = rs.getString("telefono");
                String contraseñaAdmin = rs.getString("contraseña_admin");
                String departamento = rs.getString("departamento");
                String contraseñaDelAdministrativo = rs.getString("contraseña_del_administrativo");

                Admin admin = new Admin(cedula, nombre, correo, telefono, contraseñaAdmin, departamento, contraseñaDelAdministrativo);
                admins.add(admin);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return admins;
    }


    @Override
    public void update(Admin admin) {
        String sql = "UPDATE TBL_ADMIN SET nombre=?, correo=?, telefono=?, contraseña_admin=?, departamento=?, contraseña_del_administrativo=? WHERE cedula=?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, admin.getNombre());
            stmt.setString(2, admin.getCorreo());
            stmt.setString(3, admin.getTelefono());
            stmt.setString(4, admin.getContraseñaAdmin());
            stmt.setString(5, admin.getDepartamento());
            stmt.setString(6, admin.getContraseñaAdministrativo());
            stmt.setLong(7, admin.getCedula());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Long cedula) {
        String sql = "DELETE FROM TBL_ADMIN WHERE cedula=?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, cedula);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Admin deleted successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean authenticate(Long cedula) {
        String sql = "SELECT cedula FROM TBL_ADMIN WHERE cedula=?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, cedula);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean exists(Long cedula) {
        String query = "SELECT COUNT(*) FROM TBL_ADMIN WHERE cedula = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
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
    public boolean correoExiste(String correo) {
        String sql = "SELECT COUNT(*) FROM TBL_ADMIN WHERE correo = ?";
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
    public boolean isCorreoUnico(String correo) {
        String query = "SELECT COUNT(*) FROM TBL_ADMIN WHERE correo = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, correo);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0; 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public Admin findByCedula(Long cedula) {
        String query = "SELECT * FROM TBL_ADMIN WHERE cedula = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, cedula);
            ResultSet rs = pstmt.executeQuery();
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


}
