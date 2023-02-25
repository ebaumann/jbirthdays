package de.elmar_baumann.jbirthdays.xmlrepo;

import de.elmar_baumann.jbirthdays.api.Person;
import de.elmar_baumann.jbirthdays.api.PersonRepository;
import de.elmar_baumann.jbirthdays.api.Persons;
import de.elmar_baumann.jbirthdays.util.Bundle;
import java.awt.Component;
import java.io.File;
import java.util.Collection;
import java.util.LinkedList;

/**
 * @author Elmar Baumann
 */
public final class XmlFilePersonRepositoryImpl implements PersonRepository {

    @Override
    public Collection<? extends Person> findAll() {
        Persons persons = XmlFilePersonRepository.INSTANCE.findAll();
        return persons.getPersons();
    }

    @Override
    public void save(Collection<? extends Person> persons) {
        Persons ps = new Persons();
        ps.setPersons(new LinkedList<Person>(persons));
        XmlFilePersonRepository.INSTANCE.save(ps);
    }

    void setFile(File file) {
        if (this == null) {
            throw new NullPointerException("this == null");
        }
        XmlFilePersonRepository.INSTANCE.setFile(file);
    }

    @Override
    public Component getSettingsComponent() {
        return new XmlFilePersonRepositorySettingsPanel();
    }

    @Override
    public String getDisplayName() {
        return Bundle.getString(XmlFilePersonRepositoryImpl.class, "XmlFilePersonRepositoryImpl.Displayname");
    }

    @Override
    public String getUUid() {
        return XmlFilePersonRepository.getUuid();
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }
}
