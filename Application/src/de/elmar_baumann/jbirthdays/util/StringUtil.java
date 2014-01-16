package de.elmar_baumann.jbirthdays.util;

/**
 * @author Elmar Baumann
 */
public final class StringUtil {

    public static boolean hasContent(String s) {
        return s != null && !s.trim().isEmpty();
    }

    public static String nullToEmptyString(String s) {
        return s == null
                ? ""
                : s;
    }

    private StringUtil() {
    }
}
