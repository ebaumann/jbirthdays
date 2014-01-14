package de.elmar_baumann.jbirthdays.xmlrepo;

import de.elmar_baumann.jbirthdays.api.Person;
import de.elmar_baumann.jbirthdays.api.Persons;
import de.elmar_baumann.jbirthdays.api.RepositoryChangedEvent;
import de.elmar_baumann.jbirthdays.util.XmlUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.prefs.Preferences;
import org.bushe.swing.event.EventBus;

/**
 * @author Elmar Baumann
 */
public final class XmlFilePersonRepository {

    private static final String KEY_FILENAME = "XmlFilePersonRepository.Filename";
    private static final String DEFAULT_FILENAME = System.getProperty("user.home")
            + File.separator + ".de.elmar_baumann" + File.separator
            + "JBirthdays" + File.separator + "Persons.xml";
    private File xmlFile;
    public static final XmlFilePersonRepository INSTANCE = new XmlFilePersonRepository();

    static String getFilename() {
        Preferences prefs = Preferences.userNodeForPackage(XmlFilePersonRepository.class);
        return prefs.get(KEY_FILENAME, DEFAULT_FILENAME);
    }

    private XmlFilePersonRepository() {
        this(new File(getFilename()));
    }

    XmlFilePersonRepository(File xmlFile) {
        if (xmlFile == null) {
            throw new NullPointerException("xmlFile == null");
        }
        this.xmlFile = xmlFile;
    }

    public synchronized Persons findAll() {
        if (!xmlFile.exists()) {
            return new Persons();
        }
        try (FileInputStream fis = new FileInputStream(xmlFile)) {
            return XmlUtil.unmarshal(fis, Persons.class);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public synchronized void save(Persons persons) {
        if (persons == null) {
            throw new NullPointerException("persons == null");
        }
        ensureDir();
        persons.sort(Person.CMP_ASC_BY_LAST_NAME);
        try (FileOutputStream fos = new FileOutputStream(xmlFile)) {
            XmlUtil.marshal(persons, fos);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private synchronized void ensureDir() {
        File dir = xmlFile.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
            if (!dir.exists()) {
                throw new RuntimeException("Can't create directory " + dir);
            }
        }
    }

    synchronized File getFile() {
        return xmlFile;
}

    void setFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        synchronized (this) {
            xmlFile = file;
            persistFilename(file);
        }
        EventBus.publish(new RepositoryChangedEvent(RepositoryChangedEvent.Type.LOCATION, this));
    }

    static String getUuid() {
        return XmlFilePersonRepository.class.getName();
    }

    private static void persistFilename(File file) {
        Preferences prefs = Preferences.userNodeForPackage(XmlFilePersonRepository.class);
        prefs.put(KEY_FILENAME, file.getAbsolutePath());
    }
}
