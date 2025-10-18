package main;

import report.Report;
import ui.CustomFileChooser;
import ui.CustomTitleBar;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Stream;
import javax.swing.*;

import progress.AnalysisTask;
import progress.ProgressDialog;

public class Main extends JFrame {

    // Raporu oluşturmak için kullanılacak sayaçlar ve koleksiyonlar
    private final long totalFiles = 0;
    private final long totalSize = 0;
    private final Map<String, Long> fileTypeCounts = new TreeMap<>();
    private final Set<String> detectedTechnologies = new HashSet<>();

    /**
     * Ana uygulama penceresini (JFrame) oluşturan constructor.
     */
    public Main() {
        // --- JFrame Temel Ayarları ---
        setUndecorated(true);
        // setTitle("Folder To Text Analyzer"); // CustomTitleBar tarafından yönetiliyor
        setSize(500, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Pencereyi ortala
        
        CustomTitleBar titleBar = new CustomTitleBar(this, "Folder To Text Analyzer", true);
        add(titleBar, BorderLayout.NORTH);
        
        // --- Pencere İçeriği ---
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.decode("#121212"));
        mainPanel.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.decode("#AAAAAA")));
        
        JLabel infoLabel = new JLabel("Click the button to select the project folder to analyze.", SwingConstants.CENTER);
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        infoLabel.setForeground(Color.decode("#A9B7C6"));
        
        // --- YENİ DÜZENLEME BAŞLANGICI ---

        // 1. Butonu oluştur ve stilini ayarla
        JButton selectFolderButton = new JButton("Select Project Folder...");
        selectFolderButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        selectFolderButton.setPreferredSize(new Dimension(250, 50)); // Tercih edilen boyutu ayarla
        selectFolderButton.setBackground(Color.decode("#1E1E1E"));
        selectFolderButton.setForeground(Color.decode("#B0B0B0"));
        selectFolderButton.setFocusPainted(false);
        selectFolderButton.setBorder(BorderFactory.createLineBorder(Color.decode("#4F4F4F"))); // Daha belirgin bir kenarlık

        // 2. Butonu sarmalamak için yeni bir panel oluştur (FlowLayout ile)
        JPanel buttonContainerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15)); // Dikey olarak 15 piksel boşluk
        buttonContainerPanel.setBackground(mainPanel.getBackground()); // Arka plan rengini ana panelle aynı yap
        buttonContainerPanel.add(selectFolderButton); // Butonu bu yeni panele ekle

        // --- YENİ DÜZENLEME SONU ---

        mainPanel.add(infoLabel, BorderLayout.CENTER);
        mainPanel.add(buttonContainerPanel, BorderLayout.SOUTH); // Butonu içeren paneli SOUTH'a ekle

        add(mainPanel, BorderLayout.CENTER); // mainPanel'in kendisi CENTER'da kalmalı

        // --- Buton Eylemi ---
        selectFolderButton.addActionListener(e -> {
            chooseAndProcessFolder();
        });
    }

    /**
     * Klasör seçme ve raporlama sürecini başlatan metot.
     */
    private void chooseAndProcessFolder() { 
    	CustomFileChooser fileChooser = new CustomFileChooser(this);
        File selectedFolder = fileChooser.showDialog();

        if (selectedFolder != null) {
            this.setVisible(false);
            
            ProgressDialog progressDialog = new ProgressDialog(this);
            AnalysisTask task = new AnalysisTask(this, selectedFolder.toPath(), progressDialog);
            task.execute();
            
            // Görev başladıktan hemen sonra ilerleme penceresini göster
            progressDialog.setVisible(true);
        }
    }
    
    public String generateReport(Path startPath) {
        long currentTotalFiles = 0;
        long currentTotalSize = 0;
        Map<String, Long> currentFileTypeCounts = new TreeMap<>();
        Set<String> currentDetectedTechnologies = new HashSet<>();

        try (Stream<Path> stream = Files.walk(startPath)) {
            stream.forEach(path -> {
                if (Files.isRegularFile(path)) {
                    long size = 0;
                    try {
                        size = Files.size(path);
                    } catch (IOException e) { /* ignore */ }
                    
                    currentFileTypeCounts.put(getFileExtension(path.getFileName().toString()), 
                                              currentFileTypeCounts.getOrDefault(getFileExtension(path.getFileName().toString()), 0L) + 1);
                    detectTechnology(path.getFileName().toString(), currentDetectedTechnologies);
                }
            });
            currentTotalFiles = Files.walk(startPath).filter(Files::isRegularFile).count();
            currentTotalSize = Files.walk(startPath).filter(Files::isRegularFile).mapToLong(p -> {
                try { return Files.size(p); } catch (IOException e) { return 0; }
            }).sum();

        } catch (IOException e) {
            e.printStackTrace();
            return "Hata: Klasör okunurken bir sorun oluştu.\n" + e.getMessage();
        }

        StringBuilder reportBuilder = new StringBuilder();
        reportBuilder.append("Project Overview: ").append(startPath.getFileName()).append("\n");
        reportBuilder.append("================\n\n");
        reportBuilder.append("Project Statistics:\n");
        reportBuilder.append("Total Files: ").append(currentTotalFiles).append("\n");
        reportBuilder.append("Total Size: ").append(formatSize(currentTotalSize)).append("\n\n");
        reportBuilder.append("File Types:\n");
        currentFileTypeCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(entry -> reportBuilder.append(String.format("  %s: %d files\n", entry.getKey(), entry.getValue())));
        reportBuilder.append("\n");
        
        if (!currentDetectedTechnologies.isEmpty()) {
            reportBuilder.append("Detected Technologies:\n");
            currentDetectedTechnologies.forEach(tech -> reportBuilder.append("  - ").append(tech).append("\n"));
            reportBuilder.append("\n");
        }
        reportBuilder.append("Folder Structure (Tree)\n");
        reportBuilder.append("=====================\n");
        reportBuilder.append(generateTree(startPath.toFile(), ""));
        return reportBuilder.toString();
    }
    
    private JFrame createWaitFrame() {
        JFrame waitFrame = new JFrame("Rapor Oluşturuluyor...");
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        waitFrame.add(new JLabel("Please wait, analyzing project files...", SwingConstants.CENTER));
        waitFrame.add(progressBar, BorderLayout.SOUTH);
        waitFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        waitFrame.setSize(400, 120);
        waitFrame.setLocationRelativeTo(this);
        waitFrame.setVisible(true);
        return waitFrame;
    }

    private String getFileExtension(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".");
        return (lastIndexOf == -1) ? "[no extension]" : fileName.substring(lastIndexOf);
    }
    
    private void detectTechnology(String fileName, Set<String> techSet) {
        String lowerCaseName = fileName.toLowerCase();
        if (lowerCaseName.equals("pom.xml")) techSet.add("Java (Maven)");
        if (lowerCaseName.equals("build.gradle")) techSet.add("Java (Gradle)");
        if (lowerCaseName.equals("package.json")) techSet.add("Node.js / npm");
        if (lowerCaseName.endsWith(".js") || lowerCaseName.endsWith(".jsx")) techSet.add("JavaScript");
        if (lowerCaseName.endsWith(".ts") || lowerCaseName.endsWith(".tsx")) techSet.add("TypeScript");
        if (lowerCaseName.endsWith(".tsx") || lowerCaseName.endsWith(".jsx")) techSet.add("React");
        if (lowerCaseName.endsWith(".py")) techSet.add("Python");
        if (lowerCaseName.endsWith(".csproj")) techSet.add("C# (.NET)");
        if (lowerCaseName.endsWith(".html")) techSet.add("HTML");
        if (lowerCaseName.endsWith(".css")) techSet.add("CSS");
        if (lowerCaseName.endsWith(".java")) techSet.add("Java");
        if (lowerCaseName.contains("dockerfile")) techSet.add("Docker");
    }

    private String formatSize(long size) {
        if (size <= 0) return "0 B";
        final String[] units = {"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
    
    private String generateTree(File folder, String prefix) {
        StringBuilder treeBuilder = new StringBuilder();
        File[] files = folder.listFiles();
        if (files == null) return "";
        Arrays.sort(files);
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            boolean isLast = (i == files.length - 1);
            treeBuilder.append(prefix).append(isLast ? "└── " : "├── ").append(file.getName()).append("\n");
            if (file.isDirectory()) {
                treeBuilder.append(generateTree(file, prefix + (isLast ? "    " : "│   ")));
            }
        }
        return treeBuilder.toString();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            Main mainFrame = new Main();
            mainFrame.setVisible(true);
        });
    }
}