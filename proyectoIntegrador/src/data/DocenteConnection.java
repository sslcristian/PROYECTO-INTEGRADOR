package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DocenteConnection implements DBConnection {

    private static DocenteConnection instance;
    private Connection connection;
    private final String username = "docente";
    private final String password = "docente123";
    private final String host = "192.168.254.215";
    private final String port = "1521";
    private final String service = "orcl";

    private DocenteConnection() {
        try {
            connection = DriverManager.getConnection(getConnectionString(), username, password);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error connecting as docente.");
        }
    }

    public static DocenteConnection getInstance() {
        if (instance == null || instance.connection == null || isConnectionClosed(instance.connection)) {
            instance = new DocenteConnection();
        }
        return instance;
    }

    public static void destroyInstance() {
        if (instance != null) {
            try {
                if (instance.connection != null && !instance.connection.isClosed()) {
                    instance.connection.close();
                }
            } catch (SQLException ignored) {}
            instance = null;
        }
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    private String getConnectionString() {
        return String.format("jdbc:oracle:thin:@%s:%s:%s", this.host, this.port, this.service);
    }

    private static boolean isConnectionClosed(Connection conn) {
        try {
            return conn == null || conn.isClosed();
        } catch (SQLException e) {
            return true;
        }
    }
}
