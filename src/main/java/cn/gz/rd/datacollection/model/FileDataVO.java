package cn.gz.rd.datacollection.model;

/**
 * 文件数据 VO
 * @author Peng Xiaodong
 */
public class FileDataVO {

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件数据
     */
    private byte[] fileData;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    @Override
    public String toString() {
        return "FileDataVO{" +
                "fileName='" + fileName + '\'' +
                '}';
    }
}
