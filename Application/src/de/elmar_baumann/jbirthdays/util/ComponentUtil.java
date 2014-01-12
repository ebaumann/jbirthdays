package de.elmar_baumann.jbirthdays.util;

import de.elmar_baumann.jbirthdays.ui.Dialog;
import java.awt.Component;

/**
 * @author Elmar Baumann
 */
public final class ComponentUtil {

    public static Dialog findParentDialog(Component c) {
        if (c == null) {
            throw new NullPointerException("c == null");
        }
        Component parent = c.getParent();
        while (parent != null && !(parent instanceof Dialog)) {
            parent = parent.getParent();
        }
        return parent instanceof Dialog
                ? (Dialog) parent
                : null;
    }

    private ComponentUtil() {
    }
}
