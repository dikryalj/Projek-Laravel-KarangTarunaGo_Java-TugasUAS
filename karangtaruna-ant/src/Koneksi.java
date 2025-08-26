//
//
///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//
//public class Koneksi {
//    private static final String URL = "jdbc:mysql://localhost:3306/karangan_taruna_go_java";
//    private static final String USER = "root"; // username MySQL
//    private static final String PASS = "";     // password MySQL
//
//    public static Connection getConnection() {
//        try {
//            return DriverManager.getConnection(URL, USER, PASS);
//        } catch (SQLException e) {
//            System.out.println("Koneksi Gagal: " + e.getMessage());
//            return null;
//        }
//    }
//}
