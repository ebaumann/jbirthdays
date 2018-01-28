package de.elmar_baumann.jbirthdays.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;

/**
 * @author Elmar Baumann
 */
public final class LookAndFeel {

    public static void scaleFonts() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();
        float scale = width >= 3840 && height >= 2160 // Ultra High Definition "4K" 3840 x 2160 pixels
                ? 2
                : width > 2560 && height > 1440  // Wide QHD (QHD) 2560 x 1440 pixels
                ? 1.5f
                : 1;

        scaleFonts(scale);
    }

    private static void scaleFonts(float scale) {
        if (scale == 1.0) {
            return;
        }
        for (Object key : UIManager.getLookAndFeelDefaults().keySet()) {
            if (key != null && key.toString().toLowerCase().contains("font")) {
                Font font = UIManager.getDefaults().getFont(key);
                if (font != null) {
                    int oldSize = font.getSize();
                    int newSize = Math.round(oldSize * scale);
                    font = font.deriveFont((float)newSize);
                    Logger.getLogger(LookAndFeel.class.getName()).log(Level.INFO, "Scaling font with scale {0}: {1}={2}", new Object[]{scale, key, font});
                    UIManager.put(key, font);
                }
            }
        }
    }

    private LookAndFeel() {
    }
}
