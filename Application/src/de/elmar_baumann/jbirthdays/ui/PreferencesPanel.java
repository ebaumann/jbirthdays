package de.elmar_baumann.jbirthdays.ui;

import de.elmar_baumann.jbirthdays.update.UpdateDownload;
import de.elmar_baumann.jbirthdays.util.Mnemonics;
import java.util.prefs.Preferences;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Elmar Baumann
 */
public class PreferencesPanel extends javax.swing.JPanel {

    public static final String PROPERTY_DAYS_BEFORE = "daysBefore";
    public static final String PROPERTY_DAYS_AFTER = "daysAfter";

    private static final long serialVersionUID = 1L;
    private static final String KEY_BEFORE = "DaysBefore";
    private static final String KEY_AFTER = "DaysAfter";
    private static final String KEY_IS_SCALE_FONTS = "ScaleFonts";
    private static final String KEY_FONT_SCALE_FACTOR = "FontScaleFactor";

    public PreferencesPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        Mnemonics.setMnemonics(this);
        readPreferences();
        spinnerBefore.getModel().addChangeListener(daysBeforeChangedListener);
        spinnerAfter.getModel().addChangeListener(daysBeforeChangedListener);
        spinnerFontScaleFactor.getModel().addChangeListener(settingsChangedListener);
        checkBoxAutoUpdate.setSelected(UpdateDownload.isCheckForUpdates());
    }

    private void readPreferences() {
        Preferences prefs = Preferences.userNodeForPackage(PreferencesPanel.class);
        SpinnerNumberModel beforeModel = (SpinnerNumberModel) spinnerBefore.getModel();
        SpinnerNumberModel afterModel = (SpinnerNumberModel) spinnerAfter.getModel();
        SpinnerNumberModel fontScaleModel = (SpinnerNumberModel) spinnerFontScaleFactor.getModel();

        beforeModel.setValue(prefs.getInt(KEY_BEFORE, 7));
        afterModel.setValue(prefs.getInt(KEY_AFTER, 3));
        fontScaleModel.setValue(getFontScaleFactor());
        checkBoxScaleFonts.setSelected(isScaleFonts());

        enableFontScaleFactor();
    }

    private void writePreferences() {
        Preferences prefs = Preferences.userNodeForPackage(PreferencesPanel.class);
        SpinnerNumberModel beforeModel = (SpinnerNumberModel) spinnerBefore.getModel();
        SpinnerNumberModel afterModel = (SpinnerNumberModel) spinnerAfter.getModel();
        SpinnerNumberModel fontScaleModel = (SpinnerNumberModel) spinnerFontScaleFactor.getModel();

        prefs.putInt(KEY_BEFORE, beforeModel.getNumber().intValue());
        prefs.putInt(KEY_AFTER, afterModel.getNumber().intValue());
        prefs.putBoolean(KEY_IS_SCALE_FONTS, checkBoxScaleFonts.isSelected());
        prefs.putFloat(KEY_FONT_SCALE_FACTOR, fontScaleModel.getNumber().floatValue());
    }

    private void setAutoUpdate() {
        UpdateDownload.setCheckForUpdates(checkBoxAutoUpdate.isSelected());
    }

    public int getDaysBefore() {
        SpinnerNumberModel beforeModel = (SpinnerNumberModel) spinnerBefore.getModel();
        return beforeModel.getNumber().intValue();
    }

    public int getDaysAfter() {
        SpinnerNumberModel afterModel = (SpinnerNumberModel) spinnerAfter.getModel();
        return afterModel.getNumber().intValue();
    }

    public static boolean isScaleFonts() {
        Preferences prefs = Preferences.userNodeForPackage(PreferencesPanel.class);
        return prefs.getBoolean(KEY_IS_SCALE_FONTS, false);
    }

    public static float getFontScaleFactor() {
        float fontScaleFactor = LookAndFeel.getDefaultFontScaleFactor();
        if (isScaleFonts()) {
            Preferences prefs = Preferences.userNodeForPackage(PreferencesPanel.class);
            fontScaleFactor = prefs.getFloat(KEY_FONT_SCALE_FACTOR, fontScaleFactor);
        }
        return fontScaleFactor;
    }

    public void enableFontScaleFactor() {
        spinnerFontScaleFactor.setEnabled(checkBoxScaleFonts.isSelected());
    }

    private final ChangeListener daysBeforeChangedListener = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
            SpinnerNumberModel beforeModel = (SpinnerNumberModel) spinnerBefore.getModel();
            SpinnerNumberModel afterModel = (SpinnerNumberModel) spinnerAfter.getModel();
            Preferences prefs = Preferences.userNodeForPackage(PreferencesPanel.class);
            Object source = e.getSource();
            if (source == beforeModel) {
                firePropertyChange(PROPERTY_DAYS_BEFORE, prefs.getInt(KEY_BEFORE, 7), beforeModel.getNumber().intValue() );
            } else if (source == afterModel) {
                firePropertyChange(PROPERTY_DAYS_AFTER, prefs.getInt(KEY_AFTER, 3), beforeModel.getNumber().intValue() );
            }
            writePreferences();
        }
    };

    private final ChangeListener settingsChangedListener = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
            writePreferences();
        }
    };

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        labelBefore = new javax.swing.JLabel();
        spinnerBefore = new javax.swing.JSpinner();
        labelAfter = new javax.swing.JLabel();
        spinnerAfter = new javax.swing.JSpinner();
        checkBoxAutoUpdate = new javax.swing.JCheckBox();
        panelFontScale = new javax.swing.JPanel();
        checkBoxScaleFonts = new javax.swing.JCheckBox();
        labelFontScaleFactor = new javax.swing.JLabel();
        spinnerFontScaleFactor = new javax.swing.JSpinner();
        panelFill = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        labelBefore.setLabelFor(spinnerBefore);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/elmar_baumann/jbirthdays/ui/Bundle"); // NOI18N
        labelBefore.setText(bundle.getString("PreferencesPanel.labelBefore.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        add(labelBefore, gridBagConstraints);

        spinnerBefore.setModel(new javax.swing.SpinnerNumberModel(7, 1, 31, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 0, 10);
        add(spinnerBefore, gridBagConstraints);

        labelAfter.setLabelFor(spinnerAfter);
        labelAfter.setText(bundle.getString("PreferencesPanel.labelAfter.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 10, 0, 0);
        add(labelAfter, gridBagConstraints);

        spinnerAfter.setModel(new javax.swing.SpinnerNumberModel(3, 1, 31, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.insets = new java.awt.Insets(7, 5, 0, 10);
        add(spinnerAfter, gridBagConstraints);

        checkBoxAutoUpdate.setText(bundle.getString("PreferencesPanel.checkBoxAutoUpdate.text")); // NOI18N
        checkBoxAutoUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxAutoUpdateActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 10, 0, 0);
        add(checkBoxAutoUpdate, gridBagConstraints);

        panelFontScale.setLayout(new java.awt.GridBagLayout());

        checkBoxScaleFonts.setText(bundle.getString("PreferencesPanel.checkBoxScaleFonts.text")); // NOI18N
        checkBoxScaleFonts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxScaleFontsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelFontScale.add(checkBoxScaleFonts, gridBagConstraints);

        labelFontScaleFactor.setLabelFor(spinnerFontScaleFactor);
        labelFontScaleFactor.setText(bundle.getString("PreferencesPanel.labelFontScaleFactor.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        panelFontScale.add(labelFontScaleFactor, gridBagConstraints);

        spinnerFontScaleFactor.setModel(new javax.swing.SpinnerNumberModel(1.0, 0.5, 5.0, 0.5));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panelFontScale.add(spinnerFontScaleFactor, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 10, 0, 0);
        add(panelFontScale, gridBagConstraints);

        javax.swing.GroupLayout panelFillLayout = new javax.swing.GroupLayout(panelFill);
        panelFill.setLayout(panelFillLayout);
        panelFillLayout.setHorizontalGroup(
            panelFillLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelFillLayout.setVerticalGroup(
            panelFillLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        add(panelFill, gridBagConstraints);
    }//GEN-END:initComponents

    private void checkBoxAutoUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxAutoUpdateActionPerformed
        setAutoUpdate();
    }//GEN-LAST:event_checkBoxAutoUpdateActionPerformed

    private void checkBoxScaleFontsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxScaleFontsActionPerformed
        enableFontScaleFactor();
        writePreferences();
    }//GEN-LAST:event_checkBoxScaleFontsActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox checkBoxAutoUpdate;
    private javax.swing.JCheckBox checkBoxScaleFonts;
    private javax.swing.JLabel labelAfter;
    private javax.swing.JLabel labelBefore;
    private javax.swing.JLabel labelFontScaleFactor;
    private javax.swing.JPanel panelFill;
    private javax.swing.JPanel panelFontScale;
    private javax.swing.JSpinner spinnerAfter;
    private javax.swing.JSpinner spinnerBefore;
    private javax.swing.JSpinner spinnerFontScaleFactor;
    // End of variables declaration//GEN-END:variables

}
