package de.elmar_baumann.jbirthdays.ui;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 * Usage: Call {@link JTable#setRowSorter(javax.swing.RowSorter)} with {@link #getRowSorter()}
 * and add this filter to a document as {@link DocumentListener}
 * (usually to a text field's document {@link JTextField#getDocument()}).
 *
 * @author Elmar Baumann
 */
public final class PersonSearchFilter implements DocumentListener {

    private final TableRowSorter<PersonTableModel> rowSorter;

    public PersonSearchFilter(PersonTableModel tableModel) {
        if (tableModel == null) {
            throw new NullPointerException("tableModel == null");
        }
        this.rowSorter = new TableRowSorter<>(tableModel);
    }

    public TableRowSorter<PersonTableModel> getRowSorter() {
        return rowSorter;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        updateSearchFilter(e);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        updateSearchFilter(e);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        updateSearchFilter(e);
    }

    private void updateSearchFilter(DocumentEvent e) {
        rowSorter.setRowFilter(new PersonNameRowFilter(getText(e)));
    }

    private String getText(DocumentEvent e) {
        Document doc = e.getDocument();
        int length = doc.getLength();
        if (length == 0) {
            return "";
        }
        try {
            return doc.getText(0, length - 1);
        } catch (BadLocationException ex) {
            Logger.getLogger(PersonSearchFilter.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }
}
