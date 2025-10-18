package ui;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;

// JList'teki her bir satırın nasıl görüneceğini kontrol eden sınıf
public class FileCellRenderer extends DefaultListCellRenderer {

    private final Icon directoryIcon = UIManager.getIcon("FileView.directoryIcon");
    private final Icon fileIcon = UIManager.getIcon("FileView.fileIcon");

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        // Standart görünümü al, böylece seçildiğinde arka plan rengi gibi özellikler korunur
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        
        Path path = (Path) value;
        label.setText(path.getFileName().toString()); // Tam yol yerine sadece dosya adını göster

        // Klasör mü, dosya mı olduğuna göre ikonu ayarla
        if (Files.isDirectory(path)) {
            label.setIcon(directoryIcon);
        } else {
            label.setIcon(fileIcon);
        }

        // Seçim ve arkaplan renklerini temamıza uygun hale getir
        if (isSelected) {
            label.setBackground(Color.decode("#0078D7")); // Windows'un seçim rengine benzer
            label.setForeground(Color.WHITE);
        } else {
            label.setBackground(Color.decode("#2B2B2B"));
            label.setForeground(Color.decode("#A9B7C6"));
        }
        
        label.setOpaque(true);
        label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Satırlar arasına boşluk ekle

        return label;
    }
}
