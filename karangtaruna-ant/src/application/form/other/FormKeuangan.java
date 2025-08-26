/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package application.form.other;

import raven.toast.Notifications;
import com.formdev.flatlaf.FlatClientProperties;
import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormKeuangan extends javax.swing.JPanel {
private DatabaseConnection dbConnection;
     private DefaultTableModel model;
    private int selectedId = -1;
    private List<Integer> idList = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", new Locale("id", "ID"));
    
    public FormKeuangan() {
        initComponents();

        initTable();
        loadData();
        initActionListeners();
        clearForm();

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
                + "showVerticalLines:true;" // Menambahkan properti ini
                + "intercellSpacing:0,1;"
                + "cellFocusColor:$TableHeader.hoverBackground;"
                + "selectionBackground:$TableHeader.hoverBackground;"
                + "selectionForeground:$Table.foreground;");

        jnamakategori.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Masukan Nama Kategori");
    }

    private void initTable() {
        model = new DefaultTableModel(
            new Object [][] {},
            new String [] {
                "No", "Nama Kategori", "Jenis Transaksi", "Status", "Dibuat"
            }
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jTable1.setModel(model);
    }

    private void loadData() {
        model.setRowCount(0);
        idList.clear();

        String sql = "SELECT id, nama_kategori, jenis_transaksi, status, created_at FROM kategori ORDER BY id ASC";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet r = p.executeQuery()) {

            int no = 1; 
            while (r.next()) {
                idList.add(r.getInt("id"));
                
                Object[] o = new Object[5]; 
                o[0] = no++; 
                o[1] = r.getString("nama_kategori");
                o[2] = r.getString("jenis_transaksi");
                o[3] = r.getString("status");
                
                Date createdAt = r.getTimestamp("created_at");
                o[4] = (createdAt != null) ? dateFormat.format(createdAt) : "N/A";

                model.addRow(o);
            }
        } catch (SQLException e) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, "Terjadi kesalahan saat memuat data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearForm() {
        jnamakategori.setText("");
        jjenistransaksi.setSelectedIndex(0);
        jstatus.setSelectedIndex(0);
        jTable1.clearSelection();
        selectedId = -1;
        jsave.setEnabled(false);
        jdelet.setEnabled(false);
        jkirim.setEnabled(true);
    }

    private void initActionListeners() {
        jkirim.addActionListener(e -> {
            String namaKategori = jnamakategori.getText().trim();
            if (namaKategori.isEmpty()) {
                Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Nama Kategori tidak boleh kosong!");
                return;
            }

            String sql = "INSERT INTO kategori (nama_kategori, jenis_transaksi, status) VALUES (?, ?, ?)";
            try (Connection c = DatabaseConnection.getConnection();
                 PreparedStatement p = c.prepareStatement(sql)) {

                p.setString(1, namaKategori);
                p.setString(2, jjenistransaksi.getSelectedItem().toString());
                p.setString(3, jstatus.getSelectedItem().toString());
                p.executeUpdate();

                Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Data berhasil disimpan!");
                loadData();
                clearForm();
            } catch (SQLException ex) {
                Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, "Gagal menyimpan data: " + ex.getMessage());
            }
        });
        
        jsave.addActionListener(e -> {
            if (selectedId == -1) {
                Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Pilih data yang ingin diubah terlebih dahulu.");
                return;
            }

            String namaKategori = jnamakategori.getText().trim();
            if (namaKategori.isEmpty()) {
                Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Nama Kategori tidak boleh kosong!");
                return;
            }
            
            String sql = "UPDATE kategori SET nama_kategori = ?, jenis_transaksi = ?, status = ? WHERE id = ?";
            try (Connection c = DatabaseConnection.getConnection();
                 PreparedStatement p = c.prepareStatement(sql)) {

                p.setString(1, namaKategori);
                p.setString(2, jjenistransaksi.getSelectedItem().toString());
                p.setString(3, jstatus.getSelectedItem().toString());
                p.setInt(4, selectedId);
                p.executeUpdate();

                Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Data berhasil diubah!");
                loadData();
                clearForm();
            } catch (SQLException ex) {
                Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, "Gagal mengubah data: " + ex.getMessage());
            }
        });

        jdelet.addActionListener(e -> {
            if (selectedId == -1) {
                Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Pilih data yang ingin dihapus.");
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus data ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String sql = "DELETE FROM kategori WHERE id = ?";
                try (Connection c = DatabaseConnection.getConnection();
                     PreparedStatement p = c.prepareStatement(sql)) {
                    p.setInt(1, selectedId);
                    p.executeUpdate();

                    Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Data berhasil dihapus!");
                    loadData();
                    clearForm();
                } catch (SQLException ex) {
                    Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, "Gagal menghapus data: " + ex.getMessage());
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
                    jnamakategori.setText(model.getValueAt(selectedRow, 1).toString()); 
                    jjenistransaksi.setSelectedItem(model.getValueAt(selectedRow, 2).toString()); 
                    jstatus.setSelectedItem(model.getValueAt(selectedRow, 3).toString()); 
                    
                    jsave.setEnabled(true);
                    jdelet.setEnabled(true);
                    jkirim.setEnabled(false);
                }
            }
        });
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jutama = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jjenistransaksi = new javax.swing.JComboBox<>();
        jstatus = new javax.swing.JComboBox<>();
        jsave = new javax.swing.JButton();
        jkirim = new javax.swing.JButton();
        jdelet = new javax.swing.JButton();
        jedit = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jnamakategori = new javax.swing.JTextField();
        lb = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(jTable2);

        jjenistransaksi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pemasukan", "Pengeluaran" }));

        jstatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Aktif", "Tidak Aktif" }));

        jsave.setText("Save");

        jkirim.setText("Kirim");

        jdelet.setText("Delet");

        jedit.setText("Edit");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("                          Form Keuangan");

        jnamakategori.setText("Nama Kategori");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(107, 107, 107)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jjenistransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jnamakategori, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jkirim)
                                .addComponent(jsave))
                            .addGap(32, 32, 32)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jdelet)
                                .addComponent(jedit)))
                        .addComponent(jstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(314, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(71, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jnamakategori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jjenistransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jkirim)
                        .addGap(27, 27, 27)
                        .addComponent(jsave))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jdelet)
                        .addGap(27, 27, 27)
                        .addComponent(jedit)))
                .addGap(109, 109, 109))
        );

        lb.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lb.setText("Kategori Keuangan");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "No", "Nama Kategori", "Jenis Transaksi", "Status", "Date"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane3.setViewportView(jTable1);

        javax.swing.GroupLayout jutamaLayout = new javax.swing.GroupLayout(jutama);
        jutama.setLayout(jutamaLayout);
        jutamaLayout.setHorizontalGroup(
            jutamaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jutamaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 628, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(76, Short.MAX_VALUE))
            .addComponent(lb, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jutamaLayout.setVerticalGroup(
            jutamaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jutamaLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(lb)
                .addGroup(jutamaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jutamaLayout.createSequentialGroup()
                        .addGap(70, 70, 70)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jutamaLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 52, Short.MAX_VALUE)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 594, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(22, 22, 22))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jutama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jutama, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JButton jdelet;
    private javax.swing.JButton jedit;
    private javax.swing.JComboBox<String> jjenistransaksi;
    private javax.swing.JButton jkirim;
    private javax.swing.JTextField jnamakategori;
    private javax.swing.JButton jsave;
    private javax.swing.JComboBox<String> jstatus;
    private javax.swing.JPanel jutama;
    private javax.swing.JLabel lb;
    // End of variables declaration//GEN-END:variables
}
