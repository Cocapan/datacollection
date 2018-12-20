package cn.gz.rd.datacollection.service.cron;

import cn.gz.rd.datacollection.service.JkwwwSubjectService;
import cn.gz.rd.datacollection.service.SMSService;
import cn.gz.rd.datacollection.utils.CountQuarterUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

/**
 * 教科文卫自动催办通知
 * @author Peng Xiaodong
 */
@Component
public class JkwwAutoNoticeJob {

    private final Logger logger = LoggerFactory.getLogger(JkwwAutoNoticeJob.class);

    @Autowired
    private JkwwwSubjectService subjectService;

    @Autowired
    private SMSService smsService;

    @Value("${jkww.officeOperatorPhone}")
    private String officeOperatorPhone;

    @Value("${jkww.officeDeptPhone}")
    private String officeDeptPhone;

    @Value("${jkww.officeBureauPhone}")
    private String officeBureauPhone;

    /**
     * 每天9点进行检查
     */
    @Scheduled(cron = "0 0 9 * * *")
    public void autoNotice() {
        logger.info("科教文卫开始启动检查...");
        LocalDate now = LocalDate.now();
        String statPeriod = calcStatPeriod(now);
        long days = calcExtraDays(statPeriod, now);
        boolean isAllUpload = subjectService.isAllUpload(statPeriod);
        logger.info("自动催办：统计周期 = {}, 延期天数 = {}, 数据是否已经全部上传 = {}",
                statPeriod, days, isAllUpload);
        if (days == 10) {
            noticeBeforeDeadline10Day(statPeriod);
        } else if (days == 5) {
            noticeBeforeDeadline5Day(statPeriod);
        } else if (days == 0 && !isAllUpload) {
            noticeDeadlineUnSubmit(statPeriod);
        } else if (days < 0 && !isAllUpload) {
            noticeAfterDeadlineEveryDay(-days, statPeriod);
        }
    }

    /**
     * 根据时间计算出需要催办的统计周期
     * @param now
     * @return
     */
    public String calcStatPeriod(LocalDate now) {
        String statPeriod = CountQuarterUtils.calcStatPeriod(now);
        String nextStatPeriod = CountQuarterUtils.getNextQuarter(statPeriod);
        long daysByNextStatPeriod = calcExtraDays(nextStatPeriod, now);

        if (daysByNextStatPeriod > 0 && daysByNextStatPeriod < 15) {
            return nextStatPeriod;
        } else {
            return statPeriod;
        }
    }

    /**
     * 计算超期天数
     * @param statPeriod 统计周期
     * @return 超期天数，如果为正数则为距离到期日还剩的天数；如果为负数，则为超期的天数。
     */
    public long calcExtraDays(String statPeriod, LocalDate now) {
        String[] qs = StringUtils.split(statPeriod, "Q");
        int year = Integer.parseInt(qs[0]);
        int quarter = Integer.parseInt(qs[1]);
        LocalDate deadlineDate;
        if (quarter == 1) {
            deadlineDate = LocalDate.of(year, 4, 10);
        } else if (quarter == 2) {
            deadlineDate = LocalDate.of(year, 7, 10);
        } else if (quarter == 3) {
            deadlineDate = LocalDate.of(year, 10, 10);
        } else if (quarter == 4) {
            deadlineDate = LocalDate.of(year + 1, 1, 10);
        } else {
            throw new IllegalArgumentException("不支持的统计周期：" + statPeriod);
        }

        long days = now.until(deadlineDate, ChronoUnit.DAYS);
        return days;
    }

    private void noticeBeforeDeadline10Day(String statPeriod) {
        String operatorMsg = "距离第" + statPeriod + "季度2号议案数据采集到期日还有10天，请尽快提交数据。";
        smsService.send(officeOperatorPhone, operatorMsg);
    }

    private void noticeBeforeDeadline5Day(String statPeriod) {
        String operatorMsg = "距离第" + statPeriod + "季度2号议案数据采集到期日还有5天，请尽快提交数据。";
        String deptMsg = operatorMsg;
        smsService.send(officeOperatorPhone, operatorMsg);
        smsService.send(officeDeptPhone, deptMsg);
    }

    private void noticeDeadlineUnSubmit(String statPeriod) {
        String operatorMsg = "今天是第" + statPeriod + "季度2号议案数据采集的到期日，你单位尚未提交数据，请尽快提交。";
        String deptMsg = operatorMsg;
        String bureauMsg = "尊敬的领导：今天是第" + statPeriod
                + "季度2号议案数据采集的到期日，你单位尚未提交数据，请尽快提交，谢谢！";
        smsService.send(officeOperatorPhone, operatorMsg);
        smsService.send(officeDeptPhone, deptMsg);
        smsService.send(officeBureauPhone, bureauMsg);

    }

    private void noticeAfterDeadlineEveryDay(long delayDay, String statPeriod) {
        String operatorMsg = "您单位已经逾期" + delayDay + "天未提交第"
                + statPeriod + "季度2号议案的数据，请尽快提交。";
        String deptMsg = operatorMsg;
        String bureauMsg = "您单位已经逾期" + delayDay + "天未提交第"
                + statPeriod + "季度2号议案的数据，请尽快提交，谢谢";
        smsService.send(officeOperatorPhone, operatorMsg);
        smsService.send(officeDeptPhone, deptMsg);
        smsService.send(officeBureauPhone, bureauMsg);
    }

}
