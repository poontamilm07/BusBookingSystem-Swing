package ui;

import db.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AdminDashboard extends JFrame {
    
    public AdminDashboard() {
        setTitle("Bus Booking System - Admin Panel");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // --- TABS ---
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        tabs.addTab("  Manage Buses  ", createAddBusPanel());
        tabs.addTab("  View All Bookings  ", createViewBookingPanel());
        
        // --- LOGOUT BUTTON ---
        JButton logout = new JButton("LOGOUT ADMIN");
        logout.setBackground(Color.RED);
        logout.setForeground(Color.WHITE);
        logout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logout.addActionListener(e -> {
            new LoginPage(); // Go back to main login
            dispose();
        });
        
        add(logout, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        setVisible(true);
    }

    // --- TAB 1: ADD BUS ---
    private JPanel createAddBusPanel() {
        JPanel panel = new JPanel(new GridLayout(8, 2, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JTextField name = new JTextField();
        JTextField src = new JTextField();
        JTextField dest = new JTextField();
        JTextField seats = new JTextField("40");
        JTextField price = new JTextField();
        JComboBox<String> type = new JComboBox<>(new String[]{"AC Seater", "AC Sleeper", "Non-AC Seater"});
        
        JButton addBtn = new JButton("ADD BUS TO DATABASE");
        addBtn.setBackground(new Color(0, 102, 204));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));

        panel.add(new JLabel("Bus Name:")); panel.add(name);
        panel.add(new JLabel("Source City:")); panel.add(src);
        panel.add(new JLabel("Destination City:")); panel.add(dest);
        panel.add(new JLabel("Total Seats:")); panel.add(seats);
        panel.add(new JLabel("Price (Rs):")); panel.add(price);
        panel.add(new JLabel("Bus Type:")); panel.add(type);
        panel.add(new JLabel("")); panel.add(addBtn);

        addBtn.addActionListener(e -> {
            try {
                Connection con = DBConnection.getConnection();
                String sql = "INSERT INTO bus(bus_name, source, destination, total_seats, available_seats, price, bus_type) VALUES(?,?,?,?,?,?,?)";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, name.getText());
                ps.setString(2, src.getText());
                ps.setString(3, dest.getText());
                ps.setInt(4, Integer.parseInt(seats.getText()));
                ps.setInt(5, Integer.parseInt(seats.getText())); // Available starts equal to Total
                ps.setDouble(6, Double.parseDouble(price.getText()));
                ps.setString(7, type.getSelectedItem().toString());
                
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Bus Added Successfully!");
                con.close();
            } catch (Exception ex) { 
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
        return panel;
    }

    // --- TAB 2: VIEW BOOKINGS ---
    private JPanel createViewBookingPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] cols = {"Booking ID", "User Name", "Bus Name", "Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        table.setRowHeight(25);
        
        JButton refresh = new JButton("Refresh Data");
        refresh.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refresh.addActionListener(e -> {
            model.setRowCount(0);
            try {
                Connection con = DBConnection.getConnection();
                String sql = "SELECT b.booking_id, u.name, bus.bus_name, b.booking_date, b.status " +
                             "FROM booking b " +
                             "JOIN user u ON b.user_id=u.user_id " +
                             "JOIN bus ON b.bus_id=bus.bus_id";
                ResultSet rs = con.createStatement().executeQuery(sql);
                while(rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDate(4), rs.getString(5)
                    });
                }
                con.close();
            } catch(Exception ex) { ex.printStackTrace(); }
        });
        
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(refresh, BorderLayout.SOUTH);
        return panel;
    }
}