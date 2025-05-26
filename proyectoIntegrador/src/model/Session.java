package model;

import data.DBConnectionFactory;
import data.DBConnection;

import java.sql.*;

public class Session {

    private static Usuario usuarioActual;
    private static String rolActual; // "usuario", "admin", etc.
    private static DBConnection dbConnection;

    // Usado para login y setear sesión (solo para usuario)
    public static boolean login(long cedula, String role) {
        try {
            dbConnection = DBConnectionFactory.getConnectionByRole(role);
            rolActual = role;
            Connection connection = dbConnection.getConnection();

            String query = "SELECT * FROM proyecto343.TBL_USUARIO WHERE CEDULA = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, cedula);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Usuario usuario = new Usuario(
                        resultSet.getLong("CEDULA"),
                        resultSet.getString("NOMBRE"),
                        resultSet.getString("CORREO"),
                        resultSet.getString("TELEFONO"),
                        resultSet.getString("TIPO_USUARIO"),
                        resultSet.getString("DEPARTAMENTO"),
                        resultSet.getString("CONTRASEÑA")
                );
                setUsuarioActual(usuario);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void setUsuarioActual(Usuario usuario) {
        usuarioActual = usuario;
    }

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public static String getRolActual() {
        return rolActual;
    }

    // CAMBIO: Permitir obtener conexión de administrador aunque no haya sesión activa
    public static Connection getConnection() {
        // Si hay una conexión activa, la retorna normalmente
        if (dbConnection != null) {
            return dbConnection.getConnection();
        }
        // Si no hay sesión pero se necesita registrar admin, permite obtener una nueva conexión como admin
        try {
            DBConnection tempAdminConnection = DBConnectionFactory.getConnectionByRole("admin");
            return tempAdminConnection != null ? tempAdminConnection.getConnection() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void cerrarSesion() {
        usuarioActual = null;
        if (dbConnection != null) {
            DBConnectionFactory.destroyConnectionByRole(rolActual);
            dbConnection = null;
        }
        rolActual = null;
    }
}