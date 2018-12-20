package cn.gz.rd.datacollection.model;

public class AccessoryFile {

    private int id;

    private String fileName;

    private String originalName;

    private String statisticPeriod;

    private String storageUrl;

    private String previewUrl;

    private String deptCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getStatisticPeriod() {
        return statisticPeriod;
    }

    public void setStatisticPeriod(String statisticPeriod) {
        this.statisticPeriod = statisticPeriod;
    }

    public String getStorageUrl() {
        return storageUrl;
    }

    public void setStorageUrl(String storageUrl) {
        this.storageUrl = storageUrl;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    @Override
    public String toString() {
        return "AccessoryFile{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", originalName='" + originalName + '\'' +
                ", statisticPeriod='" + statisticPeriod + '\'' +
                ", storageUrl='" + storageUrl + '\'' +
                ", previewUrl='" + previewUrl + '\'' +
                ", deptCode='" + deptCode + '\'' +
                '}';
    }
}
