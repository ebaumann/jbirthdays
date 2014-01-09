package de.elmar_baumann.jbirthdays.util;

import java.awt.Image;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

/**
 * @author Elmar Baumann
 */
public final class IconUtil {

    public static Image getIconImage(String path) {
        if (path == null) {
            throw new NullPointerException("path == null");
        }
        java.net.URL imgURL = IconUtil.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL).getImage();
        } else {
            Logger.getLogger(IconUtil.class.getName()).log(Level.SEVERE, null, "Image path not found: " + path);
        }
        return null;
    }

    private IconUtil() {
    }
}
