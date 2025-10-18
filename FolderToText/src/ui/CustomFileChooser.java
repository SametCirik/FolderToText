package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public class CustomFileChooser extends JDialog {
	private static final Color COLOR_BACKGROUND = Color.decode("#121212");
    private static final Color COLOR_BACKGROUND_PANEL = Color.decode("#1E1E1E");
    private static final Color COLOR_FOREGROUND = Color.decode("#B0B0B0");
    private static final Color COLOR_SELECTION = Color.decode("#4F4F4F");
    private static final Color COLOR_BORDER_OUTER = Color.decode("#AAAAAA"); // Dış çerçeve için parlak renk
    private static final Color COLOR_BORDER_INTERNAL = Color.decode("#4F4F4F"); // İç ayırıcılar için yumuşak renk
    private static final Color COLOR_BUTTON_BG = Color.decode("#333333");
    private static final Color COLOR_BUTTON_HOVER_BG = Color.decode("#333333");
    
    private File selectedFile = null;
    private final JList<File> fileList;
    private final DefaultListModel<File> fileListModel;
    private final JTextField pathField;
    private final JTextField selectedFileField;
    private final FileSystemView fsv = FileSystemView.getFileSystemView();
    
    private final List<File> history = new ArrayList<>();
    private int historyIndex = -1;
    private final JButton backButton, forwardButton;
    
    public CustomFileChooser(JFrame owner) {
        super(owner, "Proje Klasörünü Seçin", true);
        setUndecorated(true);
        setSize(750, 500);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(COLOR_BACKGROUND);
        contentPanel.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, COLOR_BORDER_OUTER));

        CustomTitleBar titleBar = new CustomTitleBar(this, "Select Project Folder to Analyze", false);
        
        fileListModel = new DefaultListModel<>();
        fileList = new JList<>(fileListModel);
        pathField = new JTextField();
        selectedFileField = new JTextField();
        backButton = new JButton("←");
        forwardButton = new JButton("→");

        JPanel bottomPanel = createBottomPanel();
        JPanel rightPanel = createRightPanel();
        JPanel leftPanel = createLeftPanel();

        JSplitPane splitPane = createStyledSplitPane(leftPanel, rightPanel);
        
        contentPanel.add(splitPane, BorderLayout.CENTER);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(titleBar, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        addEventListeners();
        
        populateFiles(fsv.getDefaultDirectory(), true);
    }

    private JSplitPane createStyledSplitPane(Component left, Component right) {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        splitPane.setDividerLocation(200);
        splitPane.setBorder(null);
        splitPane.setDividerSize(4);

        if (splitPane.getUI() instanceof BasicSplitPaneUI) {
            BasicSplitPaneUI ui = (BasicSplitPaneUI) splitPane.getUI();
            ui.getDivider().setBackground(COLOR_BACKGROUND); // Ayırıcı arka planı
            ui.getDivider().setBorder(BorderFactory.createLineBorder(COLOR_BORDER_INTERNAL)); // Ayırıcı kenarlığı
        }
        return splitPane;
    }

    private JPanel createLeftPanel() {
        DefaultListModel<File> quickAccessModel = new DefaultListModel<>();
        JList<File> quickAccessList = new JList<>(quickAccessModel);

        quickAccessModel.addElement(fsv.getHomeDirectory());
        quickAccessModel.addElement(fsv.getDefaultDirectory());
        File desktop = new File(System.getProperty("user.home"), "Desktop");
        if (desktop.exists()) quickAccessModel.addElement(desktop);
        for (File root : File.listRoots()) quickAccessModel.addElement(root);

        // DÜZENLEME: Renkler paletten alınıyor
        quickAccessList.setBackground(COLOR_BACKGROUND_PANEL); 
        quickAccessList.setForeground(COLOR_FOREGROUND);
        quickAccessList.setSelectionBackground(COLOR_SELECTION);
        quickAccessList.setCellRenderer(new QuickAccessCellRenderer());
        quickAccessList.setBorder(new EmptyBorder(5, 5, 5, 5));

        quickAccessList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                File selected = quickAccessList.getSelectedValue();
                if (selected != null) populateFiles(selected, true);
            }
        });

        JScrollPane scrollPane = new JScrollPane(quickAccessList);
        scrollPane.setBorder(null);
        // DÜZELTME: Sınıf adı CustomScrollBarUI olmalı
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBar());
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER); 
        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel navigationPanel = new JPanel(new BorderLayout(5, 0));
        navigationPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER_INTERNAL), // Alt çizgi
            new EmptyBorder(5, 10, 5, 10) // İç boşluk
        ));
        navigationPanel.setBackground(COLOR_BACKGROUND_PANEL); 
        navigationPanel.setOpaque(true);

        JPanel navButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        navButtonsPanel.setOpaque(false);
        styleNavButton(backButton);
        styleNavButton(forwardButton);
        JButton upButton = new JButton("↑"); 
        styleNavButton(upButton);
        navButtonsPanel.add(backButton);
        navButtonsPanel.add(forwardButton);
        navButtonsPanel.add(upButton);

        pathField.setBackground(COLOR_BACKGROUND);
        pathField.setForeground(COLOR_FOREGROUND);
        pathField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER_INTERNAL),
            new EmptyBorder(5, 5, 5, 5)
        ));
        pathField.setCaretColor(Color.WHITE);

        navigationPanel.add(navButtonsPanel, BorderLayout.WEST);
        navigationPanel.add(pathField, BorderLayout.CENTER);
        
        upButton.addActionListener(e -> navigateUp());

        fileList.setBackground(COLOR_BACKGROUND);
        fileList.setForeground(COLOR_FOREGROUND);
        fileList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fileList.setSelectionBackground(COLOR_SELECTION);
        fileList.setCellRenderer(new FileCellRenderer());
        fileList.setBorder(new EmptyBorder(0, 10, 0, 25)); 
        
        JScrollPane scrollPane = new JScrollPane(fileList);
        scrollPane.setBorder(null);

        // --- YENİ DÜZENLEME BAŞLANGICI ---
        
        // Dikey scroll bar'ı al, UI'ı ayarla ve soluna bir çizgi ekle
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUI(new CustomScrollBar());
        verticalScrollBar.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, COLOR_BORDER_INTERNAL));

        // Yatay scroll bar'ı al, UI'ı ayarla ve üstüne bir çizgi ekle
        JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
        horizontalScrollBar.setUI(new CustomScrollBar());
        horizontalScrollBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_BORDER_INTERNAL));
        
        // --- YENİ DÜZENLEME SONU ---

        panel.add(navigationPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setOpaque(false);
        // DEĞİŞİKLİK: Hem dış kenarlık (MatteBorder) hem de iç boşluk (EmptyBorder) için CompoundBorder kullanıyoruz.
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_BORDER_INTERNAL), // Üst çizgi
            new EmptyBorder(10, 10, 10, 10) // İç boşluk
        ));

        JLabel nameLabel = new JLabel("Folder name:");
        nameLabel.setForeground(COLOR_FOREGROUND);
        
        selectedFileField.setBackground(COLOR_BACKGROUND_PANEL);
        selectedFileField.setForeground(COLOR_FOREGROUND);
        selectedFileField.setBorder(BorderFactory.createLineBorder(COLOR_BORDER_INTERNAL));
        selectedFileField.setCaretColor(Color.WHITE);

        JPanel fieldPanel = new JPanel(new BorderLayout(5, 0));
        fieldPanel.setOpaque(false);
        fieldPanel.add(nameLabel, BorderLayout.WEST);
        fieldPanel.add(selectedFileField, BorderLayout.CENTER);

        JButton selectButton = new JButton("Seç");
        JButton cancelButton = new JButton("İptal");
        styleActionButton(selectButton);
        styleActionButton(cancelButton);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(selectButton);
        buttonPanel.add(cancelButton);

        panel.add(fieldPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.EAST);

        cancelButton.addActionListener(e -> { selectedFile = null; dispose(); });
        selectButton.addActionListener(e -> selectAndClose());
        
        return panel;
    }

    private void addEventListeners() {
        backButton.addActionListener(e -> navigateBack());
        forwardButton.addActionListener(e -> navigateForward());

        pathField.addActionListener(e -> {
            File newDir = new File(pathField.getText());
            if (newDir.exists() && newDir.isDirectory()) {
                populateFiles(newDir, true);
            }
        });

        fileList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                File selected = fileList.getSelectedValue();
                if (selected == null) return;
                if (selected.isDirectory()) {
                    selectedFileField.setText(selected.getAbsolutePath());
                    if (evt.getClickCount() == 2) {
                        populateFiles(selected, true);
                    }
                }
            }
        });
    }

    private void populateFiles(File directory, boolean addToHistory) {
        if (directory == null || !directory.isDirectory()) return;

        if (addToHistory) {
            // İleri gidilmiş bir geçmiş varsa, o kısmı sil
            while (history.size() > historyIndex + 1) {
                history.remove(history.size() - 1);
            }
            history.add(directory);
            historyIndex = history.size() - 1;
        }

        pathField.setText(directory.getAbsolutePath());
        selectedFileField.setText(directory.getAbsolutePath());
        fileListModel.clear();

        File[] files = fsv.getFiles(directory, true);
        Arrays.sort(files, (f1, f2) -> {
            if (f1.isDirectory() && !f2.isDirectory()) return -1;
            if (!f1.isDirectory() && f2.isDirectory()) return 1;
            return f1.getName().compareToIgnoreCase(f2.getName());
        });
        for (File file : files) {
            fileListModel.addElement(file);
        }
        updateNavigationButtons();
    }
    
    private void updateNavigationButtons() {
        backButton.setEnabled(historyIndex > 0);
        forwardButton.setEnabled(historyIndex < history.size() - 1);
    }
    
    private void navigateBack() {
        if (historyIndex > 0) {
            historyIndex--;
            populateFiles(history.get(historyIndex), false);
        }
    }

    private void navigateForward() {
        if (historyIndex < history.size() - 1) {
            historyIndex++;
            populateFiles(history.get(historyIndex), false);
        }
    }

    private void navigateUp() {
        File parent = fsv.getParentDirectory(history.get(historyIndex));
        if (parent != null) {
            populateFiles(parent, true);
        }
    }

    private void selectAndClose() {
        File targetFile = new File(selectedFileField.getText());
        if (targetFile.exists() && targetFile.isDirectory()) {
            this.selectedFile = targetFile;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Lütfen geçerli bir klasör seçin veya yolunu girin.", "Geçersiz Klasör", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void styleNavButton(JButton button) {
        button.setPreferredSize(new Dimension(40, 30));
        button.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 16)); // Daha iyi ok ikonları için
        button.setBackground(COLOR_BACKGROUND_PANEL);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(COLOR_BORDER_INTERNAL));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) button.setBackground(COLOR_BUTTON_HOVER_BG);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(COLOR_BACKGROUND_PANEL);
            }
        });
    }
    
    private void styleActionButton(JButton button) {
        button.setPreferredSize(new Dimension(100, 30));
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(COLOR_BUTTON_BG);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(COLOR_BORDER_INTERNAL));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(COLOR_BUTTON_HOVER_BG);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(COLOR_BUTTON_BG);
            }
        });
    }

    public File showDialog() {
        setVisible(true);
        return selectedFile;
    }

    // --- İç Sınıflar (Renderers) ---
    private class FileCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof File) {
                File file = (File) value;
                label.setIcon(fsv.getSystemIcon(file));
                label.setText(fsv.getSystemDisplayName(file));
                label.setBorder(new EmptyBorder(3, 3, 3, 3));
            }
            return label;
        }
    }

    private class QuickAccessCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof File) {
                File file = (File) value;
                label.setIcon(fsv.getSystemIcon(file));
                String name = fsv.getSystemDisplayName(file);
                label.setText(name.isEmpty() ? file.getPath() : name);
                label.setBorder(new EmptyBorder(5, 5, 5, 5));
            }
            return label;
        }
    }
}