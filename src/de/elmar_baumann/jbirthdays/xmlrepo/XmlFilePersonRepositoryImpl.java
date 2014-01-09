package de.elmar_baumann.jbirthdays.xmlrepo;

import de.elmar_baumann.jbirthdays.api.Person;
import de.elmar_baumann.jbirthdays.api.PersonRepository;
import de.elmar_baumann.jbirthdays.util.Bundle;
import java.util.Collection;
import java.util.LinkedList;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = PersonRepository.class)
public final class XmlFilePersonRepositoryImpl implements PersonRepository {

    private final XmlFilePersonRepository repo = new XmlFilePersonRepository();

    @Override
    public Collection<? extends Person> findAll() {
        Persons persons = repo.findAll();
        return persons.getPersons();
    }

    @Override
    public void save(Collection<? extends Person> persons) {
        Persons ps = new Persons();
        ps.setPersons(new LinkedList<>(persons));
        repo.save(ps);
    }

    @Override
    public boolean isPreferred() {
        return true; // HOOK if multiple repositories provided, read e.g. settings value
    }

    @Override
    public String getDisplayName() {
        return Bundle.getString(XmlFilePersonRepositoryImpl.class, "XmlFilePersonRepositoryImpl.Displayname");
    }
}
