package cn.gz.rd.datacollection.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 教科文卫主题信息
 */
public class JkwwwSubjectInfo {

    /**
     * 序号
     */
    private Integer no;

    /**
     * 主题编码
     */
    private String subjectCode;

    /**
     * 工委名称
     */
    private String committeeName;

    /**
     * 部门编号
     */
    private String deptCode;

    /**
     * 主题状态ID
     */
    private Integer subjectStatusId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 数据上传时间
     */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyyMMdd")
    private Date uploadDate;

    /**
     * 数据主题名称
     */
    private String subjectName;

    /**
     * 上报频度
     */
    private String uploadRate;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 是否已经上传
     */
    private String isUpload;

    /**
     * 审核状态
     */
    private String checkStatus;

    /**
     * 状态编号
     */
    private Integer statusCode;

    /**
     * 预览路径
     */
    private String previewPath;

    /**
     * 主题的图片信息，如果该主题非图片则置空。
     */
    private List<JkwwPrjImgInfo> imgInfoList;

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getCommitteeName() {
        return committeeName;
    }

    public void setCommitteeName(String committeeName) {
        this.committeeName = committeeName;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getUploadRate() {
        return uploadRate;
    }

    public void setUploadRate(String uploadRate) {
        this.uploadRate = uploadRate;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getIsUpload() {
        return isUpload;
    }

    public void setIsUpload(String isUpload) {
        this.isUpload = isUpload;
    }

    public String getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(String checkStatus) {
        this.checkStatus = checkStatus;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public Integer getSubjectStatusId() {
        return subjectStatusId;
    }

    public void setSubjectStatusId(Integer subjectStatusId) {
        this.subjectStatusId = subjectStatusId;
    }

    public String getPreviewPath() {
        return previewPath;
    }

    public void setPreviewPath(String previewPath) {
        this.previewPath = previewPath;
    }

    public List<JkwwPrjImgInfo> getImgInfoList() {
        return imgInfoList;
    }

    public void setImgInfoList(List<JkwwPrjImgInfo> imgInfoList) {
        this.imgInfoList = imgInfoList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JkwwwSubjectInfo that = (JkwwwSubjectInfo) o;
        return Objects.equals(no, that.no) &&
                Objects.equals(subjectCode, that.subjectCode) &&
                Objects.equals(committeeName, that.committeeName) &&
                Objects.equals(deptCode, that.deptCode) &&
                Objects.equals(subjectStatusId, that.subjectStatusId) &&
                Objects.equals(deptName, that.deptName) &&
                Objects.equals(uploadDate, that.uploadDate) &&
                Objects.equals(subjectName, that.subjectName) &&
                Objects.equals(uploadRate, that.uploadRate) &&
                Objects.equals(dataType, that.dataType) &&
                Objects.equals(isUpload, that.isUpload) &&
                Objects.equals(checkStatus, that.checkStatus) &&
                Objects.equals(statusCode, that.statusCode) &&
                Objects.equals(previewPath, that.previewPath) &&
                Objects.equals(imgInfoList, that.imgInfoList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(no, subjectCode, committeeName, deptCode, subjectStatusId, deptName,
                uploadDate, subjectName, uploadRate, dataType, isUpload, checkStatus, statusCode,
                previewPath, imgInfoList);
    }

    @Override
    public String toString() {
        return "JkwwwSubjectInfo{" +
                "no=" + no +
                ", subjectCode='" + subjectCode + '\'' +
                ", committeeName='" + committeeName + '\'' +
                ", deptCode='" + deptCode + '\'' +
                ", subjectStatusId=" + subjectStatusId +
                ", deptName='" + deptName + '\'' +
                ", uploadDate=" + uploadDate +
                ", subjectName='" + subjectName + '\'' +
                ", uploadRate='" + uploadRate + '\'' +
                ", dataType='" + dataType + '\'' +
                ", isUpload='" + isUpload + '\'' +
                ", checkStatus='" + checkStatus + '\'' +
                ", statusCode=" + statusCode +
                ", previewPath='" + previewPath + '\'' +
                ", imgInfoList=" + imgInfoList +
                '}';
    }
}
