package de.elmar_baumann.jbirthdays;

import de.elmar_baumann.jbirthdays.ui.BirthdaysDialog;
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
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                setLookAndFeel();
                new BirthdaysDialog().setVisible(true);
            }

            private void setLookAndFeel() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
}
