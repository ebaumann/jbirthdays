package de.elmar_baumann.jbirthdays.util;

import java.io.Closeable;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Elmar Baumann
 */
public final class IoUtil {

    public static void close(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (IOException ex) {
            Logger.getLogger(IoUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private IoUtil() {
    }
}
