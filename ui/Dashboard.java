package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Dashboard extends JFrame {

    public Dashboard() {
        setTitle("Bus Booking System - Home");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // Use Nimbus for modern look
        try { UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); } catch (Exception e) {}

        // --- BACKGROUND ---
        JPanel mainPanel = new JPanel(new GridBagLayout()); // Centers everything
        mainPanel.setBackground(new Color(240, 245, 250)); // Very light blue-grey
        setContentPane(mainPanel);

        // --- CONTAINER FOR CARDS ---
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 30, 0)); // 1 Row, 3 Cols, 30px gap
        cardsPanel.setOpaque(false);
        cardsPanel.setPreferredSize(new Dimension(800, 250));

        // --- BUTTON 1: BOOK TICKET ---
        JButton btnBook = createCard("SEARCH & BOOK", "Find buses and book seats", new Color(0, 102, 204));
        btnBook.addActionListener(e -> {
            new BookingPage(); // Open Booking Page
            dispose();         // Close Dashboard
        });

        // --- BUTTON 2: MY HISTORY ---
        JButton btnHistory = createCard("MY HISTORY", "View past bookings", new Color(0, 153, 76));
        btnHistory.addActionListener(e -> {
            new HistoryPage(); // Open History Page
            dispose();
        });

        // --- BUTTON 3: LOGOUT ---
        JButton btnLogout = createCard("LOGOUT", "Sign out securely", new Color(220, 53, 69));
        btnLogout.addActionListener(e -> {
            new LoginPage();   // Go back to Login
            dispose();
        });

        cardsPanel.add(btnBook);
        cardsPanel.add(btnHistory);
        cardsPanel.add(btnLogout);

        // --- WELCOME HEADER ---
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 40, 0); // Margin bottom
        
        JLabel welcome = new JLabel("Welcome, " + (UserSession.currentUserName != null ? UserSession.currentUserName : "User"));
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 32));
        welcome.setForeground(new Color(50, 60, 80));
        mainPanel.add(welcome, gbc);

        // Add Cards
        gbc.gridy = 1;
        mainPanel.add(cardsPanel, gbc);

        setVisible(true);
    }

    // Helper to make beautiful big buttons
    private JButton createCard(String title, String subtitle, Color color) {
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout());
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Title Label
        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(color);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 5, 0));

        // Subtitle Label
        JLabel lblSub = new JLabel(subtitle, SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(Color.GRAY);
        lblSub.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        btn.add(lblTitle, BorderLayout.NORTH);
        btn.add(lblSub, BorderLayout.CENTER);

        // Hover Effect (Shadow)
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(250, 250, 255));
                btn.setBorder(BorderFactory.createLineBorder(color, 2));
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(Color.WHITE);
                btn.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
            }
        });

        return btn;
    }
}