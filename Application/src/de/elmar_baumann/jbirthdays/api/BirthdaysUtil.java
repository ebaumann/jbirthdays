package de.elmar_baumann.jbirthdays.api;

import de.elmar_baumann.jbirthdays.util.DateInterval;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class BirthdaysUtil {

    private static final String KEY_PREFERRED = "BirthdaysUtil.PreferredPersonRepository";

    /**
     * @param persons
     * @param relDate
     * @param nDays must be 1 or more
     * @return persons havin birthday in the next nDays days
     */
    public static Collection<Person> findWithBirthdayInNdays(Collection<? extends Person> persons, Date relDate, int nDays) {
        if (persons == null) {
            throw new NullPointerException("persons == null");
        }
        if (relDate == null) {
            throw new NullPointerException("relDate == null");
        }
        if (nDays < 1) {
            throw new IllegalArgumentException("nDays less than 1: " + nDays);
        }
        Collection<Person> foundPersons = new ArrayList<>();
        for (Person person : persons) {
            if (person.isBirthdayDateValid()
                    && !isBirthdayDate(relDate, person)
                    && DateInterval.isInNdays(person.getBirthdayMonth(), person.getBirthdayDay(), relDate, nDays)) {
                foundPersons.add(person);
            }
        }
        return foundPersons;
    }

    /**
     * @param persons
     * @param relDate
     * @param nDays must be 1 or more
     * @return persons who had birthday before nDays days
     */
    public static Collection<Person> findWithBirthdayBeforeNdays(Collection<? extends Person> persons, Date relDate, int nDays) {
        if (persons == null) {
            throw new NullPointerException("persons == null");
        }
        if (relDate == null) {
            throw new NullPointerException("relDate == null");
        }
        if (nDays < 1) {
            throw new IllegalArgumentException("nDays less than 1: " + nDays);
        }
        Collection<Person> foundPersons = new ArrayList<>();
        for (Person person : persons) {
            if (person.isBirthdayDateValid()
                    && !isBirthdayDate(relDate, person)
                    && DateInterval.wasBeforeNdays(person.getBirthdayMonth(), person.getBirthdayDay(), relDate, nDays)) {
                foundPersons.add(person);
            }
        }
        return foundPersons;
    }

    public static Collection<Person> findWithBirthdayAt(Collection<? extends Person> persons, Date relDate) {
        if (persons == null) {
            throw new NullPointerException("persons == null");
        }
        if (relDate == null) {
            throw new NullPointerException("relDate == null");
        }
        Collection<Person> foundPersons = new ArrayList<>();
        for (Person person : persons) {
            if (person.isBirthdayDateValid()
                    && isBirthdayDate(relDate, person)) {
                foundPersons.add(person);
            }
        }
        return foundPersons;
    }

    private static boolean isBirthdayDate(Date date, Person person) {
        if (!person.isBirthdayDateValid()) {
            return false;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return person.getBirthdayMonth() == cal.get(Calendar.MONTH) + 1
                && person.getBirthdayDay() == cal.get(Calendar.DAY_OF_MONTH);
    }

    public static PersonRepository findPreferredRepository() {
        Preferences prefs = Preferences.userNodeForPackage(BirthdaysUtil.class);
        String prefValue = prefs.get(KEY_PREFERRED, null);
        PersonRepository defaultRepo = null;
        for (PersonRepository repo : Lookup.getDefault().lookupAll(PersonRepository.class)) {
            defaultRepo = repo;
            if (repo.getUUid().equals(prefValue)) {
                defaultRepo = repo;
                break;
            }
        }
        if (defaultRepo != null) {
            Logger.getLogger(BirthdaysUtil.class.getName()).log(Level.INFO, "Using repository: {0}", defaultRepo.getDisplayName());
            return defaultRepo;
        }
        throw new IllegalStateException("No preferred repository implemented!");
    }

    public static boolean isPreferredRepository(PersonRepository repo) {
        if (repo == null) {
            throw new NullPointerException("repo == null");
        }
        Preferences prefs = Preferences.userNodeForPackage(BirthdaysUtil.class);
        String prefValue = prefs.get(KEY_PREFERRED, null);
        return repo.getUUid().equals(prefValue);
    }

    public static void setPreferredRepositoryUuid(String uuid) {
        if (uuid == null) {
            throw new NullPointerException("uuid == null");
        }
        Preferences prefs = Preferences.userNodeForPackage(BirthdaysUtil.class);
        prefs.put(KEY_PREFERRED, uuid);
    }

    /**
     * Checks for {@link Person#isSamePerson(de.elmar_baumann.jbirthdays.api.Person)}
     * rather than the UUID.
     *
     * @param persons
     * @param person
     * @return true if at least one person in persons matches the person
     */
    public static boolean containsPerson(Collection<? extends Person> persons, Person person) {
        if (persons == null) {
            throw new NullPointerException("persons == null");
        }
        if (person == null) {
            throw new NullPointerException("person == null");
        }
        for (Person p : persons) {
            if (person.isSamePerson(p)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a person to persons if no person in persons matches to the person
     * via {@link Person#isSamePerson(de.elmar_baumann.jbirthdays.api.Person)}.
     * @param persons
     * @param person
     * @return true if added
     */
    public static boolean addIfNotContained(Collection<Person> persons, Person person) {
        if (persons == null) {
            throw new NullPointerException("persons == null");
        }
        if (!containsPerson(persons, person)) {
            persons.add(person);
            return true;
        }
        return false;
    }

    /**
     * Adds to persons in {@code to} each not contained persons from {@link from}
     * where matched determined via
     * {@link Person#isSamePerson(de.elmar_baumann.jbirthdays.api.Person)}.
     * @param from
     * @param to
     * @return count of added persons
     */
    public static int addNotContainedPersons(Collection<? extends Person> from, Collection<Person> to) {
        if (from == null) {
            throw new NullPointerException("from == null");
        }
        if (to == null) {
            throw new NullPointerException("to == null");
        }
        int added = 0;
        for (Person person : from) {
            if (addIfNotContained(to, person)) {
                added++;
            }
        }
        return added;
    }

    private BirthdaysUtil() {
    }
}
