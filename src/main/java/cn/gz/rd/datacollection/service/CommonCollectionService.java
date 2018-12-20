package cn.gz.rd.datacollection.service;

import cn.gz.rd.datacollection.dao.CommonExcelConfMapper;
import cn.gz.rd.datacollection.dao.WorkDeptRelationTblMapper;
import cn.gz.rd.datacollection.model.*;
import cn.gz.rd.datacollection.utils.CellValueUtils;
import cn.gz.rd.datacollection.utils.PoiUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;

@Service
public class CommonCollectionService {

    @Resource
    private CommonExcelConfMapper commonExcelConfMapper;

    @Resource
    private WorkDeptRelationTblMapper workDeptRelationTblMapper;

    private List<List<Object>> rowValueList = new ArrayList<>();

    /**
     * 上传文件的历史保存目录前缀
     */
    @Value("${uploadfile.history.save.root.dir}")
    private String uploadHisFileSaveDir;

    private static final Logger logger = LoggerFactory.getLogger(CommonCollectionService.class);

    /**
     * 解析Excel数据并根据Excel字段与数据库表的字段相对应的方式入库
     * @param subjectCode 数据所属的主题编号
     * @param isJkww 是否为科教文卫，科教文卫的需要特殊处理
     * @param deptCode 数据所属的部门编号，isJkww为true的时候才使用这个参数。
     * @param sheet 表格
     */
    @Transactional(rollbackFor = Exception.class)
    public void uploadExcelData(
            String subjectCode, boolean isJkww,
            String deptCode, String statisticsPeriod, Sheet sheet)
            throws CommonException {
        CommonExcelConf  excelConf = commonExcelConfMapper.selectByPrimaryKey(subjectCode);
        if (excelConf == null) {
            logger.error("没有获取到相应的配置信息， subjectCode=" + subjectCode);
            throw new CommonException("没有获取到相应的配置信息");
        }
        List<DbColumnInfo> columnInfos = commonExcelConfMapper.selectTableColumnInfo(excelConf.getTableName());
        boolean isYsw = false;
        if (!isJkww) {
            int result = workDeptRelationTblMapper.selectBudgetWorkByDeptId(deptCode);
            if (result != 0){
                isYsw = true;
            }
        }
        if (rowValueList.size() == 0){
            List<List<Object>> excelData = parseExcel(excelConf, columnInfos, isJkww,
                    isYsw, deptCode, statisticsPeriod, sheet);
            saveExcelDataToDb(excelConf, columnInfos, excelData);
        } else {
            saveExcelDataToDb(excelConf, columnInfos, rowValueList);
        }
    }

