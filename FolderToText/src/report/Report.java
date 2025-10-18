package report;

import ui.CustomScrollBar; // DÜZELTME: Doğru sınıf adı import edildi
import ui.CustomTitleBar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollBar;

public class Report extends JFrame {

    // YENİ: CustomFileChooser'dan alınan merkezi renk paleti
    private static final Color COLOR_BACKGROUND = Color.decode("#000000");
    private static final Color COLOR_FOREGROUND = Color.decode("#B0B0B0");
    private static final Color COLOR_BORDER_INTERNAL = Color.decode("#4F4F4F");
    private static final Color COLOR_BORDER_OUTER = Color.decode("#AAAAAA");

    public Report(String reportContent, String folderName) {
        setUndecorated(true);
        
        // CustomTitleBar kenarlığı zaten #AAAAAA olduğu için uyumlu
        CustomTitleBar titleBar = new CustomTitleBar(this, "Report: " + folderName, false);
        add(titleBar, BorderLayout.NORTH);

        JTextArea textArea = new JTextArea(reportContent);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        textArea.setEditable(false);
        // DÜZENLEME: Renkler merkezi paletten alınıyor
        textArea.setBackground(COLOR_BACKGROUND); 
        textArea.setForeground(COLOR_FOREGROUND);
        textArea.setCaretColor(Color.WHITE);
        textArea.setBorder(new EmptyBorder(10, 15, 10, 15)); // İç boşlukları koruyoruz
        textArea.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(textArea);
        // DÜZENLEME: Ana çerçeve kenarlığını buraya taşıyoruz
        scrollPane.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, COLOR_BORDER_OUTER));

        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        // --- Custom ScrollBar'ı Uygulama ---
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUI(new CustomScrollBar()); // DÜZELTME: Doğru sınıf adı
        verticalScrollBar.setUnitIncrement(16);
        verticalScrollBar.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, COLOR_BORDER_INTERNAL));

        JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
        horizontalScrollBar.setUI(new CustomScrollBar()); // DÜZELTME: Doğru sınıf adı
        horizontalScrollBar.setUnitIncrement(16);
        horizontalScrollBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_BORDER_INTERNAL));
        
        add(scrollPane, BorderLayout.CENTER);
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}