package de.elmar_baumann.jbirthdays.xmlrepo;

import de.elmar_baumann.jbirthdays.api.Person;
import de.elmar_baumann.jbirthdays.api.Persons;
import de.elmar_baumann.jbirthdays.api.RepositoryChangedEvent;
import de.elmar_baumann.jbirthdays.util.Bundle;
import de.elmar_baumann.jbirthdays.util.XmlUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;
import org.bushe.swing.event.EventBus;

/**
 * @author Elmar Baumann
 */
public final class XmlFilePersonRepository {

    private static final String KEY_FILENAME = "XmlFilePersonRepository.Filename";
    private static final String DEFAULT_FILENAME = System.getProperty("user.home")
            + File.separator + ".de.elmar_baumann" + File.separator
            + "JBirthdays" + File.separator + "JBirthdays-Persons.xml";
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
        Logger.getLogger(XmlFilePersonRepository.class.getName()).log(Level.INFO, "Loading persons from XML file ''{0}''", xmlFile);
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
        Logger.getLogger(XmlFilePersonRepository.class.getName()).log(Level.INFO, "Saving {0} persons to XML file ''{1}''", new Object[]{persons.getCount(), xmlFile});
        try (FileOutputStream fos = new FileOutputStream(xmlFile)) {
            XmlUtil.marshal(persons, fos);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private synchronized void ensureDir() {
        File dir = xmlFile.getParentFile();
        if (!dir.exists()) {
            Logger.getLogger(XmlFilePersonRepository.class.getName()).log(Level.INFO, "Creating directory ''{0}''", dir);
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
        if (!checkFileUi(file)) {
            return;
        }
            Logger.getLogger(XmlFilePersonRepository.class.getName()).log(Level.INFO, "Switching from database file ''{0}'' to database file ''{1}''", new Object[]{xmlFile, file});
        synchronized (this) {
            xmlFile = file;
            persistFilename(file);
        }
        EventBus.publish(new RepositoryChangedEvent(RepositoryChangedEvent.Type.LOCATION, this));
    }

    public boolean checkFileUi(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            XmlUtil.unmarshal(fis, Persons.class);
            return true;
        } catch (Throwable t) {
            String message = Bundle.getString(XmlFilePersonRepository.class, "XmlFilePersonRepository.CheckFile.Thrown", file.getName());
            String title = Bundle.getString(XmlFilePersonRepository.class, "XmlFilePersonRepository.CheckFile.Thrown.Title");
            JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    static String getUuid() {
        return XmlFilePersonRepository.class.getName();
    }

    private static void persistFilename(File file) {
        Preferences prefs = Preferences.userNodeForPackage(XmlFilePersonRepository.class);
        prefs.put(KEY_FILENAME, file.getAbsolutePath());
    }

    synchronized void moveTo(File dir) throws IOException {
        if (dir == null) {
            throw new NullPointerException("dir == null");
        }
        if (!dir.isDirectory() || dir.equals(xmlFile.getParentFile())) {
            return;
        }
        File newFile = getNewFile(dir);
        Logger.getLogger(XmlFilePersonRepository.class.getName()).log(Level.INFO, "Moving database ''{0}'' to directory ''{1}''", new Object[]{xmlFile, dir});
        Files.copy(xmlFile.toPath(), newFile.toPath());
        xmlFile.delete();
        setFile(newFile);
    }

    private synchronized File getNewFile(File dir) {
        String name = xmlFile.getName();
        File newFile = new File(dir.getAbsolutePath() + File.separator + name);
        return newFile.exists()
                ? new File(dir.getAbsolutePath() + File.separator + new SimpleDateFormat("-yyyy-MM-dd_HH-mm-ss").format(new Date()) + ".xml")
                : newFile;
    }
}
