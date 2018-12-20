package cn.gz.rd.datacollection.controller.request;

/**
 * 假期请求参数
 */
public class HolidayRequest extends PageRequest{

    /**
     * 年份
     */
    private String year;

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
