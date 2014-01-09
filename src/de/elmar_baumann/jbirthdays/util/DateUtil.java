package de.elmar_baumann.jbirthdays.util;

/**
 * @author Elmar Baumann
 */
public final class DateUtil {

    private static final int[] MAX_DAYS_OF_MONTH =
            new int[]{31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    /**
     * @param month month of year, first month is 1
     * @param day day of month, first day is 1, February can have 29 days
     * @return
     */
    public static boolean maybeDate(int month, int day) {
        return month >= 1 && month <= 12
                && day >= 1 && day <= MAX_DAYS_OF_MONTH[month - 1];
    }

    /**
     * @param month month of year, first month is 1
     * @param day
     * @param relMonth month of year relating to month, first month is 1
     * @param relDay day of month relating to day, first day is 1, February can have 29 days
     * @return true if month and day are before relMonth and relDay
     */
    public static boolean isBefore(int month, int day, int relMonth, int relDay) {
        if (!maybeDate(month, day) || !maybeDate(relMonth, relDay)) {
            throw new IllegalArgumentException(
                    "Invalid dates to compare: month = " + month + ", day = " + day
                            + " <-> month = " + relMonth + ", day = " + relDay);
        }
        return month < relMonth || month == relMonth && day < relDay;
    }

    public static boolean isValidDate(int year, int month, int day) {
        return year > 0
                && month >= 1 && month <= 12
                && day >= 1 && day <= getDaysOfMonth(year, month);
    }

    private static int getDaysOfMonth(int year, int month) {
        return isLeapYear(year) || month != 2
                ? MAX_DAYS_OF_MONTH[month - 1]
                : 28;
    }

    private static boolean isLeapYear(int year) {
        return year % 4 == 0 && year % 100 != 0 || year % 400 == 0;
    }

    private DateUtil() {
    }
}
