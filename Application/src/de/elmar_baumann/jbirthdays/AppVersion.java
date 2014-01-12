package de.elmar_baumann.jbirthdays;

import java.util.ResourceBundle;

/**
 * @author Elmar Baumann
 */
public final class AppVersion {

    public static final String VERSION = ResourceBundle.getBundle("de/elmar_baumann/jbirthdays/AppVersion").getString("Version");
    public static final String DATE  =ResourceBundle.getBundle("de/elmar_baumann/jbirthdays/AppVersion").getString("Date");

    private AppVersion() {
    }
}
