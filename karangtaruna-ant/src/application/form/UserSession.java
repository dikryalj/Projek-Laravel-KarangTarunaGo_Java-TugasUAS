/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package application.form;

/**
 *
 * @author Dzkkry
 */
public class UserSession {
     private static int id;
    private static String email;
    private static String nama;
    private static String role;

    public static void setUser(int userId, String userEmail, String userNama, String userRole) {
        id = userId;
        email = userEmail;
        nama = userNama;
        role = userRole;
    }

    public static int getId() {
        return id;
    }

    public static String getEmail() {
        return email;
    }

    public static String getNama() {
        return nama;
    }

    public static String getRole() {
        return role;
    }

    public static void clearSession() {
        id = 0;
        email = null;
        nama = null;
        role = null;
    }
}
