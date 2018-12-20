package cn.gz.rd.datacollection.service.cron;

import cn.gz.rd.datacollection.service.CommonAutoNoticeJobService;
import cn.gz.rd.datacollection.utils.DateUtlis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class CxjwAutoNoticeJob {

    private final static String CXJW_GWBM = "GWBM_01";

    private final static String YEAR_UPLOAD_PERIOD = "年";

    private final static String SEASON_UPLOAD_PERIOD = "季";

    private final static String MONTH_UPLOAD_PERIOD = "月";

    private final static String YEAR_UPLOAD_NOTICE_MESSAGE = "部门系统管理员，请您于本月内登录智慧人大城乡建设环境与资源保护联网监督数据采集系统，按系统指引上传相关数据，谢谢配合。";

    private final static String SEASON_UPLOAD_NOTICE_MESSAGE = "部门系统管理员，请您于10个工作日内登录智慧人大城乡建设环境与资源保护联网监督数据采集系统，按系统指引上传相关数据，谢谢配合。";

    private final static String MONTH_UPLOAD_NOTICE_MESSAGE = "部门系统管理员，请您于7个工作日内登录智慧人大城乡建设环境与资源保护联网监督数据采集系统，按系统指引上传相关数据，谢谢配合。";

    private final static String NOT_UPLOAD_NOTICE_MESSAGE = "部门系统管理员，请您即登录智慧人大城乡建设环境与资源保护联网监督数据采集系统，上传相关数据，如因特殊原因无法在本日内完成上传，请向城建环资工委作出书面说明，感谢您的配合。";

    private CommonAutoNoticeJobService commonAutoNoticeJobService;

    @Autowired
    public CxjwAutoNoticeJob(CommonAutoNoticeJobService commonAutoNoticeJobService){
        this.commonAutoNoticeJobService = commonAutoNoticeJobService;
    }

    /**
     * 每年2月1号9点发送
     */
    @Scheduled(cron = "0 0 9 1 2 *")
    public void yearUploadTopicAutoNotice() {
        commonAutoNoticeJobService.sentUploadNoticeMsgToDepartment(CXJW_GWBM, YEAR_UPLOAD_PERIOD, YEAR_UPLOAD_NOTICE_MESSAGE);
    }

    /**
     * 每月1号9点发送
     */
    @Scheduled(cron = "0 0 9 1 * *")
    public void monthAndSeasonUploadTopicAutoNotice() {
        commonAutoNoticeJobService.sentUploadNoticeMsgToDepartment(CXJW_GWBM, MONTH_UPLOAD_PERIOD, MONTH_UPLOAD_NOTICE_MESSAGE);
        int currentMonth = DateUtlis.getCurrentMonth();
        switch (currentMonth){
            case 1:
            case 4:
            case 7:
            case 10:
                commonAutoNoticeJobService.sentUploadNoticeMsgToDepartment(CXJW_GWBM, SEASON_UPLOAD_PERIOD, SEASON_UPLOAD_NOTICE_MESSAGE);
                break;
            default:
                break;
        }
    }

    /**
     * 每天9点进行检查
     */
    @Scheduled(cron = "0 0 9 * * *")
    public void notUploadTopicAutoCheck() {
        String yearStatisticPeriod = String.valueOf(DateUtlis.getCurrentYear() - 1);
        String monthStatisticPeriod = String.valueOf(DateUtlis.getCurrentMonth());
        commonAutoNoticeJobService.notUploadTopicAutoNotice(CXJW_GWBM, YEAR_UPLOAD_PERIOD, yearStatisticPeriod, NOT_UPLOAD_NOTICE_MESSAGE);
        commonAutoNoticeJobService.seasonNotUploadTopicAutoNotice(CXJW_GWBM, SEASON_UPLOAD_PERIOD, NOT_UPLOAD_NOTICE_MESSAGE);
        commonAutoNoticeJobService.notUploadTopicAutoNotice(CXJW_GWBM, MONTH_UPLOAD_PERIOD, monthStatisticPeriod, NOT_UPLOAD_NOTICE_MESSAGE);
    }

}
