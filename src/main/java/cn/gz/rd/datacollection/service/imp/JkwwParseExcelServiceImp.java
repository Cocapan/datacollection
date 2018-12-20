package cn.gz.rd.datacollection.service.imp;

import cn.gz.rd.datacollection.model.JkwwProjectInfo;
import cn.gz.rd.datacollection.utils.CellValueUtils;
import cn.gz.rd.datacollection.utils.ExcelUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 科教文卫工委的Excel数据解析服务
 */
@Service
public class JkwwParseExcelServiceImp {

    private static final Logger logger = LoggerFactory.getLogger(JkwwParseExcelServiceImp.class);

    public List<JkwwProjectInfo> parseProjectInfo(String fileName, InputStream is) throws IOException {

        Workbook workbook = ExcelUtils.getWorkBookByFileName(fileName, is);

        List<JkwwProjectInfo> projectList = new ArrayList<>();

        Sheet sheet = workbook.getSheetAt(0);
        int lastRowNum = sheet.getLastRowNum();
        for (int i = sheet.getFirstRowNum() + 5; i <= lastRowNum; i++) {
            Row row = sheet.getRow(i);
            JkwwProjectInfo projectInfo = new JkwwProjectInfo();

            Cell cell1 = row.getCell(0);
            if (cell1 != null) {
                Double xmxh = CellValueUtils.getDouble(cell1);
                if (xmxh == null) {
                    continue;
                }
                projectInfo.setXmxh(xmxh.intValue());
            }

            Cell cell2 = row.getCell(1);
            if (cell2 != null) {
                String xmbm = cell2.getStringCellValue();
                projectInfo.setXmbm(xmbm);
            }

            Cell cell3 = row.getCell(2);
            if (cell3 != null) {
                String sfwzdxm = cell3.getStringCellValue();
                projectInfo.setSfwzdxm(sfwzdxm);
            }

            Cell cell4 = row.getCell(3);
            if (cell4 != null) {
                Integer zdxmxh = CellValueUtils.getInt(cell4);
                projectInfo.setZdxmxh(zdxmxh);
            }

            Cell cell5 = row.getCell(4);
            if (cell5 != null) {
                String sfwgcbzxm = cell5.getStringCellValue();
                projectInfo.setSfwgcbzxm(sfwgcbzxm);
            }

            Cell cell6 = row.getCell(5);
            if (cell6 != null) {
                int gcbzxmxh = CellValueUtils.getInt(cell6);
                projectInfo.setGcbzxmxh(gcbzxmxh);
            }

            Cell cell7 = row.getCell(6);
            if (cell7 != null) {
                String fl = cell7.getStringCellValue();
                projectInfo.setFl(fl);
            }

            Cell cell8 = row.getCell(7);
            if (cell8 != null) {
                String zt = cell8.getStringCellValue();
                projectInfo.setZt(zt);
            }

            Cell cell9 = row.getCell(8);
            if (cell9 != null) {
                String xmmc = cell9.getStringCellValue();
                projectInfo.setXmmc(xmmc);
            }

            Cell cell10 = row.getCell(9);
            if (cell10 != null) {
                String zgbm = cell10.getStringCellValue();
                projectInfo.setZgbm(zgbm);
            }

            Cell cell11 = row.getCell(10);
            if (cell11 != null) {
                String xmyz = cell11.getStringCellValue();
                projectInfo.setXmyz(xmyz);
            }

            Cell cell12 = row.getCell(11);
            if (cell12 != null) {
                String xmgmhjsnr = cell12.getStringCellValue();
                projectInfo.setXmgmhjsnr(xmgmhjsnr);
            }

            Cell cell13 = row.getCell(12);
            if (cell13 != null) {
                int jhkgsj_n = (int) cell13.getNumericCellValue();
                projectInfo.setJhkgsjn(jhkgsj_n);
            }

            Cell cell14 = row.getCell(13);
            if (cell14 != null) {
                int jhkgsj_y = (int) cell14.getNumericCellValue();
                projectInfo.setJhkgsjy(jhkgsj_y);
            }

            Cell cell15 = row.getCell(14);
            if (cell15 != null) {
                int yjjcsj_n = (int) cell15.getNumericCellValue();
                projectInfo.setYjjcsjn(yjjcsj_n);
            }

            Cell cell16 = row.getCell(15);
            if (cell16 != null) {
                int yjjcsj_y = (int) cell16.getNumericCellValue();
                projectInfo.setYjjcsjy(yjjcsj_y);
            }

            Cell cell17 = row.getCell(16);
            if (cell17 != null) {
                double xmztz_ze = cell17.getNumericCellValue();
                projectInfo.setXmztzze(xmztz_ze);
            }

            Cell cell18 = row.getCell(17);
            if (cell18 != null) {
                double xmztz_czzj = cell18.getNumericCellValue();
                projectInfo.setXmztzczzj(xmztz_czzj);
            }

            projectList.add(projectInfo);
        }

        workbook.close();
        return projectList;
    }


}
