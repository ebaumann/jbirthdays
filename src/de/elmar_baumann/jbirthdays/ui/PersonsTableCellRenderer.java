package de.elmar_baumann.jbirthdays.ui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * @author Elmar Baumann
 */
public final class PersonsTableCellRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;
    private final Color alternateBackground = new Color(0xdfdfdf);
    private final Color defaultBackground;

    public PersonsTableCellRenderer() {
        defaultBackground = getBackground();
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (!isSelected) {
            setBackground(row % 2 == 0 ? defaultBackground : alternateBackground);
        }
        return label;
    }
}
