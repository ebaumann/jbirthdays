package de.elmar_baumann.jbirthdays.ui;

import de.elmar_baumann.jbirthdays.util.WindowPersistence;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

/**
 * @author Elmar Baumann
 */
public class Dialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private transient ActionListener escapeActionListener;

    public Dialog() {
        this(null, true);
    }

    public Dialog(java.awt.Dialog owner, boolean modal) {
        super(owner, modal);
        init();
    }

    private void init() {
        createEscapeActionListener();
        createWindowClosingListener();
        registerKeyStrokes();
    }

    @Override
    public void setVisible(final boolean visible) {
        if (visible) {
            restoreSizeAndLocation();
        } else {
            persistSizeAndLocation();
        }
        super.setVisible(visible);
    }

    @Override
    public void dispose() {
        persistSizeAndLocation();
        super.dispose();
    }

    private void persistSizeAndLocation() {
        Class<?> clazz = getClass();
        String key = getWindowPersistenceKey();
        WindowPersistence.persistSize(clazz, key, this);
        WindowPersistence.persistLocation(clazz, key, this);
    }

    private void restoreSizeAndLocation() {
            pack();
            Class<?> clazz = getClass();
            String key = getWindowPersistenceKey();
            WindowPersistence.restoreSize(clazz, key, this);
            if (!WindowPersistence.restoreLocation(clazz, key, this)) {
                setLocationRelativeTo(getParent());
            }
    }

    private String getWindowPersistenceKey() {
        return getClass().getName();
    }

    /**
     * Called if ESC was pressed. This implementation calls {@link #dispose()}.
     */
    protected void escape() {
        dispose();
    }

    private void registerKeyStrokes() {
        for (Component component : getComponents()) {
            if (component instanceof JComponent) {
                JComponent c = (JComponent) component;
                KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
                c.registerKeyboardAction(escapeActionListener, escapeKeyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
            }
        }
    }

    @Override
    protected JRootPane createRootPane() {
        JRootPane pane = new JRootPane();
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        pane.registerKeyboardAction(escapeActionListener, escapeKeyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        return pane;
    }

    private void createEscapeActionListener() {
        escapeActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                escape();
            }
        };
    }

    private void createWindowClosingListener() {
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                persistSizeAndLocation();
            }
        });
    }
}
