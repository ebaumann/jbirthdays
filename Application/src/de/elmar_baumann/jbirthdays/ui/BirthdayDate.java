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

    static String formatBirthdaysWeekday(Person person, boolean current) {
        if (!person.isBirthdayDateValid()) {
            return getInvalidDateString(person);
        }
        int birthdayMonth = person.getBirthdayMonth();
        int birthdayDay = person.getBirthdayDay();
        Calendar todayCal = Calendar.getInstance();
        Calendar birthdayCal = Calendar.getInstance();
        if (current) {
            int todayMonth = todayCal.get(Calendar.MONTH) + 1;
            int todayDay = todayCal.get(Calendar.DAY_OF_MONTH);
            boolean inThisYear = DateUtil.isBefore(
                    todayMonth, todayDay, birthdayMonth, birthdayDay)
                    || todayMonth == birthdayMonth && todayDay == birthdayDay;
            int thisYear = todayCal.get(Calendar.YEAR);
            birthdayCal.set(Calendar.YEAR, inThisYear ? thisYear : thisYear + 1);
        } else {
            if (person.getBirthdayYear() > 0) {
                birthdayCal.set(Calendar.YEAR, person.getBirthdayYear());
            } else {
                return "?";
            }
        }
        birthdayCal.set(Calendar.MONTH, birthdayMonth - 1);
        birthdayCal.set(Calendar.DAY_OF_MONTH, birthdayDay);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");
        return dateFormat.format(birthdayCal.getTime());
    }

    static String getInvalidDateString(Person person) {
        return Bundle.getString(BirthdayDate.class, "BirthdayDate.InvalidDate", person.getBirthdayMonth(), person.getBirthdayDay());
    }

    private Date getDate() {
        if (!person.isBirthdayDateValid()) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        boolean prevYear = dateInFuture
                ? DateUtil.isBefore(
                        person.getBirthdayMonth(), person.getBirthdayDay(),
                        cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
                : DateUtil.isBefore(
                        cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH),
                        person.getBirthdayMonth(), person.getBirthdayDay());
        if (prevYear) {
            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 1);
        }
        cal.set(Calendar.MONTH, person.getBirthdayMonth() - 1);
        cal.set(Calendar.DAY_OF_MONTH, person.getBirthdayDay());
        return cal.getTime();
    }

    @Override
    public String toString() {
        if (!person.isBirthdayDateValid()) {
            return getInvalidDateString(person);
        }
        String weekday = formatBirthdaysWeekday(person, true);
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
        return weekday + ", " + df.format(getDate());
    }

    @Override
    public int compareTo(BirthdayDate o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
