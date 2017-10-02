package de.elmar_baumann.jbirthdays.update;

import de.elmar_baumann.jbirthdays.AppVersion;
import de.elmar_baumann.jbirthdays.util.Bundle;
import de.elmar_baumann.jbirthdays.util.Version;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.YES_OPTION;

/**
 * @author Elmar Baumann
 */
public final class UpdateDownload extends Thread {

    private static final String KEY_CHECK_FOR_UPDATES = "UpdateDownload.CheckForUpdates";
    private static final String DOWNLOAD_PAGE = "http://elmar-baumann.de/JBirthdays/download.html";
    private static final String URL_VERSION_CHECK_FILE = "http://elmar-baumann.de/JBirthdays/jbirthdays-version.txt";
    private static final String VERSION_DELIMITER = ".";
    private static volatile boolean checkPending;
    private final Version currentVersion = Version.parseVersion(AppVersion.VERSION, VERSION_DELIMITER);
    private Version netVersion = currentVersion;

    public UpdateDownload() {
        super("JBirthdays: Checking for and downloading newer version");
    }

    public static boolean isCheckPending() {
        synchronized (UpdateDownload.class) {
            return checkPending;
        }
    }

    public static void checkForNewerVersion() {
        synchronized (UpdateDownload.class) {
            if (checkPending) {
                return;
            }
            checkPending = true;
        }
        new UpdateDownload().start();
    }

    public static boolean isCheckForUpdates() {
        Preferences prefs = Preferences.userNodeForPackage(UpdateDownload.class);
        return prefs.getBoolean(KEY_CHECK_FOR_UPDATES, true);
    }

    public static void setCheckForUpdates(boolean check) {
        Preferences prefs = Preferences.userNodeForPackage(UpdateDownload.class);
        prefs.putBoolean(KEY_CHECK_FOR_UPDATES, check);
    }

    @Override
    public void run() {
        try {
            Logger.getLogger(UpdateDownload.class.getName()).log(Level.INFO, "Checking for new JBirthdays Version");
            netVersion = NetVersion.getOverHttp(URL_VERSION_CHECK_FILE, VERSION_DELIMITER);
            if (hasNewerVersion()) {
                Logger.getLogger(UpdateDownload.class.getName()).log(Level.INFO, "Downloading newer JBirthdays Version{0}", netVersion);
                showNewVersionInEdt();
            } else {
                Logger.getLogger(UpdateDownload.class.getName()).log(Level.INFO, "JBirthdays is up to date");
            }
        } catch (Throwable t) {
            Logger.getLogger(UpdateDownload.class.getName()).log(Level.SEVERE, null, t);
        }
    }

    private void showNewVersionInEdt() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                showNewVersion();
            }
        });
    }

    private void showNewVersion() {
        String msg = Bundle.getString(UpdateDownload.class, "UpdateDownload.Confirm.OpenDownloadPage", currentVersion, netVersion, DOWNLOAD_PAGE);
        String title = Bundle.getString(UpdateDownload.class, "UpdateDownload.Confirm.Title");
        int result = JOptionPane.showConfirmDialog(null, msg, title, YES_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            try {
                Desktop.getDesktop().browse(new URI(DOWNLOAD_PAGE));
            } catch (Throwable t) {
                Logger.getLogger(UpdateDownload.class.getName()).log(Level.SEVERE, null, t);
            }
        }
    }

    private boolean hasNewerVersion() throws Exception {
        return currentVersion.compareTo(netVersion) < 0;
    }
}
