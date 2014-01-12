package de.elmar_baumann.jbirthdays.util;

import java.util.prefs.Preferences;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * @author Elmar Baumann
 */
public final class TableUtil {

    private static final String PREFIX_KEY_COLUMN_WIDTH = "TableUtil.ColumnWidth.";

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

    public static void persistColumnWidths(String key, JTable table) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        if (table == null) {
            throw new NullPointerException("table == null");
        }
        TableColumnModel columnModel = table.getColumnModel();
        int colCount = table.getModel().getColumnCount();
        Preferences prefs = Preferences.userNodeForPackage(TableUtil.class);
        for (int colIndex = 0; colIndex < colCount; colIndex++) {
            TableColumn column = columnModel.getColumn(colIndex);
            int width = Math.max(column.getPreferredWidth(), column.getWidth());
            prefs.putInt(createColumnKey(key, colIndex), width);
        }
    }

    public static void restoreColumnWidths(String key, JTable table, int[] defaultWidths) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        if (table == null) {
            throw new NullPointerException("table == null");
        }
        if (defaultWidths == null) {
            throw new NullPointerException("defaultWidths == null");
        }
        TableColumnModel columnModel = table.getColumnModel();
        int colCount = table.getModel().getColumnCount();
        Preferences prefs = Preferences.userNodeForPackage(TableUtil.class);
        for (int colIndex = 0; colIndex < colCount; colIndex++) {
            int width = prefs.getInt(createColumnKey(key, colIndex), -1);
            TableColumn column = columnModel.getColumn(colIndex);
            if (width > 0) {
                column.setWidth(width);
                column.setPreferredWidth(width);
            } else if (colIndex < defaultWidths.length) {
                int defaultWidth = defaultWidths[colIndex];
                column.setPreferredWidth(defaultWidth);
                column.setWidth(defaultWidth);
            }
        }
    }

    private static String createColumnKey(String key, int columnIndex) {
        return PREFIX_KEY_COLUMN_WIDTH + '.' + key + '.' + String.valueOf(columnIndex);
    }

    private TableUtil() {
    }
}
