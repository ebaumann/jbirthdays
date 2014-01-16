package de.elmar_baumann.jbirthdays.ical;

import de.elmar_baumann.jbirthdays.api.Person;
import de.elmar_baumann.jbirthdays.util.Bundle;
import de.elmar_baumann.jbirthdays.util.DateUtil;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Elmar Baumann
 */
public final class Ical {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    private static final String HEADER = "BEGIN:VCALENDAR\nPRODID:-//elmar-baumann.de/JBirthdays Export//DE\nVERSION:2.0";
    private static final String FOOTER = "\nEND:VCALENDAR\n";

    /**
     * Creates birthday events in iCal format (http://tools.ietf.org/html/rfc5545).
     * @param persons persons to create events for
     * @param os ical will writen into os in UTF-8
     * @return count of added events (person's birthdays)
     * @throws RuntimeException on write errors into os
     */
    public static int toIcal(Collection<? extends Person> persons, OutputStream os) {
        if (persons == null) {
            throw new NullPointerException("persons == null");
        }
        int countAdded = 0;
        Logger.getLogger(Ical.class.getName()).log(Level.INFO, "Got {0} persons for export to iCal", persons.size());
        StringBuilder sb = new StringBuilder(HEADER);
        for (Person person : persons) {
            if (person.isBirthdayDateValid()) {
                Logger.getLogger(Ical.class.getName()).log(Level.INFO, "Exporting to iCal: {0}", person);
                sb.append(createEvent(person));
                countAdded++;
            } else {
                Logger.getLogger(Ical.class.getName()).log(Level.INFO, "Person will not be exported to iCal because birthday date is not valid: {0}", person);
            }
        }
        sb.append(FOOTER);
        try {
            os.write(sb.toString().getBytes("UTF-8"));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        Logger.getLogger(Ical.class.getName()).log(Level.SEVERE, "Exported {0} persons to iCal", countAdded);
        return countAdded;
    }

    private static String createEvent(Person person) {
        StringBuilder sb = new StringBuilder();
        Date eventStartDate = getEventStartDate(person);
        sb.append("\n")
                .append("BEGIN:VEVENT")
                .append("\n")
                .append("DTSTART;VALUE=DATE:")
                .append(DATE_FORMAT.format(eventStartDate))
                .append("\n")
                .append("DTEND;VALUE=DATE:")
                .append(DATE_FORMAT.format(getEventEndDate(eventStartDate)))
                .append("\n")
                .append("RRULE:FREQ=YEARLY;COUNT=100")
                .append("\n")
                .append("SUMMARY:")
                .append(Bundle.getString(Ical.class, "Ical.Birthday", person.getFirstName(), person.getLastName(), getBornString(person)))
                .append("\n")
                .append("END:VEVENT");
        return sb.toString();
    }

    private static Date getEventStartDate(Person person) {
        Calendar todayCal = Calendar.getInstance();
        boolean hasBirthdayAtFeb29 = person.getBirthdayMonth() == 2 && person.getBirthdayDay() == 29;
        if (hasBirthdayAtFeb29) {
            boolean hadBirthday = todayCal.get(Calendar.MONTH) > 1;
            int thisYear = todayCal.get(Calendar.YEAR);
            Calendar eventCal = Calendar.getInstance();
            eventCal.set(Calendar.YEAR, DateUtil.getLeapYearNextTo(hadBirthday ? thisYear + 1 : thisYear));
            eventCal.set(Calendar.MONTH, 1);
            eventCal.set(Calendar.DAY_OF_MONTH, 29);
            return eventCal.getTime();
        }
        Calendar thisYearBirthdayCal = Calendar.getInstance();
        thisYearBirthdayCal.set(Calendar.MONTH, person.getBirthdayMonth() - 1);
        thisYearBirthdayCal.set(Calendar.DAY_OF_MONTH, person.getBirthdayDay());
        boolean hadBirthday = todayCal.compareTo(thisYearBirthdayCal) > 0;
        int thisYear = thisYearBirthdayCal.get(Calendar.YEAR);
        int eventStartYear = hadBirthday
                ? thisYear + 1
                : thisYear;
        Calendar eventCal = Calendar.getInstance();
        eventCal.set(Calendar.YEAR, eventStartYear);
        eventCal.set(Calendar.MONTH, person.getBirthdayMonth() - 1);
        eventCal.set(Calendar.DAY_OF_MONTH, person.getBirthdayDay());
        return eventCal.getTime();
    }

    private static String getBornString(Person person) {
        if (person.getBirthdayYear() < 1800) {
            return "";
        }
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, person.getBirthdayYear());
        cal.set(Calendar.MONTH, person.getBirthdayMonth() - 1);
        cal.set(Calendar.DAY_OF_MONTH, person.getBirthdayDay());
        return Bundle.getString(Ical.class, "Ical.Born", cal.getTime());
    }

    private static Date getEventEndDate(Date startDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    private Ical() {
    }
}
