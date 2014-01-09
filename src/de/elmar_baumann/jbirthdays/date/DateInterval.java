package de.elmar_baumann.jbirthdays.date;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Elmar Baumann
 */
public final class DateInterval {

    /**
     * @param month month of year, first month is 1
     * @param day day of month, first day is 1
     * @param relDate date nDays relating to
     * @param nDays next n days
     * @return true if day and month are in the next n days relating to relDate
     */
    public static boolean isInNdays(int month, int day, Date relDate, int nDays) {
        if (!DateUtil.maybeDate(month, day)) {
            throw new IllegalArgumentException("Invalid date - month = " + month + ", day = " + day);
        }
        if (relDate == null) {
            throw new NullPointerException("relDate == null");
        }
        if (nDays < 0) {
            throw new IllegalArgumentException("Negative nDays: " + nDays);
        }
        Calendar relDateCal = Calendar.getInstance();
        relDateCal.setTime(relDate);
        if (nDays > relDateCal.getActualMaximum(Calendar.DAY_OF_YEAR)) { // may ignore leap years
            return true;
        }
        int intervalStart = relDateCal.get(Calendar.DAY_OF_YEAR);
        int intervalEnd = intervalStart + nDays;
        Calendar yearOfRelDateCal = Calendar.getInstance();
        yearOfRelDateCal.setTime(relDate);
        yearOfRelDateCal.set(Calendar.MONTH, month - 1);
        yearOfRelDateCal.set(Calendar.DAY_OF_MONTH, day);
        int daysThisYear = yearOfRelDateCal.get(Calendar.DAY_OF_YEAR);
        if (daysThisYear >= intervalStart && daysThisYear <= intervalEnd) {
            return true;
        }
        Calendar nextYearToRelDateCal = Calendar.getInstance();
        nextYearToRelDateCal.setTime(relDate);
        nextYearToRelDateCal.set(Calendar.YEAR, relDateCal.get(Calendar.YEAR) + 1);
        nextYearToRelDateCal.set(Calendar.MONTH, month - 1);
        nextYearToRelDateCal.set(Calendar.DAY_OF_MONTH, day);
        int daysNextYear = relDateCal.getActualMaximum(Calendar.DAY_OF_YEAR)
                + nextYearToRelDateCal.get(Calendar.DAY_OF_YEAR);
        return daysNextYear >= intervalStart && daysNextYear <= intervalEnd;
    }

    /**
     * @param month month of year, first month is 1
     * @param day day of month, first day is 1
     * @param relDate date nDays relating to
     * @param nDays previous n days
     * @return true if day and month were in the past n days relating to relDate
     */
    public static boolean wasBeforeNdays(int month, int day, Date relDate, int nDays) {
        if (!DateUtil.maybeDate(month, day)) {
            throw new IllegalArgumentException("Invalid date - month = " + month + ", day = " + day);
        }
        if (relDate == null) {
            throw new NullPointerException("relDate == null");
        }
        if (nDays < 0) {
            throw new IllegalArgumentException("Negative nDays: " + nDays);
        }
        Calendar relDateCal = Calendar.getInstance();
        relDateCal.setTime(relDate);
        if (nDays > relDateCal.getActualMaximum(Calendar.DAY_OF_YEAR)) { // may ignore leap years
            return true;
        }
        if (month == relDateCal.get(Calendar.MONTH) + 1 && day == relDateCal.get(Calendar.DAY_OF_MONTH)) {
            return true;
        }
        Calendar yearOfRelDateCal = Calendar.getInstance();
        yearOfRelDateCal.setTime(relDate);
        yearOfRelDateCal.set(Calendar.MONTH, month - 1);
        yearOfRelDateCal.set(Calendar.DAY_OF_MONTH, day);
        Calendar prevYearToRelDateCal = Calendar.getInstance();
        prevYearToRelDateCal.setTime(relDate);
        prevYearToRelDateCal.set(Calendar.YEAR, relDateCal.get(Calendar.YEAR) - 1);
        prevYearToRelDateCal.set(Calendar.MONTH, month - 1);
        prevYearToRelDateCal.set(Calendar.DAY_OF_MONTH, day);
        boolean isBeforeRelDate = DateUtil.isBefore(month, day,
                relDateCal.get(Calendar.MONTH) + 1, relDateCal.get(Calendar.DAY_OF_MONTH));
        int intervalEnd = isBeforeRelDate
                ? relDateCal.get(Calendar.DAY_OF_YEAR)
                : prevYearToRelDateCal.getActualMaximum(Calendar.DAY_OF_YEAR) + relDateCal.get(Calendar.DAY_OF_YEAR);
        int intervalStart = intervalEnd - nDays;
        if (isBeforeRelDate) {
            int daysThisYear = yearOfRelDateCal.get(Calendar.DAY_OF_YEAR);
            if (daysThisYear >= intervalStart && daysThisYear <= intervalEnd) {
                return true;
            }
        }
        int daysPrevYear = prevYearToRelDateCal.get(Calendar.DAY_OF_YEAR);
        return daysPrevYear >= intervalStart && daysPrevYear <= intervalEnd;
    }

    private DateInterval() {
    }
}
