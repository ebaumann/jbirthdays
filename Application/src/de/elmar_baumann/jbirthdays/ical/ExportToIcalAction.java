package de.elmar_baumann.jbirthdays.ical;

import de.elmar_baumann.jbirthdays.api.BirthdaysUtil;
import de.elmar_baumann.jbirthdays.api.Person;
import de.elmar_baumann.jbirthdays.ui.ChoosePersonsDialog;
import de.elmar_baumann.jbirthdays.util.Bundle;
import de.elmar_baumann.jbirthdays.util.ComponentUtil;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * @author Elmar Baumann
 */
public final class ExportToIcalAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private static final String KEY_EXPORT_DIR = "ExportToIcalAction.Dir";
    private Component parent;

    public ExportToIcalAction() {
    }

    public ExportToIcalAction(Component parent) {
        this.parent = parent;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        File targetDir = chooseTargetDir();
        if (targetDir == null) {
            return;
        }
        Collection<? extends Person> persons = choosePersons();
        if (persons.isEmpty()) {
            return;
        }
        String exportFilePath = getExportFilePath(targetDir.getAbsolutePath());
        try (FileOutputStream fos = new FileOutputStream(exportFilePath)) {
            int count = Ical.toIcal(persons, fos);
            exportedMessage(count, exportFilePath);
        } catch (Throwable t) {
            Logger.getLogger(ExportToIcalAction.class.getName()).log(Level.SEVERE, null, t);
            errorMessage(t);
        }
    }

    private String getExportFilePath(String dir) {
        String filename = "JBirthdays-" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + ".ics";
        return dir + File.separator + filename;
    }

    private Collection<? extends Person> choosePersons() {
        try {
            ChoosePersonsDialog dlg = new ChoosePersonsDialog(ComponentUtil.findParentDialog(parent));
            dlg.setPersons(BirthdaysUtil.findPreferredRepository().findAll());
            dlg.setTitle(Bundle.getString(ExportToIcalAction.class, "ExportToIcalAction.ChoosePersons.Title"));
            dlg.setVisible(true);
            if (dlg.isAccepted()) {
                return dlg.getSelectedPersons();
            } else {
                return Collections.<Person>emptyList();
            }
        } catch (Throwable t) {
            errorMessage(t);
            return Collections.<Person>emptyList();
        }
    }

    private File chooseTargetDir() {
        JFileChooser fc = new JFileChooser(readCurrentDirPath());
        fc.setMultiSelectionEnabled(false);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setDialogTitle(Bundle.getString(ExportToIcalAction.class, "ExportToIcalAction.ChooseTargetDir.Title"));
        if (fc.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fc.getSelectedFile();
            persistCurrentDirPath(selectedFile.getAbsolutePath());
            return selectedFile;
        }
        return null;
    }

    private void persistCurrentDirPath(String path) {
        Preferences prefs = Preferences.userNodeForPackage(ExportToIcalAction.class);
        prefs.put(KEY_EXPORT_DIR, path);
    }

    private String readCurrentDirPath() {
        Preferences prefs = Preferences.userNodeForPackage(ExportToIcalAction.class);
        return prefs.get(KEY_EXPORT_DIR, System.getProperty("user.home"));
    }

    private void exportedMessage(int count, String filepath) {
        JOptionPane.showMessageDialog(parent,
                Bundle.getString(ExportToIcalAction.class, "ExportToIcalAction.Exported.Message", count, filepath),
                Bundle.getString(ExportToIcalAction.class, "ExportToIcalAction.Exported.Title"),
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void errorMessage(Throwable t) {
        JOptionPane.showMessageDialog(parent,
                Bundle.getString(ExportToIcalAction.class, "ExportToIcalAction.Error.Message", t.getLocalizedMessage()),
                Bundle.getString(ExportToIcalAction.class, "ExportToIcalAction.Error.Title"),
                JOptionPane.ERROR_MESSAGE);
    }
}
