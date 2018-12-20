package cn.gz.rd.datacollection.model;

import java.util.List;

public class SubjectCompleteInfo {

    private String subjectCode;

    private String subjectName;

    private Integer subjectStatusId;

    private Integer isComplete;

    private List<String> unCompleteDeptNames;

    public List<String> getUnCompleteDeptNames() {
        return unCompleteDeptNames;
    }

    public void setUnCompleteDeptNames(List<String> unCompleteDeptNames) {
        this.unCompleteDeptNames = unCompleteDeptNames;
    }

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

    public Integer getSubjectStatusId() {
        return subjectStatusId;
    }

    public void setSubjectStatusId(Integer subjectStatusId) {
        this.subjectStatusId = subjectStatusId;
    }

    public Integer getIsComplete() {
        return isComplete;
    }

    public void setIsComplete(Integer isComplete) {
        this.isComplete = isComplete;
    }
}
