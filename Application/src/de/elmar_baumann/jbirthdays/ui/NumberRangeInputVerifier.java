package de.elmar_baumann.jbirthdays.ui;

import de.elmar_baumann.jbirthdays.util.Bundle;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.text.JTextComponent;

/**
 * @author Elmar Baumann
 */
public final class NumberRangeInputVerifier extends InputVerifier implements Serializable {

    private static final long serialVersionUID = 1L;
    private final int min;
    private final int max;
    private boolean message = true;

    public NumberRangeInputVerifier(int min, int max) {
        if (max < min) {
            throw new IllegalArgumentException("Maximum is less than minimum! " + max + " < " + min);
        }
        this.min = min;
        this.max = max;
    }

    public void setDisplayMessage(boolean display) {
        this.message = display;
    }

    @Override
    public boolean verify(JComponent component) {
        boolean lengthOk = lengthOk(component);
        if (!lengthOk) {
            errorMessage(component);
        }
        return lengthOk;
    }

    private boolean lengthOk(JComponent component) {
        String string = getString(component);
        if (string.isEmpty()) {
            return true;
        }
        Integer value = toInt(string);
        if (value == null) {
            return false;
        }
        return (value >= min) && (value <= max);
    }

    private String getString(JComponent component) {
        if (component instanceof JTextComponent) {
            return ((JTextComponent) component).getText().trim();
        }
        return "";
    }

    private Integer toInt(String string) {
        try {
            return Integer.valueOf(string);
        } catch (Throwable t) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "", t);
        }

        return null;
    }

    private void errorMessage(JComponent input) {
        if (!message) {
            return;
        }
        JOptionPane.showMessageDialog(input,
                Bundle.getString(NumberRangeInputVerifier.class, "NumberRangeInputVerifier.ErrorMessage", min, max),
                Bundle.getString(NumberRangeInputVerifier.class, "NumberRangeInputVerifier.Error.Title"),
                JOptionPane.ERROR_MESSAGE);
    }
}
