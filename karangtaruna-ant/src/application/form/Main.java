
package application.form;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import application.form.other.FormAgendaKegiatan;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Form Agenda Kegiatan");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new FormAgendaKegiatan());
            frame.pack();
            frame.setLocationRelativeTo(null); 
            frame.setVisible(true);
        });
    }
}