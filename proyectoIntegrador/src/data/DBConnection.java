package data;

import java.sql.Connection;

public interface DBConnection {
    Connection getConnection();
}