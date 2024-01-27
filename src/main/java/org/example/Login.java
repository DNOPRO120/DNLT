package org.example;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login {
    private JFrame frame;
    private JPanel loginPanel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new Login().initialize();
        });
    }

    public void initialize() {
        frame = new JFrame("Management App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null);

        loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayout(3, 2, 10, 10)); // 3 hàng, 2 cột

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                char[] password = passwordField.getPassword();

                // Kiểm tra đăng nhập
                if (checkLogin(username, password)) {
                    openManagementPage();
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid username or password. Please try again.");
                }
            }
        });

        loginPanel.add(usernameLabel);
        loginPanel.add(usernameField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);
        loginPanel.add(new JLabel()); // Empty label for spacing
        loginPanel.add(loginButton);

        frame.add(loginPanel);

        frame.setVisible(true);
    }

    private boolean checkLogin(String username, char[] password) {
        // Kiểm tra đăng nhập
        return "admin".equals(username) && "admin".equals(new String(password));
    }

    private void openManagementPage() {
        JOptionPane.showMessageDialog(frame, "Login successful. Opening management page.");
        new ManagementApp().initialize();
        frame.dispose();
    }
}

