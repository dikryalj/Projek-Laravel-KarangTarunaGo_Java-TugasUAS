/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package application.form.other;

import com.raven.datechooser.EventDateChooser;
import com.raven.datechooser.SelectedAction;
import com.raven.datechooser.SelectedDate;
import com.raven.swing.TimePicker;
import com.raven.event.EventTimePicker;
import com.formdev.flatlaf.FlatClientProperties;
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
import javax.swing.JTextArea;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import java.util.Calendar;
import java.sql.Time;
import java.sql.Timestamp;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import java.text.ParseException;
import raven.toast.Notifications;





public class FormAgendaKegiatan extends javax.swing.JPanel {
 
    private DefaultTableModel model;
    private int selectedId = -1;
    private List<Integer> idList = new ArrayList<>();

    public FormAgendaKegiatan() {
       initComponents();
        timePicker1 = new TimePicker();
        timePicker2 = new TimePicker();


        timePicker1.setDisplayText(jwaktumulai);
        timePicker2.setDisplayText(jwaktuselesai);
        

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

        jtanggal.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Format: yyyy-MM-dd");
        jnamaagenda.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nama Agenda");
        jdeskripsi.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Deskripsi Kegiatan");
        jwaktumulai.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Format: HH:mm");
        jwaktuselesai.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Format: HH:mm");
        

        initTable();
        loadData();
        initActionListeners();
        clearForm();
    }
    
