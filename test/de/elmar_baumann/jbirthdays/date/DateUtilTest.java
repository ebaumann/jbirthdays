package de.elmar_baumann.jbirthdays.date;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Elmar Baumann
 */
public class DateUtilTest {

    public DateUtilTest() {
    }

    @Test
    public void testMaybeDate() {
        Assert.assertTrue(DateUtil.maybeDate(1, 1));
        Assert.assertTrue(DateUtil.maybeDate(1, 5));
        Assert.assertTrue(DateUtil.maybeDate(1, 31));
        Assert.assertFalse(DateUtil.maybeDate(1, 32));
        Assert.assertTrue(DateUtil.maybeDate(1, 2));
        Assert.assertTrue(DateUtil.maybeDate(2, 3));
        Assert.assertTrue(DateUtil.maybeDate(2, 29));
        Assert.assertTrue(DateUtil.maybeDate(3, 1));
        Assert.assertTrue(DateUtil.maybeDate(3, 31));
        Assert.assertFalse(DateUtil.maybeDate(3, 32));
        Assert.assertTrue(DateUtil.maybeDate(4, 1));
        Assert.assertTrue(DateUtil.maybeDate(4, 30));
        Assert.assertFalse(DateUtil.maybeDate(4, 31));
        Assert.assertTrue(DateUtil.maybeDate(5, 1));
        Assert.assertTrue(DateUtil.maybeDate(5, 31));
        Assert.assertFalse(DateUtil.maybeDate(5, 0));
        Assert.assertFalse(DateUtil.maybeDate(5, 40));
        Assert.assertTrue(DateUtil.maybeDate(6, 1));
        Assert.assertTrue(DateUtil.maybeDate(6, 30));
        Assert.assertFalse(DateUtil.maybeDate(6, 31));
        Assert.assertTrue(DateUtil.maybeDate(7, 1));
        Assert.assertTrue(DateUtil.maybeDate(7, 31));
        Assert.assertFalse(DateUtil.maybeDate(7, 32));
        Assert.assertTrue(DateUtil.maybeDate(8, 31));
        Assert.assertTrue(DateUtil.maybeDate(9, 1));
        Assert.assertTrue(DateUtil.maybeDate(9, 30));
        Assert.assertFalse(DateUtil.maybeDate(9, 31));
        Assert.assertTrue(DateUtil.maybeDate(10, 31));
        Assert.assertTrue(DateUtil.maybeDate(11, 30));
        Assert.assertFalse(DateUtil.maybeDate(11, 31));
        Assert.assertTrue(DateUtil.maybeDate(12, 31));
        Assert.assertFalse(DateUtil.maybeDate(12, 32));
    }

    @Test
    public void testIsBefore() {
        Assert.assertTrue(DateUtil.isBefore(1, 1, 2, 1));
        Assert.assertTrue(DateUtil.isBefore(2, 1, 2, 2));
        Assert.assertFalse(DateUtil.isBefore(2, 3, 2, 2));
    }

    @Test
    public void testIsValidDate() {
        Assert.assertTrue(DateUtil.isValidDate(2012, 1, 1));
        Assert.assertTrue(DateUtil.isValidDate(2012, 2, 29)); // 2012 is leap year
        Assert.assertFalse(DateUtil.isValidDate(2011, 2, 29));
        Assert.assertFalse(DateUtil.isValidDate(2012, 14, 29));
        Assert.assertFalse(DateUtil.isValidDate(2012, 6, 31));
        Assert.assertTrue(DateUtil.isValidDate(2012, 6, 30));
    }
}
