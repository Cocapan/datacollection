package cn.gz.rd.datacollection.utils;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * @author Peng Xiaodong
 */
public class CellValueUtilsTest {

    @Test
    public void testNullCellValue() throws IOException, InvalidFormatException {
        XSSFWorkbook workbook = new XSSFWorkbook(
                new File("C:\\Users\\Shadon\\Desktop\\单元测试.xlsx"));
        XSSFSheet sheet = workbook.getSheetAt(0);
        XSSFRow row = sheet.getRow(1);
        XSSFCell cell = row.getCell(1);
        if (cell != null) {
            CellType cellTypeEnum = cell.getCellTypeEnum();
            Assert.assertEquals(CellType.NUMERIC, cellTypeEnum);
        }
        System.out.println(cell);
    }

    @Test
    public void testDataCellValue () throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook(
                new File("C:\\Users\\Shadon\\Desktop\\解决公益性骨灰安放设施历史遗留问题工作进度表.xlsx"));
        XSSFSheet sheet = workbook.getSheetAt(0);
        XSSFRow row = sheet.getRow(4);
        XSSFCell cell = row.getCell(10);
        String string = CellValueUtils.getString(cell);
        System.out.println(string);
    }

}
