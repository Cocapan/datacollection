package cn.gz.rd.datacollection.utils;

import cn.gz.rd.datacollection.dao.CommonExcelConfMapper;
import cn.gz.rd.datacollection.model.CommonExcelConf;
import cn.gz.rd.datacollection.model.CommonException;
import cn.gz.rd.datacollection.model.DbColumnInfo;
import fr.opensagres.poi.xwpf.converter.core.IXWPFConverter;
import fr.opensagres.poi.xwpf.converter.core.ImageManager;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLConverter;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.usermodel.PictureType;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author panxuan
 * poi工具类
 */
@Component
public class PoiUtils {

    private static Logger logger = LoggerFactory.getLogger(PoiUtils.class);

    /**
     * 通用表配置数据访问
     */
    private  CommonExcelConfMapper commonExcelConfMapper;

    private static PoiUtils poiUtils;

    @Autowired
    public PoiUtils(CommonExcelConfMapper commonExcelConfMapper){
        this.commonExcelConfMapper = commonExcelConfMapper;
    }

    @PostConstruct
    public void init(){
        poiUtils  = this;
        poiUtils.commonExcelConfMapper = this.commonExcelConfMapper;
    }

    /**
     * 将2003版word转换成html
     * @param file 文件
     * @return html文件的byte数组
     * @throws Exception 异常
     */
    public static byte[] word2003ToHtml(File file) throws Exception {
        //把文件流转化为输入wordDom对象
        HWPFDocument wordDocument = new HWPFDocument(new FileInputStream(file));
        //通过反射构建dom创建者工厂,生成dom创建者,生成dom对象,生成针对Dom对象的转化器
        WordToHtmlConverter wordToHtmlConverter =
                new WordToHtmlConverter(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
        //转化器重写内部方法
        wordToHtmlConverter.setPicturesManager(
                (byte[] content, PictureType pictureType, String suggestedName, float widthInches, float heightInches)
                        ->  "image/" + suggestedName
        );
        //转化器开始转化接收到的dom对象
        wordToHtmlConverter.processDocument(wordDocument);
        //获取word中的图片
        List<Picture> pics=wordDocument.getPicturesTable().getAllPictures();
        if(pics != null && pics.size() != 0){
            for(Picture picture : pics){
                //word中图片的转存路径
                File imagePath= new File(file.getParent() + "/image/");
                if (!imagePath.exists()) {
                    logger.info("create the image directory of word 2003: " + imagePath);
                    boolean mkdirs = imagePath.mkdirs();
                    if(!mkdirs){
                        logger.error("the image directory of word 2003 create failed!");
                    }
                }
                //将word中图片保存到转存路径
                picture.writeImageContent(new FileOutputStream(new File(imagePath, picture.suggestFullFileName())));
            }
        }
        //创建html页面并将文档中内容写入页面,从加载了输入文件中的转换器中提取DOM节点
        Document htmlDocument = wordToHtmlConverter.getDocument();
        //字节码输出流
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        //从提取的DOM节点中获得内容
        DOMSource domSource = new DOMSource(htmlDocument);
        //输出流的源头
        StreamResult streamResult = new StreamResult(outStream);
        //转化工厂生成序列转化器
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer serializer = tf.newTransformer();
        //设置序列化内容格式
        serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty(OutputKeys.METHOD, "html");
        serializer.transform(domSource, streamResult);
        byte[] data = outStream.toString("UTF-8").getBytes();
        outStream.close();
        return data;
    }

    public static void word2003ToHtml(File file, FileOutputStream outputStream) throws Exception {
        //把文件流转化为输入wordDom对象
        HWPFDocument wordDocument = new HWPFDocument(new FileInputStream(file));
        //通过反射构建dom创建者工厂,生成dom创建者,生成dom对象,生成针对Dom对象的转化器
        WordToHtmlConverter wordToHtmlConverter =
                new WordToHtmlConverter(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
        //转化器重写内部方法
        wordToHtmlConverter.setPicturesManager(
                (byte[] content, PictureType pictureType, String suggestedName, float widthInches, float heightInches)
                        ->  "image/" + suggestedName
        );
        //转化器开始转化接收到的dom对象
        wordToHtmlConverter.processDocument(wordDocument);
        //获取word中的图片
        List<Picture> pics=wordDocument.getPicturesTable().getAllPictures();
        if(pics != null && pics.size() != 0){
            for(Picture picture : pics){
                //word中图片的转存路径
                File imagePath= new File(file.getParent() + "/image/");
                if (!imagePath.exists()) {
                    logger.info("create the image directory of word 2003: " + imagePath);
                    boolean mkdirs = imagePath.mkdirs();
                    if(!mkdirs){
                        logger.error("the image directory of word 2003 create failed!");
                    }
                }
                //将word中图片保存到转存路径
                picture.writeImageContent(new FileOutputStream(new File(imagePath, picture.suggestFullFileName())));
            }
        }
        //创建html页面并将文档中内容写入页面,从加载了输入文件中的转换器中提取DOM节点
        Document htmlDocument = wordToHtmlConverter.getDocument();
        //从提取的DOM节点中获得内容
        DOMSource domSource = new DOMSource(htmlDocument);
        //输出流的源头
        StreamResult streamResult = new StreamResult(outputStream);
        //转化工厂生成序列转化器
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer serializer = tf.newTransformer();
        //设置序列化内容格式
        serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty(OutputKeys.METHOD, "html");
        serializer.transform(domSource, streamResult);
    }

    /**
     * 将2007版word转换成html
     * @param file 文件
     * @return html文件的byte数组
     * @throws Exception 异常
     */
    public static String word2007ToHtml(File file) throws Exception{
        //把文件流转化为输入dom对象
        XWPFDocument document = new XWPFDocument(new FileInputStream(file));
        //转换为HTML时的相关配置选项
        XHTMLOptions options = XHTMLOptions.create();
        //生成针对Dom对象的转化器
        IXWPFConverter<XHTMLOptions> ixwpfConverter = XHTMLConverter.getInstance();
        File filePath= new File(file.getParent());
        //设置word中图片转存路径
        options.setImageManager(new ImageManager(filePath,"image"));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //将dom对象转换为html
        ixwpfConverter.convert(document, baos, options);
        String s = baos.toString("UTF-8");
        baos.flush();
        baos.close();
        return s;
    }

    public static void word2007ToHtml(File file, FileOutputStream outputStream) throws Exception{
        //把文件流转化为输入dom对象
        XWPFDocument document = new XWPFDocument(new FileInputStream(file));
        //转换为HTML时的相关配置选项
        XHTMLOptions options = XHTMLOptions.create();
        //生成针对Dom对象的转化器
        IXWPFConverter<XHTMLOptions> ixwpfConverter = XHTMLConverter.getInstance();
        File filePath= new File(file.getParent());
        //设置word中图片转存路径
        options.setImageManager(new ImageManager(filePath,"image"));
        //将dom对象转换为html
        ixwpfConverter.convert(document, outputStream, options);
    }

    /**
     * 解析表格, 1、生成html文件 2、缓存解析后的表格数据
     * @param sheet 带解析的表格
     * @param isWithStyle 是否带样式
     * @param statisticsPeriod 统计周期
     * @param topicId 主题编码
     * @param deptCode 部门编码
     * @param isDataTable 是否为数据表
     * @param isYsw 是否为预算委
     * @param isJkww 是否为教科文卫
     * @return 1、htmlString - html字符串. 2、rowValueList - 表格行数据集合
     * @throws InterruptedException 线程中断异常
     * @throws ExecutionException 线程执行异常
     * @throws CommonException 自定义异常
     */
    public static Map<String, Object> getExcelInfo(
            Sheet sheet,boolean isWithStyle, String statisticsPeriod,
            String topicId, String deptCode, boolean isDataTable,
            boolean isYsw, boolean isJkww)
            throws InterruptedException, ExecutionException, CommonException {
        boolean verifyExcelPass = true;
        //包含Html和数据的Map集合
        Map<String, Object> htmlAndDataMap = new HashMap<>();
        //行数据列表
        List<List<Object>> rowValueList = new ArrayList<>();
        //统计周期列数
        int statisticsPeriodColumn = -1;
        //提示信息列表
        List<String> noticeMessage = new ArrayList<>();
        //主题表字段信息列表
        List<DbColumnInfo> columnInfoList = new ArrayList<>();
        int firstRowNum = sheet.getFirstRowNum();
        int columnSize = 0;
        if (isDataTable){
            //通过主题编码, 获取该数据表的配置信息
            CommonExcelConf excelConf = poiUtils.commonExcelConfMapper.selectByPrimaryKey(topicId);
            //该数据表的数据起始行数
            firstRowNum = excelConf.getFirstRowNum();
            //该数据表的列数
            columnSize = excelConf.getColumnLen();
            columnInfoList = poiUtils.commonExcelConfMapper.selectTableColumnInfo(excelConf.getTableName());
            startRowVerify(firstRowNum, sheet);
            //数据库字段相对应的表格字段行
            Row fieldColumn;
            if (isYsw){
                //预算委没有字段说明行
                fieldColumn = sheet.getRow(firstRowNum - 2);
            }else {
                fieldColumn = sheet.getRow(firstRowNum - 3);
            }
            //数据表字段列数
            int columnNum;
            if (fieldColumn == null){
                throw new CommonException("起始行配置可能有误！请检查！");
            }else {
                columnNum = fieldColumn.getPhysicalNumberOfCells();
            }
            //校验列数
            if (columnNum < columnSize) {
                throw new CommonException("上传表列数可能与实际配置表列数不符！请检查！");
            }
            if (columnNum > columnSize) {
                String string = "";
                //获取数据表字段行所配置列的后一个字段单元格
                Cell fieldCell = fieldColumn.getCell(columnSize);
                if (fieldCell != null){
                    string = CellValueUtils.getString(fieldCell);
                }
                //如果单元格内容不为空, 则列数不符
                if (!StringUtils.isEmpty(string)) {
                    throw new CommonException("上传表列数可能与实际配置表列数不符！请检查！");
                }
            }
            if (!StringUtils.isEmpty(statisticsPeriod) && !StringUtils.isEmpty(topicId)) {
                if (!isYsw) {
                    //从起始行到数据行遍历寻找统计周期所在列
                    for (int i = sheet.getFirstRowNum(); i < firstRowNum; i++) {
                        //存在统计周期则跳出循环
                        if (statisticsPeriodColumn != -1) {
                            break;
                        }
                        //获取行
                        Row row = sheet.getRow(i);
                        //遍历列
                        for (int j = 0; j < columnSize; j++) {
                            Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            String stringCellValue = CellValueUtils.getString(cell);
                            if (StringUtils.equals(stringCellValue, "统计周期")) {
                                statisticsPeriodColumn = j;
                                break;
                            }
                        }
                    }
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        int lastRowNum = sheet.getLastRowNum();
        final Map[] map = getRowSpanColSpanMap(sheet);
        sb.append("<META http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
        if (isDataTable){
            sb.append("<b style='color:red'>注: 在线预览前500行数据。标黄表示统计周期不匹配；标红表示数据格式有错；标绿表示数据合格；对于同一周期的数据，系统以最后一次导入的数据条数为准。</b>");
        }
        sb.append("<table style='border-collapse:collapse;' width='100%' border='1' cellspacing='0' cellpadding='0'>");
        int num = 500;
        int threadNum = (lastRowNum % num == 0) ? (lastRowNum / num) : (lastRowNum / num + 1);
        ExecutorService service = Executors.newFixedThreadPool(threadNum);
        BlockingQueue<Future<Map<String,Object>>> queue = new LinkedBlockingQueue<>();
        for (int i = 0; i < threadNum; i++) {
            int startIndex = i * num;
            int endIndex = startIndex + num;
            if ((i + 1) == threadNum){
                endIndex = lastRowNum;
            }
            Future<Map<String,Object>> future = service.submit(parseExcel(sheet, startIndex, endIndex, firstRowNum, columnSize, columnInfoList, map, statisticsPeriodColumn, statisticsPeriod, deptCode, isWithStyle, isDataTable, isJkww, isYsw));
            queue.add(future);
        }
        int queueSize = queue.size();
        for (int i = 0; i < queueSize; i++) {
            Map<String,Object> threadResultMap = queue.take().get();
            if (i==0){
                sb.append(threadResultMap.get("htmlString"));
            }
            @SuppressWarnings("unchecked")
            List<List<Object>> threadRowValueList = (List<List<Object>>) threadResultMap.get("rowValueList");
            rowValueList.addAll(threadRowValueList);
            List<String> threadErrorMsgList = (List<String>) threadResultMap.get("errorMsgList");
            logger.error("第" + (i+1) +"个线程错误集合大小为：" + threadErrorMsgList.size());
            noticeMessage.addAll(threadErrorMsgList);
        }
        if (rowValueList.size() == 0 && isDataTable){
            verifyExcelPass = false;
        }
        service.shutdown();
        sb.append("</table>");
        if (isDataTable) {
            for (int i = 0; i < noticeMessage.size(); i++) {
                sb.append(" <p style=\"color:red;line-height: 5px\">&nbsp;&nbsp;&nbsp;&nbsp;").append(i+1).append("、").append(noticeMessage.get(i)).append("</p>");
                verifyExcelPass = false;
            }
        }
        String htmlString = sb.toString();
        if (htmlString.contains("<tr bgcolor='#ffff66'")){
            verifyExcelPass = false;
        }
        htmlAndDataMap.put("htmlString", htmlString);
        htmlAndDataMap.put("rowValueList", rowValueList);
        htmlAndDataMap.put("verifyExcelPass", verifyExcelPass);
        return htmlAndDataMap;
    }

    /**
     * 解析表格为html
     * @param sheet 表格
     * @param isWithStyle 样式
     * @return html字符串
     */
    public static String getExcelInfo(Sheet sheet,boolean isWithStyle) {
        StringBuffer sb = new StringBuffer();
        int lastRowNum = sheet.getLastRowNum();
        Map[] map = getRowSpanColSpanMap(sheet);
        sb.append("<META http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">").append("<table style='border-collapse:collapse;' width='100%' border='1' cellspacing='0' cellpadding='0'>");
        Row row;        //兼容
        Cell cell;    //兼容
        for (int rowNum = sheet.getFirstRowNum(); rowNum <= lastRowNum; rowNum++) {
            row = sheet.getRow(rowNum);
            if (row == null) {
                sb.append("<tr><td > &nbsp;</td></tr>");
                continue;
            }
            sb.append("<tr>");
            int columnNum = row.getLastCellNum();
            for (int colNum = 0; colNum <= columnNum - 1; colNum++) {
                cell = row.getCell(colNum);
                if (cell == null) {    //特殊情况 空白的单元格会返回null
                    sb.append("<td>&nbsp;</td>");
                    continue;
                }
                String stringValue = CellValueUtils.getString(cell);
                if (map[0].containsKey(rowNum + "," + colNum)) {
                    String pointString = (String) map[0].get(rowNum + "," + colNum);
                    map[0].remove(rowNum + "," + colNum);
                    int bottomRow = Integer.valueOf(pointString.split(",")[0]);
                    int bottomCol = Integer.valueOf(pointString.split(",")[1]);
                    int rowSpan = bottomRow - rowNum + 1;
                    int colSpan = bottomCol - colNum + 1;
                    sb.append("<td rowspan= '").append(rowSpan).append("' colspan= '").append(colSpan).append("' ");
                } else if (map[1].containsKey(rowNum + "," + colNum)) {
                    map[1].remove(rowNum + "," + colNum);
                    continue;
                } else {
                    sb.append("<td ");
                }
                //判断是否需要样式
                if(isWithStyle){
                    dealExcelStyle(sheet, cell, sb);//处理单元格样式
                }
                sb.append(">");
                if (stringValue == null || "".equals(stringValue.trim())) {
                    sb.append(" &nbsp; ");
                } else {
                    // 将ascii码为160的空格转换为html下的空格（&nbsp;）
                    sb.append(stringValue.replace(String.valueOf((char) 160),"&nbsp;"));
                }
                sb.append("</td>");
            }
            sb.append("</tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }

    private static Callable<Map<String,Object>> parseExcel(final Sheet sheet,
                                                           final int startIndex,
                                                           final int endIndex,
                                                           final int firstRowNum,
                                                           final int columnSize,
                                                           final List<DbColumnInfo> columnInfos,
                                                           final Map[] map,
                                                           final int statisticsPeriodColumn,
                                                           final String statisticsPeriod,
                                                           final String deptCode,
                                                           final boolean isWithStyle,
                                                           final boolean isDataTable,
                                                           final boolean isJkww,
                                                           final boolean isYsw){
        return () -> {
            boolean isRed = false;
            Map<String,Object> resultMap = new HashMap<>();
            List<List<Object>> rowValueList = new ArrayList<>();
            List<String> errorMsg = new ArrayList<>();
            StringBuffer sb = new StringBuffer();
            for (int rowNum = startIndex; rowNum <= endIndex; rowNum++) {
                boolean isNotBlankRow = false; //用于判断当前行是否为空行
                List<Object> columnValueList = new LinkedList<>();
                Row row = sheet.getRow(rowNum);
                if (row == null) {
                    sb.append("<tr><td > &nbsp;</td></tr>");
                    continue;
                }
                sb.append("<tr>");
                int columnNum = row.getLastCellNum();
                if (columnSize != 0){
                    columnNum = columnSize - 1;
                }
                for (int colNum = 0; colNum <= columnNum; colNum++) {
                    Cell cell = row.getCell(colNum, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    if (cell == null) {    //特殊情况 空白的单元格会返回null
                        sb.append("<td>&nbsp;</td>");
                        continue;
                    }
                    String stringValue = CellValueUtils.getString(cell);
                    if (isDataTable) {
                        isRed = false;
                        if (firstRowNum != 0 && rowNum < firstRowNum - 1) {
                            stringValue = CellValueUtils.getString(cell);
                        }
                        if (firstRowNum != 0 && rowNum >= firstRowNum - 1) {
                            cell = row.getCell(colNum, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            DbColumnInfo columnInfo = columnInfos.get(colNum);
                            if (columnInfo.isInt()) {
                                Integer intValue = CellValueUtils.getInt(cell);
                                stringValue = String.valueOf(intValue);
                                columnValueList.add(intValue);
                                if (intValue == null) {
                                    String intString = cell.getStringCellValue().trim();
                                    if (intValue == null && !intString.equals("")) {
                                        if (StringUtils.equalsIgnoreCase(stringValue, "null")){
                                            stringValue = "";
                                        }else {
                                            isRed = true;
                                            errorMsg.add("第" + (rowNum + 1) + "行，" + "第" + (colNum + 1) + "列，数据格式错误!应为整数类型！！");
                                        }
                                    }
                                } else {
                                    isNotBlankRow = true;
                                    Integer numericPrecision = columnInfo.getNumericPrecision();
                                    if (numericPrecision != null && numericPrecision > 0) {
                                        if (String.valueOf(intValue).length() > numericPrecision) {
                                            isRed = true;
                                            errorMsg.add("第" + (rowNum + 1) + "行，" + "第" + (colNum + 1) + "列，数据最大为" + numericPrecision + "位！");
                                        }
                                    }
                                }
                            } else if (columnInfo.isString()) {
                                stringValue = CellValueUtils.getString(cell);
                                if (stringValue != null){
                                    if (stringValue.trim().equalsIgnoreCase("null")){
                                        stringValue = "";
                                    }
                                    columnValueList.add(stringValue);
                                    if (StringUtils.isNotEmpty(stringValue)) {
                                        stringValue = stringValue.trim();
                                        if(colNum == statisticsPeriodColumn && !stringValue.equals(statisticsPeriod)){
                                            columnValueList.clear();
                                        }
                                        isNotBlankRow = true;
                                        Integer characterMaximumLength = columnInfo.getCharacterMaximumLength();
                                        if (characterMaximumLength != null && characterMaximumLength > 0) {
                                            if (String.valueOf(stringValue).length() > characterMaximumLength) {
                                                isRed = true;
                                                errorMsg.add("第" + (rowNum + 1) + "行，" + "第" + (colNum + 1) + "列，文字最多为" + characterMaximumLength + "个字！");
                                            }
                                        }
                                    }
                                }else {
                                    columnValueList.add(stringValue);
                                    if(colNum == statisticsPeriodColumn){
                                        columnValueList.clear();
                                    }
                                }
                            } else if (columnInfo.isDouble()) {
                                Double doubleValue = CellValueUtils.getDouble(cell);
                                stringValue = CellValueUtils.getDoubleString(cell);
                                columnValueList.add(doubleValue);
                                if (doubleValue == null) {
                                    String doubleString = cell.getStringCellValue().trim();
                                    if (doubleValue == null && !doubleString.equals("") ) {
                                        if (StringUtils.equalsIgnoreCase(doubleString, "null")){
                                            stringValue = "";
                                        }else {
                                            isRed = true;
                                            errorMsg.add("第" + (rowNum + 1) + "行，" + "第" + (colNum + 1) + "列，数据格式错误!应为小数类型！！");
                                        }
                                    }
                                }else {
                                    isNotBlankRow = true;
                                    Integer numericPrecision = columnInfo.getNumericPrecision();
                                    Integer numericScale = columnInfo.getNumericScale();
                                    if (numericPrecision != null && numericPrecision > 0 && numericScale != null && numericScale > 0) {
                                        String doubleString = String.valueOf(doubleValue);
                                        String[] doubleStringArray = doubleString.split(".");
                                        String unitString;
                                        String decimalString;
                                        if (doubleStringArray.length == 1) {
                                            unitString = doubleStringArray[0];
                                            if (unitString.length() > numericPrecision) {
                                                isRed = true;
                                                errorMsg.add("第" + (rowNum + 1) + "行，" + "第" + (colNum + 1) + "列，数据最大为" + numericPrecision + "位！");
                                            }
                                        } else if (doubleStringArray.length == 2) {
                                            unitString = doubleStringArray[0];
                                            decimalString = doubleStringArray[1];
                                            if ((unitString.length() + decimalString.length()) > numericPrecision) {
                                                isRed = true;
                                                errorMsg.add("第" + (rowNum + 1) + "行，" + "第" + (colNum + 1) + "列，数据应为" + numericPrecision + "位，保留" + numericScale + "位小数！");
                                            }
                                        }
                                    }
                                }
                            } else if (columnInfo.isDate()) {
                                CellType cellTypeEnum = cell.getCellTypeEnum();
                                Date dateValue = CellValueUtils.getDate(cell);
                                columnValueList.add(dateValue);
                                stringValue = CellValueUtils.getString(cell);
                                if (dateValue == null) {
                                    if(cellTypeEnum == CellType.STRING || cellTypeEnum == CellType.BLANK) {
                                        String dateString = cell.getStringCellValue().trim();
                                        if (!StringUtils.isEmpty(dateString)){
                                            if (StringUtils.equalsIgnoreCase(dateString, "null")){
                                                stringValue = "";
                                            }else {
                                                isRed = true;
                                                errorMsg.add("第" + (rowNum + 1) + "行，" + "第" + (colNum + 1) + "列，数据格式错误!请检查单元格格式是否为日期格式或数据是否为日期类型！！");
                                            }
                                        }
                                    }
                                }
                            }
                            if (statisticsPeriodColumn != -1) {
                                if (StringUtils.isNotBlank(stringValue)) {
                                    int index = sb.lastIndexOf("<tr>") + 3;
                                    if (colNum == statisticsPeriodColumn && !stringValue.equals(statisticsPeriod)) {
                                        sb = new StringBuffer(sb.substring(0, index)).append(" bgcolor='#ffff66'").append(sb.substring(index, sb.length()));
                                    }
                                    if (colNum == statisticsPeriodColumn && stringValue.equals(statisticsPeriod)) {
                                        sb = new StringBuffer(sb.substring(0, index)).append(" bgcolor='#C0FF3E'").append(sb.substring(index, sb.length()));
                                    }
                                } else {
                                    if (colNum == statisticsPeriodColumn && isNotBlankRow) {
                                        int index = sb.lastIndexOf("<tr>") + 3;
                                        sb = new StringBuffer(sb.substring(0, index)).append(" bgcolor='#ffff66'").append(sb.substring(index, sb.length()));
                                    }
                                }
                            }else {
                                if (isYsw){
                                    if (StringUtils.isNotBlank(stringValue)) {
                                        int index = sb.lastIndexOf("<tr>") + 3;
                                        sb = new StringBuffer(sb.substring(0, index)).append(" bgcolor='#C0FF3E'").append(sb.substring(index, sb.length()));
                                    }
                                }
                            }
                        }
                    }
                    if (map[0].containsKey(rowNum + "," + colNum)) {
                        String pointString = (String) map[0].get(rowNum + "," + colNum);
                        map[0].remove(rowNum + "," + colNum);
                        int bottomRow = Integer.valueOf(pointString.split(",")[0]);
                        int bottomCol = Integer.valueOf(pointString.split(",")[1]);
                        int rowSpan = bottomRow - rowNum + 1;
                        int colSpan = bottomCol - colNum + 1;
                        sb.append("<td rowspan= '").append(rowSpan).append("' colspan= '").append(colSpan).append("' ");
                    } else if (map[1].containsKey(rowNum + "," + colNum)) {
                        map[1].remove(rowNum + "," + colNum);
                        continue;
                    } else {
                        sb.append("<td ");
                    }
                    //判断是否需要样式
                    if (isDataTable){
                        if (firstRowNum != 0 && rowNum < firstRowNum - 1) {
                            if(isWithStyle){
                                dealExcelStyle(sheet, cell, sb);//处理单元格样式
                            }
                        }
                    }else {
                        dealExcelStyle(sheet, cell, sb);//处理单元格样式
                    }
                    if (isRed){
                        sb.append("style='background-color: #FF6347;'");
                    }
                    sb.append(">");
                    if (stringValue == null || "".equals(stringValue.trim())) {
                        sb.append(" &nbsp; ");
                    } else {
                        if (stringValue.equals("null")){
                            stringValue = "";
                        }
                        // 将ascii码为160的空格转换为html下的空格（&nbsp;）
                        sb.append(stringValue.replace(String.valueOf((char) 160),"&nbsp;"));
                    }
                    sb.append("</td>");
                }
                if (isDataTable){
                    if (isNotBlankRow) {
                        if (columnValueList.size() != 0) {
                            if (isJkww) { //教科文卫特殊处理，在每一行的数据末尾增加部门编号字段。
                                columnValueList.add(deptCode);
                                columnValueList.add(0);
                            }
                            if (isYsw){
                                columnValueList.add(statisticsPeriod);
                            }
                            rowValueList.add(columnValueList);
                        }
                    }
                }
                sb.append("</tr>");
            }
            resultMap.put("htmlString", sb.toString());
            resultMap.put("rowValueList", rowValueList);
            resultMap.put("errorMsgList", errorMsg);
            return resultMap;
        };
    }

    private static Map[] getRowSpanColSpanMap(Sheet sheet) {
        Map<String, String> map0 = new HashMap<>();
        Map<String, String> map1 = new HashMap<>();
        int mergedNum = sheet.getNumMergedRegions();
        CellRangeAddress range;
        for (int i = 0; i < mergedNum; i++) {
            range = sheet.getMergedRegion(i);
            int topRow = range.getFirstRow();
            int topCol = range.getFirstColumn();
            int bottomRow = range.getLastRow();
            int bottomCol = range.getLastColumn();
            map0.put(topRow + "," + topCol, bottomRow + "," + bottomCol);
            // System.out.println(topRow + "," + topCol + "," + bottomRow + "," + bottomCol);
            int tempRow = topRow;
            while (tempRow <= bottomRow) {
                int tempCol = topCol;
                while (tempCol <= bottomCol) {
                    map1.put(tempRow + "," + tempCol, "");
                    tempCol++;
                }
                tempRow++;
            }
            map1.remove(topRow + "," + topCol);
        }
        return new Map[]{ map0, map1 };
    }

    /**
     * 处理表格样式
     * @param sheet 表
     * @param cell 单元格
     * @param sb 样式
     */
    private static void dealExcelStyle(Sheet sheet,Cell cell,StringBuffer sb){
        Workbook wb = sheet.getWorkbook();
        CellStyle cellStyle = cell.getCellStyle();
        if (cellStyle != null) {
            sb.append("align='").append(cellStyle.getAlignmentEnum().name()).append("' ");//单元格内容的水平对齐方式
            sb.append("valign='").append(cellStyle.getVerticalAlignmentEnum().name()).append("' ");//单元格中内容的垂直排列方式
            if (wb instanceof XSSFWorkbook) {
                XSSFFont xf = ((XSSFCellStyle) cellStyle).getFont();
                boolean boldWeight = xf.getBold();
                sb.append("style='");
                if (boldWeight){
                    sb.append("font-weight: bold;"); // 字体加粗
                }
                sb.append("font-size: ").append(xf.getFontHeight() / 2).append("%;"); // 字体大小
                int columnWidth = sheet.getColumnWidth(cell.getColumnIndex()) ;
                sb.append("width:").append(columnWidth).append("px;");
                XSSFColor xc = xf.getXSSFColor();
                if (xc != null && !"".equals(xc.toString())) {
                    sb.append("color:#").append(xc.getARGBHex().substring(2)).append(";"); // 字体颜色
                }
                XSSFColor bgColor = (XSSFColor) cellStyle.getFillForegroundColorColor();
                if (bgColor != null && !"".equals(bgColor.toString())) {
                    sb.append("background-color:#").append(bgColor.getARGBHex().substring(2)).append(";"); // 背景颜色
                }
//                sb.append(getBorderStyle(0,cellStyle.getBorderTopEnum().getCode(), ((XSSFCellStyle) cellStyle).getTopBorderXSSFColor()));
//                sb.append(getBorderStyle(1,cellStyle.getBorderRightEnum().getCode(), ((XSSFCellStyle) cellStyle).getRightBorderXSSFColor()));
//                sb.append(getBorderStyle(2,cellStyle.getBorderBottomEnum().getCode(), ((XSSFCellStyle) cellStyle).getBottomBorderXSSFColor()));
//                sb.append(getBorderStyle(3,cellStyle.getBorderLeftEnum().getCode(), ((XSSFCellStyle) cellStyle).getLeftBorderXSSFColor()));
            }else if(wb instanceof HSSFWorkbook){
                HSSFFont hf = ((HSSFCellStyle) cellStyle).getFont(wb);
                boolean boldWeight = hf.getBold();
                short fontColor = hf.getColor();
                sb.append("style='");
                HSSFPalette palette = ((HSSFWorkbook) wb).getCustomPalette(); // 类HSSFPalette用于求的颜色的国际标准形式
                HSSFColor hc = palette.getColor(fontColor);
                if (boldWeight){
                    sb.append("font-weight: bold;"); // 字体加粗
                }
                sb.append("font-size: ").append(hf.getFontHeight() / 2).append("%;"); // 字体大小
                String fontColorStr = convertToStardColor(hc);
                if (fontColorStr != null && !"".equals(fontColorStr.trim())) {
                    sb.append("color:").append(fontColorStr).append(";"); // 字体颜色
                }
                int columnWidth = sheet.getColumnWidth(cell.getColumnIndex()) ;
                sb.append("width:").append(columnWidth).append("px;");
                short bgColor = cellStyle.getFillForegroundColor();
                hc = palette.getColor(bgColor);
                String bgColorStr = convertToStardColor(hc);
                if (bgColorStr != null && !"".equals(bgColorStr.trim())) {
                    sb.append("background-color:").append(bgColorStr).append(";"); // 背景颜色
                }
//                sb.append( getBorderStyle(palette,0,cellStyle.getBorderBottomEnum().getCode(),cellStyle.getTopBorderColor()));
//                sb.append( getBorderStyle(palette,1,cellStyle.getBorderRightEnum().getCode(),cellStyle.getRightBorderColor()));
//                sb.append( getBorderStyle(palette,3,cellStyle.getBorderLeftEnum().getCode(),cellStyle.getLeftBorderColor()));
//                sb.append( getBorderStyle(palette,2,cellStyle.getBorderBottomEnum().getCode(),cellStyle.getBottomBorderColor()));
            }
            sb.append("' ");
        }
    }

    private static String convertToStardColor(HSSFColor hc) {
        StringBuilder sb = new StringBuilder();
        if (hc != null) {
            if (HSSFColor.HSSFColorPredefined.AUTOMATIC.getIndex() == hc.getIndex()) {
                return null;
            }
            sb.append("#");
            for (int i = 0; i < hc.getTriplet().length; i++) {
                sb.append(fillWithZero(Integer.toHexString(hc.getTriplet()[i])));
            }
        }
        return sb.toString();
    }

    private static String fillWithZero(String str) {
        if (str != null && str.length() < 2) {
            return "0" + str;
        }
        return str;
    }

    private static void startRowVerify(int startRowNum, Sheet sheet) throws CommonException {
        int firstRowNum = startRowNum - 1;
        int lastRowNum = sheet.getLastRowNum();
        Row row = sheet.getRow(firstRowNum);
        //针对除表第一行外，其它行为空行情况，row为null
        if(row == null){
            throw new CommonException("起始行为空行！请检查起始行配置！");
        }else {
            //针对表第一行为空行情况，row不为null，而lastCellNum为-1
            short lastCellNum = row.getLastCellNum();
            if (lastCellNum < 0){
                throw new CommonException("起始行配置可能有误！请检查！");
            }
        }
        if (firstRowNum > lastRowNum){
            throw new CommonException("起始行配置可能有误！请检查！");
        }
    }

    /*private static boolean verifyRow(Row row, List<DbColumnInfo> columnInfos) throws CommonException {
        boolean isNotBlankRow = false;
        int rowNum = row.getRowNum();
        for (int colNum = 0;colNum < columnInfos.size();colNum++){
            Cell cell = row.getCell(colNum, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            DbColumnInfo columnInfo = columnInfos.get(colNum);
            if (columnInfo.isInt()) {
                Integer intValue = CellValueUtils.getInt(cell);
                if (intValue == null) {
                    String intString = cell.getStringCellValue().trim();
                    if (!intString.equals("") && !StringUtils.equalsIgnoreCase(intString, "null")) {
                        throw new CommonException("起始行校验失败，第" + (rowNum + 1) + "行，" + "第" + (colNum + 1) + "列，数据格式错误!应为整数类型！！");
                    }
                } else {
                    isNotBlankRow = true;
                    Integer numericPrecision = columnInfo.getNumericPrecision();
                    if (numericPrecision != null && numericPrecision > 0) {
                        if (String.valueOf(intValue).length() > numericPrecision) {
                            throw new CommonException("起始行校验失败，第" + (rowNum + 1) + "行，" + "第" + (colNum + 1) + "列，数据最大为" + numericPrecision + "位！");
                        }
                    }
                }
            } else if (columnInfo.isString()) {
                String strValue = CellValueUtils.getString(cell);
                if (StringUtils.isNotEmpty(strValue)){
                    strValue = strValue.trim();
                    isNotBlankRow = true;
                    Integer characterMaximumLength = columnInfo.getCharacterMaximumLength();
                    if (characterMaximumLength != null && characterMaximumLength > 0) {
                        if (String.valueOf(strValue).length() > characterMaximumLength) {
                            throw new CommonException("起始行校验失败，第" + (rowNum + 1) + "行，" + "第" + (colNum + 1) + "列，文字最多为" + characterMaximumLength + "个字！");
                        }
                    }
                }
            } else if (columnInfo.isDouble()) {
                Double doubleValue = CellValueUtils.getDouble(cell);
                if (doubleValue == null) {
                    String doubleString = cell.getStringCellValue().trim();
                    if (!doubleString.equals("") && !StringUtils.equalsIgnoreCase(doubleString, "null")) {
                        throw new CommonException("起始行校验失败，第" + (rowNum + 1) + "行，" + "第" + (colNum + 1) + "列，数据格式错误!应为小数类型！！");
                    }
                }else {
                    isNotBlankRow = true;
                    Integer numericPrecision = columnInfo.getNumericPrecision();
                    Integer numericScale = columnInfo.getNumericScale();
                    if (numericPrecision != null && numericPrecision > 0 && numericScale != null && numericScale > 0) {
                        String doubleString = String.valueOf(doubleValue);
                        String[] doubleStringArray = doubleString.split(".");
                        String unitString;
                        String decimalString;
                        if (doubleStringArray.length == 1) {
                            unitString = doubleStringArray[0];
                            if (unitString.length() > numericPrecision) {
                                throw new CommonException("起始行校验失败，第" + (rowNum + 1) + "行，" + "第" + (colNum + 1) + "列，数据最大为" + numericPrecision + "位！");
                            }
                        } else if (doubleStringArray.length == 2) {
                            unitString = doubleStringArray[0];
                            decimalString = doubleStringArray[1];
                            if ((unitString.length() + decimalString.length()) > numericPrecision) {
                                throw new CommonException("起始行校验失败，第" + (rowNum + 1) + "行，" + "第" + (colNum + 1) + "列，数据应为" + numericPrecision + "位，保留" + numericScale + "位小数！");
                            }
                        }
                    }
                }
            } else if (columnInfo.isDate()) {
                CellType cellTypeEnum = cell.getCellTypeEnum();
                Date dateValue = CellValueUtils.getDate(cell);
                if (dateValue == null) {
                    if(cellTypeEnum == CellType.STRING || cellTypeEnum == CellType.BLANK) {
                        String dateString = cell.getStringCellValue().trim();
                        if (!StringUtils.isEmpty(dateString) && !StringUtils.equalsIgnoreCase(dateString, "null")){
                            throw new CommonException("起始行校验失败，第" + (rowNum + 1) + "行，" + "第" + (colNum + 1) + "列，数据格式错误!请检查单元格格式是否为日期格式或数据是否为日期类型！！");
                        }
                    }
                }else {
                    isNotBlankRow = true;
                }
            }
        }
        return isNotBlankRow;
    }*/

    /*private static String[] bordesr={"border-top:","border-right:","border-bottom:","border-left:"};
    private static String[] borderStyles={"solid ","solid ","solid ","solid ","solid ","solid ","solid ","solid ","solid ","solid","solid","solid","solid","solid"};

    private static  String getBorderStyle(HSSFPalette palette ,int b,short s, short t){
        if(s==0)return  bordesr[b]+borderStyles[s]+"#d0d7e5 1px;";
        String borderColorStr = convertToStardColor( palette.getColor(t));
        borderColorStr=borderColorStr==null|| borderColorStr.length()<1?"#000000":borderColorStr;
        return bordesr[b]+borderStyles[s]+borderColorStr+" 1px;";
    }

    private static  String getBorderStyle(int b,short s, XSSFColor xc){
        if(s==0)return  bordesr[b]+borderStyles[s]+"#d0d7e5 1px;";
        if (xc != null) {
            String borderColorStr = xc.getARGBHex();//t.getARGBHex();
            borderColorStr=borderColorStr==null|| borderColorStr.length()<1?"#000000":borderColorStr.substring(2);
            return bordesr[b]+borderStyles[s]+borderColorStr+" 1px;";
        }
        return "";
    }*/

}
