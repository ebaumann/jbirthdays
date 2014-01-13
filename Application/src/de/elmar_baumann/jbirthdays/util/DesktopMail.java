package de.elmar_baumann.jbirthdays.util;

import java.awt.Desktop;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public final class DesktopMail {

    private static final String URI_TEMPLATE = "mailto:%s?subject=%s&body=%s";

    public static void sendEmail(String receipient, String subject, String body) {
        if (receipient == null) {
            throw new NullPointerException("receipient == null");
        }
        if (subject == null) {
            throw new NullPointerException("subject == null");
        }
        if (body == null) {
            throw new NullPointerException("body == null");
        }
        if (desktopSupportsEMail()) {
            String encodedSubject = urlEncode(subject);
            String encodedBody = urlEncode(body);
            String uriString = String.format(URI_TEMPLATE, receipient, encodedSubject, encodedBody);
            try {
                Desktop.getDesktop().browse(new URI(uriString));
            } catch (Throwable t) {
                Logger.getLogger(DesktopMail.class.getName()).log(Level.SEVERE, null, t);
                errorMessage(Bundle.getString(DesktopMail.class, "DesktopMail.Error.Browse", t.getLocalizedMessage()));
            }
        } else {
            errorMessage(Bundle.getString(DesktopMail.class, "DesktopMail.Error.NoDesktopSupport"));
        }
    }

    private static void errorMessage(String message) {
        JOptionPane.showMessageDialog(null, message,
                Bundle.getString(DesktopMail.class, "DesktopMail.Error.Title"),
                JOptionPane.ERROR_MESSAGE);
    }

    private static String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean desktopSupportsEMail() {
        return Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.MAIL);
    }

    private DesktopMail() {
    }
}
