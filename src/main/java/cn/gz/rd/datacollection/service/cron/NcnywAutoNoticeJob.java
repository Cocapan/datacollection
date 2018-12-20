package cn.gz.rd.datacollection.service.cron;

import cn.gz.rd.datacollection.service.CommonAutoNoticeJobService;
import cn.gz.rd.datacollection.utils.DateUtlis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class NcnywAutoNoticeJob {

    private final static String NCNYW_GWBM = "GWBM_02";

    private final static String YEAR_UPLOAD_PERIOD = "年";

    private final static String SEASON_UPLOAD_PERIOD = "季";

    private final static String MONTH_UPLOAD_PERIOD = "月";

    private final static String WEEK_UPLOAD_PERIOD = "周";

    private CommonAutoNoticeJobService commonAutoNoticeJobService;

    @Autowired
    public NcnywAutoNoticeJob(CommonAutoNoticeJobService commonAutoNoticeJobService){
        this.commonAutoNoticeJobService = commonAutoNoticeJobService;
    }

    /**
     * 每年1月1号9点发送
     */
    @Scheduled(cron = "0 0 9 1 1 *")
    public void yearUploadTopicAutoNotice() {
        commonAutoNoticeJobService.sentUploadNoticeMsgToDepartment(NCNYW_GWBM, YEAR_UPLOAD_PERIOD, getUploadNoticeMessage(YEAR_UPLOAD_PERIOD));
    }

    /**
     * 每月1号9点发送
     */
    @Scheduled(cron = "0 0 9 1 * *")
    public void seasonUploadTopicAutoNotice() {
        commonAutoNoticeJobService.sentUploadNoticeMsgToDepartment(NCNYW_GWBM, MONTH_UPLOAD_PERIOD, getUploadNoticeMessage(MONTH_UPLOAD_PERIOD));
        int currentMonth = DateUtlis.getCurrentMonth();
        switch (currentMonth){
            case 1:
            case 4:
            case 7:
            case 10:
                commonAutoNoticeJobService.sentUploadNoticeMsgToDepartment(NCNYW_GWBM, SEASON_UPLOAD_PERIOD, getUploadNoticeMessage(SEASON_UPLOAD_PERIOD));
                break;
            default:
                break;
        }
    }

    /**
     * 每天9点进行检查
     */
    @Scheduled(cron = "0 0 9 * * *")
    public void weekUploadTopicAutoNotice() {
        if (DateUtlis.isMonday(new Date())){
            commonAutoNoticeJobService.sentUploadNoticeMsgToDepartment(NCNYW_GWBM, WEEK_UPLOAD_PERIOD, getUploadNoticeMessage(WEEK_UPLOAD_PERIOD));
        }
    }

    /**
     * 每天9点进行检查
     */
    @Scheduled(cron = "0 0 9 * * *")
    public void notUploadTopicAutoCheck() {
        Date date = new Date();
        String dateString = DateUtlis.getDateString(date);
        String month = dateString.substring(4,6);
        String day = dateString.substring(6);
        String noticeMessage = "单位，贵单位应于" + month + "月" + day + "日前报送的数据已逾期，请尽快报送，谢谢您的支持和配合！（市人大常委会农村农业工委）";
        String yearStatisticPeriod = String.valueOf(DateUtlis.getCurrentYear() - 1);
        String monthStatisticPeriod = String.valueOf(DateUtlis.getCurrentMonth());
        commonAutoNoticeJobService.notUploadTopicAutoNotice(NCNYW_GWBM, YEAR_UPLOAD_PERIOD, yearStatisticPeriod, noticeMessage);
        commonAutoNoticeJobService.seasonNotUploadTopicAutoNotice(NCNYW_GWBM, SEASON_UPLOAD_PERIOD, noticeMessage);
        commonAutoNoticeJobService.notUploadTopicAutoNotice(NCNYW_GWBM, MONTH_UPLOAD_PERIOD, monthStatisticPeriod, noticeMessage);
        commonAutoNoticeJobService.weekNotUploadTopicAutoNotice(NCNYW_GWBM, noticeMessage);
    }

    private String getUploadNoticeMessage(String uploadPeriod){
        String dateString = commonAutoNoticeJobService.getUploadDeadLineDateString(NCNYW_GWBM, uploadPeriod);
        String month = dateString.substring(4,6);
        String day = dateString.substring(6);
        return  "单位，贵单位应于" + month + "月" + day + "日前报送数据，请按时报送，谢谢您的支持和配合！（市人大常委会农村农业工委）";
    }

}
