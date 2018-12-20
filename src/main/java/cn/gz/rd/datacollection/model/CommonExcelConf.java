package cn.gz.rd.datacollection.model;

public class CommonExcelConf {

    /**
     * 主题ID
     */
    private String confId;

    /**
     * 类型名称
     */
    private String typeName;

    /**
     * 对应的数据库表名称
     */
    private String tableName;

    /**
     * 起始行行号
     */
    private Integer firstRowNum;

    /**
     * 字段个数
     */
    private Integer columnLen;

    public String getConfId() {
        return confId;
    }

    public void setConfId(String confId) {
        this.confId = confId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Integer getFirstRowNum() {
        return firstRowNum;
    }

    public void setFirstRowNum(Integer firstRowNum) {
        this.firstRowNum = firstRowNum;
    }

    public Integer getColumnLen() {
        return columnLen;
    }

    public void setColumnLen(Integer columnLen) {
        this.columnLen = columnLen;
    }
}