package de.elmar_baumann.jbirthdays.ui;

import de.elmar_baumann.jbirthdays.AppVersion;
import de.elmar_baumann.jbirthdays.api.BirthdaysUtil;
import de.elmar_baumann.jbirthdays.api.Person;
import de.elmar_baumann.jbirthdays.api.RepositoryChangedEvent;
import de.elmar_baumann.jbirthdays.util.Bundle;
import de.elmar_baumann.jbirthdays.util.DesktopMail;
import de.elmar_baumann.jbirthdays.util.IconUtil;
import de.elmar_baumann.jbirthdays.util.Mnemonics;
import de.elmar_baumann.jbirthdays.util.TableUtil;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

/**
 * @author Elmar Baumann
 */
public class BirthdaysDialog extends Dialog {

    private static final long serialVersionUID = 1L;
    private static final TableCellRenderer TABLE_CELL_RENDERER = new PersonsTableCellRenderer();
    private final List<Person> allPersons = new ArrayList<>();
    private final PersonTableModel allPersonsTableModel = new PersonTableModel(false);
    private final PersonTableModel birthdayTodayTableModel = new PersonTableModel(true);
    private final PersonTableModel birthdayBeforeTableModel = new PersonTableModel(true);
    private final PersonTableModel birthdayAfterTableModel = new PersonTableModel(true);
    private final PersonSearchFilter allPersonsSearchFilter = new PersonSearchFilter(allPersonsTableModel);

