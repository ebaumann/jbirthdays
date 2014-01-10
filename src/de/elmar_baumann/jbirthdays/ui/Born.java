package de.elmar_baumann.jbirthdays.ui;

import de.elmar_baumann.jbirthdays.api.Person;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Elmar Baumann
 */
public final class Born implements Comparable<Born> {

    private final Person person;

    public Born(Person person) {
        if (person == null) {
            throw new NullPointerException("person == null");
        }
        this.person = person;
    }

    @Override
    public String toString() {
        if (!person.isBirthdayDateValid()) {
            return BirthdayDate.getInvalidDateString(person);
        }
        if (person.getBirthdayYear() <= 0) {
            return person.getBirthdayMonth() + " - " + person.getBirthdayDay();
        }
        String weekday = BirthdayDate.formatBirthdaysWeekday(person, false);
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
        return weekday + ", " + df.format(getBorn());
    }

    private Date getBorn() {
        if (!person.isBirthdayDateValid() || person.getBirthdayDay() <= 0) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, person.getBirthdayYear());
        cal.set(Calendar.MONTH, person.getBirthdayMonth() - 1);
        cal.set(Calendar.DAY_OF_MONTH, person.getBirthdayDay());
        return cal.getTime();
    }

    @Override
    public int compareTo(Born o) {
        Date thisBorn = getBorn();
        Date otherBorn = o.getBorn();
        if (thisBorn != null && otherBorn == null) {
            return 1;
        } else if (thisBorn == null && otherBorn != null) {
            return -1;
        } else if (thisBorn == null && otherBorn == null) {
            return 0;
        }
        Calendar thisBornCal = Calendar.getInstance();
        Calendar otherBornCal = Calendar.getInstance();
        thisBornCal.setTime(thisBorn);
        otherBornCal.setTime(otherBorn);
        return thisBornCal.compareTo(otherBornCal);
    }
}
