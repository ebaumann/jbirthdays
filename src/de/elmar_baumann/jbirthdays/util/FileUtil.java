package de.elmar_baumann.jbirthdays.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Elmar Baumann
 */
public final class FileUtil {

    public static List<String> getLines(File file) throws FileNotFoundException, IOException {
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        if (!file.isFile()) {
            return Collections.emptyList();
        }
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line;
            while((line = br.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    private FileUtil() {
    }
}
