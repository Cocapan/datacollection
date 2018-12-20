package cn.gz.rd.datacollection.utils;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class CountQuarterUtilsTest {

    @Test
    public void testIsValidQuarter() {

        assertTrue(CountQuarterUtils.isValidQuarter("2018Q1"));
        assertFalse(CountQuarterUtils.isValidQuarter("2018Q11"));
        assertFalse(CountQuarterUtils.isValidQuarter("20181"));
        assertFalse(CountQuarterUtils.isValidQuarter("2009Q1"));
        assertFalse(CountQuarterUtils.isValidQuarter("2018Q"));
        assertFalse(CountQuarterUtils.isValidQuarter("10000Q1"));

    }

    @Test
    public void testCalcStatPeriod() {
        Assert.assertEquals("2018Q4", CountQuarterUtils.calcStatPeriod(
                LocalDate.of(2019, 3, 30)));

        Assert.assertEquals(CountQuarterUtils.calcStatPeriod(
                LocalDate.of(2018, 1, 10)), "2017Q4");
        Assert.assertEquals(CountQuarterUtils.calcStatPeriod(
                LocalDate.of(2018, 4, 10)), "2018Q1");
        Assert.assertEquals(CountQuarterUtils.calcStatPeriod(
                LocalDate.of(2018, 7, 10)), "2018Q2");
        Assert.assertEquals(CountQuarterUtils.calcStatPeriod(
                LocalDate.of(2018, 10, 10)), "2018Q3");
        Assert.assertEquals(CountQuarterUtils.calcStatPeriod(
                LocalDate.of(2018, 12, 10)), "2018Q3");
        Assert.assertEquals(CountQuarterUtils.calcStatPeriod(
                LocalDate.of(2019, 1, 10)), "2018Q4");
    }

    @Test
    public void testGetNextQuarter() {
        Assert.assertEquals("2018Q1",
                CountQuarterUtils.getPreviousQuarter("2018Q2"));
        Assert.assertEquals("2018Q2",
                CountQuarterUtils.getPreviousQuarter("2018Q3"));
        Assert.assertEquals("2018Q3",
                CountQuarterUtils.getPreviousQuarter("2018Q4"));
        Assert.assertEquals("2018Q4",
                CountQuarterUtils.getPreviousQuarter("2019Q1"));
    }

}
