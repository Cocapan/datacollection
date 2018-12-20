package cn.gz.rd.datacollection.service;

import cn.gz.rd.datacollection.DataCenterApplicationTests;
import cn.gz.rd.datacollection.model.CommonException;
import cn.gz.rd.datacollection.utils.ExcelUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class CommonCollectionServiceTest extends DataCenterApplicationTests {

    @Autowired
    private CommonCollectionService commonCollectionService;

    @Test
    public void testUploadExcelData() throws IOException, CommonException {

        String typeName = "工程报监基本情况表明细";
        File file = new File("C:\\Users\\Shadon\\Desktop\\住房建设委\\工程报监基本情况表明细.xls");
        String fileName = file.getName();
        Workbook workbook = ExcelUtils.getWorkBookByFileName(fileName, new FileInputStream(file));

        commonCollectionService.uploadExcelData(typeName,
                false, "", "201805", workbook.getSheetAt(0));
    }

}