    public BirthdaysDialog() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        Mnemonics.setMnemonics(panelDates);
        Mnemonics.setMnemonics(panelPersons);
        Mnemonics.setMnemonics((Component) tabbedPane);
        setIconImages();
        setTableTitles();
        tableAllPersons.setModel(allPersonsTableModel);
        tableAllPersons.setRowSorter(allPersonsSearchFilter.getRowSorter());
        tableBirthdayToday.setModel(birthdayTodayTableModel);
        tableBirthdayBefore.setModel(birthdayBeforeTableModel);
        tableBirthdayAfter.setModel(birthdayAfterTableModel);
        TableUtil.restoreColumnWidths("BirthdaysDialog.tableBirthdayToday", tableBirthdayToday, birthdayTodayTableModel.getDefaultColumnWidths());
        TableUtil.restoreColumnWidths("BirthdaysDialog.tableBirthdayBefore", tableBirthdayBefore, birthdayBeforeTableModel.getDefaultColumnWidths());
        TableUtil.restoreColumnWidths("BirthdaysDialog.tableBirthdayAfter", tableBirthdayAfter, birthdayAfterTableModel.getDefaultColumnWidths());
        TableUtil.restoreColumnWidths("BirthdaysDialog.tableAllPersons", tableAllPersons, allPersonsTableModel.getDefaultColumnWidths());
        panelPreferences.addPropertyChangeListener(PreferencesPanel.PROPERTY_DAYS_BEFORE, preferencesChangedListener);
        panelPreferences.addPropertyChangeListener(PreferencesPanel.PROPERTY_DAYS_AFTER, preferencesChangedListener);
        tableAllPersons.getSelectionModel().addListSelectionListener(enableEditRemoveButtonListener);
        tableAllPersons.addMouseListener(editPersonMouseListener);
        tableBirthdayToday.addMouseListener(editPersonMouseListener);
        tableBirthdayBefore.addMouseListener(editPersonMouseListener);
        tableBirthdayAfter.addMouseListener(editPersonMouseListener);
        tableAllPersons.addKeyListener(personsTableKeyListener);
        tableBirthdayToday.addKeyListener(personsTableKeyListener);
        tableBirthdayBefore.addKeyListener(personsTableKeyListener);
        tableBirthdayAfter.addKeyListener(personsTableKeyListener);
        textFieldFilterPerson.getDocument().addDocumentListener(allPersonsSearchFilter);
        panelTools.addPropertyChangeListener(ToolsPanel.PROPERTY_IMPORTED, personImportListener);
        tableAllPersons.setDefaultRenderer(Object.class, TABLE_CELL_RENDERER);
        tableBirthdayToday.setDefaultRenderer(Object.class, TABLE_CELL_RENDERER);
        tableBirthdayBefore.setDefaultRenderer(Object.class, TABLE_CELL_RENDERER);
        tableBirthdayAfter.setDefaultRenderer(Object.class, TABLE_CELL_RENDERER);
        addWindowListener(closeListener);
        AnnotationProcessor.process(this);
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
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoadFromPreferredRepositoryWorker().execute();
        }
        });
    }

    private void savePersons() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SaveToPreferredRepositoryWorker().execute();
        }
        });
    }

    private void updateBirthdayTables() {
        Date today = new Date();
        birthdayTodayTableModel.setPersons(filterDisplayable(
                BirthdaysUtil.findWithBirthdayAt(allPersons, today)));
        birthdayBeforeTableModel.setPersons(filterDisplayable(
                BirthdaysUtil.findWithBirthdayInNdays(allPersons, today,
                        panelPreferences.getDaysBefore())));
        birthdayAfterTableModel.setPersons(filterDisplayable(
                BirthdaysUtil.findWithBirthdayBeforeNdays(allPersons, today,
                        panelPreferences.getDaysAfter())));
        setEmailButtonEnabled();
    }

    private Collection<? extends Person> filterDisplayable(Collection<? extends Person> persons) {
        Collection<Person> displayablePersons = new ArrayList<>(persons.size());
        for (Person person : persons) {
            if (person.isDisplay()) {
                displayablePersons.add(person);
            }
        }
        return displayablePersons;
    }

    private void setEmailButtonEnabled() {
        boolean personWithBirthdayHasEmail = false;
        for (Person person : birthdayTodayTableModel.getPersons()) {
            if (person.hasEmail()) {
                personWithBirthdayHasEmail = true;
                break;
            }
        }
        buttonEmail.setEnabled(personWithBirthdayHasEmail);
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

    private void editSelectedPersons(JTable table) {
        boolean edited = false;
        for (int selRow : table.getSelectedRows()) {
            PersonTableModel model = (PersonTableModel) table.getModel();
            Person person = model.getPerson(table.convertRowIndexToModel(selRow));
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

    private void sendEmail() {
        for (Person person : birthdayTodayTableModel.getPersons()) {
            if (person.hasEmail()) {
                DesktopMail.sendEmail(person.getEmail(),
                        Bundle.getString(BirthdaysDialog.class, "BirthdaysDialog.Subject"),
                        "");
            }
        }
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
            Object source = e.getSource();
            if (e.getClickCount() > 1) {
                editSelectedPersons((JTable) source);
            }
        }
    };

    private final KeyListener personsTableKeyListener = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_ENTER:
                    editSelectedPersons((JTable) e.getSource());
                    break;
                case KeyEvent.VK_DELETE:
                    removeSelectedPersons();
                    break;
                default: // do nothing
            }
        }
    };

    private final PropertyChangeListener personImportListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            loadPersons();
            updateBirthdayTables();
        }
    };

    private final WindowListener closeListener = new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
            TableUtil.persistColumnWidths("BirthdaysDialog.tableBirthdayToday", tableBirthdayToday);
            TableUtil.persistColumnWidths("BirthdaysDialog.tableBirthdayBefore", tableBirthdayBefore);
            TableUtil.persistColumnWidths("BirthdaysDialog.tableBirthdayAfter", tableBirthdayAfter);
            TableUtil.persistColumnWidths("BirthdaysDialog.tableAllPersons", tableAllPersons);
        }
    };

    private final class LoadFromPreferredRepositoryWorker extends SwingWorker<Collection<? extends Person>, Void> {

        @Override
        protected Collection<? extends Person> doInBackground() throws Exception {
            return BirthdaysUtil.findPreferredRepository().findAll();
        }

        @Override
        protected void done() {
            try {
                Collection<? extends Person> loadedPersons = get();
                allPersons.clear();
                allPersons.addAll(loadedPersons);
                Collections.sort(allPersons, Person.CMP_ASC_BY_LAST_NAME);
                allPersonsTableModel.setPersons(allPersons);
            } catch (Throwable t) {
                Logger.getLogger(BirthdaysDialog.class.getName()).log(Level.SEVERE, null, t);
                showErrorMessage(Bundle.getString(BirthdaysDialog.class, "BirthdaysDialog.ErrorMessage.LoadPersons", t.getLocalizedMessage()));
            }
        }
    }

    private final class SaveToPreferredRepositoryWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            BirthdaysUtil.findPreferredRepository().save(allPersons);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
            } catch (Throwable t) {
                Logger.getLogger(BirthdaysDialog.class.getName()).log(Level.SEVERE, null, t);
                showErrorMessage(Bundle.getString(BirthdaysDialog.class, "BirthdaysDialog.ErrorMessage.SavePersons", t.getLocalizedMessage()));
            }
        }
    };

    @EventSubscriber(eventClass = RepositoryChangedEvent.class)
    public void repositoryChanged(RepositoryChangedEvent evt) {
        loadPersons();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        GridBagConstraints gridBagConstraints;

        tabbedPane = new JTabbedPane();
        panelDates = new JPanel();
        scrollPaneBirthdayToday = new JScrollPane();
        tableBirthdayToday = new JTable();
        scrollPaneBirthdayBefore = new JScrollPane();
        tableBirthdayBefore = new JTable();
        scrollPaneBirthdayAfter = new JScrollPane();
        tableBirthdayAfter = new JTable();
        labelHintDoubleclick = new JLabel();
        buttonEmail = new JButton();
        panelPersons = new JPanel();
        scrollPaneAllPersons = new JScrollPane();
        tableAllPersons = new JTable();
        labelFilterPerson = new JLabel();
        textFieldFilterPerson = new JTextField();
        buttonAddPerson = new JButton();
        buttonRemovePerson = new JButton();
        buttonEditPerson = new JButton();
        panelRepositories = new RepositoriesPanel();
        panelTools = new ToolsPanel();
        panelPreferences = new PreferencesPanel();
        panelAbout = new JPanel();
        labelAbout = new JLabel();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(Bundle.getString(BirthdaysDialog.class, "BirthdaysDialog.Title", AppVersion.VERSION)
        );
        getContentPane().setLayout(new GridBagLayout());

        panelDates.setLayout(new GridBagLayout());

        scrollPaneBirthdayToday.setBorder(BorderFactory.createTitledBorder(""));
        scrollPaneBirthdayToday.setPreferredSize(new Dimension(400, 150));

        tableBirthdayToday.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        scrollPaneBirthdayToday.setViewportView(tableBirthdayToday);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.33;
        gridBagConstraints.insets = new Insets(10, 10, 0, 10);
        panelDates.add(scrollPaneBirthdayToday, gridBagConstraints);

        scrollPaneBirthdayBefore.setBorder(BorderFactory.createTitledBorder(""));
        scrollPaneBirthdayBefore.setPreferredSize(new Dimension(400, 150));

        tableBirthdayBefore.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        scrollPaneBirthdayBefore.setViewportView(tableBirthdayBefore);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.33;
        gridBagConstraints.insets = new Insets(5, 10, 0, 10);
        panelDates.add(scrollPaneBirthdayBefore, gridBagConstraints);

        scrollPaneBirthdayAfter.setBorder(BorderFactory.createTitledBorder(""));
        scrollPaneBirthdayAfter.setPreferredSize(new Dimension(400, 150));

        tableBirthdayAfter.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        scrollPaneBirthdayAfter.setViewportView(tableBirthdayAfter);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.33;
        gridBagConstraints.insets = new Insets(5, 10, 0, 10);
        panelDates.add(scrollPaneBirthdayAfter, gridBagConstraints);

        ResourceBundle bundle = ResourceBundle.getBundle("de/elmar_baumann/jbirthdays/ui/Bundle"); // NOI18N
        labelHintDoubleclick.setText(bundle.getString("BirthdaysDialog.labelHintDoubleclick.text")); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(5, 10, 10, 0);
        panelDates.add(labelHintDoubleclick, gridBagConstraints);

        buttonEmail.setText(bundle.getString("BirthdaysDialog.buttonEmail.text")); // NOI18N
        buttonEmail.setToolTipText(bundle.getString("BirthdaysDialog.buttonEmail.toolTipText")); // NOI18N
        buttonEmail.setEnabled(false);
        buttonEmail.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                buttonEmailActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new Insets(5, 5, 10, 10);
        panelDates.add(buttonEmail, gridBagConstraints);

        tabbedPane.addTab(bundle.getString("BirthdaysDialog.panelDates.TabConstraints.tabTitle"), panelDates); // NOI18N

        panelPersons.setLayout(new GridBagLayout());

        scrollPaneAllPersons.setPreferredSize(new Dimension(400, 200));

        tableAllPersons.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        scrollPaneAllPersons.setViewportView(tableAllPersons);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 0, 10);
        panelPersons.add(scrollPaneAllPersons, gridBagConstraints);

        labelFilterPerson.setLabelFor(textFieldFilterPerson);
        labelFilterPerson.setText(bundle.getString("BirthdaysDialog.labelFilterPerson.text")); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(7, 10, 10, 0);
        panelPersons.add(labelFilterPerson, gridBagConstraints);

        textFieldFilterPerson.setColumns(20);
        textFieldFilterPerson.setMinimumSize(new Dimension(75, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(7, 5, 10, 0);
        panelPersons.add(textFieldFilterPerson, gridBagConstraints);

        buttonAddPerson.setText(bundle.getString("BirthdaysDialog.buttonAddPerson.text")); // NOI18N
        buttonAddPerson.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                buttonAddPersonActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(7, 5, 10, 0);
        panelPersons.add(buttonAddPerson, gridBagConstraints);

        buttonRemovePerson.setText(bundle.getString("BirthdaysDialog.buttonRemovePerson.text")); // NOI18N
        buttonRemovePerson.setEnabled(false);
        buttonRemovePerson.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                buttonRemovePersonActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(7, 5, 10, 0);
        panelPersons.add(buttonRemovePerson, gridBagConstraints);

        buttonEditPerson.setText(bundle.getString("BirthdaysDialog.buttonEditPerson.text")); // NOI18N
        buttonEditPerson.setEnabled(false);
        buttonEditPerson.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                buttonEditPersonActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(7, 5, 10, 10);
        panelPersons.add(buttonEditPerson, gridBagConstraints);

        tabbedPane.addTab(bundle.getString("BirthdaysDialog.panelPersons.TabConstraints.tabTitle"), panelPersons); // NOI18N
        tabbedPane.addTab(bundle.getString("BirthdaysDialog.panelRepositories.TabConstraints.tabTitle"), panelRepositories); // NOI18N
        tabbedPane.addTab(bundle.getString("BirthdaysDialog.panelTools.TabConstraints.tabTitle"), panelTools); // NOI18N
        tabbedPane.addTab(bundle.getString("BirthdaysDialog.panelPreferences.TabConstraints.tabTitle"), panelPreferences); // NOI18N

        panelAbout.setLayout(new GridBagLayout());

        labelAbout.setText(Bundle.getString(BirthdaysDialog.class, "BirthdaysDialog.About.Text", AppVersion.VERSION, AppVersion.DATE));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panelAbout.add(labelAbout, gridBagConstraints);

        tabbedPane.addTab(bundle.getString("BirthdaysDialog.panelAbout.TabConstraints.tabTitle"), panelAbout); // NOI18N

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        getContentPane().add(tabbedPane, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonEditPersonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_buttonEditPersonActionPerformed
        editSelectedPersons(tableAllPersons);
    }//GEN-LAST:event_buttonEditPersonActionPerformed

    private void buttonRemovePersonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_buttonRemovePersonActionPerformed
        removeSelectedPersons();
    }//GEN-LAST:event_buttonRemovePersonActionPerformed

    private void buttonAddPersonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_buttonAddPersonActionPerformed
        addPerson();
    }//GEN-LAST:event_buttonAddPersonActionPerformed

    private void buttonEmailActionPerformed(ActionEvent evt) {//GEN-FIRST:event_buttonEmailActionPerformed
        sendEmail();
    }//GEN-LAST:event_buttonEmailActionPerformed

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
    private JButton buttonAddPerson;
    private JButton buttonEditPerson;
    private JButton buttonEmail;
    private JButton buttonRemovePerson;
    private JLabel labelAbout;
    private JLabel labelFilterPerson;
    private JLabel labelHintDoubleclick;
    private JPanel panelAbout;
    private JPanel panelDates;
    private JPanel panelPersons;
    private PreferencesPanel panelPreferences;
    private RepositoriesPanel panelRepositories;
    private ToolsPanel panelTools;
    private JScrollPane scrollPaneAllPersons;
    private JScrollPane scrollPaneBirthdayAfter;
    private JScrollPane scrollPaneBirthdayBefore;
    private JScrollPane scrollPaneBirthdayToday;
    private JTabbedPane tabbedPane;
    private JTable tableAllPersons;
    private JTable tableBirthdayAfter;
    private JTable tableBirthdayBefore;
    private JTable tableBirthdayToday;
    private JTextField textFieldFilterPerson;
    // End of variables declaration//GEN-END:variables
}
