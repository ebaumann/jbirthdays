package de.elmar_baumann.jbirthdays;

import de.elmar_baumann.jbirthdays.ui.BirthdaysDialog;
import de.elmar_baumann.jbirthdays.update.UpdateDownload;
import java.awt.EventQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * @author Elmar Baumann
 */
public class Main {

    public static void main(String[] args) {
        AppLoggingSystem.init();
        Logger.getLogger(Main.class.getName()).log(Level.INFO, "Starting JBirthdays {0}", AppVersion.VERSION);
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                setLookAndFeel();
                checkForUpdate();
                new BirthdaysDialog().setVisible(true);
            }

            private void setLookAndFeel() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            private void checkForUpdate() {
                if (UpdateDownload.isCheckForUpdates()) {
                    UpdateDownload.checkForNewerVersion();
                }
            }
        });
    }
}
