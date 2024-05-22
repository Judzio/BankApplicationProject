package client;
import java.sql.DriverManager;
import java.util.concurrent.ConcurrentHashMap;
import java.sql.*;

import java.util.Map;

public class DataStorage {
    static final String name = "root";
    static final String pass = "";
    static final String url = "jdbc:mysql://localhost:3306/accounts";

    static Connection connection;
    static Statement statement;
    static {
        try {
            connection = DriverManager.getConnection(url, name, pass);
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
