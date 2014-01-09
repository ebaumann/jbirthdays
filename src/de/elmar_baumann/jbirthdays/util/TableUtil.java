package de.elmar_baumann.jbirthdays.util;

import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * @author Elmar Baumann
 */
public final class TableUtil {

    /**
     * {@link JTable#setAutoResizeMode(int)} should be called with
     * {@link JTable#AUTO_RESIZE_OFF}.
     * @param table
     * @param widths
     */
    public static void setColumnWidths(JTable table, int... widths) {
        if (table == null) {
            throw new NullPointerException("table == null");
        }
        if (widths == null) {
            throw new NullPointerException("widths == null");
        }
        TableColumnModel colModel = table.getColumnModel();
        int colCount = table.getModel().getColumnCount();
        int widthCount = widths.length;
        for (int colIndex = 0; (colIndex < colCount) && (colIndex < widthCount); colIndex++) {
            TableColumn column = colModel.getColumn(colIndex);
            int width = widths[colIndex];
            column.setPreferredWidth(width);
            column.setWidth(width);
        }
    }

    private TableUtil() {
    }
}
