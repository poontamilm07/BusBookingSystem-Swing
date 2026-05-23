package db;

import java.sql.*;
import javax.swing.JOptionPane;

public class DBConnection {
    public static Connection getConnection() {
        Connection con = null;
        try {
            // Load the Driver
            Class.forName("com.mysql.cj.jdbc.Driver"); 
            
            // Define Connection Details
            String url = "jdbc:mysql://localhost:3306/bus_booking"; 
            String user = "root";
            String password = "Rubasri1512"; // Your password
            
            // Establish Connection
            con = DriverManager.getConnection(url, user, password);
            
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Driver not found! Check your lib folder.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage());
        }
        return con;
    }
}