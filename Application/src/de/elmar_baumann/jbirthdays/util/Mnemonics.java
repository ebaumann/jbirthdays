package de.elmar_baumann.jbirthdays.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;

/**
 * @author Elmar Baumann
 */
public final class Mnemonics {

    private static final Map<Character, Integer> MNEMONIC_OF_CHAR = new HashMap<>();

    static {
        MNEMONIC_OF_CHAR.put('0', KeyEvent.VK_0);
        MNEMONIC_OF_CHAR.put('1', KeyEvent.VK_1);
        MNEMONIC_OF_CHAR.put('2', KeyEvent.VK_2);
        MNEMONIC_OF_CHAR.put('3', KeyEvent.VK_3);
        MNEMONIC_OF_CHAR.put('4', KeyEvent.VK_4);
        MNEMONIC_OF_CHAR.put('5', KeyEvent.VK_5);
        MNEMONIC_OF_CHAR.put('6', KeyEvent.VK_6);
        MNEMONIC_OF_CHAR.put('7', KeyEvent.VK_7);
        MNEMONIC_OF_CHAR.put('8', KeyEvent.VK_8);
        MNEMONIC_OF_CHAR.put('9', KeyEvent.VK_9);
        MNEMONIC_OF_CHAR.put('A', KeyEvent.VK_A);
        MNEMONIC_OF_CHAR.put('B', KeyEvent.VK_B);
        MNEMONIC_OF_CHAR.put('C', KeyEvent.VK_C);
        MNEMONIC_OF_CHAR.put('D', KeyEvent.VK_D);
        MNEMONIC_OF_CHAR.put('E', KeyEvent.VK_E);
        MNEMONIC_OF_CHAR.put('F', KeyEvent.VK_F);
        MNEMONIC_OF_CHAR.put('G', KeyEvent.VK_G);
        MNEMONIC_OF_CHAR.put('H', KeyEvent.VK_H);
        MNEMONIC_OF_CHAR.put('I', KeyEvent.VK_I);
        MNEMONIC_OF_CHAR.put('J', KeyEvent.VK_J);
        MNEMONIC_OF_CHAR.put('K', KeyEvent.VK_K);
        MNEMONIC_OF_CHAR.put('L', KeyEvent.VK_L);
        MNEMONIC_OF_CHAR.put('M', KeyEvent.VK_M);
        MNEMONIC_OF_CHAR.put('N', KeyEvent.VK_N);
        MNEMONIC_OF_CHAR.put('O', KeyEvent.VK_O);
        MNEMONIC_OF_CHAR.put('P', KeyEvent.VK_P);
        MNEMONIC_OF_CHAR.put('Q', KeyEvent.VK_Q);
        MNEMONIC_OF_CHAR.put('R', KeyEvent.VK_R);
        MNEMONIC_OF_CHAR.put('S', KeyEvent.VK_S);
        MNEMONIC_OF_CHAR.put('T', KeyEvent.VK_T);
        MNEMONIC_OF_CHAR.put('U', KeyEvent.VK_U);
        MNEMONIC_OF_CHAR.put('V', KeyEvent.VK_V);
        MNEMONIC_OF_CHAR.put('W', KeyEvent.VK_W);
        MNEMONIC_OF_CHAR.put('X', KeyEvent.VK_X);
        MNEMONIC_OF_CHAR.put('Y', KeyEvent.VK_Y);
        MNEMONIC_OF_CHAR.put('Z', KeyEvent.VK_Z);
    }

    public static void setMnemonics(Container container) {
        if (container == null) {
            throw new NullPointerException("container == null");
        }
        int count = container.getComponentCount();
        setMnemonics((Component) container);
        for (int i = 0; i < count; i++) {
            Component component = container.getComponent(i);
            setMnemonics(component);
            if (component instanceof Container) {
                setMnemonics((Container) component);    // Recursive
            }
        }
    }

    public static void setMnemonics(Component component) {
        if (component == null) {
            throw new NullPointerException("component == null");
        }
        if (component instanceof JLabel) {
            JLabel label = (JLabel) component;
            String text = label.getText();
            if (text != null && !isHtmlText(text)) {
                MnemonicIndexString mnemonicIndexString = getMnemonic(text);
                if (hasMnemonic(mnemonicIndexString)) {
                    label.setText(mnemonicIndexString.string);
                    label.setDisplayedMnemonic(mnemonicIndexString.index);
                }
            }
        } else if (component instanceof AbstractButton) {
            AbstractButton button = (AbstractButton) component;
            String text = button.getText();
            if (text != null && !isHtmlText(text)) {
                MnemonicIndexString mnemonicIndexString = getMnemonic(text);
                if (hasMnemonic(mnemonicIndexString)) {
                    button.setText(mnemonicIndexString.string);
                    button.setMnemonic(mnemonicIndexString.index);
                }
            }
        } else if (component instanceof JTabbedPane) {
            setMnemonics((JTabbedPane) component);
        }
    }

    private  static MnemonicIndexString getMnemonic(String string) {
        MnemonicIndexString noMnemonicIndexString = new MnemonicIndexString(-1, string);
        if (string.length() < 2) {
            return noMnemonicIndexString;
        }
        int strlen = string.length();
        int ampersandIndex = string.indexOf('&');
        if (ampersandIndex < 0) {
            return noMnemonicIndexString;
        }
        if ((strlen < 2) || (ampersandIndex < 0) || (ampersandIndex > strlen - 2)) {
            return noMnemonicIndexString;
        }
        char mnemonicChar = string.substring(ampersandIndex + 1, ampersandIndex + 2).toUpperCase().charAt(0);
        boolean isInRange = isInRange(mnemonicChar);
        assert isInRange : "Not in Range: " + mnemonicChar + " of " + string;
        if (isInRange) {
            int mnemonic = getMnemonicOf(mnemonicChar);
            String titlePrefix = (ampersandIndex == 0)
                    ? ""
                    : string.substring(0, ampersandIndex);
            String titlePostfix = (ampersandIndex == strlen - 1)
                    ? ""
                    : string.substring(ampersandIndex + 1);
            return new MnemonicIndexString(mnemonic, titlePrefix + titlePostfix);
        }
        return noMnemonicIndexString;
    }

    private static boolean isInRange(char c) {
        return MNEMONIC_OF_CHAR.containsKey(c);
    }

    private static boolean isHtmlText(String string) {
        String lcString = string == null ? "" : string.toLowerCase().trim();
        return lcString.startsWith("<html");
    }

    private static boolean hasMnemonic(MnemonicIndexString p) {
        return MNEMONIC_OF_CHAR.containsValue(p.index);
    }

    private static int getMnemonicOf(char c) {
        return MNEMONIC_OF_CHAR.get(c);
    }

    private static void setMnemonics(JTabbedPane pane) {
        int tabCount = pane.getTabCount();
        for (int tabIndex = 0; tabIndex < tabCount; tabIndex++) {
            String title = pane.getTitleAt(tabIndex);
            if ((title != null) && (title.length() > 1)) {
                MnemonicIndexString mnemonicIndexString = getMnemonic(title);
                if (mnemonicIndexString.index != -1) {
                    pane.setTitleAt(tabIndex, mnemonicIndexString.string);
                    pane.setMnemonicAt(tabIndex, mnemonicIndexString.index);
                }
            }
        }
    }

    private static final class MnemonicIndexString {

        final int index;
        final String string;

        private MnemonicIndexString(int index, String string) {
            this.index = index;
            this.string = string;
        }
    }

    private Mnemonics() {
    }
}
