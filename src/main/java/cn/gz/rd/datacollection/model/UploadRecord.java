package cn.gz.rd.datacollection.model;

import java.util.Date;

/**
 * @author panxuan
 * 上传记录
 */
public class UploadRecord {

    /**
     * 序号
     */
    private int id;

    /**
     * 主题编号
     */
    private String topicId;

    /**
     * 主题名
     */
    private String topicName;

    /**
     * 部门编码
     */
    private String departmentId;

    /**
     * 部门名
     */
    private String departmentName;

    /**
     * 工委编码
     */
    private String workingCommitteeId;

    /**
     * 工委名
     */
    private String workingCommitteeName;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 数据条数
     */
    private int dataCount;

    /**
     * 数据存放路径
     */
    private String storagePath;

    /**
     * 上传时间
     */
    private Date uploadTime;

    /**
     * 上传用户
     */
    private String uploadUser;

    /**
     * 统计周期
     */
    private String statisticsPeriod;

    /**
     * 上报频度
     */
    private String uploadPeriod;

    /**
     * 在线预览路径
     */
    private String previewPath;

    private String superiorTopicId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getWorkingCommitteeId() {
        return workingCommitteeId;
    }

    public void setWorkingCommitteeId(String workingCommitteeId) {
        this.workingCommitteeId = workingCommitteeId;
    }

    public String getWorkingCommitteeName() {
        return workingCommitteeName;
    }

    public void setWorkingCommitteeName(String workingCommitteeName) {
        this.workingCommitteeName = workingCommitteeName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getDataCount() {
        return dataCount;
    }

    public void setDataCount(int dataCount) {
        this.dataCount = dataCount;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getUploadUser() {
        return uploadUser;
    }

    public void setUploadUser(String uploadUser) {
        this.uploadUser = uploadUser;
    }

    public String getStatisticsPeriod() {
        return statisticsPeriod;
    }

    public void setStatisticsPeriod(String statisticsPeriod) {
        this.statisticsPeriod = statisticsPeriod;
    }

    public String getUploadPeriod() {
        return uploadPeriod;
    }

    public void setUploadPeriod(String uploadPeriod) {
        this.uploadPeriod = uploadPeriod;
    }

    public String getPreviewPath() {
        return previewPath;
    }

    public void setPreviewPath(String previewPath) {
        this.previewPath = previewPath;
    }

    public String getSuperiorTopicId() {
        return superiorTopicId;
    }

    public void setSuperiorTopicId(String superiorTopicId) {
        this.superiorTopicId = superiorTopicId;
    }
}