    private void initActionListeners() {
        
        timePicker1.addEventTimePicker(new com.raven.event.EventTimePicker() {
            @Override
            public void timeSelected(String time) {
                
                jwaktumulai.setText(time);
            }
        });

        
        jmulai.addActionListener(e -> {
            
            timePicker1.showPopup(this, 100, 100);
        });
        
        jselesai.addActionListener(e -> {
            timePicker2.showPopup(this, 100, 100);
        });
        
        dateChooser1.addEventDateChooser(new EventDateChooser() {
            @Override
            public void dateSelected(SelectedAction action, SelectedDate date) {
                if (action.getAction() == SelectedAction.DAY_SELECTED) {
                    dateChooser1.hidePopup();
                }
            }
        });

        jdate.addActionListener(e -> dateChooser1.showPopup());
        
        // --- CREATE (KIRIM) ---
        jkirim.addActionListener(e -> {
            String tanggalStr = jtanggal.getText();
            String waktuMulaiStr = jwaktumulai.getText();
            String waktuSelesaiStr = jwaktuselesai.getText();
            String namaAgenda = jnamaagenda.getText();
            String deskripsi = jdeskripsi.getText();

            if (tanggalStr.trim().isEmpty() || namaAgenda.trim().isEmpty() || deskripsi.trim().isEmpty()
                    || waktuMulaiStr.trim().isEmpty() || waktuSelesaiStr.trim().isEmpty()) {
                Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Semua field harus diisi!");
                return;
            }
            
            
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
            Date tanggalDate;
            try {
                tanggalDate = sdfDate.parse(tanggalStr);
            } catch (ParseException ex) {
                Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Format tanggal tidak valid. Gunakan format yyyy-MM-dd.");
                return;
            }
            
            
            Time waktuMulaiTime, waktuSelesaiTime;
            try {
                
                SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
                
                
                Date parsedWaktuMulai = sdfTime.parse(waktuMulaiStr);
                Date parsedWaktuSelesai = sdfTime.parse(waktuSelesaiStr);
                
                
                waktuMulaiTime = new Time(parsedWaktuMulai.getTime());
                waktuSelesaiTime = new Time(parsedWaktuSelesai.getTime());
            } catch (ParseException ex) {
                Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Format waktu tidak valid. Gunakan format HH:mm");
                return;
            }
            
            try {
                Connection c = DatabaseConnection.getInstance().getConnection();
                String sql = "INSERT INTO agenda (tanggal, waktu_mulai, waktu_selesai, nama_agenda, deskripsi) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement p = c.prepareStatement(sql);
                
                p.setDate(1, new java.sql.Date(tanggalDate.getTime()));
                p.setTime(2, waktuMulaiTime);
                p.setTime(3, waktuSelesaiTime);
                p.setString(4, namaAgenda);
                p.setString(5, deskripsi);
                
                p.executeUpdate();
                p.close();
                
                Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Data berhasil di simpan");
                loadData();
                clearForm();
            } catch (SQLException ex) {
                Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Gagal menyimpan data:" + ex.getMessage());
            } catch (Exception ex) {
                Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Gagal terhubung ke database:" + ex.getMessage());
            }
        });

        // --- UPDATE (SAVE) ---
        jsave.addActionListener(e -> {
            if (selectedId == -1) {
                Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Pilih data yang ingin diubah terlebih dahulu.");
                return;
            }
            
            String tanggalStr = jtanggal.getText();
            String waktuMulaiStr = jwaktumulai.getText();
            String waktuSelesaiStr = jwaktuselesai.getText();
            String namaAgenda = jnamaagenda.getText();
            String deskripsi = jdeskripsi.getText();
            
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
            Date tanggalDate;
            try {
                tanggalDate = sdfDate.parse(tanggalStr);
            } catch (ParseException ex) {
                Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Format tanggal tidak valid. Gunakan format yyyy-MM-dd.");
                return;
            }

            
            Time waktuMulaiTime, waktuSelesaiTime;
            try {
               
                SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
                
                
                Date parsedWaktuMulai = sdfTime.parse(waktuMulaiStr);
                Date parsedWaktuSelesai = sdfTime.parse(waktuSelesaiStr);
                
                
                waktuMulaiTime = new Time(parsedWaktuMulai.getTime());
                waktuSelesaiTime = new Time(parsedWaktuSelesai.getTime());
            } catch (ParseException ex) {
                Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Format waktu tidak valid. Gunakan format HH:mm.");
                return;
            }
            
            try {
                Connection c = DatabaseConnection.getInstance().getConnection();
                String sql = "UPDATE agenda SET tanggal = ?, waktu_mulai = ?, waktu_selesai = ?, nama_agenda = ?, deskripsi = ? WHERE id_agenda = ?";
                PreparedStatement p = c.prepareStatement(sql);
                
                p.setDate(1, new java.sql.Date(tanggalDate.getTime()));
                p.setTime(2, waktuMulaiTime);
                p.setTime(3, waktuSelesaiTime);
                p.setString(4, namaAgenda);
                p.setString(5, deskripsi);
                p.setInt(6, selectedId);
                
                p.executeUpdate();
                p.close();
                
                Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Data berhasil diubah!");
                loadData();
                clearForm();
            } catch (SQLException ex) {
                Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Gagal mengubah data: " + ex.getMessage());
            } catch (Exception ex) {
                Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Gagal terhubung ke database: " + ex.getMessage());
            }
        });
        
        // --- DELETE (DELET) ---
        jdelet.addActionListener(e -> {
            if (selectedId == -1) {
                Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Pilih data yang ingin dihapus.");
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus data ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Connection c = DatabaseConnection.getInstance().getConnection();
                    String sql = "DELETE FROM agenda WHERE id_agenda = ?";
                    PreparedStatement p = c.prepareStatement(sql);
                    p.setInt(1, selectedId);
                    
                    p.executeUpdate();
                    p.close();
                    
                    Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Data berhasil dihapus!");
                    loadData();
                    clearForm();
                } catch (SQLException ex) {
                    Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Gagal menghapus data: " + ex.getMessage());
                } catch (Exception ex) {
                    Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Gagal terhubung ke database: " + ex.getMessage());
                }
            }
        });
        
        // --- BATAL ---
        jedit.setText("Batal"); 
        jedit.addActionListener(e -> {
            clearForm();
        });
        
