package bankserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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
