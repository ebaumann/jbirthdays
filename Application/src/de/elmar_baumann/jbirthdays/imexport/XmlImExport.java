package de.elmar_baumann.jbirthdays.imexport;

import de.elmar_baumann.jbirthdays.api.Person;
import de.elmar_baumann.jbirthdays.api.Persons;
import de.elmar_baumann.jbirthdays.util.XmlUtil;
import de.elmar_baumann.jbirthdays.xmlrepo.XmlFilePersonRepository;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import javax.xml.bind.JAXBException;

/**
 * @author Elmar Baumann
 */
public final class XmlImExport {

    public static void exportToFile(Collection<? extends Person> persons, File file) throws FileNotFoundException, IOException, JAXBException {
        if (persons == null) {
            throw new NullPointerException("persons == null");
        }
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        Persons p = new Persons();
        p.setPersons(new ArrayList<Person>(persons));
        try (FileOutputStream fos = new FileOutputStream(file)) {
            XmlUtil.marshal(p, fos);
        }
    }

    public static Collection<? extends Person> importFromFile(File file) throws JAXBException, UnsupportedEncodingException, FileNotFoundException, IOException {
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        if (!XmlFilePersonRepository.INSTANCE.checkFileUi(file)) {
            return Collections.emptyList();
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            Persons persons = XmlUtil.unmarshal(fis, Persons.class);
            return persons.getPersons();
        }
    }

    private XmlImExport() {
    }
}
