package ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import javax.swing.*;

public class CustomTitleBar extends JPanel {
    private Point initialClickForDrag;
    // DEĞİŞİKLİK: JFrame yerine daha genel olan Window sınıfını kullanıyoruz.
    private final Window ownerWindow; 
    private Rectangle normalBounds;
    private boolean frameIsMaximized = false;
    private final int PREFERRED_HEIGHT = 30;

    /**
     * @param owner Pencerenin sahibi (JFrame veya JDialog olabilir).
     * @param title Pencere başlığı.
     * @param isExitOnClose Kapatma düğmesi uygulamayı sonlandıracaksa true.
     */
    public CustomTitleBar(Window owner, String title, boolean isExitOnClose) {
        this.ownerWindow = owner; // DEĞİŞİKLİK
        setLayout(new BorderLayout());
        setBackground(Color.decode("#121212"));
        setPreferredSize(new Dimension(0, PREFERRED_HEIGHT));
        setBorder(BorderFactory.createCompoundBorder(
        	BorderFactory.createMatteBorder(1, 1, 0, 1, Color.decode("#AAAAAA")),
        	BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#4F4F4F"))
        ));

        // --- Sağ Kontrol Düğmeleri Paneli ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setOpaque(false);

        JButton hideButton = createTitleBarButton("_");
        // DEĞİŞİKLİK: setState Frame sınıfına ait, bu yüzden kontrol ekliyoruz.
        hideButton.addActionListener(e -> {
            if (ownerWindow instanceof Frame) {
                ((Frame) ownerWindow).setState(Frame.ICONIFIED);
            }
        });
        buttonPanel.add(hideButton);

        JButton maximizeButton = createTitleBarButton("\u25A2");
        maximizeButton.addActionListener(e -> toggleMaximize());
        buttonPanel.add(maximizeButton);

        JButton closeButton = createTitleBarButton("X");
        closeButton.setBackground(Color.decode("#C94C4C"));
        
        if (isExitOnClose) {
            closeButton.addActionListener(e -> System.exit(0));
        } else {
            // dispose() metodu hem JFrame hem JDialog'da var.
            closeButton.addActionListener(e -> ownerWindow.dispose()); 
        }
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.EAST);
        
        // --- Sol Tarafa Simge Ekleme ---
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, (PREFERRED_HEIGHT - 22) / 2));
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(buttonPanel.getPreferredSize());
        
        URL iconURL = getClass().getResource("/images/AppLogoImage.png");
        if (iconURL != null) {
            ImageIcon originalIcon = new ImageIcon(iconURL);
            Image scaledImage = originalIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            leftPanel.add(new JLabel(new ImageIcon(scaledImage)));
            ownerWindow.setIconImage(originalIcon.getImage()); // DEĞİŞİKLİK
        } else {
            System.err.println("Uygulama simgesi bulunamadı: /images/AppLogoImage.png");
        }
        add(leftPanel, BorderLayout.WEST);
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setForeground(Color.decode("#B0B0B0"));
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        add(titleLabel, BorderLayout.CENTER);

        // Sürükleme Dinleyicisi
        MouseAdapter dragListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) initialClickForDrag = e.getPoint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (initialClickForDrag != null && !frameIsMaximized) {
                    Point newScreenPoint = e.getLocationOnScreen();
                    // DEĞİŞİKLİK
                    ownerWindow.setLocation(newScreenPoint.x - initialClickForDrag.x, newScreenPoint.y - initialClickForDrag.y);
                }
            }
        };
        addMouseListener(dragListener);
        addMouseMotionListener(dragListener);
    }
    
    private void toggleMaximize() {
        if (frameIsMaximized) {
            ownerWindow.setBounds(normalBounds != null ? normalBounds : new Rectangle(100, 100, 800, 600));
        } else {
            // DEĞİŞİKLİK
            normalBounds = ownerWindow.getBounds();
            ownerWindow.setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());
        }
        frameIsMaximized = !frameIsMaximized;
    }
    
    // createTitleBarButton metodu aynı kalabilir...
    private JButton createTitleBarButton(String text) {
        JButton button = new JButton(text);
        button.setFocusable(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setBackground(this.getBackground());
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(45, PREFERRED_HEIGHT));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                if (!"X".equals(button.getText())) button.setBackground(Color.decode("#4F4F4F"));
            }
            @Override
            public void mouseExited(MouseEvent evt) {
                if (!"X".equals(button.getText())) button.setBackground(CustomTitleBar.this.getBackground());
            }
        });
        return button;
    }
}