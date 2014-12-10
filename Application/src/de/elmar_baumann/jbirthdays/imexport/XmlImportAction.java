package de.elmar_baumann.jbirthdays.imexport;

import de.elmar_baumann.jbirthdays.api.BirthdaysUtil;
import de.elmar_baumann.jbirthdays.api.Person;
import de.elmar_baumann.jbirthdays.api.PersonRepository;
import de.elmar_baumann.jbirthdays.ui.ChoosePersonsDialog;
import de.elmar_baumann.jbirthdays.util.Bundle;
import de.elmar_baumann.jbirthdays.util.ComponentUtil;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
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
public final class XmlImportAction extends AbstractAction {

    public static final String PROPERTY_IMPORTED = "imported";
    private static final long serialVersionUID = 1L;
    private static final String KEY_IMPORT_DIR = "XmlImportAction.ImportDir";
    private final Component parentComponent;

    public XmlImportAction() {
        this(null);
    }

    public XmlImportAction(Component parentComponent) {
        this.parentComponent = parentComponent;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        File file = chooseFile();
        if (file == null) {
            return;
        }
        try {
            Collection<? extends Person> persons = choosePersons(XmlImExport.importFromFile(file));
            if (persons.isEmpty()) {
                return;
            }
            PersonRepository repo = BirthdaysUtil.findPreferredRepository();
            Collection<Person> currentPersons = new ArrayList<>(repo.findAll());
            int countAdded = BirthdaysUtil.addNotContainedPersons(persons, currentPersons);
            repo.save(currentPersons);
            firePropertyChange(PROPERTY_IMPORTED, false, true);
            showSuccessMessage(countAdded);
        } catch (Throwable t) {
            Logger.getLogger(XmlImportAction.class.getName()).log(Level.SEVERE, null, t);
            showErrorMessage(t);
        }
    }

    private Collection<? extends Person> choosePersons(Collection<? extends Person> fromPersons) {
        if (fromPersons.isEmpty()) {
            return Collections.emptyList();
        }
        ChoosePersonsDialog dlg = new ChoosePersonsDialog(ComponentUtil.findParentDialog(parentComponent));
        dlg.setPersons(fromPersons);
        dlg.setVisible(true);
        return dlg.isAccepted()
                ? dlg.getSelectedPersons()
                : Collections.<Person>emptyList();
    }

    private File chooseFile() {
        JFileChooser fc = new JFileChooser(getImportDirpath());
        fc.setDialogTitle(Bundle.getString(XmlExportAction.class, "XmlImportAction.Filechooser.Title"));
        String filterDescription = Bundle.getString(XmlImportAction.class, "XmlImportAction.FileFilterDescription");
        FileNameExtensionFilter filter = new FileNameExtensionFilter(filterDescription, "xml");
        fc.setFileFilter(filter);
        fc.setAcceptAllFileFilterUsed(false);
        fc.setMultiSelectionEnabled(false);
        if (fc.showOpenDialog(parentComponent) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            persistImportDirpath(file);
            return file;
        }
        return null;
    }

    private String getImportDirpath() {
        Preferences prefs = Preferences.userNodeForPackage(XmlImportAction.class);
        return prefs.get(KEY_IMPORT_DIR, System.getProperty("user.home"));
    }

    private void persistImportDirpath(File file) {
        Preferences prefs = Preferences.userNodeForPackage(XmlImportAction.class);
        prefs.put(KEY_IMPORT_DIR, file.getParent());
    }

    private void showSuccessMessage(int count) {
        JOptionPane.showMessageDialog(parentComponent,
                Bundle.getString(XmlExportAction.class, "XmlImportAction.Success.Message", count),
                Bundle.getString(XmlExportAction.class, "XmlImportAction.Success.Title"),
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorMessage(Throwable t) {
        JOptionPane.showMessageDialog(parentComponent,
                Bundle.getString(XmlExportAction.class, "XmlImportAction.Error.Message", t.getLocalizedMessage()),
                Bundle.getString(XmlExportAction.class, "XmlImportAction.Error.Title"),
                JOptionPane.ERROR_MESSAGE);
    }
}
