package cn.gz.rd.datacollection.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.text.DecimalFormat;
import java.util.Date;

public class CellValueUtils {

    public static String getString(Cell cell) {
        if (cell == null) {
            return null;
        }
        CellType cellTypeEnum = cell.getCellTypeEnum();
        if(cellTypeEnum == CellType.BLANK) {
            return null;

        } else if (cellTypeEnum == CellType.STRING) {
            String stringCellValue = cell.getStringCellValue();
            if (StringUtils.equalsIgnoreCase(stringCellValue.trim(),"null")){
                return null;
            }
            return stringCellValue;
        } else if (cellTypeEnum == CellType.NUMERIC) {
            double numericCellValue = cell.getNumericCellValue();
            DecimalFormat df = new DecimalFormat("0");
            String numString = df.format(numericCellValue);
            return numString;
//            short dataFormat = cell.getCellStyle().getDataFormat();
//            if (dataFormat == 57 || dataFormat == 177) {
//                return DateFormatUtils.format(cell.getDateCellValue(), "yyyy-MM-dd");
//
//            } else if (dataFormat == 22) {
//                return DateFormatUtils.format(cell.getDateCellValue(), "yyyy-MM-dd HH:mm:ss");
//
//            }else if (dataFormat == 14 || dataFormat == 176) {
//                return DateFormatUtils.format(cell.getDateCellValue(), "yyyy/MM/dd");
//
//            } else {
//                return numString;
//                /*// 如果数据包含小数部分全为0，则去掉小数部分。
//                double decimalPart = numericCellValue - (int) numericCellValue;
//                if (decimalPart == 0) {
//                    return (int) numericCellValue + "";
//                } else {
//                    return numericCellValue + "";
//                }*/
//
//            }

        } else if (cellTypeEnum == CellType.FORMULA &&
                cell.getCachedFormulaResultTypeEnum() == CellType.NUMERIC) {
            return cell.getNumericCellValue() + "";
        } else if (cellTypeEnum == CellType.FORMULA &&
                cell.getCachedFormulaResultTypeEnum() == CellType.STRING) {
            return cell.getStringCellValue();
        } else {
            return cell.getStringCellValue();
        }
    }

    public static String getDoubleString(Cell cell) {
        if (cell == null) {
            return null;
        }
        CellType cellTypeEnum = cell.getCellTypeEnum();
        if(cellTypeEnum == CellType.BLANK) {
            return null;

        } else if (cellTypeEnum == CellType.STRING) {
            String stringCellValue = cell.getStringCellValue();
            if (StringUtils.equalsIgnoreCase(stringCellValue.trim(),"null")){
                return null;
            }
            return stringCellValue;
        } else if (cellTypeEnum == CellType.NUMERIC) {
            double numericCellValue = cell.getNumericCellValue();
            return String.valueOf(numericCellValue);
        } else if (cellTypeEnum == CellType.FORMULA &&
                cell.getCachedFormulaResultTypeEnum() == CellType.NUMERIC) {
            return cell.getNumericCellValue() + "";
        } else if (cellTypeEnum == CellType.FORMULA &&
                cell.getCachedFormulaResultTypeEnum() == CellType.STRING) {
            return cell.getStringCellValue();
        } else {
            return cell.getStringCellValue();
        }
    }

    public static Double getDouble(Cell cell) {
        if (cell == null) {
            return null;
        }
        CellType cellTypeEnum = cell.getCellTypeEnum();
        if (cellTypeEnum == CellType.STRING) {
            String cellValue = cell.getStringCellValue();
            if (NumberUtils.isParsable(cellValue)) {
                return Double.valueOf(cellValue);
            } else {
                return null;
            }
        } else if(cellTypeEnum == CellType.NUMERIC) {
            return cell.getNumericCellValue();

        } else if(cellTypeEnum == CellType.BLANK) {
            return null;

        } else if (cellTypeEnum == CellType.FORMULA &&
                cell.getCachedFormulaResultTypeEnum() == CellType.NUMERIC) {
            return cell.getNumericCellValue();

        } else if (cellTypeEnum == CellType.FORMULA &&
                cell.getCachedFormulaResultTypeEnum() == CellType.STRING) {
            return Double.valueOf(cell.getStringCellValue());
        } else if (cellTypeEnum == CellType.FORMULA &&
                cell.getCachedFormulaResultTypeEnum() == CellType.ERROR) {
            return 0.0;
        }else {
            return cell.getNumericCellValue();
        }
    }

    public static Integer getInt(Cell cell) {
        if (cell == null) {
            return null;
        }
        CellType cellTypeEnum = cell.getCellTypeEnum();

        if (cellTypeEnum == CellType.STRING) {
            String cellValue = cell.getStringCellValue();
            if (StringUtils.isNumeric(cellValue)) {
                return Integer.valueOf(cellValue);
            } else {
                return null;
            }

        } else if (cellTypeEnum == CellType.NUMERIC) {
            double cellValue = cell.getNumericCellValue();
            return (int) cellValue;

        } else if(cellTypeEnum == CellType.BLANK) {
            return null;

        } else if (cellTypeEnum == CellType.FORMULA &&
                cell.getCachedFormulaResultTypeEnum() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();

        } else if (cellTypeEnum == CellType.FORMULA &&
                cell.getCachedFormulaResultTypeEnum() == CellType.STRING) {
            return Integer.valueOf(cell.getStringCellValue());

        } else {
            return (int)cell.getNumericCellValue();
        }
    }

    static Date getDate(Cell cell) {
        if (cell == null) {
            return null;
        }
        CellType cellTypeEnum = cell.getCellTypeEnum();
        if (cellTypeEnum == CellType.NUMERIC) {
            return cell.getDateCellValue();
        }else {
            return null;
        }
    }

    public static void setValue(Cell cell, Integer value) {
        if (value != null && cell != null) {
            cell.setCellValue(value);
        }
    }

    public static void setValue(Cell cell, String value) {
        if (value != null && cell != null) {
            cell.setCellValue(value);
        }
    }

    public static void setValue(Cell cell, Double value) {
        if (value != null && cell != null) {
            cell.setCellValue(value);
        }
    }

    public static boolean isEmpty(Cell cell) {
        if (cell == null) {
            return true;
        }
        CellType cellType = cell.getCellTypeEnum();
        return cellType == CellType.BLANK;
    }
}
