package de.elmar_baumann.jbirthdays;

import de.elmar_baumann.jbirthdays.util.Bundle;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

/**
 * @author Elmar Baumann
 */
public final class AppLoggingSystem {

    private static final File LOGFILE;
    // Keeping a Reference ensures not loosing the Handlers (LogManager stores Loggers as Weak References)
    private static final Logger APP_LOGGER = Logger.getLogger("de.elmar_baumann.jbirthdays");
    private static final Level LOG_LEVEL = Level.ALL;
    private static FileHandler fileHandler;
    private static Handler systemOutHandler;
    private static volatile boolean init;

    static {
        LOGFILE = new File(System.getProperty("user.home") + File.separator
                + ".de.elmar_baumann" + File.separator + "jbirthdays"
                + File.separator + "JBirthdays-Log.txt");
    }

    public static void init() {
        synchronized (AppLoggingSystem.class) {
            if (init) {
                return;
            }
            init = true;
        }
        try {
            System.out.println("Init JBirthdays logging system. Log file: " + LOGFILE);
            ensureLogDirectoryExists();
            createAndAddHandlersToAppLogger();
        } catch (Throwable t) {
            Logger.getLogger(AppLoggingSystem.class.getName()).log(Level.SEVERE, null, t);
        } finally {
            APP_LOGGER.setLevel(Level.ALL); // Handlers are restricting the output
            APP_LOGGER.setUseParentHandlers(false);
            LogManager.getLogManager().addLogger(APP_LOGGER);
            setDefaultUncaughtExceptionHandler();
        }
    }

    private static void ensureLogDirectoryExists() {
        File dir = LOGFILE.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private static void createAndAddHandlersToAppLogger() throws IOException {
        createSystemOutHandler();
        createFileHandler();
        // Writing errors of others to the error logfile
        Logger.getLogger("").addHandler(fileHandler);
        Logger.getLogger("").addHandler(systemOutHandler);
    }

    private static void createSystemOutHandler() throws SecurityException {
        systemOutHandler = new StreamHandler(System.out, new SimpleFormatter());
        systemOutHandler.setLevel(LOG_LEVEL);
        APP_LOGGER.addHandler(systemOutHandler);
    }

    private static void createFileHandler() throws UnsupportedEncodingException, IOException, SecurityException {
        fileHandler = new FileHandler(LOGFILE.getAbsolutePath());
        fileHandler.setLevel(LOG_LEVEL);
        fileHandler.setFormatter(new SimpleFormatter());
        fileHandler.setEncoding("UTF-8");
        APP_LOGGER.addHandler(fileHandler);
    }

    private static void setDefaultUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                APP_LOGGER.log(Level.SEVERE, null, e);
            }
        });
    }

    public static File getLogfile() {
        return LOGFILE;
    }

    public static Action createViewLogfileAction() {
        return new ViewLogfileAction();
    }

    private static final class ViewLogfileAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            if (desktopSupportsOpen()) {
                try {
                    Desktop.getDesktop().open(LOGFILE);
                } catch (Throwable t) {
                    Logger.getLogger(ViewLogfileAction.class.getName()).log(Level.SEVERE, null, t);
                    errorMessageOpen(t);
                }
            } else {
                errorMessageDtSupport();
            }
        }

        private boolean desktopSupportsOpen() {
            return Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN);
        }

        private void errorMessageDtSupport() {
            String message = Bundle.getString(AppLoggingSystem.class, "AppLoggingSystem.ErrorMessageDtSupport.Message", LOGFILE);
            String title = Bundle.getString(AppLoggingSystem.class, "AppLoggingSystem.ErrorMessageDtSupport.Title");
            JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
        }

        private void errorMessageOpen(Throwable t) {
            String message = Bundle.getString(AppLoggingSystem.class, "AppLoggingSystem.ErrorMessageOpen.Message", LOGFILE, t);
            String title = Bundle.getString(AppLoggingSystem.class, "AppLoggingSystem.ErrorMessageOpen.Title");
            JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
        }
    }

    private AppLoggingSystem() {
    }
}
