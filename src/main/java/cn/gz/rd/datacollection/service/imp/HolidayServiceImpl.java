package cn.gz.rd.datacollection.service.imp;

import cn.gz.rd.datacollection.controller.request.HolidayRequest;
import cn.gz.rd.datacollection.controller.request.PageRequest;
import cn.gz.rd.datacollection.dao.HolidayMapper;
import cn.gz.rd.datacollection.model.CommonException;
import cn.gz.rd.datacollection.model.Holiday;
import cn.gz.rd.datacollection.model.User;
import cn.gz.rd.datacollection.service.HolidayService;
import cn.gz.rd.datacollection.utils.DateUtlis;
import cn.gz.rd.datacollection.utils.ResultMapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * @author panxuan
 * 假期服务实现类
 */
@Service
public class HolidayServiceImpl implements HolidayService {

    /**
     * 采集系统开始年份
     */
    @Value("${data.collection.start.year}")
    private int startYear;

    /**
     * 采集系统开始月份
     */
    @Value("${data.collection.start.month}")
    private int startMonth;

    private HolidayMapper holidayMapper;

    @Autowired
    public HolidayServiceImpl(HolidayMapper holidayMapper){
        this.holidayMapper = holidayMapper;
    }

    @Override
    public Map<String, Object> addHoliday(HttpSession session, Holiday holiday) throws CommonException {
        verifyHoliday(holiday.getStartDate(), holiday.getEndDate());
        //从session中获取用户信息
        User user = (User) session.getAttribute("user");
        String userName = user.getUserName();
        holiday.setCreateUser(userName);
        holiday.setModifyUser(userName);
        int result = holidayMapper.insert(holiday);
        if (result == 0){
            throw new CommonException("假期插入失败!");
        }
        return ResultMapUtils.getResultMap("id", holiday.getId());
    }

    @Override
    public void alterHoliday(HttpSession session, Holiday holiday) throws CommonException {
        verifyHoliday(holiday.getStartDate(), holiday.getEndDate());
        //从session中获取用户信息
        User user = (User) session.getAttribute("user");
        holiday.setModifyUser(user.getUserName());
        int result = holidayMapper.updateByPrimaryKey(holiday);
        if (result == 0){
            throw new CommonException("假期更新失败!");
        }
    }

    @Override
    public void deleteHoliday(int id) throws CommonException {
        int result = holidayMapper.deleteByPrimaryKey(id);
        if (result == 0){
            throw new CommonException("假期删除失败!");
        }
    }

    @Override
    public Map<String, Object> getHolidayList(PageRequest request) {
        int pageNum = request.getPageNum();
        int pageSize = request.getPageSize();
        int startSize = (pageNum - 1) * pageSize;
        List<Holiday> holidays = holidayMapper.pageQueryAll(startSize, pageSize);
        int count = holidayMapper.countAll();
        return ResultMapUtils.getResultMap(holidays, count);
    }

    @Override
    public Map<String, Object> getHolidayByCondition(HolidayRequest request) {
        int pageNum = request.getPageNum();
        int pageSize = request.getPageSize();
        int startSize = (pageNum - 1) * pageSize;
        List<Holiday> holidays;
        int count;
        String yearString = request.getYear();
        if (yearString.equals("全部")){
            holidays = holidayMapper.pageQueryAll(startSize, pageSize);
            count = holidayMapper.countAll();
        }else {
            int year = Integer.parseInt(yearString);
            holidays = holidayMapper.pageQueryByCondition(year, startSize, pageSize);
            count = holidayMapper.countByCondition(year);
        }
        return ResultMapUtils.getResultMap(holidays, count);
    }

    @Override
    public Map<String, Object> getYears() {
        List<String> yearList = new ArrayList<>();
        yearList.add("全部");
        yearList.addAll(holidayMapper.getYearList());
        if (yearList.size() == 2){
            yearList.remove(0);
        }
        return ResultMapUtils.getResultMap(yearList);
    }

