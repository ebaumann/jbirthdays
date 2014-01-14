package de.elmar_baumann.jbirthdays.xmlrepo;

import de.elmar_baumann.jbirthdays.api.Person;
import de.elmar_baumann.jbirthdays.api.Persons;
import java.io.File;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Elmar Baumann
 */
public class XmlFilePersonRepositoryTest {

    private static final File xmlFile = new File(System.getProperty("java.io.tmpdir") + File.separator + "XmlFilePersonsRepositoryTest.xml");

    @Test
    public void testFindAll() {
        Person p1 = new Person();
        Person p2 = new Person();
        p1.setFirstName("Peter");
        p1.setLastName("Miller");
        p2.setFirstName("James");
        p2.setLastName("Bond");
        Persons persons = new Persons();
        persons.setPersons(Arrays.asList(p1, p2));
        try {
            XmlFilePersonRepository repo = new XmlFilePersonRepository(xmlFile);
            repo.save(persons);
            Persons foundPersons = repo.findAll();
            Assert.assertTrue(foundPersons.getPersons().containsAll(persons.getPersons()));
        } finally {
            xmlFile.delete();
        }
    }

    @Test
    public void testSave() {
        Person p = new Person();
        p.setFirstName("Peter");
        p.setLastName("Miller");
        Persons persons = new Persons();
        persons.setPersons(Arrays.asList(p));
        try {
            XmlFilePersonRepository repo = new XmlFilePersonRepository(xmlFile);
            repo.save(persons);
            Persons foundPersons = repo.findAll();
            Assert.assertTrue(foundPersons.getPersons().containsAll(persons.getPersons()));
            repo.save(new Persons());
            foundPersons = repo.findAll();
            Assert.assertTrue(foundPersons.getPersons().isEmpty());
        } finally {
            xmlFile.delete();
        }
    }

}
