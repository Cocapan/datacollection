package cn.gz.rd.datacollection.model;

/**
 * 数据主题信息表
 * @author panxuan
 */
public class Topic {

    /**
     * 主题编码
     */
    private String topicId;

    /**
     * 数据主题名称
     */
    private String topicName;

    /**
     * 上级主题编码
     */
    private String superiorTopicId;

    /**
     * 主题级别
     */
    private int topicLevel;

    /**
     * 主题类型
     */
    private String topicType;

    /**
     *主题存放表名
     */
    private String storageTableName;

    /**
     * 主题文件存放路径
     */
    private String topicFileStoragePath;

    /**
     * 数据上报频度
     */
    private String uploadPeriod;

    /**
     * 上传截止时间
     */
    private int uploadDeadLine;

    /**
     * 采集方式
     */
    private String colllectWay;

    /**
     * 所属工委编码
     */
    private String workingCommitteeId;

    /**
     * 备注
     */
    private String comment;

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

    public String getSuperiorTopicId() {
        return superiorTopicId;
    }

    public void setSuperiorTopicId(String superiorTopicId) {
        this.superiorTopicId = superiorTopicId;
    }

    public int getTopicLevel() {
        return topicLevel;
    }

    public void setTopicLevel(int topicLevel) {
        this.topicLevel = topicLevel;
    }

    public String getTopicType() {
        return topicType;
    }

    public void setTopicType(String topicType) {
        this.topicType = topicType;
    }

    public String getStorageTableName() {
        return storageTableName;
    }

    public void setStorageTableName(String storageTableName) {
        this.storageTableName = storageTableName;
    }

    public String getTopicFileStoragePath() {
        return topicFileStoragePath;
    }

    public void setTopicFileStoragePath(String topicFileStoragePath) {
        this.topicFileStoragePath = topicFileStoragePath;
    }

    public String getUploadPeriod() {
        return uploadPeriod;
    }

    public void setUploadPeriod(String uploadPeriod) {
        this.uploadPeriod = uploadPeriod;
    }

    public int getUploadDeadLine() {
        return uploadDeadLine;
    }

    public void setUploadDeadLine(int uploadDeadLine) {
        this.uploadDeadLine = uploadDeadLine;
    }

    public String getColllectWay() {
        return colllectWay;
    }

    public void setColllectWay(String colllectWay) {
        this.colllectWay = colllectWay;
    }

    public String getWorkingCommitteeId() {
        return workingCommitteeId;
    }

    public void setWorkingCommitteeId(String workingCommitteeId) {
        this.workingCommitteeId = workingCommitteeId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
