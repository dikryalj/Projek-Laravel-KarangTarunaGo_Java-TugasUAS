/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

import java.sql.*;
import javax.swing.JOptionPane;

/**
 * Database Connectiona Manager dengan Singleton Pattern
 * @author Dzkkry
 */
public class DatabaseConnection {
    private static final String DB_HOST = "";
    private static final String DB_PORT = "";
    private static final String DB_NAME = "";
    private static final String DB_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME;
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";
    
    private static DatabaseConnection instance;
    private Connection connection;
    
    private DatabaseConnection() {
        initializeConnection();
    }

    private void initializeConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            System.out.println("✅ Database connected successfully!");
            System.out.println("📊 Database: " + DB_NAME);
            System.out.println("🏠 Host: " + DB_HOST + ":" + DB_PORT);
            System.out.println("👤 User: " + DB_USER);
            
        } catch (ClassNotFoundException e) {
            showError("MySQL JDBC Driver tidak ditemukan!", e);
            System.err.println("❌ Pastikan MySQL Connector/J sudah ada di classpath");
        } catch (SQLException e) {
            showError("Error koneksi ke database!", e);
            System.err.println("❌ Periksa konfigurasi database:");
            System.err.println("   - MySQL server running?");
            System.err.println("   - Database '" + DB_NAME + "' sudah dibuat?");
            System.err.println("   - Username/password benar?");
        }
    }
    
    /**
     * Mendapatkan instance DatabaseConnection (Singleton)
     * @return singleton instance of DatabaseConnection
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public static Connection getConnection() throws SQLException {
        DatabaseConnection dbInstance = getInstance();
        try {
            if (dbInstance.connection == null || dbInstance.connection.isClosed() || !dbInstance.connection.isValid(5)) {
                System.out.println("🔄 Reconnecting to database...");
                dbInstance.initializeConnection();
            }
        } catch (SQLException e) {
            System.err.println("❌ Error checking connection validity: " + e.getMessage());
            dbInstance.initializeConnection();
        }
        if (dbInstance.connection == null) {
            throw new SQLException("Failed to establish a database connection.");
        }
        return dbInstance.connection;
    }
    

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("🔒 Database connection closed.");
            } catch (SQLException e) {
                System.err.println("❌ Error closing database connection: " + e.getMessage());
            }
        }
    }

    public boolean isConnectionValid() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(5);
        } catch (SQLException e) {
            System.err.println("❌ Error checking connection validity: " + e.getMessage());
            return false;
        }
    }
    
    private void showError(String message, Exception e) {
        String fullMessage = message + "\nDetail: " + e.getMessage();
        System.err.println("❌ " + fullMessage);
        
        if (!java.awt.GraphicsEnvironment.isHeadless()) {
            try {
                JOptionPane.showMessageDialog(null, fullMessage, "Database Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception dialogException) {
                System.err.println("❌ Error showing dialog: " + dialogException.getMessage());
            }
        }
    }
    

    public String getDatabaseInfo() {
        try {
            Connection conn = getConnection();
            if (conn != null) {
                DatabaseMetaData metaData = conn.getMetaData();
                return String.format("Database: %s\nURL: %s\nUser: %s\nDriver: %s %s",
                    DB_NAME,
                    metaData.getURL(),
                    metaData.getUserName(),
                    metaData.getDriverName(),
                    metaData.getDriverVersion()
                );
            }
        } catch (SQLException e) {
            return "Error getting database info: " + e.getMessage();
        }
        return "No connection available";
    }
    

    public boolean createTablesIfNotExist() {
        String createAnggotaTable = """
            CREATE TABLE IF NOT EXISTS anggota (
                id INT AUTO_INCREMENT PRIMARY KEY,
                username VARCHAR(50) NOT NULL UNIQUE,
                nama_lengkap VARCHAR(100) NOT NULL,
                gender ENUM('Laki-Laki', 'Perempuan') NOT NULL,
                jabatan ENUM('Ketua', 'Wakil Ketua', 'Seketaris', 'Bendahara') NOT NULL,
                status ENUM('Aktif', 'Tidak Aktif') DEFAULT 'Aktif',
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                INDEX idx_username (username),
                INDEX idx_status (status)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
            """;
            
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createAnggotaTable);
            System.out.println("✅ Tables created/verified successfully");
            return true;
        } catch (SQLException e) {
            showError("Error creating tables!", e);
        }
        return false;
    }

    public boolean testConnection() {
    try {
        return getConnection() != null;
    } catch (SQLException e) {
        System.err.println("❌ Error testing connection: " + e.getMessage());
        return false;
    }
}
}