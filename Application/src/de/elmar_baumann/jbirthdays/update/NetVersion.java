package de.elmar_baumann.jbirthdays.update;

import de.elmar_baumann.jbirthdays.util.Version;
import de.elmar_baumann.jbirthdays.util.HttpUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Elmar Baumann
 */
public final class NetVersion {

    public static Version getOverHttp(String httpUrl, String versionDelimiter) throws MalformedURLException, IOException, NumberFormatException, IllegalArgumentException {
        if (httpUrl == null) {
            throw new NullPointerException("httpUrl == null");
        }
        URL url = new URL(httpUrl);
        ByteArrayOutputStream os = new ByteArrayOutputStream(10 * 1024);
        HttpUtil.write(url, os, null);
        String content = os.toString();
        int beginIndex = content.indexOf("<span class=\"version\">");
        if (beginIndex >= 0) {
            int endIndex = content.indexOf("</span>", beginIndex + 1);
            if (endIndex <= beginIndex) {
                return null;
            }
            String versionString = content.substring(beginIndex + 22, endIndex);
            return Version.parseVersion(versionString, versionDelimiter);
        }
        return null;
    }

    private NetVersion() {
    }
}
