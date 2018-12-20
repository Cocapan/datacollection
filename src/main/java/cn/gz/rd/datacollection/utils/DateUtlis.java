package cn.gz.rd.datacollection.utils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author panxuan
 * 时间工具类
 */
public class DateUtlis {

    /**
     * 根据相对索引获取该月
     * @param index 索引, 0表示上月, 1表示当前月, 2表示下月
     * @return 月份数
     */
    public static int getMonth(int index){
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH) + index;
    }

    /**
     * 获取某年某月的第一天日期
     * @return 本月第一天日期
     */
    public static Date getFistDayInMonth(int year, int month){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR,year - calendar.get(Calendar.YEAR));
        calendar.add(Calendar.MONTH,month - (calendar.get(Calendar.MONTH) + 1));
        calendar.set(Calendar.DAY_OF_MONTH,1);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar.getTime();
    }

    /**
     * 获取某年某月的最后一天日期
     * @return 本月最后一天日期
     */
    public static Date getLastDayInMonth(int year, int month){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR,year - calendar.get(Calendar.YEAR));
        calendar.add(Calendar.MONTH,month - (calendar.get(Calendar.MONTH) + 1));
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY,23);
        calendar.set(Calendar.MINUTE,59);
        calendar.set(Calendar.SECOND,59);
        calendar.set(Calendar.MILLISECOND,999);
        return calendar.getTime();
    }

    public static Date getLastTimeInDay(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY,23);
        calendar.set(Calendar.MINUTE,59);
        calendar.set(Calendar.SECOND,59);
        calendar.set(Calendar.MILLISECOND,999);
        return calendar.getTime();
    }

    /**
     * 根据相对索引获取该月第一天日期
     * @param index 索引, -1表示上月, 0表示当前月, 1表示下月
     * @return 该月第一天日期
     */
    public static Date getMonthDate(int index){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        //!!!不能用set方法, set方法实现可能是按一个月30天进行处理, 31天时会出现bug, 改用add方法
        // calendar.set(Calendar.MONTH, index);
        calendar.add(Calendar.MONTH, index);
        return calendar.getTime();
    }

    /**
     * 根据日期获取上个月的日期
     * @param date 日期
     * @return 该日期上月日期
     */
    public static Date getLastMonthDate(Date date){
        Calendar calendar = Calendar.getInstance();
        //!!!不能用set方法, set方法实现可能是按一个月30天进行处理, 31天时会出现bug, 改用add方法
        // calendar.set(Calendar.MONTH, index);
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);
        return calendar.getTime();
    }

    /**
     * 获取当前年
     * @return 当前年份数
     */
    public static int getCurrentYear(){
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 获取当前月
     * @return 当前月份数
     */
    public static int getCurrentMonth(){
        return getMonth(1);
    }

    /**
     * 获取指定天
     * @param index 索引
     * @return 当前天数
     */
    public static String getDay(int index, Date lastDayInMonth){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(lastDayInMonth);
        calendar.add(Calendar.DAY_OF_MONTH, -index);
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(date);
    }

    /**
     * 获取两个日期的时差
     * @param startDate 起始日期
     * @param endDate 结束日期
     * @return 时差
     */
    public static long getTimeDifference(Date startDate, Date endDate){
        long different = endDate.getTime() - startDate.getTime();
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        return (different/hoursInMilli);
    }

    /**
     * 获取两个日期的天数差
     * @param startDate 起始日期
     * @param endDate 结束日期
     * @return 时差
     */
    public static long getDayDifference(Date startDate, Date endDate){
        long different = endDate.getTime() - startDate.getTime();
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        return (different/daysInMilli);
    }

    /**
     * 根据年月日获取时间
     * @param year 年
     * @param month 月
     * @param day 日
     * @return 时间
     */
    public static Date getDate(int year, int month, int day){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month-1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar.getTime();
    }

    public static Map<Integer,Map<Integer,Set<String>>> getDateMap(Date startDate, Date endDate){
        Map<Integer,Map<Integer,Set<String>>> yearMonthDateMap = new HashMap<>();
        Map<Integer,Set<String>> monthDateMap = new HashMap<>();
        Set<String> dateList = new HashSet<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startDate);
        int startYear = startCalendar.get(Calendar.YEAR);
        int startMonth = startCalendar.get(Calendar.MONTH) + 1;
        long dayDifference = DateUtlis.getDayDifference(startDate, endDate) + 1;
        for (int i=0; i<dayDifference; i++){
            if (i == 0){
                startCalendar.add(Calendar.DAY_OF_MONTH, 0);
            }else {
                startCalendar.add(Calendar.DAY_OF_MONTH, 1);
            }
            int currentYear = startCalendar.get(Calendar.YEAR);
            int currentMonth = startCalendar.get(Calendar.MONTH) + 1;
            if (currentYear == startYear && currentMonth == startMonth){
                dateList.add(format.format(startCalendar.getTime()));
            }
            if (currentYear == startYear && currentMonth > startMonth){
                yearMonthDateMap.put(startYear, monthDateMap);
                dateList = new HashSet<>();
                startMonth = currentMonth;
                dateList.add(format.format(startCalendar.getTime()));
            }
            if (currentYear > startYear){
                yearMonthDateMap.put(startYear,monthDateMap);
                dateList = new HashSet<>();
                monthDateMap = new HashMap<>();
                startYear = currentYear;
                startMonth = currentMonth;
                dateList.add(format.format(startCalendar.getTime()));
            }
            monthDateMap.put(startMonth, dateList);
        }
        yearMonthDateMap.put(startYear, monthDateMap);
        return yearMonthDateMap;
    }

    public static String getCurrentDateString(){
        return getDateString(new Date());
    }

    public static String getDateString(Date date){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(date);
    }

    public static List<String> getMonthWorkDayList(int year, int month){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Calendar instance = Calendar.getInstance();
        List<String> dateList = new ArrayList<>();
        Date firstDayInMonth = DateUtlis.getFistDayInMonth(year, month);
        Date lastDayInMonth = DateUtlis.getLastDayInMonth(year, month);
        long dayDifference = getDayDifference(firstDayInMonth, lastDayInMonth) + 1;
        instance.setTime(firstDayInMonth);
        for (int i=0;i<dayDifference;i++){
            if (i == 0){
                instance.add(Calendar.DAY_OF_MONTH,0);
            }else {
                instance.add(Calendar.DAY_OF_MONTH,1);
            }
            if (isWorkDay(instance.get(Calendar.DAY_OF_WEEK))){
                dateList.add(format.format(instance.getTime()));
            }
        }
        return dateList;
    }

    public static List<String> getMonthDayList(int year, int month){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Calendar instance = Calendar.getInstance();
        List<String> dateList = new ArrayList<>();
        Date firstDayInMonth = DateUtlis.getFistDayInMonth(year, month);
        Date lastDayInMonth = DateUtlis.getLastDayInMonth(year, month);
        long dayDifference = getDayDifference(firstDayInMonth, lastDayInMonth) + 1;
        instance.setTime(firstDayInMonth);
        for (int i=0;i<dayDifference;i++){
            if (i == 0){
                instance.add(Calendar.DAY_OF_MONTH,0);
            }else {
                instance.add(Calendar.DAY_OF_MONTH,1);
            }
            dateList.add(format.format(instance.getTime()));
        }
        return dateList;
    }

    private static boolean isWorkDay(int dayOfWeek){
        switch (dayOfWeek){
            case Calendar.SATURDAY:
            case Calendar.SUNDAY:
                return false;
            default:
                return true;
        }
    }

    public static List<Integer> getYearList(int startYear, int endYear){
       List<Integer> yearList = new ArrayList<>();
       for (int year = startYear;year<=endYear;year++){
           yearList.add(year);
       }
       return yearList;
    }

    public static Map<Integer,List<Integer>> getMonthList(int startYear, int startMonth, int currentYear, int currentMonth){
        Map<Integer,List<Integer>> yearMonthListMap = new HashMap<>();
        List<Integer> monthList;
        if (startYear == currentYear){
            monthList = new ArrayList<>();
            for (int month = startMonth;month <= currentMonth; month++){
                monthList.add(month);
            }
            yearMonthListMap.put(startYear, monthList);
        }
        if ((currentYear-startYear) == 1){
            monthList = new ArrayList<>();
            for (int month = startMonth;month <= 12; month++) {
                monthList.add(month);
            }
            yearMonthListMap.put(startYear, monthList);
            monthList = new ArrayList<>();
            for (int month = 1;month <= currentMonth; month++) {
                monthList.add(month);
            }
            yearMonthListMap.put(currentYear, monthList);
        }
        if ((currentYear-startYear) > 1){
            for (int year = startYear; year <= currentYear; year++){
                if (year == startYear){
                    monthList = new ArrayList<>();
                    for (int month = startMonth;month <= 12; month++) {
                        monthList.add(month);
                    }
                    yearMonthListMap.put(startYear, monthList);
                }else if (year == currentYear){
                    monthList = new ArrayList<>();
                    for (int month = 1;month <= currentMonth; month++) {
                        monthList.add(month);
                    }
                    yearMonthListMap.put(currentYear, monthList);
                }else {
                    monthList = new ArrayList<>();
                    for (int month = 1;month <= 12; month++) {
                        monthList.add(month);
                    }
                    yearMonthListMap.put(year, monthList);
                }
            }
        }
        return yearMonthListMap;
    }

    public static Map<String, Integer> getWeekMap(Date startDate, Date endDate){
        Calendar instance = Calendar.getInstance();
        Map<String, Integer> weekMap = new HashMap<>();
        weekMap.put("startWeek", getWeekOfYear(startDate));
        instance.setTime(endDate);
        if (instance.get(Calendar.MONTH) == 11){
            int endWeek = DateUtlis.getMaxWeekNumOfYear(instance.get(Calendar.YEAR));
            weekMap.put("endWeek", endWeek);
        }else {
            weekMap.put("endWeek", getWeekOfYear(endDate));
        }
        return weekMap;
    }

    public static boolean isRepeatWeek(Date startDate){
        Calendar instance = Calendar.getInstance();
        Date thisWeekMonday = getThisWeekMonday(startDate);
        instance.setTime(thisWeekMonday);
        int thisWeekMondayMonth = instance.get(Calendar.MONTH);
        instance.setTime(startDate);
        int startDateMonth = instance.get(Calendar.MONTH);
        return (startDateMonth - thisWeekMondayMonth) == 1;
    }

    private static Date getThisWeekMonday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 获得当前日期是一个星期的第几天
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        // 获得当前日期是一个星期的第几天
        int day = cal.get(Calendar.DAY_OF_WEEK);
        // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
        return cal.getTime();
    }

