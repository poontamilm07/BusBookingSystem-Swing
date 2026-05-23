package ui;

import db.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingDialog extends JDialog {

    private int busId;
    private double basePrice;
    private int selectedSeat = -1; // -1 means no seat selected
    private JLabel priceLabel;
    private JCheckBox foodCheck, snackCheck;
    private boolean bookingSuccess = false;

    public BookingDialog(JFrame parent, int busId, String busName, double price) {
        super(parent, "Select Seat - " + busName, true);
        this.busId = busId;
        this.basePrice = price;

        setSize(600, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // --- 1. HEADER ---
        JLabel title = new JLabel("Select your Seat for " + busName);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        // --- 2. SEAT MAP (The Visual Grid) ---
        JPanel seatPanel = new JPanel(new GridLayout(4, 10, 10, 10)); // 4 rows, 10 cols
        seatPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Get list of already booked seats from DB
        List<Integer> bookedSeats = getBookedSeats();

        ButtonGroup group = new ButtonGroup(); // Ensures only 1 seat is selected

        for (int i = 1; i <= 40; i++) {
            JToggleButton seatBtn = new JToggleButton(String.valueOf(i));
            seatBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            
            if (bookedSeats.contains(i)) {
                // SEAT IS TAKEN
                seatBtn.setBackground(new Color(255, 102, 102)); // Light Red
                seatBtn.setEnabled(false);
                seatBtn.setText("X");
            } else {
                // SEAT IS FREE
                seatBtn.setBackground(new Color(144, 238, 144)); // Light Green
                int seatNum = i;
                seatBtn.addActionListener(e -> {
                    selectedSeat = seatNum;
                    updatePrice();
                });
                group.add(seatBtn);
            }
            seatPanel.add(seatBtn);
        }
        add(seatPanel, BorderLayout.CENTER);

        // --- 3. BOTTOM PANEL (Add-ons & Pay) ---
        JPanel bottomPanel = new JPanel(new GridLayout(3, 1));
        
        // Add-ons
        JPanel addonPanel = new JPanel(new FlowLayout());
        foodCheck = new JCheckBox("Veg Meals (+₹150)");
        snackCheck = new JCheckBox("Snacks (+₹50)");
        foodCheck.addActionListener(e -> updatePrice());
        snackCheck.addActionListener(e -> updatePrice());
        addonPanel.add(foodCheck);
        addonPanel.add(snackCheck);
        
        // Price Display
        priceLabel = new JLabel("Total Price: ₹" + basePrice);
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        priceLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Pay Button
        JButton payBtn = new JButton("Confirm & Pay");
        payBtn.setBackground(new Color(0, 102, 204));
        payBtn.setForeground(Color.WHITE);
        payBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        payBtn.addActionListener(e -> processBooking());

        bottomPanel.add(addonPanel);
        bottomPanel.add(priceLabel);
        bottomPanel.add(payBtn);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private List<Integer> getBookedSeats() {
        List<Integer> list = new ArrayList<>();
        try {
            Connection con = DBConnection.getConnection();
            String sql = "SELECT seat_no FROM booking WHERE bus_id = ? AND status = 'Booked'";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, busId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rs.getInt("seat_no"));
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private void updatePrice() {
        double total = basePrice;
        if (foodCheck.isSelected()) total += 150;
        if (snackCheck.isSelected()) total += 50;
        priceLabel.setText("Total Price: ₹" + total);
    }

    private void processBooking() {
        if (selectedSeat == -1) {
            JOptionPane.showMessageDialog(this, "Please click on a Green seat to select it!");
            return;
        }

        try {
            Connection con = DBConnection.getConnection();
            String sql = "INSERT INTO booking(user_id, bus_id, seat_no, booking_date, status) VALUES(?,?,?,?,?)";
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, UserSession.currentUserId);
            ps.setInt(2, busId);
            ps.setInt(3, selectedSeat);
            ps.setDate(4, Date.valueOf(LocalDate.now()));
            ps.setString(5, "Booked");

            if (ps.executeUpdate() > 0) {
                // Update available seats count
                PreparedStatement psUp = con.prepareStatement("UPDATE bus SET available_seats = available_seats - 1 WHERE bus_id=?");
                psUp.setInt(1, busId);
                psUp.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Booking Confirmed for Seat #" + selectedSeat);
                bookingSuccess = true;
                dispose(); // Close Dialog
            }
            con.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    public boolean isBookingSuccess() {
        return bookingSuccess;
    }
}