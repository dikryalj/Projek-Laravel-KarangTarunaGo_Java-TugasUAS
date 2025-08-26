package database;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import database.DatabaseConnection;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== TEST KONEKSI DATABASE ===");
        
        try {
            DatabaseConnection dbConn = DatabaseConnection.getInstance();
            Connection conn = dbConn.getConnection();
            
            if (conn != null && dbConn.testConnection()) {
                System.out.println("✅ Koneksi ke database BERHASIL!");
                System.out.println("📊 Database: ");
                System.out.println("🏠 Host: ");
                System.out.println("👤 User: ");
                System.out.println("🔗 Status: " + (conn.isClosed() ? "Closed" : "Connected"));
                
                System.out.println("\n=== MEMBUAT TABEL ===");
                boolean tablesCreated = dbConn.createTablesIfNotExist();
                if (tablesCreated) {
                    System.out.println("✅ Tabel berhasil dibuat/diverifikasi!");
                } else {
                    System.out.println("❌ Gagal membuat tabel!");
                }
                
                System.out.println("\n=== INFORMASI DATABASE ===");
                System.out.println(dbConn.getDatabaseInfo());
                
            } else {
                System.out.println("❌ Koneksi ke database GAGAL!");
                System.out.println("🔧 Pastikan MySQL server sudah running");
                System.out.println("🔧 Pastikan database 'karangan_taruna_go_java' sudah dibuat");
                System.out.println("🔧 Periksa username dan password database");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Koneksi ke database GAGAL!");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}