/*    private static int getWeek(Date date){
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        return instance.get(Calendar.WEEK_OF_YEAR);
    }*/

    public static int getWeekOfYear(Date date) {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setMinimalDaysInFirstWeek(7);
        c.setTime(date);
        return c.get(Calendar.WEEK_OF_YEAR);
    }

    private static int getMaxWeekNumOfYear(int year) {
        Calendar c = new GregorianCalendar();
        c.set(year, Calendar.DECEMBER, 31, 23, 59, 59);
        return getWeekOfYear(c.getTime());
    }

    public static List<String> getDayListByWeek(int week){
        List<String> dayList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.WEEK_OF_YEAR, week);
        calendar.set(Calendar.DAY_OF_WEEK,2);
        for (int i=0;i<7;i++){
            if (i == 0){
                calendar.add(Calendar.DAY_OF_MONTH, 0);
            }else {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
            dayList.add(getDateString(calendar.getTime()));
        }
        return dayList;
    }

    public static int getMonthDifference(int startYear, int startMonth){
        int currentMonth = DateUtlis.getCurrentMonth();
        int currentYear = DateUtlis.getCurrentYear();
        int index = 0;
        if (startYear == currentYear){
            index = currentMonth - startMonth;
        }
        if ((currentYear - startYear) == 1){
            index = currentMonth - 1 + 12 - startMonth + 1;
        }
        if ((currentYear - startYear) > 1){
            index = currentMonth - 1 + 12 - startMonth + 1 + 12*(currentYear-startYear-1);
        }
        return index;
    }

    public static boolean isMonday(Date date){
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        return instance.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY;
    }

}
