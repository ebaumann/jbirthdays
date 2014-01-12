package de.elmar_baumann.jbirthdays.ui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * @author Elmar Baumann
 */
public final class OnlyDigitsDocumentFilter extends DocumentFilter {

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        if (onlyDigitsOrEmpty(text)) {
            super.replace(fb, offset, length, text, attrs);
        }
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        if (onlyDigitsOrEmpty(string)) {
            super.insertString(fb, offset, string, attr);
        }
    }

    private boolean onlyDigitsOrEmpty(String s) {
        if (s.isEmpty()) {
            return true;
        }
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
