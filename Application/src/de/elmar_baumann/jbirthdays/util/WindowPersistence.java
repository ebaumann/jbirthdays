package de.elmar_baumann.jbirthdays.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

/**
 * @author Elmar Baumann
 */
public final class WindowPersistence {

    public static final String KEY_POSTFIX_WIDTH = ".Width";
    public static final String KEY_POSTFIX_HEIGHT = ".Height";
    public static final String KEY_POSTFIX_X = ".X";
    public static final String KEY_POSTFIX_Y = ".Y";

    public static void persistSize(Class<?> forPackageClass, String key, Component component) {
        if (forPackageClass == null) {
            throw new NullPointerException("forPackageClass == null");
        }
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        if (component == null) {
            throw new NullPointerException("component == null");
        }
        int width = component.getWidth();
        int height = component.getHeight();
        try {
            Preferences prefs = Preferences.userNodeForPackage(forPackageClass);
            prefs.putInt(key + KEY_POSTFIX_WIDTH, width);
            prefs.putInt(key + KEY_POSTFIX_HEIGHT, height);
        } catch (Throwable throwable) {
            Logger.getLogger(WindowPersistence.class.getName()).log(Level.SEVERE, null, throwable);
        }
    }

    public static void persistLocation(Class<?> forPackageClass, String key, Component component) {
        if (forPackageClass == null) {
            throw new NullPointerException("forPackageClass == null");
        }
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        if (component == null) {
            throw new NullPointerException("component == null");
        }
        Point location = component.getLocation();
        try {
            Preferences prefs = Preferences.userNodeForPackage(forPackageClass);
            prefs.putInt(key + KEY_POSTFIX_X, location.x);
            prefs.putInt(key + KEY_POSTFIX_Y, location.y);
        } catch (Throwable throwable) {
            Logger.getLogger(WindowPersistence.class.getName()).log(Level.SEVERE, null, throwable);
        }
    }

    public static boolean restoreSize(Class<?> forPackageClass, String key, Component component) {
        if (forPackageClass == null) {
            throw new NullPointerException("forPackageClass == null");
        }
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        if (component == null) {
            throw new NullPointerException("component == null");
        }
        try {
            Preferences prefs = Preferences.userNodeForPackage(forPackageClass);
            int width = prefs.getInt(key + KEY_POSTFIX_WIDTH, -1);
            int height = prefs.getInt(key + KEY_POSTFIX_HEIGHT, -1);
            Dimension preferredSize = component.getPreferredSize();
            if (width >= preferredSize.width && height >= preferredSize.height) {
                component.setSize(width, height);
                return true;
            }
        } catch (Throwable throwable) {
            Logger.getLogger(WindowPersistence.class.getName()).log(Level.SEVERE, null, throwable);
        }
        return false;
    }

    public static boolean restoreLocation(Class<?> forPackageClass, String key, Component component) {
        if (forPackageClass == null) {
            throw new NullPointerException("forPackageClass == null");
        }
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        if (component == null) {
            throw new NullPointerException("component == null");
        }
        try {
            Preferences prefs = Preferences.userNodeForPackage(forPackageClass);
            int x = prefs.getInt(key + KEY_POSTFIX_X, -1);
            int y = prefs.getInt(key + KEY_POSTFIX_Y, -1);
            if ((x >= 0) && (y >= 0)) {
                component.setLocation(x, y);
                return true;
            }
        } catch (Throwable throwable) {
            Logger.getLogger(WindowPersistence.class.getName()).log(Level.SEVERE, null, throwable);
        }
        return false;
    }

    private WindowPersistence() {
    }
}
