package cn.gz.rd.datacollection.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class ExcelUtils {

    private static final Logger logger = LoggerFactory.getLogger(ExcelUtils.class);

    public static Workbook getWorkBookByFileName(String fileName, InputStream is) throws IOException {
        Workbook workbook;
        if (StringUtils.endsWithIgnoreCase(fileName, ".xls")) {
            workbook = new HSSFWorkbook(is);
        } else if (StringUtils.endsWithIgnoreCase(fileName, ".xlsx")) {
            workbook = new XSSFWorkbook(is);
        } else {
            logger.error("不支持的文件类型：" + fileName);
            throw new IllegalArgumentException("不支持的文件类型：" + fileName);
        }
        return workbook;
    }

}
