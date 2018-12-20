package cn.gz.rd.datacollection.controller.request;

/**
 * 上传记录模块所需参数
 */
public class UploadRecordRequest extends PageRequest{

    /**
     * 部门编码/工委编码
     */
    private String id;

    /**
     * 工委名
     */
    private String departmentName;

    /**
     * 主题名
     */
    private String topicName;

    /**
     * 上报频度
     */
    private String uploadPeriod;

    /**
     * 日期数组
     */
    private String[] dateArray;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getUploadPeriod() {
        return uploadPeriod;
    }

    public void setUploadPeriod(String uploadPeriod) {
        this.uploadPeriod = uploadPeriod;
    }

    public String[] getDateArray() {
        return dateArray;
    }

    public void setDateArray(String[] dateArray) {
        this.dateArray = dateArray;
    }

}
