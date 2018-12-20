package cn.gz.rd.datacollection.model;

import org.apache.commons.lang3.StringUtils;

/**
 * 数据库中表的字段信息
 */
public class DbColumnInfo {

    /**
     * 字段名称
     */
    private String columnName;

    /**
     * 字段数据类型
     */
    private String dataType;

    /**
     * 如果为浮点数，则为浮点数的小数位数。
     */
    private Integer numericScale;

    private Integer characterMaximumLength;

    private Integer numericPrecision;

    public boolean isInt() {
        if (StringUtils.equalsIgnoreCase("int", dataType)) {
            return true;
        } else if (StringUtils.equalsIgnoreCase("decimal", dataType)
                && (numericScale == null || numericScale == 0)) {
            return true;
        } else if (StringUtils.equalsIgnoreCase("numeric", dataType)
                && (numericScale == null || numericScale == 0)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isString() {
        if (StringUtils.containsIgnoreCase(dataType, "char")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isDouble() {
        if (StringUtils.equalsIgnoreCase("decimal", dataType)
                && numericScale != null && numericScale > 0) {
            return true;
        } else if (StringUtils.equalsIgnoreCase("numeric", dataType)
                && numericScale != null && numericScale > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isDate() {
        if (StringUtils.equalsIgnoreCase("datetime", dataType)) {
            return true;
        } else if (StringUtils.equalsIgnoreCase("timestamp", dataType)) {
            return true;
        } else {
            return false;
        }
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Integer getNumericScale() {
        return numericScale;
    }

    public void setNumericScale(Integer numericScale) {
        this.numericScale = numericScale;
    }

    public Integer getCharacterMaximumLength() {
        return characterMaximumLength;
    }

    public void setCharacterMaximumLength(Integer characterMaximumLength) {
        this.characterMaximumLength = characterMaximumLength;
    }

    public Integer getNumericPrecision() {
        return numericPrecision;
    }

    public void setNumericPrecision(Integer numericPrecision) {
        this.numericPrecision = numericPrecision;
    }

    @Override
    public String toString() {
        return "DbColumnInfo{" +
                "columnName='" + columnName + '\'' +
                ", dataType='" + dataType + '\'' +
                ", numericScale=" + numericScale +
                ", characterMaximumLength=" + characterMaximumLength +
                ", numericPrecision=" + numericPrecision +
                '}';
    }
}
