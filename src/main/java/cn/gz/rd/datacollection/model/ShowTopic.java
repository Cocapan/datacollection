package cn.gz.rd.datacollection.model;

public class ShowTopic extends Topic {

    /**
     * 数据所属工委
     */
    private String workingCommitteeName;

    /**
     * 数据提供部门
     */
    private String departmentName;

    /**
     * 部门编码
     */
    private String departmentId;

    /**
     * 上传时间
     */
    private String uploadTime;

    /**
     * 统计周期
     */
    private String statisticsPeriod;

    private String uploadDeadLineDate;

    private boolean isOverTime;

    public String getWorkingCommitteeName() {
        return workingCommitteeName;
    }

    public void setWorkingCommitteeName(String workingCommitteeName) {
        this.workingCommitteeName = workingCommitteeName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getStatisticsPeriod() {
        return statisticsPeriod;
    }

    public void setStatisticsPeriod(String statisticsPeriod) {
        this.statisticsPeriod = statisticsPeriod;
    }

    public String getUploadDeadLineDate() {
        return uploadDeadLineDate;
    }

    public void setUploadDeadLineDate(String uploadDeadLineDate) {
        this.uploadDeadLineDate = uploadDeadLineDate;
    }

    public boolean isOverTime() {
        return isOverTime;
    }

    public void setOverTime(boolean overTime) {
        isOverTime = overTime;
    }
}
