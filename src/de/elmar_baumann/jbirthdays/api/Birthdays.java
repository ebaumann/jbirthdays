package de.elmar_baumann.jbirthdays.api;

import de.elmar_baumann.jbirthdays.util.DateInterval;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

/**
 * @author Elmar Baumann
 */
public final class Birthdays {

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

    private Birthdays() {
    }
}
