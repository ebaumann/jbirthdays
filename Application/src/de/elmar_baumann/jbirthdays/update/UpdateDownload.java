package de.elmar_baumann.jbirthdays.update;

import de.elmar_baumann.jbirthdays.AppVersion;
import de.elmar_baumann.jbirthdays.util.Bundle;
import de.elmar_baumann.jbirthdays.util.HttpUtil;
import de.elmar_baumann.jbirthdays.util.SystemUtil;
import de.elmar_baumann.jbirthdays.util.Version;
import java.awt.EventQueue;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;

/**
 * @author Elmar Baumann
 */
public final class UpdateDownload extends Thread {

    private static final String KEY_CHECK_FOR_UPDATES = "UpdateDownload.CheckForUpdates";
    private static final String FILENAME_WINDOWS = "JBirthdays-Setup.exe";
    private static final String FILENAME_ZIP = "JBirthdays.zip";
    private static final String URL_VERSION_CHECK_FILE = "http://elmar-baumann.de/JBirthdays/jbirthdays-version.txt";
    private static final String URL_WIN_INSTALLER = "http://elmar-baumann.de/JBirthdays/dist/JBirthdays-setup.exe";
    private static final String URL_ZIP = "http://elmar-baumann.de/JBirthdays/dist/JBirthdays.zip";
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
                download();
            } else {
                Logger.getLogger(UpdateDownload.class.getName()).log(Level.INFO, "JBirthdays is up to date");
            }
        } catch (Throwable t) {
            Logger.getLogger(UpdateDownload.class.getName()).log(Level.SEVERE, null, t);
        }
    }

    private void download() {
        File targetFile = getDownloadFile();
        try {
            BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(targetFile));
            HttpUtil.write(new URL(getDownloadUrl()), os, null);
            showSuccessMessage(targetFile);
        } catch (Throwable t) {
            Logger.getLogger(UpdateDownload.class.getName()).log(Level.SEVERE, null, t);
        }
    }

    private void showSuccessMessage(final File downloadFile) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(null,
                        Bundle.getString(UpdateDownload.class, "UpdateDownload.Success", netVersion, downloadFile),
                        Bundle.getString(UpdateDownload.class, "UpdateDownload.Success.Title"),
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    private String getDownloadUrl() {
        return SystemUtil.isWindows()
                ? URL_WIN_INSTALLER
                : URL_ZIP;
    }

    private boolean hasNewerVersion() throws Exception {
        return currentVersion.compareTo(netVersion) < 0;
    }

    private File getDownloadFile() {
        String dirname = System.getProperty("user.home")
                + File.separator + ".de.elmar_baumann" + File.separator + "JBirthdays";
        String filename = SystemUtil.isWindows()
                ? FILENAME_WINDOWS
                : FILENAME_ZIP;
        return new File(dirname + File.separator + filename);
    }
}
