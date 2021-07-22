package WUSL.dbClass;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class dbConnect {

    private static Connection connection;
    public static Connection getConnection() {

        if (connection == null) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/inventoryInfo", "root", "root");
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
            catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.out.println("Connected to database");
        }
        //System.out.println("Connected to database");

        return connection;
    }


}
