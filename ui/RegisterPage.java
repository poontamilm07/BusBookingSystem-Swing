package ui;

import db.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RegisterPage extends JFrame {

    JTextField nameField, emailField, phoneField;
    JPasswordField passwordField;
    JComboBox<String> roleCombo; // NEW: Role Selector

    public RegisterPage() {
        setTitle("Create Account");
        setSize(900, 750); // Taller for new field
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

        // Card
        JPanel card = new JPanel();
        card.setLayout(null);
        card.setPreferredSize(new Dimension(450, 650));
        card.setBackground(Color.WHITE);

        JLabel title = new JLabel("Join Us Today", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setBounds(0, 30, 450, 40);
        card.add(title);

        int y = 90;

        // 1. Role Selector
        addLabel(card, "Register As:", y);
        roleCombo = new JComboBox<>(new String[]{"User", "Admin"});
        roleCombo.setBounds(50, y + 25, 350, 30);
        card.add(roleCombo);
        
        y += 70;

        // 2. Name
        addLabel(card, "Full Name / Username", y);
        nameField = new JTextField();
        nameField.setBounds(50, y + 25, 350, 30);
        styleField(nameField);
        card.add(nameField);

        y += 70;

        // 3. Email
        addLabel(card, "Email Address (Users Only)", y);
        emailField = new JTextField();
        emailField.setBounds(50, y + 25, 350, 30);
        styleField(emailField);
        card.add(emailField);

        y += 70;

        // 4. Phone
        addLabel(card, "Phone (Users Only)", y);
        phoneField = new JTextField();
        phoneField.setBounds(50, y + 25, 350, 30);
        styleField(phoneField);
        card.add(phoneField);

        y += 70;

        // 5. Password
        addLabel(card, "Password", y);
        passwordField = new JPasswordField();
        passwordField.setBounds(50, y + 25, 350, 30);
        styleField(passwordField);
        card.add(passwordField);

        // 6. Button
        JButton regBtn = new JButton("CREATE ACCOUNT");
        regBtn.setBounds(50, 520, 350, 45);
        regBtn.setBackground(new Color(0, 153, 76));
        regBtn.setForeground(Color.WHITE);
        regBtn.setFocusPainted(false);
        regBtn.addActionListener(e -> registerUser());
        card.add(regBtn);

        // Back Link
        JLabel loginLink = new JLabel("Back to Login");
        loginLink.setBounds(0, 580, 450, 20);
        loginLink.setHorizontalAlignment(SwingConstants.CENTER);
        loginLink.setForeground(new Color(0, 102, 204));
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { new LoginPage(); dispose(); }
        });
        card.add(loginLink);

        add(card);
        setVisible(true);
    }

    private void addLabel(JPanel p, String text, int y) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(Color.GRAY);
        l.setBounds(50, y, 350, 20);
        p.add(l);
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.LIGHT_GRAY));
    }

    private void registerUser() {
        String role = roleCombo.getSelectedItem().toString();
        String name = nameField.getText();
        String pass = new String(passwordField.getPassword());
        
        if (name.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Password are required!");
            return;
        }

        try {
            Connection con = DBConnection.getConnection();
            if (role.equals("User")) {
                // Register as User
                String sql = "INSERT INTO `user`(name, email, password, phone, role) VALUES(?,?,?,?,?)";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, name);
                ps.setString(2, emailField.getText());
                ps.setString(3, pass);
                ps.setString(4, phoneField.getText());
                ps.setString(5, "User");
                ps.executeUpdate();
            } else {
                // Register as Admin
                String sql = "INSERT INTO admin(username, password) VALUES(?,?)";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, name);
                ps.setString(2, pass);
                ps.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "Registration Successful! Please Login.");
            new LoginPage();
            dispose();
            con.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}