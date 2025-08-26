package application.form;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Color;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import application.Application;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import raven.toast.Notifications;
import org.mindrot.jbcrypt.BCrypt;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.swing.SwingUtilities;


public class PanelLogin extends JPanel {

    private static final Logger LOGGER = Logger.getLogger(PanelLogin.class.getName());
    

    private static final String EMAIL_REGEX = 
        "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin;


    public PanelLogin() {
        initComponents();
        setLayout(new MigLayout("fillx,wrap,insets 30 40 50 40, width 320", "[fill]", "[]20[][]15[][]30[]"));
        putClientProperty(FlatClientProperties.STYLE, ""
                + "background:$Login.background;"
                + "arc:20;");
    }


    private void initComponents() {
        JLabel lblTitle = new JLabel("Login");
        lblTitle.putClientProperty(FlatClientProperties.STYLE, "font:$h1.font");

        JLabel lblEmail = new JLabel("Email:");
        txtEmail = new JTextField();
        txtEmail.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your email");

        JLabel lblPassword = new JLabel("Password:");
        txtPassword = new JPasswordField();
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your password");
        

        JPanel passwordPanel = new JPanel(new MigLayout("insets 0, fill", "[grow][]", "[]"));
        JButton btnShowPassword = new JButton("👁");
        btnShowPassword.setToolTipText("Show/Hide Password");
        btnShowPassword.addActionListener(e -> {
            if (txtPassword.getEchoChar() == 0) {
                txtPassword.setEchoChar('*');
                btnShowPassword.setText("👁");
            } else {
                txtPassword.setEchoChar((char) 0);
                btnShowPassword.setText("👁");
            }
        });
        
        passwordPanel.add(txtPassword, "grow");
        passwordPanel.add(btnShowPassword, "w 30!, h 30!");

        btnLogin = new JButton("Login");
btnLogin.setBackground(new Color(45, 45, 45));    
btnLogin.setForeground(new Color(255, 255, 255)); 
        btnLogin.addActionListener(evt -> btnLoginActionPerformed(evt));
        

        txtEmail.addActionListener(evt -> txtPassword.requestFocus());
        txtPassword.addActionListener(evt -> btnLogin.doClick());

        add(lblTitle, "gapy 10");
        add(lblEmail);
        add(txtEmail);
        add(lblPassword);
        add(passwordPanel);  
        add(btnLogin, "gapy 20");
    }


    private boolean validateInput(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            showNotification(Notifications.Type.WARNING, "Email tidak boleh kosong.");
            txtEmail.requestFocus();
            return false;
        }

        if (password == null || password.isEmpty()) {
            showNotification(Notifications.Type.WARNING, "Password tidak boleh kosong.");
            txtPassword.requestFocus();
            return false;
        }

        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            showNotification(Notifications.Type.WARNING, "Format email tidak valid!");
            txtEmail.requestFocus();
            txtEmail.selectAll();
            return false;
        }

        if (password.length() < 3) { 
            showNotification(Notifications.Type.WARNING, "Password terlalu pendek!");
            txtPassword.requestFocus();
            txtPassword.selectAll();
            return false;
        }

        return true;
    }

 
    private void showNotification(Notifications.Type type, String message) {
        SwingUtilities.invokeLater(() -> {
            Notifications.getInstance().show(type, Notifications.Location.TOP_CENTER, message);
        });
    }

    private boolean authenticateUser(String email, String password) {
        final String sql = "SELECT id, email, nama, role, password FROM users WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, email.trim().toLowerCase()); 
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("password");
                    
                    
                    if (BCrypt.checkpw(password, hashedPassword)) {
                        
                        int userId = rs.getInt("id");
                        String userEmail = rs.getString("email");
                        String userNama = rs.getString("nama");
                        String userRole = rs.getString("role");

                        UserSession.setUser(userId, userEmail, userNama, userRole);
                        
                        SwingUtilities.invokeLater(() -> {
                            showNotification(Notifications.Type.SUCCESS, 
                                "Login berhasil! Selamat datang " + userNama);
                            Application.login();
                        });
                        
                        LOGGER.info("User logged in successfully: " + userEmail);
                        return true;
                    } else {
                        LOGGER.warning("Failed login attempt for email: " + email + " - Invalid password");
                    }
                } else {
                    LOGGER.warning("Failed login attempt for email: " + email + " - User not found or inactive");
                }
            }
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Database error during login: " + ex.getMessage(), ex);
            SwingUtilities.invokeLater(() -> {
                showNotification(Notifications.Type.ERROR, "Terjadi kesalahan sistem. Silakan coba lagi.");
            });
            return false;
        }
        
        
        SwingUtilities.invokeLater(() -> {
            showNotification(Notifications.Type.WARNING, "Login gagal! Email atau password salah.");
        });
        return false;
    }


    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {

        btnLogin.setEnabled(false);
        btnLogin.setText("Logging in...");
        
        try {
            String email = txtEmail.getText();
            String password = new String(txtPassword.getPassword());

            
            if (!validateInput(email, password)) {
                return;
            }

            
            SwingUtilities.invokeLater(() -> {
                new Thread(() -> {
                    boolean success = authenticateUser(email, password);
                    
                    SwingUtilities.invokeLater(() -> {
                        if (!success) {
                            
                            txtPassword.setText("");
                            txtPassword.requestFocus();
                        }
                        
                        btnLogin.setEnabled(true);
                        btnLogin.setText("Login");
                    });
                }).start();
            });
            
        } finally {
            
            txtPassword.setText("");
        }
    }
}