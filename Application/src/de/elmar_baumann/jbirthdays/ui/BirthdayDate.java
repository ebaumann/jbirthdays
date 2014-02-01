package de.elmar_baumann.jbirthdays.ui;

import de.elmar_baumann.jbirthdays.api.Person;
import de.elmar_baumann.jbirthdays.util.Bundle;
import de.elmar_baumann.jbirthdays.util.DateUtil;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Elmar Baumann
 */
public final class BirthdayDate implements Comparable<BirthdayDate> {

    private final Person person;
    private final boolean dateInFuture;

    public BirthdayDate(Person person, boolean dateInFuture) {
        if (this == null) {
            throw new NullPointerException("this == null");
        }
        this.person = person;
        this.dateInFuture = dateInFuture;
    }

    static String getInvalidDateString(Person person) {
        return Bundle.getString(BirthdayDate.class, "BirthdayDate.InvalidDate", person.getBirthdayMonth(), person.getBirthdayDay());
    }

    private Date getDate() {
        if (!person.isBirthdayDateValid()) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        int todayMonth = cal.get(Calendar.MONTH) + 1;
        int todayDay = cal.get(Calendar.DAY_OF_MONTH);
        int birthdayMonth = person.getBirthdayMonth();
        int birthdayDay = person.getBirthdayDay();
        boolean prevYear = dateInFuture
                ? DateUtil.isBefore(birthdayMonth, birthdayDay, todayMonth, todayDay)
                : DateUtil.isBefore(todayMonth, todayDay, birthdayMonth, birthdayDay);
        if (prevYear) {
            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 1);
        }
        cal.set(Calendar.MONTH, birthdayMonth - 1);
        cal.set(Calendar.DAY_OF_MONTH, birthdayDay);
        return cal.getTime();
    }

    @Override
    public String toString() {
        if (!person.isBirthdayDateValid()) {
            return getInvalidDateString(person);
        }
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
        Date date = getDate();
        SimpleDateFormat weekdayFormat = new SimpleDateFormat("EEEE");
        String weekday = weekdayFormat.format(date);
        return weekday + ", " + df.format(date);
    }

    @Override
    public int compareTo(BirthdayDate o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