    public Map<String, Object> uploadFileToNginx(
            String filePath, String statisticsPeriod, String topicId,
            String deptCode, boolean isDataTable, boolean isJkww)
            throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        boolean isYsw = false;
        if (!isJkww){
            int result = workDeptRelationTblMapper.selectBudgetWorkByDeptId(deptCode);
            if (result != 0){
                isYsw = true;
            }
        }
        boolean verifyExcelPass = true;
        String previewUrl;
        File originalFile = new File(filePath);
        if (!originalFile.exists()) {
            throw new CommonException("在线预览失败!");
        }
        String path = originalFile.getParent();
        String originalFileName = originalFile.getName();
        String ext = originalFileName.substring(originalFileName.lastIndexOf(".")+1);
        String newFileName = UUID.randomUUID().toString() + "." + ext;
        String newFilePath = path + "/" + newFileName;
        IOUtils.copy(new FileInputStream(originalFile), new FileOutputStream(new File(path, newFileName)));
        String newFilePvwName;//新文件预览名
        FileOutputStream fileOutputStream = null;
        String htmlString;
        switch (ext){
            case "doc":
                newFilePvwName = path + "/" + newFileName.substring(0,newFileName.lastIndexOf(".")) + ".html";
                fileOutputStream = new FileOutputStream(new File(newFilePvwName));
//                fileOutputStream.write(PoiUtils.word2003ToHtml(originalFile));
                PoiUtils.word2003ToHtml(originalFile, fileOutputStream);
                fileOutputStream.flush();
                break;
            case "docx":
                newFilePvwName = path + "/" + newFileName.substring(0,newFileName.lastIndexOf(".")) + ".html";
                fileOutputStream = new FileOutputStream(new File(newFilePvwName));
                htmlString = PoiUtils.word2007ToHtml(originalFile);
                String contentType = "<META http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">";
                String headString = "<head>";
                htmlString.indexOf(headString);
                String newHtmlString = htmlString.substring(0, htmlString.indexOf(headString) + headString.length()) + contentType + htmlString.substring(htmlString.indexOf(headString) + headString.length(), htmlString.length());
//                int headIndex = htmlString.indexOf(headString) + headString.length();
                fileOutputStream.write(newHtmlString.getBytes());
                /*fileOutputStream.write( htmlString.getBytes(),0,headIndex);
                fileOutputStream.write(contentType.getBytes());
                fileOutputStream.write(htmlString.getBytes(),headIndex,htmlString.length()-headIndex);*/
                fileOutputStream.flush();
                break;
            case "xls":
                newFilePvwName = path + "/" + newFileName.substring(0,newFileName.lastIndexOf(".")) + ".html";
                Workbook hssfWorkbook = WorkbookFactory.create(originalFile);
                HSSFWorkbook hWb = (HSSFWorkbook) hssfWorkbook;
                if (hWb.getNumberOfSheets() > 0){
                    fileOutputStream = new FileOutputStream(new File(newFilePvwName));
                    Map<String, Object> excelMap  = PoiUtils.getExcelInfo(hWb.getSheetAt(0), true, statisticsPeriod, topicId, deptCode,isDataTable,isYsw,isJkww);
                    htmlString = (String) excelMap.get("htmlString");
                    rowValueList = (List<List<Object>>) excelMap.get("rowValueList");
                    verifyExcelPass = (boolean) excelMap.get("verifyExcelPass");
                    fileOutputStream.write(htmlString.getBytes("utf-8"));
                    fileOutputStream.flush();
                }
                break;
            case "xlsx":
                newFilePvwName = path + "/" + newFileName.substring(0,newFileName.lastIndexOf(".")) + ".html";
                Workbook xssfWorkbook = WorkbookFactory.create(originalFile);
                XSSFWorkbook xWb = (XSSFWorkbook) xssfWorkbook;
                if (xWb.getNumberOfSheets() > 0) {
                    fileOutputStream = new FileOutputStream(new File(newFilePvwName));
                    Map<String, Object> excelMap = PoiUtils.getExcelInfo(xWb.getSheetAt(0), true, statisticsPeriod, topicId, deptCode,isDataTable,isYsw,isJkww);
                    htmlString = (String) excelMap.get("htmlString");
                    rowValueList = (List<List<Object>>) excelMap.get("rowValueList");
                    verifyExcelPass = (boolean) excelMap.get("verifyExcelPass");
                    fileOutputStream.write(htmlString.getBytes("utf-8"));
                    fileOutputStream.flush();
                }
                break;
            case "pdf":
            case "jpg":
            case "PDF":
            case "JPG":
            case "PNG":
            case "png":
            case "jpeg":
            case "JPEG":
            case "BMP":
            case "bmp":
            case "ceb":
            case "dwg":
            case "ppt":
            case "pptx":
            case "psd":
            case "rtf":
            case "tif":
                newFilePvwName = newFilePath;
                break;
            default:
                throw new CommonException("文件格式类型错误!");
        }
        if (fileOutputStream != null){
            fileOutputStream.close();
        }
        previewUrl = "/history" + newFilePvwName.substring(uploadHisFileSaveDir.length(), newFilePvwName.length());
        resultMap.put("previewUrl", previewUrl);
        resultMap.put("verifyExcelPass", verifyExcelPass);
        return  resultMap;
    }

    public Map<String, String> uploadExcelToNginx(String filePath) throws Exception {
        File originalFile = new File(filePath);
        String path = originalFile.getParent();
        String originalFileName = originalFile.getName();
        String ext = originalFileName.substring(originalFileName.lastIndexOf(".")+1);
        String newFilePvwName;//新文件      预览名
        FileOutputStream fileOutputStream = null;
        int numberOfSheets;
        Map<String, String> topicIdAndFilePvwPaths = new HashMap<>();
        switch (ext){
            case "xls":
                Workbook hssfWorkbook = WorkbookFactory.create(originalFile);
                numberOfSheets = hssfWorkbook.getNumberOfSheets();
                for (int i=0;i<numberOfSheets;i++){
                    newFilePvwName = originalFileName.substring(0,originalFileName.lastIndexOf(".")) + i + ".html";
                    String topicId = hssfWorkbook.getSheetAt(i).getSheetName();
                    String filePvw = path.substring(uploadHisFileSaveDir.length(), path.length())+ "/" + newFilePvwName;
                    topicIdAndFilePvwPaths.put(topicId, filePvw);
                    fileOutputStream = new FileOutputStream(new File(path, newFilePvwName));
                    Map<String, Object> resultMap = PoiUtils.getExcelInfo(hssfWorkbook.getSheetAt(i), true, null, null, null, false, true, false);
                    rowValueList = (List<List<Object>>) resultMap.get("rowValueList");
                    fileOutputStream.write(((String) resultMap.get("htmlString")).getBytes("utf-8"));
                    fileOutputStream.flush();
                }
                break;
            case "xlsx":
                Workbook xssfWorkbook = WorkbookFactory.create(originalFile);
                numberOfSheets = xssfWorkbook.getNumberOfSheets();
                for (int i=0;i<numberOfSheets;i++){
                    newFilePvwName = originalFileName.substring(0,originalFileName.lastIndexOf(".")) + i + ".html";
                    String topicId = xssfWorkbook.getSheetAt(i).getSheetName();
                    String filePvw = path.substring(uploadHisFileSaveDir.length(), path.length()) + "/" + newFilePvwName;
                    topicIdAndFilePvwPaths.put(topicId, filePvw);
                    fileOutputStream = new FileOutputStream(new File(path,newFilePvwName));
                    Map<String, Object> resultMap = PoiUtils.getExcelInfo(xssfWorkbook.getSheetAt(i), true, null, null, null, false, true, false);
                    rowValueList = (List<List<Object>>) resultMap.get("rowValueList");
                    fileOutputStream.write(((String) resultMap.get("htmlString")).getBytes("utf-8"));
                    fileOutputStream.flush();
                }
                break;
            default:
                throw new CommonException("文件格式类型错误!");
        }
        if (fileOutputStream != null){
            fileOutputStream.close();
        }
        return topicIdAndFilePvwPaths;
    }

    private List<List<Object>> parseExcel(
            CommonExcelConf excelConf, List<DbColumnInfo> columnInfos,
            boolean isJkww, boolean isYsw, String deptCode,
            String statisticsPeriod, Sheet sheet)
            throws CommonException {
        List<List<Object>> rowValueList = new ArrayList<>();

        int columnSize = excelConf.getColumnLen();
        int firstRowNum = excelConf.getFirstRowNum();

        int columnNum = sheet.getRow(firstRowNum - 2).getPhysicalNumberOfCells();
        if (columnNum > columnSize) {
            String string = CellValueUtils.getString(sheet.getRow(firstRowNum - 2).getCell(columnSize + 1));
            if (!StringUtils.isEmpty(string)){
                if (isYsw){
                    throw new CommonException(sheet.getSheetName() + "表, 列数不符!");
                }else {
                    throw new CommonException("列数不符!");
                }
            }
        }
        if (columnNum < columnSize) {
            if (isYsw){
                throw new CommonException(sheet.getSheetName() + "表, 列数不符!");
            }else {
                throw new CommonException("列数不符!");
            }
        }
        int lastRowNum = sheet.getLastRowNum();
        int statisticsPeriodColumn = -1;
        if (!isYsw){
            for (int i=sheet.getFirstRowNum(); i<firstRowNum; i++){
                if (statisticsPeriodColumn != -1){
                    break;
                }
                Row row = sheet.getRow(i);
                for (int j=0; j <=columnSize; j++){
                    Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    String stringCellValue = CellValueUtils.getString(cell);
                    if (StringUtils.equals(stringCellValue, "统计周期")){
                        statisticsPeriodColumn = j;
                        break;
                    }
                }
            }
        }
        for (int i = firstRowNum - 1; i <= lastRowNum; i++) {
            Row row = sheet.getRow(i);
            int startCellNum = 0;
            int lastCellNum = columnSize - 1;

            List<Object> columnValueList = new LinkedList<>();

            boolean isNotBlankRow = false; //用于判断当前行是否为空行
            for (int j = startCellNum; j <= lastCellNum; j++) {
                if (row == null){
                    continue;
                }
                Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                DbColumnInfo columnInfo = columnInfos.get(j);
                logger.debug("Excel解析：columnInfo = " + columnInfo
                        + ", 单元格位置：rowIndex=" + cell.getRowIndex()
                        + ", cellIndex=" + j + ", cellValue="
                        + CellValueUtils.getString(cell));

                if (columnInfo.isInt()) {
                    Integer intValue = CellValueUtils.getInt(cell);
                    columnValueList.add(intValue);
                    if (intValue != null) {
                        isNotBlankRow = true;
                    }
                    if (intValue == null && !cell.getStringCellValue().equals("")){
                        if (isYsw){
                            throw new CommonException(sheet.getSheetName() + "表, " +  "第" + (i + 1) + "行, " + "第" + (j + 1) + "列, 数据格式错误!");
                        }else {
                            throw new CommonException("第" + (i + 1) + "行, " + "第" + (j + 1) + "列, 数据格式错误!");
                        }
                    }
                } else if (columnInfo.isString()) {
                    String strValue = CellValueUtils.getString(cell);
                    columnValueList.add(strValue);
                    if (StringUtils.isNotBlank(strValue)) {
                        if(j == statisticsPeriodColumn && !strValue.equals(statisticsPeriod)){
                            columnValueList.clear();
                            continue;
                        }
                        isNotBlankRow = true;
                    }else {
                        if(j == statisticsPeriodColumn){
                            columnValueList.clear();
                        }
                    }
                } else if (columnInfo.isDouble()) {
                    Double doubleValue = CellValueUtils.getDouble(cell);
                    columnValueList.add(doubleValue);
                    if (doubleValue != null) {
                        isNotBlankRow = true;
                    }
                    if (doubleValue == null && !cell.getStringCellValue().equals("")){
                        if (isYsw){
                            throw new CommonException(sheet.getSheetName() + "表, " +  "第" + (i + 1) + "行, " + "第" + (j + 1) + "列, 数据格式错误!");
                        }else {
                            throw new CommonException("第" + (i + 1) + "行, " + "第" + (j + 1) + "列, 数据格式错误!");
                        }
                    }

                } else if (columnInfo.isDate()) {
                    CellType cellTypeEnum = cell.getCellTypeEnum();
                    if (cellTypeEnum != CellType.NUMERIC && !StringUtils.isEmpty(cell.getStringCellValue())){
                        if (isYsw){
                            throw new CommonException(sheet.getSheetName() + "表, " +  "日期格式错误, 请检查单元格格式是否为日期格式!");
                        }else {
                            throw new CommonException("日期格式错误, 请检查单元格格式是否为日期格式!");
                        }
                    }
                    Date dateValue = cell.getDateCellValue();
                    columnValueList.add(dateValue);
                    if (dateValue != null) {
                        isNotBlankRow = true;
                    }
                    if (dateValue == null && !cell.getStringCellValue().equals("")){
                        if (isYsw){
                            throw new CommonException(sheet.getSheetName() + "表, " +  "第" + (i + 1) + "行, " + "第" + (j + 1) + "列, 数据格式错误!");
                        }else {
                            throw new CommonException("第" + (i + 1) + "行, " + "第" + (j + 1) + "列, 数据格式错误!");
                        }
                    }
                } else {
                    logger.error("不支持的数据类型：" + columnInfo.getDataType());
                }
            }

            if (isNotBlankRow) {
                if (columnValueList.size() != 0) {
                    if (isJkww) { //教科文卫特殊处理，在每一行的数据末尾增加部门编号字段。
                        columnValueList.add(deptCode);
                    }
                    if (isYsw){
                        columnValueList.add(statisticsPeriod);
                    }
                    rowValueList.add(columnValueList);
                }
            } else {
                break;
            }
        }
        if (rowValueList.size() == 0){
            throw new CommonException("统计周期不匹配!");
        }
        return rowValueList;
    }

    private void saveExcelDataToDb(final CommonExcelConf excelConf,
                                  final List<DbColumnInfo> columnInfos,
                                  final List<List<Object>> rowValues) {

        StringBuilder sqlBuilder = new StringBuilder(excelConf.getTableName());
        sqlBuilder.append("(");
        final int size = columnInfos.size();
        for (int i = 0; i < size; i++) {
            DbColumnInfo columnInfo = columnInfos.get(i);
            sqlBuilder.append(columnInfo.getColumnName());
            if ((i + 1) < size) {
                sqlBuilder.append(",");
            }
        }
        sqlBuilder.append(")");
        int j = 0;
        int rowSize = rowValues.size();
        for (int i = 0; i < rowSize; i++) {
            if (i % 2000 == 0) {
                j++;
                int startSize = (j - 1) * 2000;
                int endSize = startSize + 2000;
                if ((rowSize - endSize) < 2000) {
                    commonExcelConfMapper.batchInsertList(sqlBuilder.toString(), rowValues.subList(startSize, rowSize));
                } else {
                    commonExcelConfMapper.batchInsertList(sqlBuilder.toString(), rowValues.subList(startSize, endSize));
                }
            }
        }
    }
}
