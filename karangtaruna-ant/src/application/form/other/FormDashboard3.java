package application.form.other;


import com.formdev.flatlaf.FlatClientProperties;
import raven.toast.Notifications;
import com.formdev.flatlaf.FlatLaf;
import database.DatabaseConnection;
import java.sql.*;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
import java.util.Calendar;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import java.awt.Color;
import javax.swing.JPanel;


public class FormDashboard3 extends javax.swing.JPanel {

   
    
    public FormDashboard3() {

 initComponents();
mypan.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        
        boolean isDarkTheme = FlatLaf.isLafDark();

        
        String fontColorForTheme = isDarkTheme
                ? "foreground:$Label.foreground" 
                : "foreground:#000000"; 

        
        String panelBackground = isDarkTheme
                ? "lighten($Panel.background,5%)"
                : "darken($Panel.background,2%)";

       
        lb.putClientProperty(FlatClientProperties.STYLE, ""
                + "font:$h1.font;"
                + fontColorForTheme);

        
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

        jkategorikeuangan.getTableHeader().putClientProperty(FlatClientProperties.STYLE, ""
                + "height:30;"
                + "hoverBackground:null;"
                + "pressedBackground:null;"
                + "separatorColor:$TableHeader.background;"
                + "font:bold;");
        jkategorikeuangan.putClientProperty(FlatClientProperties.STYLE, ""
                + "rowHeight:70;"
                + "showHorizontalLines:true;"
                + "showVerticalLines:true;"
                + "intercellSpacing:0,1;"
                + "cellFocusColor:$TableHeader.hoverBackground;"
                + "selectionBackground:$TableHeader.hoverBackground;"
                + "selectionForeground:$Table.foreground;");

        // ===== STYLING JPANEL DENGAN BORDER RADIUS DAN THEME-AWARE =====

        if (jPanel1 != null) {
            jPanel1.putClientProperty(FlatClientProperties.STYLE, ""
                    + "arc:12;"
                    + "background:" + panelBackground + ";");
        }

        if (jPanel2 != null) {
            jPanel2.putClientProperty(FlatClientProperties.STYLE, ""
                    + "arc:12;"
                    + "background:" + panelBackground + ";");
        }

        if (jPanel3 != null) {
            jPanel3.putClientProperty(FlatClientProperties.STYLE, ""
                    + "arc:12;"
                    + "background:" + panelBackground + ";");
        }


        if (jPanel5 != null) {
            jPanel5.putClientProperty(FlatClientProperties.STYLE, ""
                    + "arc:12;"
                    + "background:" + panelBackground + ";");
        }

        if (jPanel6 != null) {
            jPanel6.putClientProperty(FlatClientProperties.STYLE, ""
                    + "arc:12;"
                    + "background:" + panelBackground + ";");
        }

        if (jPanel7 != null) {
            jPanel7.putClientProperty(FlatClientProperties.STYLE, ""
                    + "arc:12;"
                    + "background:" + panelBackground + ";");
        }

        if (jPanel18 != null) {
            jPanel18.putClientProperty(FlatClientProperties.STYLE, ""
                    + "arc:12;"
                    + "background:" + panelBackground + ";");
            
        }

        // ===== STYLING SEMUA JLABEL DENGAN THEME-AWARE FONT COLOR =====

        JLabel[] allLabels = {
            jLabel1, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6, jLabel7, jLabel8, jLabel9, jLabel10,
            jLabel15, jLabel16, jLabel17, jLabel18, jLabel19, jLabel20, jpemasukan, jpengeluaran,jsaldo, janggotaaktif, jagendabulan, jpemasukanbulanan, jpengeluaranbulanan
        };

        for (JLabel label : allLabels) {
            if (label != null) {
                String currentStyle = (String) label.getClientProperty(FlatClientProperties.STYLE);
                if (currentStyle != null && !currentStyle.isEmpty()) {
                    String newStyle = currentStyle.replaceAll("foreground:[^;]*;?", "") + ";" + fontColorForTheme;
                    label.putClientProperty(FlatClientProperties.STYLE, newStyle);
                } else {
                    label.putClientProperty(FlatClientProperties.STYLE, fontColorForTheme);
                }
            }
        }

        updateAllComponentsForTheme(fontColorForTheme, panelBackground);

        loadDashboardData();
    }

