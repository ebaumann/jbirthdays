package de.elmar_baumann.jbirthdays.util;

import de.elmar_baumann.jbirthdays.util.DateInterval;
import java.util.Calendar;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Elmar Baumann
 */
public class DateIntervalTest {

    public DateIntervalTest() {
    }

    @Test
    public void testIsInNdays() {
        Date date = createDate(2014, 2, 15);
        Assert.assertTrue(DateInterval.isInNdays(2, 15, date, 0));
        Assert.assertTrue(DateInterval.isInNdays(2, 15, date, 1));
        Assert.assertTrue(DateInterval.isInNdays(2, 16, date, 1));
        Assert.assertTrue(DateInterval.isInNdays(2, 22, date, 7));
        Assert.assertFalse(DateInterval.isInNdays(2, 23, date, 7));
        Assert.assertFalse(DateInterval.isInNdays(2, 17, date, 1));
        Assert.assertTrue(DateInterval.isInNdays(2, 14, date, 366));
        date = createDate(2016, 2, 27); // 2016 is leap year
        Assert.assertTrue(DateInterval.isInNdays(3, 1, date, 3));
        Assert.assertFalse(DateInterval.isInNdays(3, 2, date, 3));
        date = createDate(2014, 12, 28);
        Assert.assertFalse(DateInterval.isInNdays(12, 31, date, 2));
        Assert.assertTrue(DateInterval.isInNdays(12, 31, date, 3));
        Assert.assertFalse(DateInterval.isInNdays(1, 1, date, 3));
        Assert.assertTrue(DateInterval.isInNdays(1, 1, date, 7));
        Assert.assertTrue(DateInterval.isInNdays(1, 4, date, 7));
        Assert.assertFalse(DateInterval.isInNdays(1, 5, date, 7));
    }

    @Test
    public void testWasBeforeNdays() {
        Date date = createDate(2014, 2, 15);
        Assert.assertTrue(DateInterval.wasBeforeNdays(2, 15, date, 0));
        Assert.assertTrue(DateInterval.wasBeforeNdays(2, 14, date, 1));
        Assert.assertFalse(DateInterval.wasBeforeNdays(2, 13, date, 1));
        Assert.assertTrue(DateInterval.wasBeforeNdays(2, 8, date, 7));
        Assert.assertFalse(DateInterval.wasBeforeNdays(2, 7, date, 7));
        Assert.assertTrue(DateInterval.wasBeforeNdays(2, 16, date, 366));
        date = createDate(2016, 3, 1); // 2016 is leap year
        Assert.assertTrue(DateInterval.wasBeforeNdays(2, 28, date, 2));
        Assert.assertFalse(DateInterval.wasBeforeNdays(2, 28, date, 1));
        date = createDate(2015, 1, 5);
        Assert.assertFalse(DateInterval.wasBeforeNdays(12, 31, date, 4));
        Assert.assertTrue(DateInterval.wasBeforeNdays(12, 31, date, 5));
        Assert.assertFalse(DateInterval.wasBeforeNdays(1, 1, date, 3));
        Assert.assertTrue(DateInterval.wasBeforeNdays(1, 1, date, 7));
    }

    private Date createDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day);
        return cal.getTime();
    }
}
