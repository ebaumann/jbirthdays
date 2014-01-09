package de.elmar_baumann.jbirthdays.xmlrepo;

import de.elmar_baumann.jbirthdays.api.Person;
import de.elmar_baumann.jbirthdays.util.XmlUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * @author Elmar Baumann
 */
public final class XmlFilePersonRepository {

    private static final String DEFAULT_FILENAME = System.getProperty("user.home")
            + File.separator + ".de.elmar_baumann" + File.separator
            + "JBirthdays" + File.separator + "Persons.xml";
    private final File xmlFile;

    public XmlFilePersonRepository() {
        this(new File(DEFAULT_FILENAME));
    }

    public XmlFilePersonRepository(File xmlFile) {
        if (xmlFile == null) {
            throw new NullPointerException("xmlFile == null");
        }
        this.xmlFile = xmlFile;
    }

    public Persons findAll() {
        if (!xmlFile.exists()) {
            return new Persons();
        }
        try (FileInputStream fis = new FileInputStream(xmlFile)) {
            return XmlUtil.unmarshal(fis, Persons.class);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void save(Persons persons) {
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

    private void ensureDir() {
        File dir = xmlFile.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
            if (!dir.exists()) {
                throw new RuntimeException("Can't create directory " + dir);
            }
        }
    }
}
