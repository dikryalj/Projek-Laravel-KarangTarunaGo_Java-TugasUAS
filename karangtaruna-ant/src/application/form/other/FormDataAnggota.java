/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package application.form.other;



import com.formdev.flatlaf.FlatClientProperties;
import raven.toast.Notifications;
import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.mindrot.jbcrypt.BCrypt;

public class FormDataAnggota extends javax.swing.JPanel {

  private DefaultTableModel model;
    private int selectedId = -1;
    private List<Integer> idList = new ArrayList<>();

    public FormDataAnggota() {
        initComponents();
        lb.putClientProperty(FlatClientProperties.STYLE, ""
                + "font:$h1.font");
        jTable1.getTableHeader().putClientProperty(FlatClientProperties.STYLE, ""
                + "height:30;"
                + "hoverBackground:null;"
                + "pressedBackground:null;"
                + "separatorColor:$TableHeader.background;"
                + "font:bold;");

        jTable1.putClientProperty(FlatClientProperties.STYLE, ""
                + "rowHeight:70;"
                + "showHorizontalLines:true;"
                + "showVerticalLines:true;"
                + "intercellSpacing:0,1;"
                + "cellFocusColor:$TableHeader.hoverBackground;"
                + "selectionBackground:$TableHeader.hoverBackground;"
                + "selectionForeground:$Table.foreground;");
        
        jusername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Username");
        jnamalengkap.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nama Lengkap");
        jPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Password");
        
        initTable();
        loadData();
        initActionListeners();
        clearForm();
    }
    