        // --- SELEKSI TABEL ---
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int selectedRow = jTable1.getSelectedRow();
                if (selectedRow != -1) {
                    selectedId = idList.get(selectedRow);
                    
                    
                    String tanggalStr = model.getValueAt(selectedRow, 1).toString();
                    String waktuMulaiStr = model.getValueAt(selectedRow, 2).toString();
                    String waktuSelesaiStr = model.getValueAt(selectedRow, 3).toString();
                    
                    try {
                        
                        jtanggal.setText(tanggalStr);
                        jwaktumulai.setText(waktuMulaiStr);
                        jwaktuselesai.setText(waktuSelesaiStr);
                        jnamaagenda.setText(model.getValueAt(selectedRow, 4).toString());
                        jdeskripsi.setText(model.getValueAt(selectedRow, 5).toString());

                        jsave.setEnabled(true);
                        jdelet.setEnabled(true);
                        jkirim.setEnabled(false);
                    } catch (Exception ex) {
                        Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Error parsing data from table: " + ex.getMessage());
                    }
                }
            }
        });
    }
    
    
    private void initTable() {
        model = new DefaultTableModel(
            new Object [][] {},
            new String [] {
                "Id", "Tanggal", "Waktu Mulai", "Waktu Selesai", "Nama Agenda", "Deskripsi", "Dibuat"
            }
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jTable1.setModel(model);
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(50);
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(150);
        jTable1.getColumnModel().getColumn(5).setPreferredWidth(200);
        jTable1.getColumnModel().getColumn(6).setPreferredWidth(120);
    }
    
    
    private void loadData() {
        model.setRowCount(0);
        idList.clear();
        
        try {
            Connection c = DatabaseConnection.getInstance().getConnection();
            String sql = "SELECT id_agenda, tanggal, waktu_mulai, waktu_selesai, nama_agenda, deskripsi, created_at FROM agenda ORDER BY tanggal, waktu_mulai ASC";
            Statement s = c.createStatement();
            ResultSet r = s.executeQuery(sql);
            
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
            SimpleDateFormat sdfCreated = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            
            while (r.next()) {
                idList.add(r.getInt("id_agenda"));
                Object[] o = new Object[7];
                o[0] = r.getInt("id_agenda");
                o[1] = sdfDate.format(r.getDate("tanggal"));
                o[2] = sdfTime.format(r.getTime("waktu_mulai"));
                o[3] = sdfTime.format(r.getTime("waktu_selesai"));
                o[4] = r.getString("nama_agenda");
                o[5] = r.getString("deskripsi");
                
                Date created_at = r.getTimestamp("created_at");
                o[6] = sdfCreated.format(created_at);

                model.addRow(o);
            }
            
            r.close();
            s.close();
        } catch (SQLException e) {
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Terjadi kesalahan saat memuat data: " + e.getMessage());
        } catch (Exception e) {
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Gagal terhubung ke database: " + e.getMessage());
        }
    }
    
    
    private void clearForm() {
        jtanggal.setText("");
        jwaktumulai.setText("");
        jwaktuselesai.setText("");
        jnamaagenda.setText("");
        jdeskripsi.setText("");
        jTable1.clearSelection();
        selectedId = -1;
        jsave.setEnabled(false);
        jdelet.setEnabled(false);
        jkirim.setEnabled(true);
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dateChooser1 = new com.raven.datechooser.DateChooser();
        timePicker1 = new com.raven.swing.TimePicker();
        timePicker2 = new com.raven.swing.TimePicker();
        jutama = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jsave = new javax.swing.JButton();
        jkirim = new javax.swing.JButton();
        jdelet = new javax.swing.JButton();
        jedit = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jnamaagenda = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jtanggal = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        jwaktumulai = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        jwaktuselesai = new javax.swing.JFormattedTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jdeskripsi = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();
        jdate = new javax.swing.JButton();
        jmulai = new javax.swing.JButton();
        jselesai = new javax.swing.JButton();
        lb = new javax.swing.JLabel();

        dateChooser1.setForeground(new java.awt.Color(102, 102, 102));
        dateChooser1.setDateFormat("yyy-MM-dd");
        dateChooser1.setTextRefernce(jtanggal);

        timePicker1.setForeground(new java.awt.Color(102, 102, 102));
        timePicker1.setToolTipText("");
        timePicker1.setDisplayText(jwaktumulai);
        timePicker1.setName(""); // NOI18N

        timePicker2.setForeground(new java.awt.Color(102, 102, 102));
        timePicker2.setDisplayText(jwaktuselesai);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "No", "Nama Agenda", "Tanggal", "Waktu Mulai", "Waktu Selesai"
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
        jLabel1.setToolTipText("");

        jLabel2.setText("Nama Agenda");

        jLabel3.setText("Tanggal");

        jtanggal.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jtanggal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtanggalActionPerformed(evt);
            }
        });

        jLabel4.setText("Waktu Mulai");

        jLabel5.setText("Waktu Selesai");

        jdeskripsi.setColumns(20);
        jdeskripsi.setRows(5);
        jScrollPane2.setViewportView(jdeskripsi);

        jLabel6.setText("Deskripsi");

        jdate.setText("...");
        jdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jdateActionPerformed(evt);
            }
        });

        jmulai.setText("...");
        jmulai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmulaiActionPerformed(evt);
            }
        });

        jselesai.setText("...");
        jselesai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jselesaiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 389, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel6)
                            .addComponent(jLabel4)
                            .addComponent(jLabel2)
                            .addComponent(jnamaagenda)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jwaktuselesai, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
                            .addComponent(jwaktumulai, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtanggal, javax.swing.GroupLayout.Alignment.LEADING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jdate, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                            .addComponent(jmulai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jselesai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jsave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                        .addComponent(jedit))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jkirim)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jdelet)))
                .addGap(84, 84, 84))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(51, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGap(3, 3, 3)
                .addComponent(jnamaagenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jdate))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jwaktumulai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jmulai))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jwaktuselesai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jselesai))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addGap(3, 3, 3)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jkirim)
                    .addComponent(jdelet))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jsave)
                    .addComponent(jedit))
                .addGap(77, 77, 77))
        );

        lb.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lb.setText("Agenda");

        javax.swing.GroupLayout jutamaLayout = new javax.swing.GroupLayout(jutama);
        jutama.setLayout(jutamaLayout);
        jutamaLayout.setHorizontalGroup(
            jutamaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jutamaLayout.createSequentialGroup()
                .addGroup(jutamaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jutamaLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 620, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lb, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jutamaLayout.setVerticalGroup(
            jutamaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jutamaLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(lb)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jutamaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 623, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(324, 324, 324))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jutama, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(244, 244, 244))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jutama, javax.swing.GroupLayout.PREFERRED_SIZE, 657, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jkirimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jkirimActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jkirimActionPerformed

    private void jtanggalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtanggalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtanggalActionPerformed

    private void jdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jdateActionPerformed
        dateChooser1.showPopup();
    }//GEN-LAST:event_jdateActionPerformed

    private void jmulaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmulaiActionPerformed
 
        timePicker1.showPopup(this, 1000, 100);
    }//GEN-LAST:event_jmulaiActionPerformed

    private void jselesaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jselesaiActionPerformed
        timePicker2.showPopup(this, 100, 100);
    }//GEN-LAST:event_jselesaiActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.raven.datechooser.DateChooser dateChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton jdate;
    private javax.swing.JButton jdelet;
    private javax.swing.JTextArea jdeskripsi;
    private javax.swing.JButton jedit;
    private javax.swing.JButton jkirim;
    private javax.swing.JButton jmulai;
    private javax.swing.JTextField jnamaagenda;
    private javax.swing.JButton jsave;
    private javax.swing.JButton jselesai;
    private javax.swing.JFormattedTextField jtanggal;
    private javax.swing.JPanel jutama;
    private javax.swing.JFormattedTextField jwaktumulai;
    private javax.swing.JFormattedTextField jwaktuselesai;
    private javax.swing.JLabel lb;
    private com.raven.swing.TimePicker timePicker1;
    private com.raven.swing.TimePicker timePicker2;
    // End of variables declaration//GEN-END:variables
}
