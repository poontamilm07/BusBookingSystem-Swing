package ui;

import db.DBConnection;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginPage extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JCheckBox showPassword;
    private JComboBox<String> roleCombo; // NEW: Role Selector

    public LoginPage() {
        setTitle("Bus Booking System - Secure Login");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // Background
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(51, 153, 255), 0, getHeight(), new Color(0, 51, 102));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setLayout(new GridBagLayout());
        setContentPane(backgroundPanel);

        // --- CARD ---
        JPanel card = new JPanel();
        card.setLayout(null);
        card.setPreferredSize(new Dimension(420, 560)); // Made taller for Role selector
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(0,0,0,50), 1));

        // Header
        JLabel iconLabel = new JLabel("🚌"); 
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 50));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setBounds(0, 20, 420, 60);
        card.add(iconLabel);

        JLabel title = new JLabel("Welcome Back");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBounds(0, 80, 420, 40);
        card.add(title);

        // --- FORM ---
        int y = 140;

        // 1. Role Selector (NEW)
        JLabel lblRole = new JLabel("Login As");
        lblRole.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblRole.setForeground(Color.GRAY);
        lblRole.setBounds(50, y, 320, 20);
        card.add(lblRole);

        roleCombo = new JComboBox<>(new String[]{"User", "Admin"});
        roleCombo.setBounds(50, y + 25, 320, 35);
        roleCombo.setBackground(Color.WHITE);
        card.add(roleCombo);

        y += 70;

        // 2. Email/Username Field
        JLabel lblEmail = new JLabel("Email / Username");
        lblEmail.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblEmail.setForeground(Color.GRAY);
        lblEmail.setBounds(50, y, 320, 20);
        card.add(lblEmail);

        emailField = new JTextField();
        emailField.setBounds(50, y + 25, 320, 35);
        styleField(emailField);
        card.add(emailField);

        y += 70;

        // 3. Password Field
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPass.setForeground(Color.GRAY);
        lblPass.setBounds(50, y, 320, 20);
        card.add(lblPass);

        passwordField = new JPasswordField();
        passwordField.setBounds(50, y + 25, 320, 35);
        styleField(passwordField);
        card.add(passwordField);

        // Show Password
        showPassword = new JCheckBox("Show Password");
        showPassword.setBounds(48, y + 65, 150, 20);
        showPassword.setBackground(Color.WHITE);
        showPassword.addActionListener(e -> {
            if (showPassword.isSelected()) passwordField.setEchoChar((char) 0);
            else passwordField.setEchoChar('•');
        });
        card.add(showPassword);

        // 4. Login Button
        JButton loginBtn = new JButton("LOGIN");
        loginBtn.setBounds(50, y + 100, 320, 45);
        loginBtn.setBackground(new Color(0, 102, 204));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setFocusPainted(false);
        loginBtn.addActionListener(e -> loginUser());
        card.add(loginBtn);

        // 5. Register Link
        JLabel registerLink = new JLabel("Create New Account");
        registerLink.setBounds(0, y + 160, 420, 20);
        registerLink.setHorizontalAlignment(SwingConstants.CENTER);
        registerLink.setFont(new Font("Segoe UI", Font.BOLD, 13));
        registerLink.setForeground(new Color(0, 102, 204));
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new RegisterPage();
                dispose();
            }
        });
        card.add(registerLink);

        add(card);
        setVisible(true);
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.LIGHT_GRAY));
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { field.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(0, 102, 204))); }
            public void focusLost(FocusEvent e) { field.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.LIGHT_GRAY)); }
        });
    }

    private void loginUser() {
        String input = emailField.getText();
        String pass = new String(passwordField.getPassword());
        String role = roleCombo.getSelectedItem().toString();

        if (input.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields");
            return;
        }

        try {
            Connection con = DBConnection.getConnection();
            
            if (role.equals("User")) {
                // --- USER LOGIN ---
                String sql = "SELECT * FROM `user` WHERE email=? AND password=?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, input);
                ps.setString(2, pass);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    UserSession.currentUserId = rs.getInt("user_id");
                    UserSession.currentUserName = rs.getString("name");
                    new Dashboard(); // Open User Dashboard
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid User Credentials");
                }
            } else {
                // --- ADMIN LOGIN ---
                String sql = "SELECT * FROM admin WHERE username=? AND password=?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, input);
                ps.setString(2, pass);
                if (ps.executeQuery().next()) {
                    new AdminDashboard(); // Open Admin Dashboard
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Admin Credentials");
                }
            }
            con.close();
        } catch (Exception ex) { ex.printStackTrace(); }
    }
    
    public static void main(String[] args) {
        new LoginPage();
    }
}