    private void initTable() {
        model = new DefaultTableModel(
            new Object [][] {},
            new String [] {
                "Id", "Username", "Nama Lengkap", "Gender", "Jabatan", "Status", "Dibuat"
            }
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jTable1.setModel(model);
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(50);
    }
    
    private void loadData() {
        model.setRowCount(0);
        idList.clear();
        
        try (Connection c = DatabaseConnection.getInstance().getConnection()) {
            String sql = "SELECT id, username, nama_lengkap, gender, jabatan, status, dibuat FROM anggota ORDER BY id ASC";
            try (Statement s = c.createStatement();
                 ResultSet r = s.executeQuery(sql)) {
                
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                
                while (r.next()) {
                    idList.add(r.getInt("id"));
                    Object[] o = new Object[7];
                    o[0] = r.getInt("id");
                    o[1] = r.getString("username");
                    o[2] = r.getString("nama_lengkap");
                    o[3] = r.getString("gender");
                    o[4] = r.getString("jabatan");
                    o[5] = r.getString("status");
                    
                    Date tanggalDibuat = r.getTimestamp("dibuat");
                    o[6] = sdf.format(tanggalDibuat);

                    model.addRow(o);
                }
            }
        } catch (SQLException e) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, "Terjadi kesalahan saat memuat data: " + e.getMessage());
        }
    }
    
    private void clearForm() {
        jusername.setText("");
        jnamalengkap.setText("");
        jPassword.setText("");
        jgender.setSelectedIndex(0);
        jjabatan.setSelectedIndex(0);
        jrole.setSelectedIndex(0);
        jaktiv.setSelectedIndex(0);
        jPassword.setEnabled(true);
        jrole.setEnabled(true);
        jaktiv.setEnabled(true);
        jTable1.clearSelection();
        selectedId = -1;
        jsave.setEnabled(false);
        jdelet.setEnabled(false);
        jkirim.setEnabled(true);
    }
    
    private void initActionListeners() {
        jkirim.addActionListener(e -> {
            String username = jusername.getText();
            String namaLengkap = jnamalengkap.getText();
            String password = new String(jPassword.getPassword());
            String gender = jgender.getSelectedItem().toString();
            String jabatan = jjabatan.getSelectedItem().toString();
            String status = jaktiv.getSelectedItem().toString();
            String role = jrole.getSelectedItem().toString();

            if (username.trim().isEmpty() || namaLengkap.trim().isEmpty() || password.isEmpty()) {
                Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Username, Nama Lengkap, dan Password tidak boleh kosong!");
                return;
            }
            
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            Connection c = null;
            try {
                c = DatabaseConnection.getInstance().getConnection();
                c.setAutoCommit(false);

                String sqlAnggota = "INSERT INTO anggota (username, nama_lengkap, gender, jabatan, status) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement pAnggota = c.prepareStatement(sqlAnggota)) {
                    pAnggota.setString(1, username);
                    pAnggota.setString(2, namaLengkap);
                    pAnggota.setString(3, gender);
                    pAnggota.setString(4, jabatan);
                    pAnggota.setString(5, status);
                    pAnggota.executeUpdate();
                }

                String sqlUsers = "INSERT INTO users (email, username, nama, password, role) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement pUser = c.prepareStatement(sqlUsers)) {
                    pUser.setString(1, username);
                    pUser.setString(2, username);
                    pUser.setString(3, namaLengkap);
                    pUser.setString(4, hashedPassword);
                    pUser.setString(5, role);
                    
                    pUser.executeUpdate();
                    System.out.println("Data user berhasil disimpan.");
                }
                c.commit();
                Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Data berhasil disimpan di tabel Anggota dan User!");
                loadData();
                clearForm();
                
            } catch (SQLException ex) {
                try {
                    if (c != null) {
                        c.rollback();
                    }
                } catch (SQLException rollbackEx) {
                    System.err.println("Gagal melakukan rollback: " + rollbackEx.getMessage());
                }
                Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, "Gagal menyimpan data: " + ex.getMessage());
            } finally {
                if (c != null) {
                    try {
                        c.close();
                    } catch (SQLException closeEx) {
                        System.err.println("Gagal menutup koneksi: " + closeEx.getMessage());
                    }
                }
            }
        });

        jsave.addActionListener(e -> {
            if (selectedId == -1) {
                Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Pilih data yang ingin diubah terlebih dahulu.");
                return;
            }
            
            String username = jusername.getText();
            String namaLengkap = jnamalengkap.getText();
            String gender = jgender.getSelectedItem().toString();
            String jabatan = jjabatan.getSelectedItem().toString();
            String status = jaktiv.getSelectedItem().toString();
            String password = new String(jPassword.getPassword());
            String role = jrole.getSelectedItem().toString();
            
            Connection c = null;
            try {
                c = DatabaseConnection.getInstance().getConnection();
                c.setAutoCommit(false);

                String oldUsername = model.getValueAt(jTable1.getSelectedRow(), 1).toString();

                String sqlAnggota = "UPDATE anggota SET username = ?, nama_lengkap = ?, gender = ?, jabatan = ?, status = ? WHERE id = ?";
                try (PreparedStatement pAnggota = c.prepareStatement(sqlAnggota)) {
                    pAnggota.setString(1, username);
                    pAnggota.setString(2, namaLengkap);
                    pAnggota.setString(3, gender);
                    pAnggota.setString(4, jabatan);
                    pAnggota.setString(5, status);
                    pAnggota.setInt(6, selectedId);
                    pAnggota.executeUpdate();
                }
                
                String sqlUser = "UPDATE users SET email = ?, username = ?, nama = ?, role = ? WHERE username = ?";
                try (PreparedStatement pUser = c.prepareStatement(sqlUser)) {
                    pUser.setString(1, username);
                    pUser.setString(2, username);
                    pUser.setString(3, namaLengkap);
                    pUser.setString(4, role);
                    pUser.setString(5, oldUsername);
                    pUser.executeUpdate();
                }

                c.commit();
                Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Data berhasil diubah!");
                loadData();
                clearForm();
            } catch (SQLException ex) {
                try {
                    if (c != null) {
                        c.rollback();
                    }
                } catch (SQLException rollbackEx) {
                    System.err.println("Gagal melakukan rollback: " + rollbackEx.getMessage());
                }
                Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, "Gagal mengubah data: " + ex.getMessage());
            } finally {
                if (c != null) {
                    try {
                        c.close();
                    } catch (SQLException closeEx) {
                        System.err.println("Gagal menutup koneksi: " + closeEx.getMessage());
                    }
                }
            }
        });
        
        jdelet.addActionListener(e -> {
            if (selectedId == -1) {
                Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Pilih data yang ingin dihapus.");
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus data ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Connection c = null;
                try {
                    c = DatabaseConnection.getInstance().getConnection();
                    c.setAutoCommit(false);

                    String username = model.getValueAt(jTable1.getSelectedRow(), 1).toString();

                    String sqlAnggota = "DELETE FROM anggota WHERE id = ?";
                    try (PreparedStatement pAnggota = c.prepareStatement(sqlAnggota)) {
                        pAnggota.setInt(1, selectedId);
                        pAnggota.executeUpdate();
                    }

                    String sqlUser = "DELETE FROM users WHERE username = ?";
                    try (PreparedStatement pUser = c.prepareStatement(sqlUser)) {
                        pUser.setString(1, username);
                        pUser.executeUpdate();
                    }
                    
                    c.commit();
                    Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Data berhasil dihapus!");
                    loadData();
                    clearForm();
                } catch (SQLException ex) {
                    try {
                        if (c != null) {
                            c.rollback();
                        }
                    } catch (SQLException rollbackEx) {
                        System.err.println("Gagal melakukan rollback: " + rollbackEx.getMessage());
                    }
                    Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, "Gagal menghapus data: " + ex.getMessage());
                } finally {
                    if (c != null) {
                        try {
                            c.close();
                        } catch (SQLException closeEx) {
                            System.err.println("Gagal menutup koneksi: " + closeEx.getMessage());
                        }
                    }
                }
            }
        });
        
        jedit.setText("Batal");
        jedit.addActionListener(e -> {
            clearForm();
        });
        
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int selectedRow = jTable1.getSelectedRow();
                if (selectedRow != -1) {
                    selectedId = idList.get(selectedRow);
                    
                    jusername.setText(model.getValueAt(selectedRow, 1).toString());
                    jnamalengkap.setText(model.getValueAt(selectedRow, 2).toString());
                    jgender.setSelectedItem(model.getValueAt(selectedRow, 3).toString());
                    jjabatan.setSelectedItem(model.getValueAt(selectedRow, 4).toString());
                    jaktiv.setSelectedItem(model.getValueAt(selectedRow, 5).toString());
                    
                    String roleFromDb = getRoleFromUsername(model.getValueAt(selectedRow, 1).toString());
                    if (roleFromDb != null) {
                        jrole.setSelectedItem(roleFromDb);
                    }
                    
                    jPassword.setEnabled(false);
                    jrole.setEnabled(false);
                    jaktiv.setEnabled(true);
                    
                    jsave.setEnabled(true);
                    jdelet.setEnabled(true);
                    jkirim.setEnabled(false);
                }
            }
        });
    }

    private String getRoleFromUsername(String username) {
        String role = null;
        String sql = "SELECT role FROM users WHERE username = ?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, username);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) {
                    role = r.getString("role");
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil role: " + e.getMessage());
        }
        return role;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jutama = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jsave = new javax.swing.JButton();
        jkirim = new javax.swing.JButton();
        jdelet = new javax.swing.JButton();
        jedit = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jgender = new javax.swing.JComboBox<>();
        jusername = new javax.swing.JTextField();
        jnamalengkap = new javax.swing.JTextField();
        jjabatan = new javax.swing.JComboBox<>();
        jaktiv = new javax.swing.JComboBox<>();
        jrole = new javax.swing.JComboBox<>();
        jPassword = new javax.swing.JPasswordField();
        lb = new javax.swing.JLabel();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "No", "Username", "Nama Lengkap", "Gender", "Jabatan", "Status", "Dibuat"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jsave.setText("Save");

        jkirim.setText("Kirim");
        jkirim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jkirimActionPerformed(evt);
            }
        });

        jdelet.setText("Delet");

        jedit.setText("Edit");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setText("             Form  Data Anggota");

        jgender.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Laki-laki", "Perempuan" }));

        jjabatan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Ketua", "Wakil Ketua", "Bendahara", "Seketaris" }));

        jaktiv.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Aktiv", "Tidak Aktiv" }));

        jrole.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Admin", "User" }));

        jPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPasswordActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(107, 107, 107)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jsave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                        .addComponent(jedit))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jkirim)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jdelet))
                    .addComponent(jgender, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jusername)
                    .addComponent(jnamalengkap)
                    .addComponent(jjabatan, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jaktiv, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jrole, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPassword))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(51, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(jusername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jnamalengkap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jaktiv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(jgender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jjabatan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jrole, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jkirim)
                    .addComponent(jdelet))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jsave)
                    .addComponent(jedit))
                .addGap(123, 123, 123))
        );

        lb.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lb.setText("Data Anggota");

        javax.swing.GroupLayout jutamaLayout = new javax.swing.GroupLayout(jutama);
        jutama.setLayout(jutamaLayout);
        jutamaLayout.setHorizontalGroup(
            jutamaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jutamaLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 661, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(44, 44, 44))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jutamaLayout.createSequentialGroup()
                .addComponent(lb, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jutamaLayout.setVerticalGroup(
            jutamaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jutamaLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(lb)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
                .addGroup(jutamaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 623, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jutama, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jutama, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jkirimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jkirimActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jkirimActionPerformed

    private void jPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPasswordActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jPasswordActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPasswordField jPassword;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JComboBox<String> jaktiv;
    private javax.swing.JButton jdelet;
    private javax.swing.JButton jedit;
    private javax.swing.JComboBox<String> jgender;
    private javax.swing.JComboBox<String> jjabatan;
    private javax.swing.JButton jkirim;
    private javax.swing.JTextField jnamalengkap;
    private javax.swing.JComboBox<String> jrole;
    private javax.swing.JButton jsave;
    private javax.swing.JTextField jusername;
    private javax.swing.JPanel jutama;
    private javax.swing.JLabel lb;
    // End of variables declaration//GEN-END:variables
}
