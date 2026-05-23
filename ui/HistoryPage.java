package ui;

import db.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class HistoryPage extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    public HistoryPage() {
        setTitle("My Travel History");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(new Color(0, 153, 76));
        JButton btnBack = new JButton("← BACK TO DASHBOARD");
        btnBack.setForeground(Color.WHITE);
        btnBack.setBackground(new Color(0, 153, 76));
        btnBack.setBorderPainted(false);
        btnBack.addActionListener(e -> { new Dashboard(); dispose(); });
        header.add(btnBack);
        add(header, BorderLayout.NORTH);

        // Table
        // Note: Column 4 is Status, Column 5 is HIDDEN BusID (needed for reviews)
        model = new DefaultTableModel(new String[]{"Ticket ID", "Bus Name", "Date", "Status", "BusID"}, 0);
        table = new JTable(model);
        table.setRowHeight(30);
        // Hide BusID column from view but keep data
        table.removeColumn(table.getColumnModel().getColumn(4));
        
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Actions Panel
        JPanel bottom = new JPanel();
        JButton btnCancel = new JButton("CANCEL TICKET");
        btnCancel.setBackground(Color.RED); 
        btnCancel.setForeground(Color.WHITE);
        
        JButton btnReview = new JButton("RATE SERVICE");
        btnReview.setBackground(Color.ORANGE);
        
        btnCancel.addActionListener(e -> cancelTicket());
        btnReview.addActionListener(e -> rateBus());
        
        bottom.add(btnCancel);
        bottom.add(btnReview);
        add(bottom, BorderLayout.SOUTH);

        loadHistory();
        setVisible(true);
    }

    private void loadHistory() {
        model.setRowCount(0);
        try {
            Connection con = DBConnection.getConnection();
            String sql = "SELECT b.booking_id, bus.bus_name, b.booking_date, b.status, bus.bus_id FROM booking b JOIN bus ON b.bus_id=bus.bus_id WHERE b.user_id=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, UserSession.currentUserId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getDate(3), rs.getString(4), rs.getInt(5)});
            }
            con.close();
        } catch(Exception e) { e.printStackTrace(); }
    }

    private void cancelTicket() {
        int row = table.getSelectedRow();
        if(row == -1) { JOptionPane.showMessageDialog(this, "Select a ticket to cancel!"); return; }
        
        String status = (String) model.getValueAt(row, 3);
        if(status.equalsIgnoreCase("Cancelled")) { JOptionPane.showMessageDialog(this, "This ticket is already cancelled."); return; }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel?", "Confirm", JOptionPane.YES_NO_OPTION);
        if(confirm == JOptionPane.YES_OPTION) {
            int bookingId = (int) model.getValueAt(row, 0);
            try {
                Connection con = DBConnection.getConnection();
                // 1. Update Status
                PreparedStatement ps = con.prepareStatement("UPDATE booking SET status='Cancelled' WHERE booking_id=?");
                ps.setInt(1, bookingId);
                ps.executeUpdate();
                
                // 2. Increase Seat Count (Optional, but good logic)
                // You would need bus_id here to update the bus table
                
                JOptionPane.showMessageDialog(this, "Ticket Cancelled Successfully.");
                loadHistory();
                con.close();
            } catch(Exception e) { e.printStackTrace(); }
        }
    }

    private void rateBus() {
        int row = table.getSelectedRow();
        if(row == -1) { JOptionPane.showMessageDialog(this, "Select a trip to rate!"); return; }
        
        int busId = (int) model.getValueAt(row, 4); // Getting the hidden BusID
        
        String ratingStr = JOptionPane.showInputDialog(this, "Rate this bus (1 to 5):");
        if(ratingStr == null) return;
        
        try {
            int rating = Integer.parseInt(ratingStr);
            if(rating < 1 || rating > 5) { JOptionPane.showMessageDialog(this, "Please enter 1-5"); return; }
            
            String comment = JOptionPane.showInputDialog(this, "Write a short comment:");
            
            Connection con = DBConnection.getConnection();
            String sql = "INSERT INTO reviews(user_id, bus_id, rating, comment) VALUES(?,?,?,?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, UserSession.currentUserId);
            ps.setInt(2, busId);
            ps.setInt(3, rating);
            ps.setString(4, comment);
            ps.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Review Submitted! Thank you.");
            con.close();
        } catch(NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Rating Number");
        } catch(Exception e) { e.printStackTrace(); }
    }
}