package cn.gz.rd.datacollection.utils;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;

/**
 * 统计周期相关的工具类
 * @author Peng Xiaodong
 */
public class CountQuarterUtils {

    /**
     * 获取上一个季度的统计周期
     * @param currentQuarter 当前季度的统计周期
     * @return 上一个季度的统计周期
     */
    public static String getPreviousQuarter(String currentQuarter) {
        String[] qs = StringUtils.split(currentQuarter, "Q");
        int year = Integer.valueOf(qs[0]);
        int quarter = Integer.valueOf(qs[1]);
        if (quarter == 2) {
            quarter = 4;
            year = year - 1;
        } else {
            if (year == 2018){
                quarter = quarter - 1;
            }else {
                quarter = quarter - 2;
            }
        }
        return year + "Q" + quarter;
    }

    /**
     * 获取下一个季度的统计周期
     *
     * @param currentQuarter 统计周期
     * @return 下一季度的统计周期
     */
    public static String getNextQuarter(String currentQuarter) {
        String[] qs = StringUtils.split(currentQuarter, "Q");
        int year = Integer.valueOf(qs[0]);
        int quarter = Integer.valueOf(qs[1]);
        if (quarter == 4) {
            quarter = 2;
            year = year + 1;
        } else {
            quarter = quarter + 4;
        }
        return year + "Q" + quarter;
    }

    /**
     * 是否为一个合法的季度型的统计周期格式，例如：2018Q1
     * @param statPeriod 统计周期
     */
    public static boolean isValidQuarter(String statPeriod) {

        if (StringUtils.isEmpty(statPeriod)) {
            return false;
        }

        if (!StringUtils.contains(statPeriod, "Q")) {
            return false;
        }

        if (statPeriod.length() != 6) {
            return false;
        }

        String[] qs = statPeriod.split("Q");
        String yearStr = qs[0];
        String quarterStr = qs[1];

        if (!StringUtils.isNumeric(yearStr) || !StringUtils.isNumeric(quarterStr)) {
            return false;
        }

        int year = Integer.valueOf(yearStr);
        int quarter = Integer.valueOf(quarterStr);

        if (year < 2017 || year > 9999) {
            return false;
        }

        if (quarter < 1 || quarter > 4) {
            return false;
        }
        return true;
    }

    /**
     * 根据时间计算需要催办的统计周期
     * @param date 时间
     */
    public static String calcStatPeriod(LocalDate date) {
        int year = date.getYear();
        int month = date.getMonthValue();
        if (month >= 1 && month <= 3) {
            return --year + "Q4";

        } else if (month >= 4 && month <= 6) {
            return year + "Q1";

        } else if (month >= 7 && month <= 9) {
            return year + "Q2";

        } else if (month >= 10 && month <= 12) {
            return year + "Q3";

        } else {
            throw new IllegalArgumentException("月份有问题，请检查！month = " + month);
        }
    }

    /**
     * 根据统计周期判断上传频度
     * 第二季度和第四季度能查询到半年和季度类型的数据
     * 第一季度和第三季度只能查询季度类型的数据
     */
    public static String getUploadRate(String countRate) {
        String uploadRate = null; //空表示可以查询到季度和半年类型的数据
        if (StringUtils.endsWithAny(countRate, "Q1", "Q3")) {
            uploadRate = "季";
        }
        return uploadRate;
    }
}