    private void updateAllComponentsForTheme(String fontColorStyle, String panelBackground) {
        JLabel[] allLabels = {
            jLabel1, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6, jLabel7, jLabel8, jLabel9, jLabel10,
            jLabel15, jLabel16, jLabel17, jLabel18, jLabel19, jLabel20
        };

        for (JLabel label : allLabels) {
            if (label != null) {
                updateLabelStyle(label, fontColorStyle);
            }
        }
    }

    private void updateLabelStyle(JLabel label, String fontColorStyle) {
        String currentStyle = (String) label.getClientProperty(FlatClientProperties.STYLE);
        if (currentStyle != null && !currentStyle.isEmpty()) {
            String newStyle = currentStyle.replaceAll("foreground:[^;]*;?", "") + ";" + fontColorStyle;
            label.putClientProperty(FlatClientProperties.STYLE, newStyle);
        } else {
            label.putClientProperty(FlatClientProperties.STYLE, fontColorStyle);
        }
    }


    private void loadKeuanganData() throws SQLException {
        try (Connection con = DatabaseConnection.getConnection()) {
            String sqlTotals = "SELECT "
                    + "COALESCE(SUM(CASE WHEN k.jenis_transaksi = 'Pemasukan' THEN p.nominal ELSE 0 END), 0) AS total_pemasukan, "
                    + "COALESCE(SUM(CASE WHEN k.jenis_transaksi = 'Pengeluaran' THEN p.nominal ELSE 0 END), 0) AS total_pengeluaran "
                    + "FROM pencatatan p "
                    + "JOIN kategori k ON p.jenis_transaksi = k.nama_kategori"; 
            try (PreparedStatement ps = con.prepareStatement(sqlTotals); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double totalPemasukan = rs.getDouble("total_pemasukan");
                    double totalPengeluaran = rs.getDouble("total_pengeluaran");
                    double saldo = totalPemasukan - totalPengeluaran;

                    jpemasukan.setText(formatRupiah(totalPemasukan));
                    jpengeluaran.setText(formatRupiah(totalPengeluaran));
                    jsaldo.setText(formatRupiah(saldo));
                }
            }

            Calendar cal = Calendar.getInstance();
            int month = cal.get(Calendar.MONTH) + 1;
            int year = cal.get(Calendar.YEAR);

            String sqlMonthly = "SELECT "
                    + "COALESCE(SUM(CASE WHEN k.jenis_transaksi = 'Pemasukan' THEN p.nominal ELSE 0 END), 0) AS pemasukan_bulanan, "
                    + "COALESCE(SUM(CASE WHEN k.jenis_transaksi = 'Pengeluaran' THEN p.nominal ELSE 0 END), 0) AS pengeluaran_bulanan "
                    + "FROM pencatatan p "
                    + "JOIN kategori k ON p.jenis_transaksi = k.nama_kategori " 
                    + "WHERE MONTH(p.created_at) = ? AND YEAR(p.created_at) = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlMonthly)) {
                ps.setInt(1, month);
                ps.setInt(2, year);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        double pemasukanBulanan = rs.getDouble("pemasukan_bulanan");
                        double pengeluaranBulanan = rs.getDouble("pengeluaran_bulanan");
                        jpemasukanbulanan.setText(formatRupiah(pemasukanBulanan));
                        jpengeluaranbulanan.setText(formatRupiah(pengeluaranBulanan));
                    }
                }
            }
        }
    }

    private void loadDashboardData() {
    try {
        loadKeuanganData();
        loadAnggotaData(); 
        loadAgendaData();


        loadTableAnggota();
        loadTableKategori();
    } catch (SQLException e) {
        e.printStackTrace();
        Notifications.getInstance().show(Notifications.Type.ERROR, "Gagal memuat data dari database.");
    }
}
    
