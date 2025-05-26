package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AdminConnection implements DBConnection {

    private static AdminConnection instance;
    private Connection connection;
    private final String username = " subadmin";
    private final String password = "admin123";
    private final String host = "192.168.254.215";
    private final String port = "1521";
    private final String service = "orcl";

    private AdminConnection() {
        try {
            connection = DriverManager.getConnection(getConnectionString(), username, password);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error connecting as admin.");
        }
    }

    public static AdminConnection getInstance() {
        if (instance == null) instance = new AdminConnection();
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
}