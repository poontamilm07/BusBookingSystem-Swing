package ui;

import db.DBConnection;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Date;

public class BookingPage extends JFrame {

    private JComboBox<String> sourceCombo, destCombo;
    private JSpinner dateSpinner;
    private JTable busTable;
    private DefaultTableModel busModel;
    private String[] cities = {"Select City", "Chennai", "Bangalore", "Salem", "Coimbatore", "Madurai"};

    public BookingPage() {
        setTitle("Search & Book Bus");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- HEADER ---
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(new Color(0, 51, 102));
        JButton btnBack = new JButton("← BACK");
        btnBack.setForeground(Color.WHITE);
        btnBack.setBackground(new Color(0, 51, 102));
        btnBack.addActionListener(e -> { new Dashboard(); dispose(); });
        header.add(btnBack);
        add(header, BorderLayout.NORTH);

        // --- SEARCH PANEL ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        sourceCombo = new JComboBox<>(cities);
        destCombo = new JComboBox<>(cities);
        
        // Date Picker logic
        SpinnerDateModel model = new SpinnerDateModel();
        dateSpinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(editor);
        dateSpinner.setPreferredSize(new Dimension(120, 30));

        JButton btnSearch = new JButton("SEARCH BUSES");
        btnSearch.setBackground(Color.ORANGE);
        btnSearch.addActionListener(e -> searchBuses());

        searchPanel.add(new JLabel("From:")); searchPanel.add(sourceCombo);
        searchPanel.add(new JLabel("To:")); searchPanel.add(destCombo);
        searchPanel.add(new JLabel("Date:")); searchPanel.add(dateSpinner);
        searchPanel.add(btnSearch);

        // Add Search Panel below Header
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(header, BorderLayout.NORTH);
        topContainer.add(searchPanel, BorderLayout.CENTER);
        add(topContainer, BorderLayout.NORTH);

        // --- TABLE ---
        // Added 'Bus Type' column
        String[] cols = {"ID", "Bus Name", "Type", "Source", "Dest", "Price", "Seats"};
        busModel = new DefaultTableModel(cols, 0);
        busTable = new JTable(busModel);
        busTable.setRowHeight(30);
        add(new JScrollPane(busTable), BorderLayout.CENTER);

        // --- BOOK BUTTON ---
        JPanel bottom = new JPanel();
        JButton btnBook = new JButton("SELECT SEAT & BOOK");
        btnBook.setBackground(new Color(0, 153, 76));
        btnBook.setForeground(Color.WHITE);
        btnBook.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBook.addActionListener(e -> openBookingDialog());
        bottom.add(btnBook);
        add(bottom, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void searchBuses() {
        busModel.setRowCount(0);
        try {
            Connection con = DBConnection.getConnection();
            String sql = "SELECT * FROM bus WHERE source=? AND destination=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, sourceCombo.getSelectedItem().toString());
            ps.setString(2, destCombo.getSelectedItem().toString());
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                busModel.addRow(new Object[]{
                    rs.getInt("bus_id"), rs.getString("bus_name"),
                    rs.getString("bus_type"), // Fetching new column
                    rs.getString("source"), rs.getString("destination"),
                    rs.getDouble("price"), rs.getInt("available_seats")
                });
            }
            con.close();
        } catch(Exception e) { e.printStackTrace(); }
    }

    private void openBookingDialog() {
        int row = busTable.getSelectedRow();
        if(row == -1) { JOptionPane.showMessageDialog(this, "Select a bus first!"); return; }
        
        int busId = (int) busModel.getValueAt(row, 0);
        String name = (String) busModel.getValueAt(row, 1);
        double price = (double) busModel.getValueAt(row, 5);
        
        // Open your existing Booking Dialog
        BookingDialog dialog = new BookingDialog(this, busId, name, price);
        dialog.setVisible(true);
    }
}