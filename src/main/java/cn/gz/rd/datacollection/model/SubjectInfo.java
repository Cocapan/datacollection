package cn.gz.rd.datacollection.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Arrays;
import java.util.Date;

public class SubjectInfo {

    private String subjectCode;

    private String subjectName;

    private String parentSubjectCode;

    private Integer level;

    private String subjectType;

    private String saveLocation;

    private String uploadFrequency;

    private Integer uploadDeadline;

    private String templatePath;

    private Integer dataStartRow;

    private Integer columnNum;

    private String deptCode;

    private String[] deptCodes;

    private String[] deptNames;

    private String committeeCode;

    private String deptName;

    private String committeeName;

    private String comment;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createDate;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateDate;

    private String createUserCode;

    private String updateUserCode;

    /**
     * 表示数据是否已经删除，伪删除标志。
     */
    private Integer enableFlag;

    /**
     * 表示是否可用
     */
    private Integer usableFlag;

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getParentSubjectCode() {
        return parentSubjectCode;
    }

    public void setParentSubjectCode(String parentSubjectCode) {
        this.parentSubjectCode = parentSubjectCode;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(String subjectType) {
        this.subjectType = subjectType;
    }

    public String getSaveLocation() {
        return saveLocation;
    }

    public void setSaveLocation(String saveLocation) {
        this.saveLocation = saveLocation;
    }

    public String getUploadFrequency() {
        return uploadFrequency;
    }

    public void setUploadFrequency(String uploadFrequency) {
        this.uploadFrequency = uploadFrequency;
    }

    public Integer getUploadDeadline() {
        return uploadDeadline;
    }

    public void setUploadDeadline(Integer uploadDeadline) {
        this.uploadDeadline = uploadDeadline;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    public Integer getDataStartRow() {
        return dataStartRow;
    }

    public void setDataStartRow(Integer dataStartRow) {
        this.dataStartRow = dataStartRow;
    }

    public Integer getColumnNum() {
        return columnNum;
    }

    public void setColumnNum(Integer columnNum) {
        this.columnNum = columnNum;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public String getCommitteeCode() {
        return committeeCode;
    }

    public void setCommitteeCode(String committeeCode) {
        this.committeeCode = committeeCode;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getCreateUserCode() {
        return createUserCode;
    }

    public void setCreateUserCode(String createUserCode) {
        this.createUserCode = createUserCode;
    }

    public String getUpdateUserCode() {
        return updateUserCode;
    }

    public void setUpdateUserCode(String updateUserCode) {
        this.updateUserCode = updateUserCode;
    }

    public Integer getEnableFlag() {
        return enableFlag;
    }

    public void setEnableFlag(Integer enableFlag) {
        this.enableFlag = enableFlag;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getCommitteeName() {
        return committeeName;
    }

    public void setCommitteeName(String committeeName) {
        this.committeeName = committeeName;
    }

    public Integer getUsableFlag() {
        return usableFlag;
    }

    public void setUsableFlag(Integer usableFlag) {
        this.usableFlag = usableFlag;
    }

    public String[] getDeptCodes() {
        return deptCodes;
    }

    public void setDeptCodes(String[] deptCodes) {
        this.deptCodes = deptCodes;
    }

    public String[] getDeptNames() {
        return deptNames;
    }

    public void setDeptNames(String[] deptNames) {
        this.deptNames = deptNames;
    }

    @Override
    public String toString() {
        return "SubjectInfo{" +
                "subjectCode='" + subjectCode + '\'' +
                ", subjectName='" + subjectName + '\'' +
                ", parentSubjectCode='" + parentSubjectCode + '\'' +
                ", level=" + level +
                ", subjectType='" + subjectType + '\'' +
                ", saveLocation='" + saveLocation + '\'' +
                ", uploadFrequency='" + uploadFrequency + '\'' +
                ", uploadDeadline=" + uploadDeadline +
                ", templatePath='" + templatePath + '\'' +
                ", dataStartRow=" + dataStartRow +
                ", columnNum=" + columnNum +
                ", deptCode='" + deptCode + '\'' +
                ", deptCodes=" + Arrays.toString(deptCodes) +
                ", deptNames=" + Arrays.toString(deptNames) +
                ", committeeCode='" + committeeCode + '\'' +
                ", deptName='" + deptName + '\'' +
                ", committeeName='" + committeeName + '\'' +
                ", comment='" + comment + '\'' +
                ", createDate=" + createDate +
                ", updateDate=" + updateDate +
                ", createUserCode='" + createUserCode + '\'' +
                ", updateUserCode='" + updateUserCode + '\'' +
                ", enableFlag=" + enableFlag +
                ", usableFlag=" + usableFlag +
                '}';
    }
}