    @Override
    public List<String> getWorkDayMap(int year, int month){
        Set<String> dateList = getDateList(year, month);
        List<String> monthWorkDays = DateUtlis.getMonthWorkDayList(year, month);
        List<String> removeDateList = new ArrayList<>();
        for (String monthWorkDay : monthWorkDays){
            if (dateList != null){
                for (String date : dateList){
                    if (monthWorkDay.equals(date)){
                        removeDateList.add(monthWorkDay);
                    }
                }
            }
        }
        monthWorkDays.removeAll(removeDateList);
        Set<String> extraWorkDates = getExtraWorkDates(year, month);
        List<String> extraRemoveWorkDateList = new ArrayList<>();
        for (String date : extraWorkDates){
            for (String monthWorkDay : monthWorkDays){
                if (monthWorkDay.equals(date)){
                    extraRemoveWorkDateList.add(monthWorkDay);
                }
            }
        }
        extraWorkDates.removeAll(extraRemoveWorkDateList);
        monthWorkDays.addAll(extraWorkDates);
        Collections.sort(monthWorkDays);
        return monthWorkDays;
    }

    private Set<String> getExtraWorkDates(int year, int month){
        List<Holiday> extraWorkDateHolidays = holidayMapper.getExtraWorkDateByYearAndMonth(year, month);
        Set<String> extraWorkDates = new HashSet<>();
        for (Holiday extraWorkDateHoliday : extraWorkDateHolidays){
            extraWorkDates.add(DateUtlis.getDateString(extraWorkDateHoliday.getFirstExtraWorkDate()));
            if (extraWorkDateHoliday.getSecondExtraWorkDate() != null){
                extraWorkDates.add(DateUtlis.getDateString(extraWorkDateHoliday.getSecondExtraWorkDate()));
            }
            if (extraWorkDateHoliday.getThirdExtraWorkDate() != null){
                extraWorkDates.add(DateUtlis.getDateString(extraWorkDateHoliday.getThirdExtraWorkDate()));
            }
        }
        return extraWorkDates;
    }

    private Set<String> getDateList(int year, int month){
        Map<Integer,Map<Integer,Set<String>>> yearMonthDateMap = new HashMap<>();
        List<Holiday> holidays = holidayMapper.queryAll();
        for (Holiday holiday : holidays){
            Date startDate = holiday.getStartDate();
            Date endDate = holiday.getEndDate();
            if (startDate != null && endDate != null){
                Map<Integer, Map<Integer, Set<String>>> dateMap = DateUtlis.getDateMap(startDate, endDate);
                for (int y=startYear;y<=year;y++){
                    Map<Integer, Set<String>> allMonthDateMap = yearMonthDateMap.get(y);
                    if (allMonthDateMap == null && y == startYear){
                        allMonthDateMap = new HashMap<>();
                    }
                    Map<Integer, Set<String>> oneMonthDateMap = dateMap.get(y);
                    if (allMonthDateMap != null){
                        for (int m=startMonth;m<=month;m++){
                            Set<String> allDateString = allMonthDateMap.get(m);
                            if (oneMonthDateMap != null){
                                Set<String> oneDateString = oneMonthDateMap.get(m);
                                if (allDateString != null){
                                    if (oneDateString!=null){
                                        allDateString.addAll(oneDateString);
                                    }
                                }else {
                                    if (oneDateString!=null){
                                        allMonthDateMap.put(m, oneDateString);
                                        yearMonthDateMap.put(y, allMonthDateMap);
                                    }
                                }
                            }
                        }
                    }else {
                        yearMonthDateMap.putAll(dateMap);
                    }
                }
            }
        }
        Map<Integer, Set<String>> monthDates = yearMonthDateMap.get(year);
        Set<String> dateList = new HashSet<>();
        if (monthDates != null){
            dateList = monthDates.get(month);
        }
        return dateList;
    }

    @Override
    public void getWorkDayMap(List<String> workDatMap, int year, int month, int uploadDeadLine){
        if (workDatMap.size() < uploadDeadLine){
            if (month == 12){
                month = 1;
                year = year + 1;
            }else {
                month = month + 1;
            }
            workDatMap.addAll(getWorkDayMap(year, month));
            getWorkDayMap(workDatMap, year, month, uploadDeadLine);
        }
    }

    private void verifyHoliday(Date startDate, Date endDate) throws CommonException {
        long dayDifference = DateUtlis.getDayDifference(startDate, endDate);
        if (dayDifference < 0){
            throw new CommonException("假期结束日期应大于假期起始日期!");
        }
    }

}