private void loadAnggotaData() throws SQLException {
    try (Connection con = DatabaseConnection.getConnection()) {
        System.out.println("Berhasil terhubung ke database.");
        String sqlAnggotaAktif = "SELECT COUNT(*) FROM anggota WHERE status = 'Aktiv'";
        try (PreparedStatement ps = con.prepareStatement(sqlAnggotaAktif); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int jumlah = rs.getInt(1);
                System.out.println("Jumlah anggota aktif ditemukan: " + jumlah);
                janggotaaktif.setText(String.valueOf(jumlah));
            } else {
                System.out.println("Tidak ada hasil dari kueri.");
            }
        }
    } catch (SQLException e) {
        System.err.println("SQL Exception: " + e.getMessage());
        e.printStackTrace();
    }
}

    private void loadAgendaData() throws SQLException {
        try (Connection con = DatabaseConnection.getConnection()) {
            Calendar cal = Calendar.getInstance();
            int month = cal.get(Calendar.MONTH) + 1;
            int year = cal.get(Calendar.YEAR);
            String sqlAgendaBulanan = "SELECT COUNT(*) FROM agenda WHERE MONTH(tanggal) = ? AND YEAR(tanggal) = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlAgendaBulanan)) {
                ps.setInt(1, month);
                ps.setInt(2, year);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        jagendabulan.setText(String.valueOf(rs.getInt(1)));
                    }
                }
            }
        }
    }

    private void loadTableAnggota() throws SQLException {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
        String sql = "SELECT nama_lengkap, jabatan, gender, status FROM anggota";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("nama_lengkap"));
                row.add(rs.getString("jabatan"));
                row.add(rs.getString("gender"));
                row.add(rs.getString("status"));
                model.addRow(row);
            }
        }
    }

    private void loadTableKategori() throws SQLException {
        DefaultTableModel model = (DefaultTableModel) jkategorikeuangan.getModel();
        model.setRowCount(0); 
        
        String sql = "SELECT nama_kategori, jenis_transaksi FROM kategori";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("nama_kategori"));
                row.add(rs.getString("jenis_transaksi"));
                model.addRow(row);
            }
        }
    }

    private String formatRupiah(double number) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return formatter.format(number);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane3 = new javax.swing.JScrollPane();
        mypan = new javax.swing.JPanel();
        lb = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jpemasukan = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        janggotaaktif = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jpengeluaran = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jagendabulan = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jsaldo = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jpemasukanbulanan = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jpengeluaranbulanan = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jkategorikeuangan = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();

        lb.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lb.setText("Dashboard");

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));
        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(204, 204, 204));
        jLabel1.setText("Total Pemasukan");

        jpemasukan.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jpemasukan.setForeground(new java.awt.Color(255, 255, 255));
        jpemasukan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jpemasukan.setText("RP. 2");
        jpemasukan.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(204, 204, 204));
        jLabel3.setText("Total Pemasukan Keseluruhan");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addContainerGap(44, Short.MAX_VALUE))
            .addComponent(jpemasukan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpemasukan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3))
        );

        jPanel2.setBackground(new java.awt.Color(51, 51, 51));
        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(204, 204, 204));
        jLabel5.setText("Anggota Aktif");

        janggotaaktif.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        janggotaaktif.setForeground(new java.awt.Color(255, 255, 255));
        janggotaaktif.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        janggotaaktif.setText("RP. 2");
        janggotaaktif.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(204, 204, 204));
        jLabel6.setText("Jumlah Anggota Aktif");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel6)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(janggotaaktif, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(janggotaaktif, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6))
        );

        jPanel3.setBackground(new java.awt.Color(51, 51, 51));
        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(204, 204, 204));
        jLabel7.setText("Total Pengeluaran");

        jpengeluaran.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jpengeluaran.setForeground(new java.awt.Color(255, 255, 255));
        jpengeluaran.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jpengeluaran.setText("RP. 2");
        jpengeluaran.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(204, 204, 204));
        jLabel8.setText("Total Pengeluaran Keseluruhan");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel7)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel8)
                .addContainerGap(44, Short.MAX_VALUE))
            .addComponent(jpengeluaran, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpengeluaran, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8))
        );

        jPanel5.setBackground(new java.awt.Color(51, 51, 51));
        jPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(204, 204, 204));
        jLabel9.setText("Agenda Bulan Ini");

        jagendabulan.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jagendabulan.setForeground(new java.awt.Color(255, 255, 255));
        jagendabulan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jagendabulan.setText("RP. 2");
        jagendabulan.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(204, 204, 204));
        jLabel10.setText("Agenda Bulan Ini");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel9)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel10)
                .addContainerGap(138, Short.MAX_VALUE))
            .addComponent(jagendabulan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jagendabulan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel10))
        );

        jPanel6.setBackground(new java.awt.Color(51, 51, 51));
        jPanel6.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(204, 204, 204));
        jLabel15.setText("Saldo");

        jsaldo.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jsaldo.setForeground(new java.awt.Color(255, 255, 255));
        jsaldo.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jsaldo.setText("RP. 2");
        jsaldo.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(204, 204, 204));
        jLabel16.setText("Surplus");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel15)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel16)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jsaldo, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jsaldo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel16))
        );

        jPanel7.setBackground(new java.awt.Color(51, 51, 51));
        jPanel7.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(204, 204, 204));
        jLabel17.setText("Pemasukan Bulan Ini");

        jpemasukanbulanan.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jpemasukanbulanan.setForeground(new java.awt.Color(255, 255, 255));
        jpemasukanbulanan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jpemasukanbulanan.setText("RP. 2");
        jpemasukanbulanan.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(204, 204, 204));
        jLabel18.setText("Total Pemasukan Bulan Ini");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jLabel17)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jLabel18)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jpemasukanbulanan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpemasukanbulanan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel18))
        );

        jPanel18.setBackground(new java.awt.Color(51, 51, 51));
        jPanel18.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(204, 204, 204));
        jLabel19.setText("Pengeluaran Bulan Ini");

        jpengeluaranbulanan.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jpengeluaranbulanan.setForeground(new java.awt.Color(255, 255, 255));
        jpengeluaranbulanan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jpengeluaranbulanan.setText("RP. 2");
        jpengeluaranbulanan.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(204, 204, 204));
        jLabel20.setText("Total Pengeluaran Bulan Ini");

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addComponent(jLabel19)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addComponent(jLabel20)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jpengeluaranbulanan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpengeluaranbulanan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel20))
        );

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Nama Lengkap", "Jabatan", "Gender", "Status"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("Daftar Anggota Terbaru");

        jkategorikeuangan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Nama Kategori", "Jenis"
            }
        ));
        jScrollPane2.setViewportView(jkategorikeuangan);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setText("Kategori Keuangan");

        javax.swing.GroupLayout mypanLayout = new javax.swing.GroupLayout(mypan);
        mypan.setLayout(mypanLayout);
        mypanLayout.setHorizontalGroup(
            mypanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mypanLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lb, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(mypanLayout.createSequentialGroup()
                .addGap(120, 120, 120)
                .addGroup(mypanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(mypanLayout.createSequentialGroup()
                        .addGroup(mypanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(mypanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1)
                    .addComponent(jScrollPane2))
                .addGap(0, 16, Short.MAX_VALUE))
        );
        mypanLayout.setVerticalGroup(
            mypanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mypanLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lb)
                .addGap(36, 36, 36)
                .addGroup(mypanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(mypanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(mypanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(248, Short.MAX_VALUE))
        );

        jScrollPane3.setViewportView(mypan);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1029, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1316, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel jagendabulan;
    private javax.swing.JLabel janggotaaktif;
    private javax.swing.JTable jkategorikeuangan;
    private javax.swing.JLabel jpemasukan;
    private javax.swing.JLabel jpemasukanbulanan;
    private javax.swing.JLabel jpengeluaran;
    private javax.swing.JLabel jpengeluaranbulanan;
    private javax.swing.JLabel jsaldo;
    private javax.swing.JLabel lb;
    private javax.swing.JPanel mypan;
    // End of variables declaration//GEN-END:variables
}
