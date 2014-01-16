package de.elmar_baumann.jbirthdays.ui;

import de.elmar_baumann.jbirthdays.api.Person;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

/**
 * @author Elmar Baumann
 */
public final class PersonsTableCellRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;
    private final Color alternateBackground = new Color(0xdfdfdf);
    private final Color defaultBackground;
    private final Color defaultForeground;
    private final Font defaultFont;
    private final Font notDisplayFont;

    public PersonsTableCellRenderer() {
        defaultBackground = getBackground();
        defaultFont = getFont();
        notDisplayFont = defaultFont.deriveFont(Font.ITALIC);
        defaultForeground = getForeground();
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (!isSelected) {
            setBackground(row % 2 == 0 ? defaultBackground : alternateBackground);
        }
        setFont(table, row);
        return label;
    }

    private void setFont(JTable table, int row) {
        TableModel model = table.getModel();
        if (model instanceof PersonTableModel) {
            Person person = ((PersonTableModel) model).getPerson(row);
            setFont(person.isDisplay() ? defaultFont : notDisplayFont);
            setForeground(person.isDisplay() ? defaultForeground : Color.GRAY);
        }
    }
}
