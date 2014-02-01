package de.elmar_baumann.jbirthdays.ui;

import de.elmar_baumann.jbirthdays.api.Person;
import static de.elmar_baumann.jbirthdays.ui.BirthdayDate.getInvalidDateString;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
        String weekday = formatBirthdaysWeekday(person);
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
        return weekday + ", " + df.format(getBorn());
    }

    private String formatBirthdaysWeekday(Person person) {
        if (!person.isBirthdayDateValid()) {
            return getInvalidDateString(person);
        }
        int birthdayMonth = person.getBirthdayMonth();
        int birthdayDay = person.getBirthdayDay();
        Calendar birthdayCal = Calendar.getInstance();
        if (person.getBirthdayYear() > 0) {
            birthdayCal.set(Calendar.YEAR, person.getBirthdayYear());
        } else {
            return "?";
        }
        birthdayCal.set(Calendar.MONTH, birthdayMonth - 1);
        birthdayCal.set(Calendar.DAY_OF_MONTH, birthdayDay);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");
        return dateFormat.format(birthdayCal.getTime());
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
