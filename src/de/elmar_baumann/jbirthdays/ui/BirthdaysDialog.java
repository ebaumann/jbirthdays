package de.elmar_baumann.jbirthdays.ui;

import de.elmar_baumann.jbirthdays.api.Birthdays;
import de.elmar_baumann.jbirthdays.api.Person;
import de.elmar_baumann.jbirthdays.api.PersonRepository;
import de.elmar_baumann.jbirthdays.util.Bundle;
import de.elmar_baumann.jbirthdays.util.IconUtil;
import de.elmar_baumann.jbirthdays.util.TableUtil;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public class BirthdaysDialog extends Dialog {

    private static final long serialVersionUID = 1L;
    private static final int[] COL_WIDTH_BIRTHDAY_TABLES = new int[]{150,200,75,150,200};
    private final PreferencesPanel panelPreferences = new PreferencesPanel();
    private final List<Person> allPersons = new ArrayList<>();
    private final PersonTableModel allPersonsTableModel = new PersonTableModel(false);
    private final PersonTableModel birthdayTodayTableModel = new PersonTableModel(true);
    private final PersonTableModel birthdayBeforeTableModel = new PersonTableModel(true);
    private final PersonTableModel birthdayAfterTableModel = new PersonTableModel(true);
    private final TableRowSorter<PersonTableModel> allPersonsRowSorter = new TableRowSorter<>(allPersonsTableModel);
    private final TableRowSorter<PersonTableModel> birthdayTodayRowSorter = new TableRowSorter<>(allPersonsTableModel);
    private final TableRowSorter<PersonTableModel> birthdayBeforeRowSorter = new TableRowSorter<>(allPersonsTableModel);
    private final TableRowSorter<PersonTableModel> birthdayAfterSorter = new TableRowSorter<>(allPersonsTableModel);

    public BirthdaysDialog() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        // Matisse throws java.lang.ClassNotFoundException: de.elmar_baumann.jbirthdays.ui.PreferencesPanel,
        // INFO [org.netbeans.modules.form.MetaComponentCreator]: Cannot load component class de.elmar_baumann.jbirthdays.ui.PreferencesPanel from project <path>JBirthdays.
        tabbedPane.addTab(Bundle.getString(BirthdaysDialog.class, "BirthdaysDialog.TabTitle.Preferences"), panelPreferences);
        tableAllPersons.setModel(allPersonsTableModel);
        tableAllPersons.setRowSorter(allPersonsRowSorter);
        tableBirthdayToday.setModel(birthdayTodayTableModel);
        tableBirthdayBefore.setModel(birthdayBeforeTableModel);
        tableBirthdayAfter.setModel(birthdayAfterTableModel);
        TableUtil.setColumnWidths(tableBirthdayToday, COL_WIDTH_BIRTHDAY_TABLES);
        TableUtil.setColumnWidths(tableBirthdayBefore, COL_WIDTH_BIRTHDAY_TABLES);
        TableUtil.setColumnWidths(tableBirthdayAfter, COL_WIDTH_BIRTHDAY_TABLES);
        setIconImages();
        setTableTitles();
        panelPreferences.addPropertyChangeListener(PreferencesPanel.PROPERTY_DAYS_BEFORE, preferencesChangedListener);
        panelPreferences.addPropertyChangeListener(PreferencesPanel.PROPERTY_DAYS_AFTER, preferencesChangedListener);
        tableAllPersons.getSelectionModel().addListSelectionListener(enableEditRemoveButtonListener);
        tableAllPersons.addMouseListener(editPersonMouseListener);
        tableAllPersons.addKeyListener(personsTableKeyListener);
        textFieldFilterPerson.getDocument().addDocumentListener(filterPersonListener);
    }



    private void setIconImages() {
        String iconFolder = "/de/elmar_baumann/jbirthdays/icons/";
        setIconImages(Arrays.asList(
                IconUtil.getIconImage(iconFolder + "Birthdays.png"),
                IconUtil.getIconImage(iconFolder + "Birthdays24.png"),
                IconUtil.getIconImage(iconFolder + "Birthdays32.png"),
                IconUtil.getIconImage(iconFolder + "Birthdays48.png")));
    }

    private void setTableTitles() {
        TitledBorder borderToday = (TitledBorder) scrollPaneBirthdayToday.getBorder();
        borderToday.setTitle(Bundle.getString(BirthdaysDialog.class, "BirthdaysDialog.TableTitle.Today", new Date()));
        setBeforeTableTitle();
        TitledBorder borderAfter = (TitledBorder) scrollPaneBirthdayAfter.getBorder();
        borderAfter.setTitle(Bundle.getString(BirthdaysDialog.class, "BirthdaysDialog.TableTitle.After", panelPreferences.getDaysAfter()));
    }

    private final PropertyChangeListener preferencesChangedListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            setTableTitles();
            updateBirthdayTables();
        }
    };

    private void setBeforeTableTitle() {
        TitledBorder borderBefore = (TitledBorder) scrollPaneBirthdayBefore.getBorder();
        borderBefore.setTitle(Bundle.getString(BirthdaysDialog.class, "BirthdaysDialog.TableTitle.Before", panelPreferences.getDaysBefore()));
    }

    private void loadPersons() {
        try {
            Collection<? extends Person> loadedPersons = findPreferredRepository().findAll();
            allPersons.clear();
            allPersons.addAll(loadedPersons);
            Collections.sort(allPersons, Person.CMP_ASC_BY_LAST_NAME);
            allPersonsTableModel.setPersons(allPersons);
        } catch (Throwable t) {
            Logger.getLogger(BirthdaysDialog.class.getName()).log(Level.SEVERE, null, t);
            showErrorMessage(Bundle.getString(BirthdaysDialog.class, "BirthdaysDialog.ErrorMessage.LoadPersons", t.getLocalizedMessage()));
        }
    }

    private void savePersons() {
        try {
            findPreferredRepository().save(allPersons);
        } catch (Throwable t) {
            Logger.getLogger(BirthdaysDialog.class.getName()).log(Level.SEVERE, null, t);
            showErrorMessage(Bundle.getString(BirthdaysDialog.class, "BirthdaysDialog.ErrorMessage.SavePersons", t.getLocalizedMessage()));
        }
    }

    private void updateBirthdayTables() {
        Date today = new Date();
        birthdayTodayTableModel.setPersons(Birthdays.findWithBirthdayAt(allPersons, today));
        birthdayBeforeTableModel.setPersons(Birthdays.findWithBirthdayInNdays(allPersons, today, panelPreferences.getDaysBefore()));
        birthdayAfterTableModel.setPersons(Birthdays.findWithBirthdayBeforeNdays(allPersons, today, panelPreferences.getDaysAfter()));
    }

    private PersonRepository findPreferredRepository() {
        for(PersonRepository repo : Lookup.getDefault().lookupAll(PersonRepository.class)) {
            if (repo.isPreferred()) {
                Logger.getLogger(BirthdaysDialog.class.getName()).log(Level.INFO, "Using repository: {0}", repo.getDisplayName());
                return repo;
            }
        }
        throw new IllegalStateException("No preferred repository implemented!");
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                Bundle.getString(BirthdaysDialog.class, "BirthdaysDialog.Error.Title"),
                JOptionPane.ERROR_MESSAGE);
    }

    private void addPerson() {
        EditPersonDialog dlg = new EditPersonDialog(this);
        dlg.setVisible(true);
        if (dlg.isAccepted()) {
            allPersons.add(dlg.save());
            savePersonsAndUpdateTables();
        }
    }

    private void editSelectedPersons() {
        boolean edited = false;
        for (int selRow : tableAllPersons.getSelectedRows()) {
            Person person = allPersonsTableModel.getPerson(tableAllPersons.convertRowIndexToModel(selRow));
            EditPersonDialog dlg = new EditPersonDialog(this, person);
            dlg.setVisible(true);
            if (dlg.isAccepted()) {
                dlg.save();
                edited = true;
            }
        }
        if (edited) {
            savePersonsAndUpdateTables();

        }
    }

    private void removeSelectedPersons() {
        if (!confirmRemove()) {
            return;
        }
        boolean removed = false;
        for (int selRow : tableAllPersons.getSelectedRows()) {
            Person person = allPersonsTableModel.getPerson(tableAllPersons.convertRowIndexToModel(selRow));
            if (allPersons.remove(person)) {
                removed = true;
            }
        }
        if (removed) {
            savePersonsAndUpdateTables();
        }
    }

    private boolean confirmRemove() {
        String message = Bundle.getString(BirthdaysDialog.class, "BirthdaysDialog.ConfirmRemove");
        return JOptionPane.showConfirmDialog(this, message) == JOptionPane.YES_OPTION;
    }

    private void savePersonsAndUpdateTables() {
        savePersons();
        loadPersons();
        updateBirthdayTables();
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible && allPersons.isEmpty()) {
            loadPersons();
            updateBirthdayTables();
        }
        super.setVisible(visible);
    }

    private final ListSelectionListener enableEditRemoveButtonListener = new ListSelectionListener() {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                buttonEditPerson.setEnabled(e.getFirstIndex() >= 0);
                buttonRemovePerson.setEnabled(e.getFirstIndex() >= 0);
            }
        }
    };

    private final MouseListener editPersonMouseListener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() > 1) {
                editSelectedPersons();
            }
        }
    };

    private final KeyListener personsTableKeyListener = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_ENTER:
                    editSelectedPersons();
                    break;
                case KeyEvent.VK_DELETE:
                    removeSelectedPersons();
                    break;
                default: // do nothing
            }
        }
    };

    private final DocumentListener filterPersonListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            updateSearchFilter();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateSearchFilter();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            updateSearchFilter();
        }

        private void updateSearchFilter() {
            allPersonsRowSorter.setRowFilter(new PersonNameRowFilter(textFieldFilterPerson.getText()));
        }
    };

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedPane = new javax.swing.JTabbedPane();
        panelDates = new javax.swing.JPanel();
        scrollPaneBirthdayToday = new javax.swing.JScrollPane();
        tableBirthdayToday = new javax.swing.JTable();
        scrollPaneBirthdayBefore = new javax.swing.JScrollPane();
        tableBirthdayBefore = new javax.swing.JTable();
        scrollPaneBirthdayAfter = new javax.swing.JScrollPane();
        tableBirthdayAfter = new javax.swing.JTable();
        panelPersons = new javax.swing.JPanel();
        scrollPaneAllPersons = new javax.swing.JScrollPane();
        tableAllPersons = new javax.swing.JTable();
        labelFilterPerson = new javax.swing.JLabel();
        textFieldFilterPerson = new javax.swing.JTextField();
        buttonAddPerson = new javax.swing.JButton();
        buttonRemovePerson = new javax.swing.JButton();
        buttonEditPerson = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/elmar_baumann/jbirthdays/ui/Bundle"); // NOI18N
        setTitle(bundle.getString("BirthdaysDialog.title")); // NOI18N

        scrollPaneBirthdayToday.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        tableBirthdayToday.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        scrollPaneBirthdayToday.setViewportView(tableBirthdayToday);

        scrollPaneBirthdayBefore.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        tableBirthdayBefore.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        scrollPaneBirthdayBefore.setViewportView(tableBirthdayBefore);

        scrollPaneBirthdayAfter.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        tableBirthdayAfter.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        scrollPaneBirthdayAfter.setViewportView(tableBirthdayAfter);

        javax.swing.GroupLayout panelDatesLayout = new javax.swing.GroupLayout(panelDates);
        panelDates.setLayout(panelDatesLayout);
        panelDatesLayout.setHorizontalGroup(
            panelDatesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDatesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollPaneBirthdayToday)
                    .addComponent(scrollPaneBirthdayAfter, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 707, Short.MAX_VALUE)
                    .addComponent(scrollPaneBirthdayBefore, javax.swing.GroupLayout.DEFAULT_SIZE, 707, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelDatesLayout.setVerticalGroup(
            panelDatesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPaneBirthdayToday, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                .addGap(11, 11, 11)
                .addComponent(scrollPaneBirthdayBefore, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scrollPaneBirthdayAfter, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedPane.addTab(bundle.getString("BirthdaysDialog.panelDates.TabConstraints.tabTitle"), panelDates); // NOI18N

        scrollPaneAllPersons.setViewportView(tableAllPersons);

        labelFilterPerson.setLabelFor(textFieldFilterPerson);
        org.openide.awt.Mnemonics.setLocalizedText(labelFilterPerson, bundle.getString("BirthdaysDialog.labelFilterPerson.text")); // NOI18N

        textFieldFilterPerson.setColumns(20);

        org.openide.awt.Mnemonics.setLocalizedText(buttonAddPerson, bundle.getString("BirthdaysDialog.buttonAddPerson.text")); // NOI18N
        buttonAddPerson.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddPersonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(buttonRemovePerson, bundle.getString("BirthdaysDialog.buttonRemovePerson.text")); // NOI18N
        buttonRemovePerson.setEnabled(false);
        buttonRemovePerson.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemovePersonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(buttonEditPerson, bundle.getString("BirthdaysDialog.buttonEditPerson.text")); // NOI18N
        buttonEditPerson.setEnabled(false);
        buttonEditPerson.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonEditPersonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelPersonsLayout = new javax.swing.GroupLayout(panelPersons);
        panelPersons.setLayout(panelPersonsLayout);
        panelPersonsLayout.setHorizontalGroup(
            panelPersonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPersonsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPersonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPersonsLayout.createSequentialGroup()
                        .addComponent(labelFilterPerson)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textFieldFilterPerson, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(buttonAddPerson)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonRemovePerson)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonEditPerson))
                    .addComponent(scrollPaneAllPersons, javax.swing.GroupLayout.DEFAULT_SIZE, 707, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelPersonsLayout.setVerticalGroup(
            panelPersonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPersonsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPaneAllPersons, javax.swing.GroupLayout.DEFAULT_SIZE, 458, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPersonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonEditPerson)
                    .addComponent(buttonRemovePerson)
                    .addComponent(buttonAddPerson)
                    .addComponent(labelFilterPerson)
                    .addComponent(textFieldFilterPerson, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        tabbedPane.addTab(bundle.getString("BirthdaysDialog.panelPersons.TabConstraints.tabTitle"), panelPersons); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedPane)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedPane)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonAddPersonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_buttonAddPersonActionPerformed
        addPerson();
    }//GEN-LAST:event_buttonAddPersonActionPerformed

    private void buttonEditPersonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_buttonEditPersonActionPerformed
        editSelectedPersons();
    }//GEN-LAST:event_buttonEditPersonActionPerformed

    private void buttonRemovePersonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_buttonRemovePersonActionPerformed
        removeSelectedPersons();
    }//GEN-LAST:event_buttonRemovePersonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BirthdaysDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                BirthdaysDialog dialog = new BirthdaysDialog();
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAddPerson;
    private javax.swing.JButton buttonEditPerson;
    private javax.swing.JButton buttonRemovePerson;
    private javax.swing.JLabel labelFilterPerson;
    private javax.swing.JPanel panelDates;
    private javax.swing.JPanel panelPersons;
    private javax.swing.JScrollPane scrollPaneAllPersons;
    private javax.swing.JScrollPane scrollPaneBirthdayAfter;
    private javax.swing.JScrollPane scrollPaneBirthdayBefore;
    private javax.swing.JScrollPane scrollPaneBirthdayToday;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTable tableAllPersons;
    private javax.swing.JTable tableBirthdayAfter;
    private javax.swing.JTable tableBirthdayBefore;
    private javax.swing.JTable tableBirthdayToday;
    private javax.swing.JTextField textFieldFilterPerson;
    // End of variables declaration//GEN-END:variables
}
