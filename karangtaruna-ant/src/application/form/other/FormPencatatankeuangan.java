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
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class FormPencatatankeuangan extends javax.swing.JPanel {


    private DefaultTableModel model;
    private int selectedId = -1;
    private List<Integer> idList = new ArrayList<>();
    
    public FormPencatatankeuangan() {
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
                + "showVerticalLines:true;" // Menambahkan properti ini
                + "intercellSpacing:0,1;"
                + "cellFocusColor:$TableHeader.hoverBackground;"
                + "selectionBackground:$TableHeader.hoverBackground;"
                + "selectionForeground:$Table.foreground;");

        
        jdibuatoeh.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Dibuat Oleh");
        jdeskripsi.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Masukan Deskripsi");
        
        initTable();
        loadJenisTransaksi();
        initActionListeners();
        loadData();
        clearForm();
    }
    
    private void initTable() {
        model = new DefaultTableModel(
            new Object [][] {},
            new String [] {
                "No", "Nama Kategori", "Jenis Transaksi", "Status", "Nominal", "Deskripsi", "Di catat oleh", "Tanggal" // Mengubah "Date" menjadi "Tanggal" agar sesuai
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

        try {
            Connection c = DatabaseConnection.getInstance().getConnection();
            Statement s = c.createStatement();
            String sql = "SELECT p.id, k.nama_kategori, k.jenis_transaksi, k.status, p.nominal, p.deskripsi, p.dibuat_oleh, p.created_at " + // Asumsi nama kolom `created_at` di tabel `pencatatan`
                         "FROM pencatatan p " +
                         "JOIN kategori k ON p.jenis_transaksi = k.nama_kategori " +
                         "ORDER BY p.id ASC";
            ResultSet r = s.executeQuery(sql);

            DecimalFormat df = new DecimalFormat("#,###.00");
            int no = 1;

            while (r.next()) {
                idList.add(r.getInt("id"));
                Object[] o = new Object[8];
                o[0] = no++;
                o[1] = r.getString("nama_kategori");
                o[2] = r.getString("jenis_transaksi");
                o[3] = r.getString("status");
                
                double nominal = r.getDouble("nominal");
                o[4] = df.format(nominal);
                
                o[5] = r.getString("deskripsi");
                o[6] = r.getString("dibuat_oleh");
                o[7] = r.getString("created_at"); 
                model.addRow(o);
            }
            r.close();
            s.close();
        } catch (SQLException e) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, "Terjadi kesalahan saat memuat data: " + e.getMessage());
        } catch (Exception e) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, "Gagal terhubung ke database: " + e.getMessage());
        }
    }
    

    
    private void loadJenisTransaksi() {
        jjenistransaksi.removeAllItems();
        jjenistransaksi.addItem("-- Pilih Jenis Transaksi --");

        try {
            Connection c = DatabaseConnection.getInstance().getConnection();
            String sql = "SELECT nama_kategori FROM kategori WHERE status = 'Aktif'";
            Statement s = c.createStatement();
            ResultSet r = s.executeQuery(sql);

            while (r.next()) {
                jjenistransaksi.addItem(r.getString("nama_kategori"));
            }
            r.close();
            s.close();
        } catch (SQLException e) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, "Terjadi kesalahan saat memuat jenis transaksi: " + e.getMessage());
        } catch (Exception e) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, "Gagal terhubung ke database: " + e.getMessage());
        }
    }
    
    private void clearForm() {
        jjenistransaksi.setSelectedIndex(0);
        jdibuatoeh.setText("");
        Jnominal.setValue(0.0d);
        jdeskripsi.setText("");
        jTable1.clearSelection();
        selectedId = -1;
        jsave.setEnabled(false);
        jdelet.setEnabled(false);
        jkirim.setEnabled(true);
    }
    
    private void initActionListeners() {
        jkirim.addActionListener(e -> {
            String jenisTransaksi = jjenistransaksi.getSelectedItem().toString();
            String dibuatOleh = jdibuatoeh.getText();
            double nominal = ((Number) Jnominal.getValue()).doubleValue();
            String deskripsi = jdeskripsi.getText();
    
            if (jenisTransaksi.equals("-- Pilih Jenis Transaksi --") || dibuatOleh.trim().isEmpty() || nominal < 0) {
                Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Jenis Transaksi, Nominal (tidak boleh negatif), dan Dibuat Oleh tidak boleh kosong!");
                return;
            }
    
            try {
                Connection c = DatabaseConnection.getInstance().getConnection();
                String sql = "INSERT INTO pencatatan (jenis_transaksi, nominal, deskripsi, dibuat_oleh) VALUES (?, ?, ?, ?)";
                PreparedStatement p = c.prepareStatement(sql);
                p.setString(1, jenisTransaksi);
                p.setDouble(2, nominal);
                p.setString(3, deskripsi);
                p.setString(4, dibuatOleh);
    
                p.executeUpdate();
                p.close();
    
                Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Data berhasil disimpan!");
                loadData();
                clearForm();
            } catch (SQLException ex) {
                Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, "Gagal menyimpan data: " + ex.getMessage());
            } catch (Exception ex) {
                Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, "Gagal terhubung ke database: " + ex.getMessage());
            }
        });
    
        jsave.addActionListener(e -> {
            if (selectedId == -1) {
                Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Pilih data yang ingin diubah terlebih dahulu.");
                return;
            }
            
            String jenisTransaksi = jjenistransaksi.getSelectedItem().toString();
            String dibuatOleh = jdibuatoeh.getText();
            double nominal = ((Number) Jnominal.getValue()).doubleValue();
            String deskripsi = jdeskripsi.getText();
            
            if (jenisTransaksi.equals("-- Pilih Jenis Transaksi --") || dibuatOleh.trim().isEmpty() || nominal < 0) {
                Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Jenis Transaksi, Nominal (tidak boleh negatif), dan Dibuat Oleh tidak boleh kosong!");
                return;
            }
    
            try {
                Connection c = DatabaseConnection.getInstance().getConnection();
                String sql = "UPDATE pencatatan SET jenis_transaksi = ?, nominal = ?, deskripsi = ?, dibuat_oleh = ? WHERE id = ?";
                PreparedStatement p = c.prepareStatement(sql);
                p.setString(1, jenisTransaksi);
                p.setDouble(2, nominal);
                p.setString(3, deskripsi);
                p.setString(4, dibuatOleh);
                p.setInt(5, selectedId);
    
                p.executeUpdate();
                p.close();
    
                Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Data berhasil diubah!");
                loadData();
                clearForm();
            } catch (SQLException ex) {
                Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, "Gagal mengubah data: " + ex.getMessage());
            } catch (Exception ex) {
                Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, "Gagal terhubung ke database: " + ex.getMessage());
            }
        });
    
        jdelet.addActionListener(e -> {
            if (selectedId == -1) {
                Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Pilih data yang ingin dihapus.");
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus data ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Connection c = DatabaseConnection.getInstance().getConnection();
                    String sql = "DELETE FROM pencatatan WHERE id = ?";
                    PreparedStatement p = c.prepareStatement(sql);
                    p.setInt(1, selectedId);
    
                    p.executeUpdate();
                    p.close();
    
                    Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Data berhasil dihapus!");
                    loadData();
                    clearForm();
                } catch (SQLException ex) {
                    Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, "Gagal menghapus data: " + ex.getMessage());
                } catch (Exception ex) {
                    Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, "Gagal terhubung ke database: " + ex.getMessage());
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
                    
                    jjenistransaksi.setSelectedItem(model.getValueAt(selectedRow, 1).toString());
                    
                    try {
                        NumberFormat nf = new DecimalFormat("#,###.00");
                        Number parsedNominal = nf.parse(model.getValueAt(selectedRow, 2).toString());
                        Jnominal.setValue(parsedNominal.doubleValue());
                    } catch (ParseException ex) {
                        Jnominal.setValue(0.0d);
                    }
                    
                    jdeskripsi.setText(model.getValueAt(selectedRow, 3).toString());
                    jdibuatoeh.setText(model.getValueAt(selectedRow, 4).toString());
                    
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

        jutama = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jsave = new javax.swing.JButton();
        jkirim = new javax.swing.JButton();
        jdelet = new javax.swing.JButton();
        jedit = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jjenistransaksi = new javax.swing.JComboBox<>();
        jdibuatoeh = new javax.swing.JTextField();
        Jnominal = new javax.swing.JSpinner();
        Jnominal = new javax.swing.JSpinner();
        jScrollPane2 = new javax.swing.JScrollPane();
        jdeskripsi = new javax.swing.JTextArea();
        lb = new javax.swing.JLabel();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "No", "Nama Kategori", "Jenis Transaksi", "Status", "Nominal", "Deskripsi", "Di catat oleh", "Date"
            }
        ));
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(5).setResizable(false);
        }

        jsave.setText("Save");

        jkirim.setText("Kirim");
        jkirim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jkirimActionPerformed(evt);
            }
        });

        jdelet.setText("Delet");

        jedit.setText("Edit");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("                    Form  Pencatatan Keuangan");

        jjenistransaksi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pemasukan", "Pengeluaran" }));

        Jnominal.setEditor(new javax.swing.JSpinner.NumberEditor(Jnominal, "#,##0.00"));

        jdeskripsi.setColumns(20);
        jdeskripsi.setRows(5);
        jScrollPane2.setViewportView(jdeskripsi);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(107, 107, 107)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jkirim)
                            .addComponent(jsave))
                        .addGap(32, 32, 32)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jdelet)
                            .addComponent(jedit)))
                    .addComponent(jjenistransaksi, 0, 182, Short.MAX_VALUE)
                    .addComponent(jdibuatoeh)
                    .addComponent(Jnominal)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(71, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jjenistransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jdibuatoeh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Jnominal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21)
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
        lb.setText("Pencatatan Keuangan");

        javax.swing.GroupLayout jutamaLayout = new javax.swing.GroupLayout(jutama);
        jutama.setLayout(jutamaLayout);
        jutamaLayout.setHorizontalGroup(
            jutamaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jutamaLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 587, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(76, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jutamaLayout.createSequentialGroup()
                .addComponent(lb, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 623, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
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

    private void jkirimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jkirimActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jkirimActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSpinner Jnominal;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton jdelet;
    private javax.swing.JTextArea jdeskripsi;
    private javax.swing.JTextField jdibuatoeh;
    private javax.swing.JButton jedit;
    private javax.swing.JComboBox<String> jjenistransaksi;
    private javax.swing.JButton jkirim;
    private javax.swing.JButton jsave;
    private javax.swing.JPanel jutama;
    private javax.swing.JLabel lb;
    // End of variables declaration//GEN-END:variables
}
