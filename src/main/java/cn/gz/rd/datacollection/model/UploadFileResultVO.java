package cn.gz.rd.datacollection.model;

public class UploadFileResultVO {

    private Integer subjectStatusId;

    private Integer imgInfoId;

    public Integer getSubjectStatusId() {
        return subjectStatusId;
    }

    public void setSubjectStatusId(Integer subjectStatusId) {
        this.subjectStatusId = subjectStatusId;
    }

    public Integer getImgInfoId() {
        return imgInfoId;
    }

    public void setImgInfoId(Integer imgInfoId) {
        this.imgInfoId = imgInfoId;
    }

    @Override
    public String toString() {
        return "UploadFileResultVO{" +
                "subjectStatusId=" + subjectStatusId +
                ", imgInfoId=" + imgInfoId +
                '}';
    }
}
