package main;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.*;

public class FolderToText {

    // Raporu oluşturmak için kullanılacak sayaçlar ve koleksiyonlar
    private long totalFiles = 0;
    private long totalSize = 0;
    private final Map<String, Long> fileTypeCounts = new TreeMap<>(); // Alfabetik sıralama için TreeMap
    private final Set<String> detectedTechnologies = new HashSet<>();

    public static void main(String[] args) {
        // İşletim sisteminin doğal görünümünü kullan
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // GUI thread'inde çalıştır
        SwingUtilities.invokeLater(() -> {
            new FolderToText().createAndShowGUI();
        });
    }

    private void createAndShowGUI() {
        // Klasör seçme penceresi oluştur
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Analiz Edilecek Proje Klasörünü Seçin");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);

        // Pencereyi göster ve kullanıcının seçimini al
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = fileChooser.getSelectedFile();
            // Raporu oluştur ve göster
            String report = generateReport(selectedFolder.toPath());
            showReport(report, selectedFolder.getName());
        } else {
            // Kullanıcı bir klasör seçmediyse veya iptal ettiyse programı sonlandır.
            System.exit(0);
        }
    }
    
    // Ana rapor oluşturma metodu
    public String generateReport(Path startPath) {
        // Her yeni rapor oluşturulduğunda sayaçları sıfırla
        totalFiles = 0;
        totalSize = 0;
        fileTypeCounts.clear();
        detectedTechnologies.clear();

        try (Stream<Path> stream = Files.walk(startPath)) {
            List<Path> paths = stream.collect(Collectors.toList());
            
            for(Path path : paths) {
                if (Files.isRegularFile(path)) {
                    // İstatistikleri topla
                    totalFiles++;
                    try {
                        totalSize += Files.size(path);
                    } catch (IOException e) {
                        // Dosya okunamazsa görmezden gel
                    }
                    
                    // Dosya uzantısını al ve say
                    String extension = getFileExtension(path.getFileName().toString());
                    fileTypeCounts.put(extension, fileTypeCounts.getOrDefault(extension, 0L) + 1);

                    // Teknolojileri tespit et
                    detectTechnology(path.getFileName().toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Hata: Klasör okunurken bir sorun oluştu.\n" + e.getMessage();
        }

        // Rapor metnini oluşturmak için StringBuilder kullan
        StringBuilder reportBuilder = new StringBuilder();

        reportBuilder.append("Project Overview: ").append(startPath.getFileName()).append("\n");
        reportBuilder.append("================\n\n");

        reportBuilder.append("Project Statistics:\n");
        reportBuilder.append("Total Files: ").append(totalFiles).append("\n");
        reportBuilder.append("Total Size: ").append(formatSize(totalSize)).append("\n\n");

        reportBuilder.append("File Types:\n");
        fileTypeCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed()) // Değere göre tersten sırala
                .forEach(entry -> reportBuilder.append(String.format("  %s: %d files\n", entry.getKey(), entry.getValue())));
        reportBuilder.append("\n");
        
        if (!detectedTechnologies.isEmpty()) {
            reportBuilder.append("Detected Technologies:\n");
            detectedTechnologies.forEach(tech -> reportBuilder.append("  - ").append(tech).append("\n"));
            reportBuilder.append("\n");
        }

        reportBuilder.append("Folder Structure (Tree)\n");
        reportBuilder.append("=====================\n");
        reportBuilder.append(generateTree(startPath.toFile(), ""));
        
        return reportBuilder.toString();
    }
    
    // Dosya uzantısını bulan yardımcı metot
    private String getFileExtension(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "[no extension]"; // uzantısız dosyalar
        }
        return fileName.substring(lastIndexOf);
    }
    
    // Dosya adına göre teknoloji tespiti yapan basit bir metot
    private void detectTechnology(String fileName) {
        String lowerCaseName = fileName.toLowerCase();
        if (lowerCaseName.equals("pom.xml")) detectedTechnologies.add("Java (Maven)");
        if (lowerCaseName.equals("build.gradle")) detectedTechnologies.add("Java (Gradle)");
        if (lowerCaseName.equals("package.json")) detectedTechnologies.add("Node.js / npm");
        if (lowerCaseName.endsWith(".js") || lowerCaseName.endsWith(".jsx")) detectedTechnologies.add("JavaScript");
        if (lowerCaseName.endsWith(".ts") || lowerCaseName.endsWith(".tsx")) detectedTechnologies.add("TypeScript");
        if (lowerCaseName.endsWith(".tsx") || lowerCaseName.endsWith(".jsx")) detectedTechnologies.add("React");
        if (lowerCaseName.endsWith(".py")) detectedTechnologies.add("Python");
        if (lowerCaseName.endsWith(".csproj")) detectedTechnologies.add("C# (.NET)");
        if (lowerCaseName.endsWith(".html")) detectedTechnologies.add("HTML");
        if (lowerCaseName.endsWith(".css")) detectedTechnologies.add("CSS");
        if (lowerCaseName.endsWith(".java")) detectedTechnologies.add("Java");
        if (lowerCaseName.contains("dockerfile")) detectedTechnologies.add("Docker");
    }

    // Byte cinsinden boyutu KB, MB, GB olarak formatlayan metot
    private String formatSize(long size) {
        if (size <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
    
    // Klasör yapısını ağaç formatında oluşturan recursive metot
    private String generateTree(File folder, String prefix) {
        StringBuilder treeBuilder = new StringBuilder();
        File[] files = folder.listFiles();
        if (files == null) return "";

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            boolean isLast = (i == files.length - 1);
            
            treeBuilder.append(prefix);
            treeBuilder.append(isLast ? "└── " : "├── ");
            treeBuilder.append(file.getName()).append("\n");

            if (file.isDirectory()) {
                String newPrefix = prefix + (isLast ? "    " : "│   ");
                treeBuilder.append(generateTree(file, newPrefix));
            }
        }
        return treeBuilder.toString();
    }
    
    // Sonucu bir pencerede göstermek için
    private void showReport(String report, String folderName) {
        // Raporu göstermek için yeni bir çerçeve (JFrame) oluştur
        JFrame reportFrame = new JFrame("Rapor: " + folderName);
        reportFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Pencere kapatıldığında program sonlansın

        JTextArea textArea = new JTextArea(report);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);
        textArea.setCaretPosition(0); // Metnin en başına odaklan

        JScrollPane scrollPane = new JScrollPane(textArea);

        reportFrame.add(scrollPane);
        reportFrame.setSize(800, 600);
        reportFrame.setLocationRelativeTo(null); // Pencereyi ekranın ortasında aç
        reportFrame.setVisible(true);
    }
}