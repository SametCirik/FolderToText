package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class CustomScrollBar extends BasicScrollBarUI {

    private final int SCROLL_BAR_WIDTH = 16;
    private final Color TRACK_COLOR = Color.decode("#2B2B2B");
    private final Color THUMB_COLOR = Color.decode("#555555");
    private final Color THUMB_HOVER_COLOR = Color.decode("#888888");

    // Üst ve Alt Ok Düğmelerini Kaldırır
    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }

    private JButton createZeroButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0));
        button.setMinimumSize(new Dimension(0, 0));
        button.setMaximumSize(new Dimension(0, 0));
        return button;
    }

    // Scrollbar'ın arkasındaki yolu (track) çizer
    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        g.setColor(TRACK_COLOR);
        g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
    }

    // Kaydırıcının kendisini (thumb) çizer
    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        // Kenarları yumuşatmak (anti-aliasing) için Graphics2D kullan
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fare thumb'ın üzerindeyse rengi değiştir
        if (isThumbRollover()) {
            g2.setColor(THUMB_HOVER_COLOR);
        } else {
            g2.setColor(THUMB_COLOR);
        }
        
        // Yuvarlak kenarlı bir dörtgen çiz
        g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2, thumbBounds.width - 4, thumbBounds.height - 4, 10, 10);
        g2.dispose();
    }

    // Scrollbar'ın genişliğini ayarlar
    @Override
    public Dimension getPreferredSize(JComponent c) {
        return new Dimension(SCROLL_BAR_WIDTH, super.getPreferredSize(c).height);
    }
}