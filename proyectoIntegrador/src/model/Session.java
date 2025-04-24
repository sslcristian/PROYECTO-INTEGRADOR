package model;

import java.sql.*;

public class Session {

    private static Usuario usuarioActual;

   
    public static void setUsuarioActual(Usuario usuario) {
        usuarioActual = usuario;
    }

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

   
    public static void cerrarSesion() {
        usuarioActual = null;
    }

 
    public static void login(long cedula) {
        try {
         
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mi_base_de_datos", "usuario", "contraseña");

          
            String query = "SELECT * FROM TBL_USUARIO WHERE cedula = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, cedula);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
              
                Usuario usuario = new Usuario(
                        resultSet.getLong("cedula"),
                        resultSet.getString("nombre"),
                        resultSet.getString("correo"),
                        resultSet.getString("telefono"),
                        resultSet.getString("tipoUsuario"),
                        resultSet.getString("departamento"),
                        resultSet.getString("contraseña")
                );
             
                setUsuarioActual(usuario);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
