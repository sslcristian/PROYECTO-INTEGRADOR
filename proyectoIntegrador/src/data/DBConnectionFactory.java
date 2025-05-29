package data;

public class DBConnectionFactory {

    public static DBConnection getConnectionByRole(String role) {
        switch (role.toLowerCase()) {
            case "admin":
                return AdminConnection.getInstance();
            case "docente":
                return DocenteConnection.getInstance();
            case "usuario":
                return UserConnection.getInstance();
            default:
                throw new IllegalArgumentException("Rol no válido: " + role);
        }
    }

    public static void destroyConnectionByRole(String role) {
        switch (role.toLowerCase()) {
            case "admin":
                AdminConnection.destroyInstance();
                break;
            case "docente":
                DocenteConnection.destroyInstance();
                break;
            case "usuario":
                UserConnection.destroyInstance();
                break;
        }
    }
}