package de.elmar_baumann.jbirthdays.api;

import java.awt.Component;
import java.util.Collection;

/**
 * Methods may throw exceptions.
 *
 * @author Elmar Baumann
 */
public interface PersonRepository {

    Collection<? extends Person> findAll();

    void save(Collection<? extends Person> persons);

    Component getSettingsComponent();

    String getDisplayName();

    String getUUid();

    boolean isReadOnly();
}
