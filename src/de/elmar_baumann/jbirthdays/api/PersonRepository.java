package de.elmar_baumann.jbirthdays.api;

import java.util.Collection;

/**
 * Methods may throw exceptions.
 *
 * @author Elmar Baumann
 */
public interface PersonRepository {

    Collection<? extends Person> findAll();

    void save(Collection<? extends Person> persons);

    boolean isPreferred();

    String getDisplayName();
}
