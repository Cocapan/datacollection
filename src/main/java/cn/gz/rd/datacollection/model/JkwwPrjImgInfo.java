package cn.gz.rd.datacollection.model;

import java.util.Date;

public class JkwwPrjImgInfo {

    private Integer infoId;

    private Integer projectStatusId;

    private String imgFileName;

    private String imgSavePath;

    private String imgPreviewPath;

    private String imgNewSavePath;

    private Date uploadDate;

    private String uploadUser;

    public Integer getInfoId() {
        return infoId;
    }

    public String getImgPreviewPath() {
        return imgPreviewPath;
    }

    public void setImgPreviewPath(String imgPreviewPath) {
        this.imgPreviewPath = imgPreviewPath;
    }

    public void setInfoId(Integer infoId) {
        this.infoId = infoId;
    }

    public Integer getProjectStatusId() {
        return projectStatusId;
    }

    public void setProjectStatusId(Integer projectStatusId) {
        this.projectStatusId = projectStatusId;
    }

    public String getImgFileName() {
        return imgFileName;
    }

    public void setImgFileName(String imgFileName) {
        this.imgFileName = imgFileName;
    }

    public String getImgSavePath() {
        return imgSavePath;
    }

    public void setImgSavePath(String imgSavePath) {
        this.imgSavePath = imgSavePath;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getUploadUser() {
        return uploadUser;
    }

    public void setUploadUser(String uploadUser) {
        this.uploadUser = uploadUser;
    }

    public String getImgNewSavePath() {
        return imgNewSavePath;
    }

    public void setImgNewSavePath(String imgNewSavePath) {
        this.imgNewSavePath = imgNewSavePath;
    }
}