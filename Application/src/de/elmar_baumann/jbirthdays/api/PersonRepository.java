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

    /**
     * If multiple implementations existing, the preferred repository will be used.
     * @return
     */
    boolean isPreferred();

    String getDisplayName();
}
