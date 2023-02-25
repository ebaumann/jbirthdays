package de.elmar_baumann.jbirthdays.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Uses the service provider interface to lookup all Implementations of
 * {@link PersonRepository}s.
 *
 * @author Elmar Baumann
 */
public final class PersonRepositoryLookup {

    /**
     * @return All Implementations of {@link PersonRepository}s.
     */
    public static Collection<PersonRepository> allRepositories() {
        Collection<PersonRepository> repositories = new ArrayList<>();
        ServiceLoader<PersonRepository> serviceLoader = ServiceLoader.load(PersonRepository.class);
        Iterator<PersonRepository> it = serviceLoader.iterator();
        while (it.hasNext()) {
            repositories.add(it.next());
        }
        return repositories;
    }

    private PersonRepositoryLookup() {
    }
}
