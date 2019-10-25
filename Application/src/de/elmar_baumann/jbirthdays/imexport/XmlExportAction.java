package de.elmar_baumann.jbirthdays.imexport;

import de.elmar_baumann.jbirthdays.api.BirthdaysUtil;
import de.elmar_baumann.jbirthdays.api.Person;
import de.elmar_baumann.jbirthdays.ui.ChoosePersonsDialog;
import de.elmar_baumann.jbirthdays.util.Bundle;
import de.elmar_baumann.jbirthdays.util.ComponentUtil;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * @author Elmar Baumann
 */
public final class XmlExportAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private static final String KEY_EXPORT_DIR = "XmlExportAction.ExportDir";
    private final Component parentComponent;

    public XmlExportAction() {
        this(null);
    }

    public XmlExportAction(Component parentComponent) {
        this.parentComponent = parentComponent;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            Collection<? extends Person> persons = choosePersons();
            if (persons.isEmpty()) {
                return;
            }
            File file = chooseFile();
            if (file == null) {
                return;
            }
            XmlImExport.exportToFile(persons, file);
            showSuccessMessage(persons.size());
        } catch (Throwable t) {
            Logger.getLogger(XmlExportAction.class.getName()).log(Level.SEVERE, null, t);
            showErrorMessage(t);
        }
    }

    private Collection<? extends Person> choosePersons() {
        ChoosePersonsDialog dlg = new ChoosePersonsDialog(ComponentUtil.findParentDialog(parentComponent));
        dlg.setTitle(Bundle.getString(XmlExportAction.class, "XmlExportAction.PersonChooser.Title"));
        dlg.setPersons(BirthdaysUtil.findPreferredRepository().findAll());
        dlg.setVisible(true);
        return dlg.isAccepted()
                ? dlg.getSelectedPersons()
                : Collections.<Person>emptyList();
    }

    private File chooseFile() {
        JFileChooser fc = new JFileChooser(getExportDirpath());
        fc.setDialogTitle(Bundle.getString(XmlExportAction.class, "XmlExportAction.Filechooser.Title"));
        String filterDescription = Bundle.getString(XmlImportAction.class, "XmlExportAction.FileFilterDescription");
        FileNameExtensionFilter filter = new FileNameExtensionFilter(filterDescription, "xml");
        fc.setFileFilter(filter);
        fc.setAcceptAllFileFilterUsed(false);
        fc.setMultiSelectionEnabled(false);
        if (fc.showOpenDialog(parentComponent) == JFileChooser.APPROVE_OPTION) {
            File file = ensureXmlSuffix(fc.getSelectedFile());
            persistExportDirpath(file);
            if (checkOverwrite(file)) {
                return file;
            }
        }
        return null;
    }

    private File ensureXmlSuffix(File file) {
        if (file.getName().toLowerCase().endsWith(".xml")) {
            return file;
        }
        return new File(file.getAbsolutePath() + ".xml");
    }

    private boolean checkOverwrite(File file) {
        String message = Bundle.getString(XmlExportAction.class, "XmlExportAction.Confirm.Overwrite", file);
        return !file.exists()
                || JOptionPane.showConfirmDialog(parentComponent, message) == JOptionPane.YES_OPTION;
    }

    private String getExportDirpath() {
        Preferences prefs = Preferences.userNodeForPackage(XmlExportAction.class);
        return prefs.get(KEY_EXPORT_DIR, System.getProperty("user.home"));
    }

    private void persistExportDirpath(File file) {
        Preferences prefs = Preferences.userNodeForPackage(XmlExportAction.class);
        prefs.put(KEY_EXPORT_DIR, file.getParent());
    }

    private void showSuccessMessage(int count) {
        JOptionPane.showMessageDialog(parentComponent,
                Bundle.getString(XmlExportAction.class, "XmlExportAction.Success.Message", count),
                Bundle.getString(XmlExportAction.class, "XmlExportAction.Success.Title"),
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorMessage(Throwable t) {
        JOptionPane.showMessageDialog(parentComponent,
                Bundle.getString(XmlExportAction.class, "XmlExportAction.Error.Message", t.getLocalizedMessage()),
                Bundle.getString(XmlExportAction.class, "XmlExportAction.Error.Title"),
                JOptionPane.ERROR_MESSAGE);
    }
}
