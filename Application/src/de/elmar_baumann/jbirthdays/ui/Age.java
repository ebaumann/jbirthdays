package de.elmar_baumann.jbirthdays.ui;

import de.elmar_baumann.jbirthdays.api.Person;
import de.elmar_baumann.jbirthdays.util.Bundle;
import java.util.Calendar;

/**
 * @author Elmar Baumann
 */
public final class Age implements Comparable<Age> {

    private final Person person;
    private final boolean showNextYearAge;

    public Age(Person person) {
        this(person, false);
    }

    /**
     * @param person
     * @param showNextYearAge Default: false
     */
    public Age(Person person, boolean showNextYearAge) {
        if (person == null) {
            throw new NullPointerException("person == null");
        }
        this.person = person;
        this.showNextYearAge = showNextYearAge;
    }

    @Override
    public String toString() {
        int age = getAge();
        if (age < 0) {
            return "?";
        }
        return showNextYearAge
                ? Bundle.getString(Age.class, "Age.AgeWithNextYear", age, age + 1)
                : Bundle.getString(Age.class, "Age.Age", age);
    }

    private int getAge() {
        if (!person.isBirthdayDateValid() || person.getBirthdayYear() <= 0) {
            return -1;
        }
        Calendar todayCal = Calendar.getInstance();
        int thisYear = todayCal.get(Calendar.YEAR);
        int thisMonth = todayCal.get(Calendar.MONTH) + 1;
        int thisDay = todayCal.get(Calendar.DAY_OF_MONTH);
        int maxAge = thisYear - person.getBirthdayYear();
        boolean isMaxAge = thisMonth > person.getBirthdayMonth()
                || thisMonth == person.getBirthdayMonth() && thisDay >= person.getBirthdayDay();
        return isMaxAge
                ? maxAge
                : maxAge - 1;
    }

    @Override
    public int compareTo(Age o) {
        int thisAge = getAge();
        int otherAge = o.getAge();
        return thisAge == otherAge
                ? 0
                : thisAge > otherAge
                ? 1
                : -1;
    }
}
