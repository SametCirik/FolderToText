package progress;

import main.Main;
import report.Report;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.SwingWorker;

/**
 * Dosya sistemi analizini arka planda yürüten ve ilerlemeyi
 * ProgressDialog'a bildiren SwingWorker.
 */
public class AnalysisTask extends SwingWorker<String, Object[]> {

    private final Main mainFrame;
    private final Path startPath;
    private final ProgressDialog progressDialog;

    public AnalysisTask(Main mainFrame, Path startPath, ProgressDialog progressDialog) {
        this.mainFrame = mainFrame;
        this.startPath = startPath;
        this.progressDialog = progressDialog;
    }

    @Override
    protected String doInBackground() throws Exception {
        // --- 1. Toplam dosya sayısını hesapla (ilerleme çubuğu için) ---
        List<Path> paths;
        try (Stream<Path> stream = Files.walk(startPath)) {
            paths = stream.filter(Files::isRegularFile).collect(Collectors.toList());
        }
        long totalFiles = paths.size();

        if (totalFiles == 0) {
            return "Seçilen klasörde analiz edilecek dosya bulunamadı.";
        }

        // --- 2. Rapor için gerekli verileri topla ---
        long processedFiles = 0;
        long totalSize = 0;
        Map<String, Long> fileTypeCounts = new TreeMap<>();
        Set<String> detectedTechnologies = new HashSet<>();
        
        for (Path path : paths) {
            // İstatistikleri topla
            try {
                totalSize += Files.size(path);
            } catch (IOException e) { /* ignore */ }
            
            String extension = getFileExtension(path.getFileName().toString());
            fileTypeCounts.put(extension, fileTypeCounts.getOrDefault(extension, 0L) + 1);

            detectTechnology(path.getFileName().toString(), detectedTechnologies);

            // İlerlemeyi yayınla
            processedFiles++;
            int percent = (int) ((processedFiles * 100.0) / totalFiles);
            String fileName = path.getFileName().toString();
            publish(new Object[]{percent, fileName});
            
            // Simülasyon için küçük bir bekleme (opsiyonel)
            Thread.sleep(5);
        }

        // --- 3. Toplanan verilerle raporu oluştur ---
        StringBuilder reportBuilder = new StringBuilder();
        reportBuilder.append("Project Overview: ").append(startPath.getFileName()).append("\n");
        reportBuilder.append("================\n\n");

        reportBuilder.append("Project Statistics:\n");
        reportBuilder.append("Total Files: ").append(totalFiles).append("\n");
        reportBuilder.append("Total Size: ").append(formatSize(totalSize)).append("\n\n");

        reportBuilder.append("File Types:\n");
        fileTypeCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
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

    @Override
    protected void process(List<Object[]> chunks) {
        Object[] lastChunk = chunks.get(chunks.size() - 1);
        int percent = (Integer) lastChunk[0];
        String fileName = (String) lastChunk[1];
        progressDialog.updateProgress(percent, fileName);
    }

    @Override
    protected void done() {
        progressDialog.close();
        try {
            String reportContent = get();
            Report reportFrame = new Report(reportContent, startPath.getFileName().toString());
            
            reportFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    mainFrame.setVisible(true);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            mainFrame.setVisible(true);
        }
    }

    // ========================================================================
    // --- YARDIMCI METOTLAR (Main sınıfından buraya taşındı) ---
    // ========================================================================

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
}