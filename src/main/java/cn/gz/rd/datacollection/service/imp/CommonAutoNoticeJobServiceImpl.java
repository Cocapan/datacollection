package cn.gz.rd.datacollection.service.imp;

import cn.gz.rd.datacollection.dao.DepartmentMapper;
import cn.gz.rd.datacollection.dao.TopicMapper;
import cn.gz.rd.datacollection.dao.UserMapper;
import cn.gz.rd.datacollection.model.Department;
import cn.gz.rd.datacollection.service.CommonAutoNoticeJobService;
import cn.gz.rd.datacollection.service.HolidayService;
import cn.gz.rd.datacollection.service.SMSService;
import cn.gz.rd.datacollection.service.TopicService;
import cn.gz.rd.datacollection.utils.DateUtlis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CommonAutoNoticeJobServiceImpl implements CommonAutoNoticeJobService {

    private final static String WEEK_UPLOAD_PERIOD = "周";

    private UserMapper userMapper;

    private DepartmentMapper departmentMapper;

    private TopicMapper topicMapper;

    private SMSService smsService;

    private TopicService topicService;

    private HolidayService holidayService;

    @Autowired
    public CommonAutoNoticeJobServiceImpl(UserMapper userMapper, DepartmentMapper departmentMapper, TopicMapper topicMapper, SMSService smsService, TopicService topicService, HolidayService holidayService){
        this.userMapper = userMapper;
        this.departmentMapper = departmentMapper;
        this.topicMapper = topicMapper;
        this.smsService = smsService;
        this.topicService = topicService;
        this.holidayService = holidayService;
    }

    @Override
    public void notUploadTopicAutoNotice(String workingCommitteeId, String uploadPeriod, String statisticPeriod, String noticeMessage) {
        Date date = new Date();
        String dateString = DateUtlis.getDateString(date);
        String uploadDeadLineDateString = getUploadDeadLineDateString(workingCommitteeId, uploadPeriod);
        List<Department> departments = departmentMapper.selectDepartmentsByWorkingCommitteeIdAndUploadPeriod(workingCommitteeId, uploadPeriod);
        for (Department department : departments){
            if (uploadDeadLineDateString.equals(dateString)){
                boolean isAllUpload = topicService.isAllUpload(workingCommitteeId, department.getDepartmentId(), uploadPeriod, statisticPeriod);
                if (!isAllUpload){
                    sentOverTimeNoticeMsgToDepartment(department, noticeMessage);
                }
            }
        }
    }

    @Override
    public void seasonNotUploadTopicAutoNotice(String workingCommitteeId, String uploadPeriod, String noticeMessage) {
        int currentYear = DateUtlis.getCurrentYear();
        int currentMonth = DateUtlis.getCurrentMonth();
        switch (currentMonth){
            case 1:
                String statisticPeriod = (currentYear-1) + "Q" + 4;
                notUploadTopicAutoNotice(workingCommitteeId, uploadPeriod, statisticPeriod, noticeMessage);
                break;
            case 4:
                statisticPeriod = currentYear + "Q" + 1;
                notUploadTopicAutoNotice(workingCommitteeId, uploadPeriod, statisticPeriod, noticeMessage);
                break;
            case 7:
                statisticPeriod = currentYear + "Q" + 2;
                notUploadTopicAutoNotice(workingCommitteeId, uploadPeriod, statisticPeriod, noticeMessage);
                break;
            case 10:
                statisticPeriod = currentYear + "Q" + 3;
                notUploadTopicAutoNotice(workingCommitteeId, uploadPeriod, statisticPeriod, noticeMessage);
                break;
            default:
                break;
        }
    }

    @Override
    public void sentUploadNoticeMsgToDepartment(String workingCommitteeId, String uploadPeriod, String noticeMessage) {
        List<Department> departments = departmentMapper.selectDepartmentsByWorkingCommitteeIdAndUploadPeriod(workingCommitteeId, uploadPeriod);
        for (Department department : departments){
            String phoneNumber = userMapper.selectPhoneNumberByDepartmentId(department.getDepartmentId());
            smsService.send(phoneNumber, department.getDepartmentName() + noticeMessage);
        }
    }

    private void sentOverTimeNoticeMsgToDepartment(Department department, String noticeMessage) {
        String phoneNumber = userMapper.selectPhoneNumberByDepartmentId(department.getDepartmentId());
        smsService.send(phoneNumber, department.getDepartmentName() + noticeMessage);
    }

    @Override
    public void weekNotUploadTopicAutoNotice(String workingCommitteeId, String noticeMessage) {
        int weekOfYear = DateUtlis.getWeekOfYear(new Date());
        List<String> dayListByWeek = DateUtlis.getDayListByWeek(weekOfYear);
        int currentYear = DateUtlis.getCurrentYear();
        int currentMonth = DateUtlis.getCurrentMonth();
        List<String> workDayMap = holidayService.getWorkDayMap(currentYear, currentMonth);
        int maxUploadDeadLine = topicMapper.selectMaxUploadDeadLine();
        holidayService.getWorkDayMap(workDayMap, currentYear, currentMonth, workDayMap.size() + maxUploadDeadLine);
        String statisticPeriod = 2018 +"W" + weekOfYear;
        for (String day : dayListByWeek){
            if (workDayMap.contains(day)){
                List<Department> departments = departmentMapper.selectDepartmentsByWorkingCommitteeIdAndUploadPeriod(workingCommitteeId, WEEK_UPLOAD_PERIOD);
                for (Department department : departments){
                    int uploadDeadLine = topicMapper.selectMaxUploadDeadLineByDepartmentIdAndUploadPeriod(department.getDepartmentId(), WEEK_UPLOAD_PERIOD);
                    if (workDayMap.get(workDayMap.indexOf(day) + uploadDeadLine).equals(DateUtlis.getDateString(new Date()))){
                        boolean isAllUpload = topicService.isAllUpload(workingCommitteeId, department.getDepartmentId(), WEEK_UPLOAD_PERIOD, statisticPeriod);
                        if (!isAllUpload){
                            sentOverTimeNoticeMsgToDepartment(department, noticeMessage);
                        }
                    }
                }
                break;
            }
        }
    }

    @Override
    public String getUploadDeadLineDateString(String workingCommitteeId, String uploadPeriod) {
        int uploadDeadLine = topicMapper.selectMaxUploadDeadLineByWorkingCommitteeIdUploadPeriod(workingCommitteeId, uploadPeriod);
        int currentYear = DateUtlis.getCurrentYear();
        int currentMonth = DateUtlis.getCurrentMonth();
        List<String> workDayMap;
        if (uploadPeriod.equals("年")){
            workDayMap = holidayService.getWorkDayMap(currentYear, 1);
            int maxUploadDeadLine = topicMapper.selectMaxUploadDeadLine();
            holidayService.getWorkDayMap(workDayMap, currentYear, 1, workDayMap.size() + maxUploadDeadLine);
        }else {
            workDayMap = holidayService.getWorkDayMap(currentYear, currentMonth);
            int maxUploadDeadLine = topicMapper.selectMaxUploadDeadLine();
            holidayService.getWorkDayMap(workDayMap, currentYear, currentMonth, workDayMap.size() + maxUploadDeadLine);
        }
        if (uploadPeriod.equals("周")){
            int weekOfYear = DateUtlis.getWeekOfYear(new Date());
            List<String> dayListByWeek = DateUtlis.getDayListByWeek(weekOfYear);
            for (String day : dayListByWeek){
                if (workDayMap.contains(day)) {
                    return workDayMap.get(workDayMap.indexOf(day) + uploadDeadLine);
                }
            }
        }
        return workingCommitteeId.equals("GWBM_01") ? workDayMap.get(uploadDeadLine - 1) : workDayMap.get(uploadDeadLine);
    }

}
