package de.elmar_baumann.jbirthdays.ui;

import de.elmar_baumann.jbirthdays.api.Person;
import de.elmar_baumann.jbirthdays.util.Bundle;
import java.util.Calendar;

/**
 * @author Elmar Baumann
 */
public final class Age implements Comparable<Age> {

    private final Person person;

    public Age(Person person) {
        if (person == null) {
            throw new NullPointerException("person == null");
        }
        this.person = person;
    }

    @Override
    public String toString() {
        int age = getAge();
        if (age < 0) {
            return "?";
        }
        return Bundle.getString(Age.class, "Age.Age", age);
    }

    private int getAge() {
        if (!person.isBirthdayDateValid() || person.getBirthdayYear() <= 0) {
            return -1;
        }
        Calendar todayCal = Calendar.getInstance();
        int thisYear = todayCal.get(Calendar.YEAR);
        return thisYear - person.getBirthdayYear();
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
