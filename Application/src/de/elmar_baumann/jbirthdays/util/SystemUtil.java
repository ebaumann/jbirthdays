package de.elmar_baumann.jbirthdays.util;

/**
 * @author Elmar Baumann
 */
public final class SystemUtil {

    public static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("windows");
    }

    public static boolean isLinux() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("linux");
    }

    public static boolean isMac() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("mac");
    }

    private SystemUtil() {
    }
}
