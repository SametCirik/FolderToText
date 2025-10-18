package progress;

import ui.CustomTitleBar;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.net.URL; // URL import'u eklendi
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ProgressDialog extends JDialog {

    private final JProgressBar progressBar;
    private final JLabel fileLabel;

    public ProgressDialog(JFrame owner) {
        super(owner, "Generating Report...", true);
        setUndecorated(true);
        setSize(500, 330); // Pencereyi resme yer açmak için biraz büyütelim
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // --- Başlık Çubuğu ---
        CustomTitleBar titleBar = new CustomTitleBar(this, "Analyzing...", false);
        add(titleBar, BorderLayout.NORTH);

        // --- YENİ YAPI: Arka Plan Resmi Olan Ana Panel ---
        URL imageUrl = getClass().getResource("/images/ProgressBackground.png"); // Resminizin yolu
        ImageIcon backgroundIcon = (imageUrl != null) ? new ImageIcon(imageUrl) : null;
        
        // JLabel hem resim hem de container olarak kullanılacak
        JLabel backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setLayout(new BorderLayout());
        backgroundLabel.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.decode("#AAAAAA")));
        
        // --- İlerleme bileşenlerini barındıran iç panel ---
        JPanel progressPanel = new JPanel(new BorderLayout(0, 5));
        progressPanel.setOpaque(false); // Arka plan resminin görünmesi için şeffaf yap
        progressPanel.setBorder(new EmptyBorder(10, 15, 15, 15)); // Kenar boşlukları

        // --- İlerleme Çubuğu ---
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        progressBar.setBackground(Color.decode("#1E1E1E"));
        // İstenen rengi ayarla
        progressBar.setForeground(Color.decode("#BF7C1E")); 
        progressBar.setBorder(BorderFactory.createLineBorder(Color.decode("#4F4F4F")));
        progressPanel.add(progressBar, BorderLayout.CENTER);
        
        // --- Dosya Etiketi ---
        fileLabel = new JLabel("Starting...");
        fileLabel.setForeground(Color.decode("#A9B7C6"));
        fileLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        fileLabel.setHorizontalAlignment(SwingConstants.LEFT);
        fileLabel.setPreferredSize(new Dimension(100, 25));
        progressPanel.add(fileLabel, BorderLayout.SOUTH);

        // İç paneli, arka plan resminin ALT kısmına yerleştir
        backgroundLabel.add(progressPanel, BorderLayout.SOUTH);

        add(backgroundLabel, BorderLayout.CENTER);
    }

    public void updateProgress(int percent, String fileName) {
        progressBar.setValue(percent);
        progressBar.setString(percent + "%");
        // Dosya adı çok uzunsa, başını "..." ile kısalt
        if (fileName.length() > 50) {
            fileName = "..." + fileName.substring(fileName.length() - 47);
        }
        fileLabel.setText("İşleniyor: " + fileName);
    }

    public void close() {
        setVisible(false);
        dispose();
    }
}