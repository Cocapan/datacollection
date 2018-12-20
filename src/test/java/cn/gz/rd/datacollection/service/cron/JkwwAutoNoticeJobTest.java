package cn.gz.rd.datacollection.service.cron;

import cn.gz.rd.datacollection.DataCenterApplicationTests;
import cn.gz.rd.datacollection.utils.CountQuarterUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

/**
 * @author Peng Xiaodong
 */
public class JkwwAutoNoticeJobTest extends DataCenterApplicationTests {

    @Autowired
    private JkwwAutoNoticeJob autoNoticeJob;

    @Test
    public void testCalcExtraDays() {
        Assert.assertEquals(
                autoNoticeJob.calcExtraDays("2018Q1",
                LocalDate.of(2018, 4, 10)), 0L);
        Assert.assertEquals(
                autoNoticeJob.calcExtraDays("2018Q1",
                        LocalDate.of(2018, 4, 11)), -1L);
        Assert.assertEquals(
                autoNoticeJob.calcExtraDays("2018Q1",
                        LocalDate.of(2018, 4, 9)), 1L);
        Assert.assertEquals(
                autoNoticeJob.calcExtraDays("2018Q4",
                        LocalDate.of(2019, 1, 10)), 0L);
        Assert.assertEquals(
                autoNoticeJob.calcExtraDays("2018Q4",
                        LocalDate.of(2019, 1, 9)), 1L);
        Assert.assertEquals(
                autoNoticeJob.calcExtraDays("2018Q4",
                        LocalDate.of(2019, 1, 11)), -1L);

        Assert.assertEquals(
                autoNoticeJob.calcExtraDays("2019Q1",
                        LocalDate.of(2019, 3, 30)), 10L);
    }

    @Test
    public void testCalcStatPeriod() {
        Assert.assertEquals("2019Q1",
                autoNoticeJob.calcStatPeriod(LocalDate.of(2019, 3, 30)));
        Assert.assertEquals("2018Q4",
                autoNoticeJob.calcStatPeriod(LocalDate.of(2019, 3, 15)));
        Assert.assertEquals("2019Q3",
                autoNoticeJob.calcStatPeriod(LocalDate.of(2019, 12, 10)));
        Assert.assertEquals("2019Q4",
                autoNoticeJob.calcStatPeriod(LocalDate.of(2019, 12, 30)));
        Assert.assertEquals("2017Q4",
                autoNoticeJob.calcStatPeriod(LocalDate.of(2018, 2, 10)));
    }

    @Test
    public void testAutoNotice() {
        autoNoticeJob.autoNotice();
    }
}
