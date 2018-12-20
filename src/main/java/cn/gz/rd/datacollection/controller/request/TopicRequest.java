package cn.gz.rd.datacollection.controller.request;

/**
 * @author panxuan
 * 主题模块所需参数
 */
public class TopicRequest extends PageRequest{

    /**
     * 年份
     */
    private String year;

    /**
     * 月份
     */
    private String month;

    /**
     * 部门名
     */
    private String departmentName;

    /**
     * 主题类型
     * 文件/数据表
     */
    private String topicType;

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getTopicType() {
        return topicType;
    }

    public void setTopicType(String topicType) {
        this.topicType = topicType;
    }
}
