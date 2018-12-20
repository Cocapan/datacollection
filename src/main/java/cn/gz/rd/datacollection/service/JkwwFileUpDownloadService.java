package cn.gz.rd.datacollection.service;

import cn.gz.rd.datacollection.dao.*;
import cn.gz.rd.datacollection.model.*;
import cn.gz.rd.datacollection.utils.CellValueUtils;
import cn.gz.rd.datacollection.utils.ExcelUtils;
import com.jcraft.jsch.SftpException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 文件的上传和下载相关服务
 */
@Service
public class JkwwFileUpDownloadService {

    public final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DeptSubjectInfoService deptSubjectInfoService;

    @Autowired
    private JkwwwSubjectService subjectService;

    @Autowired
    private JkwwProjectImgMapper projectImgMapper;

    @Autowired
    private JkwwPrjImgInfoMapper prjImgInfoMapper;

    @Autowired
    private CommonCollectionService commonCollectionService;

    @Autowired
    private JkwwPrjDataSummaryService prjDataSummaryService;

    @Autowired
    private JkwwProjectPlanAndImplementMapper planAndImplementMapper;

    @Autowired
    private JkwwProblemScheduleMapper problemScheduleMapper;

    @Autowired
    private JkwwAccessoryMapper jkwwAccessoryMapper;

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private SFTPService sftpService;

    /**
     * 上传文件的历史保存目录前缀
     */
    @Value("${uploadfile.history.save.root.dir}")
    private String uploadHisFileSaveDir;

    /**
     * 文件上传
     * @param userCode 用户编码
     * @param subjectInfo 主题信息
     * @param dataDeptCode 数据所属的部门编码
     * @param countRate 统计周期
     * @param dataType 数据类型
     * @param file 上传的文件
     * @param previewPath 预览路径
     * @return 为图片的时候返回ID，其他时候返回null。
     */
    @Transactional(rollbackFor = Exception.class)
    public synchronized UploadFileResultVO uploadFile(
            String userCode, SubjectInfo subjectInfo, String countRate, String dataDeptCode,
            String dataType, MultipartFile file, Integer subjectStatusId, String previewPath)
            throws Exception {
        UploadFileResultVO resultVO = new UploadFileResultVO();
        String subjectCode = subjectInfo.getSubjectCode();
        String originalFileName = file.getOriginalFilename();
        String saveFileName = System.currentTimeMillis() + "_" + originalFileName;

        String fileSavePath;
        SFTPClient sftpClient = null;
        DeptSubjectInfo deptSubjectInfo = null;
        String uploadNewPath = null;
        try {
            sftpClient = sftpService.createClientAndLgoin();
            String saveDirPath = "/jkww/" + countRate + "/";
            fileSavePath = sftpService.uploadHisFile(sftpClient, saveDirPath, saveFileName, file);
            if (StringUtils.equals("文件", dataType)) {//文件还需要上传一份到最新目录中
                String saveDir = subjectInfo.getSaveLocation();
                String subjectName = subjectInfo.getSubjectName();
                uploadNewPath = sftpService.uploadNewFileByJkww(
                        sftpClient, saveDir, originalFileName,
                        file.getInputStream(), subjectName, countRate, subjectCode);
            }

            if (subjectStatusId != null) { //存在则直接从数据库获取，然后进行更新。

                deptSubjectInfo = deptSubjectInfoService.selectBySubjectStatusId(subjectStatusId);
                deptSubjectInfo.setSjccscsj(new Date());
                deptSubjectInfo.setScsjdyhbh(userCode);
                deptSubjectInfo.setWjcflj(fileSavePath);
                deptSubjectInfo.setWjmc(originalFileName);
                deptSubjectInfo.setYllj(previewPath);

                //审核未通过的数据再次提交时需要更改状态为待审核
                Integer subjectStatus = deptSubjectInfo.getSjzt();
                if (subjectStatus == 2) {
                    subjectStatus = 0;
                } else if (subjectStatus == 5) {
                    subjectStatus = 6;
                }
                deptSubjectInfo.setSjzt(subjectStatus);
                deptSubjectInfoService.updateDeptSubjectInfo(deptSubjectInfo);

            } else { //不存在则新建

                deptSubjectInfo = new DeptSubjectInfo();
                deptSubjectInfo.setZtbh(subjectCode);
                deptSubjectInfo.setBmbh(dataDeptCode);
                deptSubjectInfo.setSjzt(0);
                deptSubjectInfo.setSjccscsj(new Date());
                deptSubjectInfo.setSjtjzq(countRate);
                deptSubjectInfo.setScsjdyhbh(userCode);
                deptSubjectInfo.setWjcflj(fileSavePath);
                deptSubjectInfo.setWjmc(originalFileName);
                deptSubjectInfo.setYllj(previewPath);
                deptSubjectInfoService.saveDeptSubjectInfo(deptSubjectInfo);
                subjectStatusId = deptSubjectInfo.getZtztid();
            }
        } finally {
            sftpService.close(sftpClient);
        }

        //保存操作记录
        DeptSubjectOperateInfo operateInfo = new DeptSubjectOperateInfo();
        operateInfo.setZtztid(deptSubjectInfo.getZtztid());
        operateInfo.setCzlx("upload");
        operateInfo.setCzsj(new Date());
        operateInfo.setSjcclj(fileSavePath);
        operateInfo.setCzyhbh(userCode);
        operateInfo.setWjmc(originalFileName);
        deptSubjectInfoService.saveDeptSubjectOperateInfo(operateInfo);

        if (StringUtils.equals("数据表", dataType)) { //Excel数据文件

            subjectService.deleteDeptData(dataDeptCode, countRate, subjectCode);
            Workbook workbook = ExcelUtils.getWorkBookByFileName(originalFileName, file.getInputStream());
            commonCollectionService.uploadExcelData(subjectCode,
                    true, dataDeptCode, countRate, workbook.getSheetAt(0));

        } else {

            String projectCode = projectImgMapper.selectProjectCodeBySubjectCode(subjectCode);
            if (StringUtils.isNotBlank(projectCode)) { //是否为图片文件
                JkwwPrjImgInfo imgInfo = new JkwwPrjImgInfo();
                imgInfo.setImgFileName(originalFileName);
                String pre = "/data/file";
                String previewUrl = fileSavePath.substring(pre.length());
                imgInfo.setImgSavePath(fileSavePath);
                imgInfo.setImgPreviewPath(previewUrl);
                imgInfo.setProjectStatusId(deptSubjectInfo.getZtztid());
                imgInfo.setUploadDate(new Date());
                imgInfo.setUploadUser(userCode);
                imgInfo.setImgNewSavePath(uploadNewPath);
                List<JkwwPrjImgInfo> jkwwPrjImgInfos = prjImgInfoMapper.selectByFileNameAndSubjectCode(originalFileName, subjectCode, countRate);
                if (jkwwPrjImgInfos != null && jkwwPrjImgInfos.size() != 0){
                    prjImgInfoMapper.updateByPrimaryKey(imgInfo);
                    resultVO.setImgInfoId(jkwwPrjImgInfos.get(jkwwPrjImgInfos.size()-1).getInfoId());
                }else {
                    prjImgInfoMapper.insertSelective(imgInfo);
                    resultVO.setImgInfoId(imgInfo.getInfoId());
                }
            }
        }
        resultVO.setSubjectStatusId(subjectStatusId);
        return resultVO;
    }

    /**
     * 附件上传
     * @param countRate 统计周期
     * @param deptCode 数据所属的部门编码
     * @param file 上传的文件
     * @return 为图片的时候返回ID，其他时候返回null。
     */
    @Transactional(rollbackFor = Exception.class)
    public AccessoryFile uploadAccessoryFile(String countRate, String deptCode, MultipartFile file) throws Exception {
        String originalFileName = file.getOriginalFilename();
        String[] originalFilePath = originalFileName.split("\\\\");
        originalFileName = originalFilePath[originalFilePath.length-1];
        String fileName = originalFileName.substring(0, originalFileName.lastIndexOf(".")) + "_" + countRate + "." + originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
        String saveFileName = System.currentTimeMillis() + "_" + fileName;
        SFTPClient sftpClient = null;
        String saveDirPath = "/jkww/附件/" + countRate + "/";
        String uploadNewPath;
        try {
            sftpClient = sftpService.createClientAndLgoin();
            String uploadHisFile = sftpService.uploadHisFile(sftpClient, saveDirPath, saveFileName, file);
            Map<String, Object> resultMap = commonCollectionService.uploadFileToNginx(uploadHisFile, countRate, null, deptCode, false, true);
            String departmentName = departmentMapper.selectDepartmentNameByDepartmentId(deptCode);
            if (departmentName.equals("市政府办公厅")){
                departmentName = "市发改委";
            }
            String saveDir = "/教科文卫工委/附件/" + departmentName;
            uploadNewPath = sftpService.uploadNewFile(sftpClient, saveDir, fileName, file.getInputStream());
            AccessoryFile accessoryFile = jkwwAccessoryMapper.selectByFileNameCountRateAndDeptCode(fileName, countRate, deptCode);
            if (accessoryFile == null){
                jkwwAccessoryMapper.insert(fileName, originalFileName, countRate, uploadNewPath, String.valueOf(resultMap.get("previewUrl")), deptCode);
            }else {
                accessoryFile.setStatisticPeriod(countRate);
                accessoryFile.setStorageUrl(uploadNewPath);
                accessoryFile.setPreviewUrl(String.valueOf(resultMap.get("previewUrl")));
                accessoryFile.setDeptCode(deptCode);
                jkwwAccessoryMapper.updateByStorageUrl(accessoryFile);
            }
        } finally {
            sftpService.close(sftpClient);
        }
        return jkwwAccessoryMapper.selectByFileNameCountRateAndDeptCode(fileName, countRate, deptCode);
    }

    /**
     * 附件列表
     * @param countRate 统计周期
     * @param deptCode 数据所属的部门编码
     * @return 附件文件列表。
     */
    @Transactional(rollbackFor = Exception.class)
    public List<AccessoryFile> listAccessoryFiles(String countRate, String deptCode){
        return jkwwAccessoryMapper.selectByCountRateAndDeptCode(countRate, deptCode);
    }

    /**
     * 删除附件文件
     * @param id 附件文件唯一标识
     * @return 附件文件列表。
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteAccessoryFiles(int id){
        SFTPClient sftpClient = null;
        String storageUrl = jkwwAccessoryMapper.selectStorageUrlById(id);
        try {
            sftpClient = sftpService.createClientAndLgoin();
            sftpClient.delete(storageUrl);
        } finally {
            sftpService.close(sftpClient);
        }
        jkwwAccessoryMapper.deleteById(id);
    }

    /**
     * 文件下载
     * @param subjectStatusIds 主题编号
     * @param countRate 统计周期
     * @return File
     * @throws IOException
     */
    public File downloadFile(List<Integer> subjectStatusIds, String countRate)
            throws IOException, SftpException {
        List<DeptSubjectInfo> deptSubjectInfos =
                deptSubjectInfoService.selectManySubjectAtCountRate(subjectStatusIds, countRate);
        List<File> downloadFiles = new ArrayList<>();

        SFTPClient sftpClient = null;
        try {
            sftpClient = sftpService.createClientAndLgoin();
            for (DeptSubjectInfo deptSubject : deptSubjectInfos) {
                String uploadFilePath = deptSubject.getWjcflj();
                String uploadFileName = deptSubject.getWjmc();
                String subjectCode = deptSubject.getZtbh();
                Integer subjectStatusId = deptSubject.getZtztid();

                String projectCode = projectImgMapper.selectProjectCodeBySubjectCode(subjectCode);
                if (StringUtils.isNotBlank(projectCode)) { //如果为图片则下载所有图片

                    List<JkwwPrjImgInfo> prjImgInfos;
                    if (subjectStatusId != null) {
                        prjImgInfos = prjImgInfoMapper.selectByPrjStatusId(subjectStatusId);
                    } else {
                        prjImgInfos = new ArrayList<>(0);
                    }
                    int i = 1;
                    for (JkwwPrjImgInfo imgInfo : prjImgInfos) {
                        String imgFileName = i + "_" + imgInfo.getImgFileName();
                        ++ i;
                        String imgSavePath = imgInfo.getImgSavePath();
                        String imgSaveDir = FilenameUtils.getFullPath(imgSavePath);
                        String imgSaveFileName = FilenameUtils.getName(imgSavePath);
                        byte[] bytes = sftpClient.download(imgSaveDir, imgSaveFileName);
                        if (bytes != null) {
                            File file = new File(System.getProperty("java.io.tmpdir") + System.currentTimeMillis() + "_"  + imgFileName );
                            FileUtils.writeByteArrayToFile(file, bytes);
                            downloadFiles.add(file);
                        }
                    }

                } else {
                    String fileSaveDir = FilenameUtils.getFullPath(uploadFilePath);
                    String fileSaveFileName = FilenameUtils.getName(uploadFilePath);
                    byte[] bytes = sftpClient.download(fileSaveDir, fileSaveFileName);
                    if (bytes != null) {
                        File file = new File(System.getProperty("java.io.tmpdir")  + System.currentTimeMillis() + "_"  +  uploadFileName);
                        FileUtils.writeByteArrayToFile(file, bytes);
                        downloadFiles.add(file);
                    }
                }
            }
        } finally {
            sftpService.close(sftpClient);
        }

        int size = downloadFiles.size();
        if (size == 1) {

            return downloadFiles.get(0);

        } else {//将文件打包
            ZipOutputStream out = null;
            File zipFile = new File(System.getProperty("java.io.tmpdir")
                    + "批量下载" + System.currentTimeMillis() + ".zip");
            try {
                out = new ZipOutputStream(new FileOutputStream(zipFile));
                for (File file : downloadFiles) {
                    FileInputStream in = null;
                    try {
                        in = new FileInputStream(file);
                        out.putNextEntry(new ZipEntry(file.getName()));
                        IOUtils.copy(in, out);
                    } finally {
                        IOUtils.closeQuietly(in);
                    }
                }
            } finally {
                IOUtils.closeQuietly(out);
            }
            return zipFile;
        }
    }

    /**
     * 下载项目分领域统计信息Excel文件
     * @param countRate 统计周期
     * @return Excel
     */
    public byte[] downloadPrjStatInfo(String countRate)
            throws IOException, InvalidFormatException {

        List<JkwwExeCondition> statInfos = subjectService.selectStatInfo(countRate);
        String templateFilePath = "/templates/jkww/ExportTemplates/“十三五”时期市本级社会民生基础设施分领域统计表.xlsx";
        InputStream in = getClass().getResourceAsStream(templateFilePath);
        File tmpFile = new File(System.getProperty("java.io.tmpdir") + File.separatorChar
                + UUID.randomUUID().toString() +".xlsx");
        FileUtils.copyInputStreamToFile(in, tmpFile);
        XSSFWorkbook workbook = new XSSFWorkbook(tmpFile);
        XSSFSheet sheet = workbook.getSheetAt(0);
        int startRowIndex = 3;

        for (JkwwExeCondition exeCondition : statInfos) {
            XSSFRow dataRow = sheet.getRow(startRowIndex);
            if (dataRow == null) {
                dataRow = sheet.createRow(startRowIndex);
            }
            ++ startRowIndex;

            XSSFCell cell0 = dataRow.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell0, exeCondition.getFl());

            XSSFCell cell1 = dataRow.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell1, exeCondition.getBndtzjhZe());

            XSSFCell cell2 = dataRow.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell2, exeCondition.getBndtzjhCzzjwce());

            XSSFCell cell3 = dataRow.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell3, exeCondition.getSyjdWce());

            XSSFCell cell4 = dataRow.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell4, exeCondition.getSyjdCzzjwce());

            XSSFCell cell5 = dataRow.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell5, exeCondition.getBjdWce());

            XSSFCell cell6 = dataRow.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell6, exeCondition.getBjdBsyjdjj());

            XSSFCell cell7 = dataRow.getCell(7, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell7, exeCondition.getBjdCzzjwce());

            XSSFCell cell8 = dataRow.getCell(8, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell8, exeCondition.getBjdCzzjBsyjdjj());

            XSSFCell cell9 = dataRow.getCell(9, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell9, exeCondition.getQntqljWce());

            XSSFCell cell10 = dataRow.getCell(10, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell10, exeCondition.getQntqljWcl());

            XSSFCell cell11 = dataRow.getCell(11, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell11, exeCondition.getQntqljCzzjwce());

            XSSFCell cell12 = dataRow.getCell(12, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell12, exeCondition.getQntqljCzzjwcl());

            XSSFCell cell13 = dataRow.getCell(13, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell13, exeCondition.getBjdljWce());

            XSSFCell cell14 = dataRow.getCell(14, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell14, exeCondition.getBjdljBqntqjj());

            XSSFCell cell15 = dataRow.getCell(15, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell15, exeCondition.getBjdljCzzjwce());

            XSSFCell cell16 = dataRow.getCell(16, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell16, exeCondition.getBjdljCzzjBqntqjj());

            XSSFCell cell17 = dataRow.getCell(17, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell17, exeCondition.getBjdljWcl());

            XSSFCell cell18 = dataRow.getCell(18, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell18, exeCondition.getBjdljBqntqjjb());

            XSSFCell cell19 = dataRow.getCell(19, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell19, exeCondition.getBjdljCzzjwcl());

            XSSFCell cell20 = dataRow.getCell(20, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell20, exeCondition.getBjdljCzzjBqntqjjb());
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }

    /**
     * 下载项目的详细信息Excel
     * @param className 分类名称
     * @param status 状态
     * @param mainDeptName 主管部门
     * @param ownerName 业主名称
     * @param projectName 项目名称
     * @param countRate 统计周期
     * @return Excel
     */
    public byte[] downloadPrjDetailInfo(
            String className, String status,
            String mainDeptName, String ownerName,
            String projectName, String countRate) throws IOException, InvalidFormatException {
        List<JkwwExeCondition> projectInfos = subjectService.selectExePlan(
                className, status, mainDeptName, ownerName, projectName, countRate, null, null);
        return createExeConditionExcel(projectInfos);
    }

    /**
     * 生成《“十三五”时期市本级社会民生基础设施计划执行情况表》Excel表格
     * @param projectInfos 数据
     * @return XSSFWorkbook
     */
    private byte[] createExeConditionExcel(List<JkwwExeCondition> projectInfos)
            throws IOException, InvalidFormatException {
        String templateFilePath = "/templates/jkww/ExportTemplates/“十三五”时期市本级社会民生基础设施计划执行情况表.xlsx";
        InputStream in = getClass().getResourceAsStream(templateFilePath);
        File tmpFile = new File(System.getProperty("java.io.tmpdir") + File.separatorChar
                + UUID.randomUUID().toString() +".xlsx");
        FileUtils.copyInputStreamToFile(in, tmpFile);
        XSSFWorkbook workbook = new XSSFWorkbook(tmpFile);
        XSSFSheet sheet = workbook.getSheetAt(0);
        int startRowIndex = 5;
        for (JkwwExeCondition exeCondition : projectInfos) {
            XSSFRow dataRow = sheet.getRow(startRowIndex);
            if (dataRow == null) {
                dataRow = sheet.createRow(startRowIndex);
            }
            ++ startRowIndex;

            XSSFCell cell1 = dataRow.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell1, exeCondition.getXmxh());

            XSSFCell cell2 = dataRow.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell2, exeCondition.getXmbm());

            XSSFCell cell3 = dataRow.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell3, exeCondition.getFl());

            XSSFCell cell4 = dataRow.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell4, exeCondition.getZt());

            XSSFCell cell5 = dataRow.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell5, exeCondition.getXmmc());

            XSSFCell cell6 = dataRow.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell6, exeCondition.getZgbm());

            XSSFCell cell7 = dataRow.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell7, exeCondition.getXmyz());

            XSSFCell cell8 = dataRow.getCell(7, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell8, exeCondition.getBndtzjhZe());

            XSSFCell cell9 = dataRow.getCell(8, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell9, exeCondition.getBndtzjhCzzjwce());

            XSSFCell cell10 = dataRow.getCell(9, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell10, exeCondition.getSyjdWce());

            XSSFCell cell11 = dataRow.getCell(10, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell11, exeCondition.getSyjdCzzjwce());

            XSSFCell cell12 = dataRow.getCell(11, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell12, exeCondition.getBjdWce());

            XSSFCell cell13 = dataRow.getCell(12, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell13, exeCondition.getBjdBsyjdjj());

            XSSFCell cell14 = dataRow.getCell(13, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell14, exeCondition.getBjdCzzjwce());

            XSSFCell cell15 = dataRow.getCell(14, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell15, exeCondition.getBjdCzzjBsyjdjj());

            XSSFCell cell16 = dataRow.getCell(15, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell16, exeCondition.getQntqljWce());

            XSSFCell cell17 = dataRow.getCell(16, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell17, exeCondition.getQntqljWcl());

            XSSFCell cell18 = dataRow.getCell(17, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell18, exeCondition.getQntqljCzzjwce());

            XSSFCell cell19 = dataRow.getCell(18, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell19, exeCondition.getQntqljCzzjwcl());

            XSSFCell cell20 = dataRow.getCell(19, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell20, exeCondition.getBjdljWce());

            XSSFCell cell21 = dataRow.getCell(20, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell21, exeCondition.getBjdljBqntqjj());

            XSSFCell cell22 = dataRow.getCell(21, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell22, exeCondition.getBjdljCzzjwce());

            XSSFCell cell23 = dataRow.getCell(22, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell23, exeCondition.getBjdljCzzjBqntqjj());

            XSSFCell cell24 = dataRow.getCell(23, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell24, exeCondition.getBjdljWcl());

            XSSFCell cell25 = dataRow.getCell(24, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell25, exeCondition.getBjdljBqntqjjb());

            XSSFCell cell26 = dataRow.getCell(25, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell26, exeCondition.getBjdljCzzjwcl());

            XSSFCell cell27 = dataRow.getCell(26, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell27, exeCondition.getBjdljCzzjBqntqjjb());

            XSSFCell cell28 = dataRow.getCell(27, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell28, exeCondition.getGcjsjdsm());

            XSSFCell cell29 = dataRow.getCell(28, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell29, exeCondition.getTbr());

            XSSFCell cell30 = dataRow.getCell(29, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell30, exeCondition.getLxdh());

            XSSFCell cell31 = dataRow.getCell(30, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell31, exeCondition.getBz());
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }

    /**
     * 获取汇总的《“十三五”时期市本级社会民生基础设施年度计划表》的Excel二进制数据
     */
    public byte[] downloadYearlyPlanSummary(
            String className, String status, String mainDeptName,
            String ownerName, String projectName, String countRate)
            throws IOException, InvalidFormatException {
        List<JkwwYearlyPlan> jkwwYearlyPlans = prjDataSummaryService.queryYearlyPlanByCondition(
                className, status, mainDeptName, ownerName,
                projectName, countRate, null, null);
        return createYearLyPlanExcel(jkwwYearlyPlans);
    }

    /**
     * 生成《“十三五”时期市本级社会民生基础设施年度计划表》Excel表格
     * @param yearlyPlans 数据
     * @return byte[]
     */
    private byte[] createYearLyPlanExcel(List<JkwwYearlyPlan> yearlyPlans)
            throws IOException, InvalidFormatException {

        String templateFilePath = "/templates/jkww/ExportTemplates/“十三五”时期市本级社会民生基础设施年度计划表.xlsx";
        InputStream in = getClass().getResourceAsStream(templateFilePath);

        File tmpFile = new File(System.getProperty("java.io.tmpdir") + File.separatorChar
                + UUID.randomUUID().toString() +".xlsx");
        FileUtils.copyInputStreamToFile(in, tmpFile);
        XSSFWorkbook workbook = new XSSFWorkbook(tmpFile);
        XSSFSheet sheet = workbook.getSheetAt(0);
        int startRowIndex = 6;

        for (int i = 0, size = yearlyPlans.size(); i < size; i++) {
            JkwwYearlyPlan yearlyPlan = yearlyPlans.get(i);
            XSSFRow row = sheet.getRow(startRowIndex);
            if (row == null) {
                row = sheet.createRow(startRowIndex);
            }
            ++ startRowIndex;

            XSSFCell cell1 = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell1, yearlyPlan.getXmxh());

            XSSFCell cell2 = row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell2, yearlyPlan.getXmbm());

            XSSFCell cell3 = row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell3, yearlyPlan.getFl());

            XSSFCell cell4 = row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell4, yearlyPlan.getZt());

            XSSFCell cell5 = row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell5, yearlyPlan.getXmmc());

            XSSFCell cell6 = row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell6, yearlyPlan.getZgbm());

            XSSFCell cell7 = row.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell7, yearlyPlan.getXmyz());

            XSSFCell cell8 = row.getCell(7, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell8, yearlyPlan.getXmgmhjsnr());

            XSSFCell cell9 = row.getCell(8, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell9, yearlyPlan.getJhkgsjn());

            XSSFCell cell10 = row.getCell(9, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell10, yearlyPlan.getJhkgsjy());

            XSSFCell cell11 = row.getCell(10, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell11, yearlyPlan.getYjjcsjn());

            XSSFCell cell12 = row.getCell(11, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell12, yearlyPlan.getYjjcsjy());

            XSSFCell cell13 = row.getCell(12, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell13, yearlyPlan.getXmztzze());

            XSSFCell cell14 = row.getCell(13, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell14, yearlyPlan.getXmztzczzj());

            XSSFCell cell15 = row.getCell(14, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell15, yearlyPlan.getJzsndljwctzZe());

            XSSFCell cell16 = row.getCell(15, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell16, yearlyPlan.getJzsndljwctzQzczzj());

            XSSFCell cell17 = row.getCell(16, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell17, yearlyPlan.getBndtzjhNdjsnr());

            XSSFCell cell18 = row.getCell(17, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell18, yearlyPlan.getBndtzjhTzjhze());

            XSSFCell cell19 = row.getCell(18, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell19, yearlyPlan.getBndtzjhCzzjXj());

            XSSFCell cell20 = row.getCell(19, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell20, yearlyPlan.getBndtzjhCzzjZycz());

            XSSFCell cell21 = row.getCell(20, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell21, yearlyPlan.getBndtzjhCzzjScz());

            XSSFCell cell22 = row.getCell(21, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell22, yearlyPlan.getBndtzjhCzzjSczZe());

            XSSFCell cell23 = row.getCell(22, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell23, yearlyPlan.getBndtzjhCzzjSczTczj());

            XSSFCell cell24 = row.getCell(23, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell24, yearlyPlan.getBndtzjhCzzjSczGlzxzj());

            XSSFCell cell25 = row.getCell(24, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell25, yearlyPlan.getBndtzjhCzzjSczZfzw());

            XSSFCell cell26 = row.getCell(25, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell26, yearlyPlan.getBndtzjhCzzjQcz());

            XSSFCell cell27 = row.getCell(26, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell27, yearlyPlan.getBndtzjhShzj());

            XSSFCell cell28 = row.getCell(27, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell28, yearlyPlan.getYdtj());

            XSSFCell cell29 = row.getCell(28, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell29, yearlyPlan.getGhtj());

            XSSFCell cell30 = row.getCell(29, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell30, yearlyPlan.getZdtj());

            XSSFCell cell31 = row.getCell(30, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell31, yearlyPlan.getZjly());

            XSSFCell cell32 = row.getCell(31, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell32, yearlyPlan.getTbr());

            XSSFCell cell33 = row.getCell(32, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell33, yearlyPlan.getLxdh());

            XSSFCell cell34 = row.getCell(33, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell34, yearlyPlan.getBz());
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }

    /**
     * 获取汇总的《“十三五”时期市本级社会民生基础设施建设项目进度总表》的Excel二进制数据
     * @param countRate 统计周期
     * @return byte[]
     */
    public byte[] downloadScheduleSummary(String countRate)
            throws IOException, InvalidFormatException {
        List<JkwwPrjPlanSummeryStr> prjPlanSummeryStrs = prjDataSummaryService.queryPrjScheduleSummary(countRate);
        return createScheduleSummaryExcel(prjPlanSummeryStrs);
    }

    /**
     * 获取汇总的《“十三五”时期市本级社会民生基础设施建设项目进度表》的Excel二进制数据
     */
    public byte[] downloadSchedule(
            String className, String status, String mainDeptName,
            String ownerName, String projectName, String countRate, Boolean isKeyPrj)
            throws IOException, InvalidFormatException {
        List<JkwwProjectScheduleSummary> scheduleSummaries = prjDataSummaryService.queryScheduleSummaryByCondition(
                className, status, mainDeptName, ownerName,
                projectName, countRate, isKeyPrj, null, null);
        return createScheduleExcel(scheduleSummaries);
    }

    /**
     * 生成《“十三五”时期市本级社会民生基础设施建设项目进度总表》Excel表格
     * @param prjSummaryList 数据
     * @return byte[]
     */
    private byte[] createScheduleSummaryExcel(List<JkwwPrjPlanSummeryStr> prjSummaryList)
            throws IOException, InvalidFormatException {
        String templateFilePath = "/templates/jkww/ExportTemplates/“十三五”时期市本级社会民生基础设施建设项目进度总表.xlsx";
        InputStream in = getClass().getResourceAsStream(templateFilePath);
        File tmpFile = new File(System.getProperty("java.io.tmpdir") + File.separatorChar
                + UUID.randomUUID().toString() +".xlsx");
        FileUtils.copyInputStreamToFile(in, tmpFile);
        XSSFWorkbook workbook = new XSSFWorkbook(tmpFile);
        XSSFSheet sheet = workbook.getSheetAt(0);
        int startRowIndex = 3;

        for (int i = 0, size = prjSummaryList.size(); i < size; i++) {
            JkwwPrjPlanSummeryStr scheduleSummary = prjSummaryList.get(i);
            XSSFRow row = sheet.getRow(startRowIndex);
            if (row == null) {
                row = sheet.createRow(startRowIndex);
            }
            ++ startRowIndex;

            //TODO: 需要修改导出模板

            XSSFCell cell1 = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell1, scheduleSummary.getClassName());

            XSSFCell cell2 = row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell2, scheduleSummary.getZdsgzxfa());

            XSSFCell cell3 = row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell3, scheduleSummary.getZdqk());

            XSSFCell cell4 = row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell4, scheduleSummary.getQdjssgxkzqk());

            XSSFCell cell5 = row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell5, scheduleSummary.getYdzbqk());

            XSSFCell cell6 = row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell6, scheduleSummary.getYspfqk());

            XSSFCell cell7 = row.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell7, scheduleSummary.getZtbqk());

            XSSFCell cell8 = row.getCell(7, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell8, scheduleSummary.getDjdw());

            XSSFCell cell9 = row.getCell(8, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell9, scheduleSummary.getTjjdqk());

            XSSFCell cell10 = row.getCell(9, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell10, scheduleSummary.getNsgcqk());

            XSSFCell cell11 = row.getCell(10, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell11, scheduleSummary.getPtgchjgcqk());

            XSSFCell cell12 = row.getCell(11, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell12, scheduleSummary.getYsyjsyqk());

            XSSFCell cell13 = row.getCell(12, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell13, "详见分表");

        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }

    /**
     * 生成《“十三五”时期市本级社会民生基础设施建设项目进度表》Excel表格
     * @param prjSummaryList 数据
     * @return byte[]
     */
    private byte[] createScheduleExcel(List<JkwwProjectScheduleSummary> prjSummaryList)
            throws IOException, InvalidFormatException {
        String templateFilePath = "/templates/jkww/ExportTemplates/“十三五”时期市本级社会民生基础设施建设项目进度表.xlsx";
        InputStream in = getClass().getResourceAsStream(templateFilePath);
        File tmpFile = new File(System.getProperty("java.io.tmpdir") + File.separatorChar
                + UUID.randomUUID().toString() +".xlsx");
        FileUtils.copyInputStreamToFile(in, tmpFile);
        XSSFWorkbook workbook = new XSSFWorkbook(tmpFile);
        XSSFSheet sheet = workbook.getSheetAt(0);
        int startRowIndex = 3;

        for (int i = 0, size = prjSummaryList.size(); i < size; i++) {
            JkwwProjectScheduleSummary scheduleSummary = prjSummaryList.get(i);
            XSSFRow row = sheet.getRow(startRowIndex);
            if (row == null) {
                row = sheet.createRow(startRowIndex);
            }
            ++ startRowIndex;

            XSSFCell cell1 = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell1, scheduleSummary.getXmmc());

            XSSFCell cell2 = row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell2, scheduleSummary.getLdqgzSfzdsgzxfa());

            XSSFCell cell3 = row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell3, scheduleSummary.getLdqgzZdqkwcl());

            XSSFCell cell4 = row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell4, scheduleSummary.getLdqgzSfqdjssgxkz());

            XSSFCell cell5 = row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell5, scheduleSummary.getLdqgzYdzbqkwcl());

            XSSFCell cell6 = row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell6, scheduleSummary.getLdqgzYssfpf());

            XSSFCell cell7 = row.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell7, scheduleSummary.getSgjcqkZtbsfwc());

            XSSFCell cell8 = row.getCell(7, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell8, scheduleSummary.getSgjcqkSfcydj());

            XSSFCell cell9 = row.getCell(8, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell9, scheduleSummary.getSgjcqkJsdw());

            XSSFCell cell10 = row.getCell(9, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell10, scheduleSummary.getSgjcqkYzdw());

            XSSFCell cell11 = row.getCell(10, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell11, scheduleSummary.getSgjcqkTjjdbfl());

            XSSFCell cell12 = row.getCell(11, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell12, scheduleSummary.getSgjcqkNsgcjdbfl());

            XSSFCell cell13 = row.getCell(12, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell13, scheduleSummary.getPtgcHjgcjdbfl());

            XSSFCell cell14 = row.getCell(13, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell14, scheduleSummary.getSfysbyjsy());

            XSSFCell cell15 = row.getCell(14, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell15, scheduleSummary.getCzqtjdc());

            XSSFCell cell16 = row.getCell(15, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell16, scheduleSummary.getTbr());

            XSSFCell cell17 = row.getCell(16, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell17, scheduleSummary.getLxdh());

            XSSFCell cell18 = row.getCell(17, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell18, scheduleSummary.getBz());
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }

    /**
     * 获取汇总的《民生基础设施布局规划及其实施方案制定实施情况表》的Excel二级制数据
     * @param countRate 统计周期
     * @return byte[]
     */
    public byte[] downloadPlanAndImplSummary(String countRate)
            throws IOException, InvalidFormatException {
        List<JkwwProjectPlanAndImplement> prjPlanAndImplList =
                planAndImplementMapper.selectDeptData(null, countRate);
        return createPlanAndImplExcel(prjPlanAndImplList);
    }

    /**
     * 生成《民生基础设施布局规划及其实施方案制定实施情况表》Excel表格
     * @param prjPlanAndImplList 数据
     * @return byte[]
     */
    private byte[] createPlanAndImplExcel(List<JkwwProjectPlanAndImplement> prjPlanAndImplList)
            throws IOException, InvalidFormatException {
        String templateFilePath = "/templates/jkww/ExportTemplates/民生基础设施布局规划及其实施方案制定实施情况表.xlsx";
        InputStream in = getClass().getResourceAsStream(templateFilePath);
        File tmpFile = new File(System.getProperty("java.io.tmpdir") + File.separatorChar
                + UUID.randomUUID().toString() +".xlsx");
        FileUtils.copyInputStreamToFile(in, tmpFile);
        XSSFWorkbook workbook = new XSSFWorkbook(tmpFile);
        XSSFSheet sheet = workbook.getSheetAt(0);
        int startRowIndex = 3;

        for (int i = 0, size = prjPlanAndImplList.size(); i < size; i++) {
            JkwwProjectPlanAndImplement prjPlanAndImpl = prjPlanAndImplList.get(i);
            XSSFRow row = sheet.getRow(startRowIndex);
            if (row == null) {
                row = sheet.createRow(startRowIndex);
            }
            ++ startRowIndex;

            XSSFCell cell1 = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell1, prjPlanAndImpl.getGzrw());

            XSSFCell cell2 = row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell2, prjPlanAndImpl.getYjzdqk());

            XSSFCell cell3 = row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell3, prjPlanAndImpl.getPsqk());

            XSSFCell cell4 = row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell4, prjPlanAndImpl.getPfqk());

            XSSFCell cell5 = row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell5, prjPlanAndImpl.getGhzxmlxqk());

            XSSFCell cell6 = row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell6, prjPlanAndImpl.getGhssqk());

            XSSFCell cell7 = row.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell7, prjPlanAndImpl.getCzwtjdc());

            XSSFCell cell8 = row.getCell(7, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell8, prjPlanAndImpl.getTbr());

            XSSFCell cell9 = row.getCell(8, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell9, prjPlanAndImpl.getLxdh());

            XSSFCell cell10 = row.getCell(9, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell10, prjPlanAndImpl.getGzjd());
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }

    /**
     * 获取汇总的《解决历史遗留问题进度表》的Excel二级制数据
     * @param countRate 统计周期
     * @return byte[]
     */
    public byte[] downloadProblemScheduleSummary(String countRate)
            throws IOException, InvalidFormatException {
        List<JkwwProblemSchedule> problemSchedules =
                problemScheduleMapper.selectDeptData(null, countRate);
        return createProblemScheduleExcel(problemSchedules);
    }

    /**
     * 生成《解决历史遗留问题进度表》Excel表格
     * @param problemSchedules 数据
     * @return byte[]
     */
    private byte[] createProblemScheduleExcel(List<JkwwProblemSchedule> problemSchedules)
            throws IOException, InvalidFormatException {
        String templateFilePath = "/templates/jkww/ExportTemplates/解决历史遗留问题进度表.xlsx";
        InputStream in = getClass().getResourceAsStream(templateFilePath);
        File tmpFile = new File(System.getProperty("java.io.tmpdir") + File.separatorChar
                + UUID.randomUUID().toString() +".xlsx");
        FileUtils.copyInputStreamToFile(in, tmpFile);
        XSSFWorkbook workbook = new XSSFWorkbook(tmpFile);
        XSSFSheet sheet = workbook.getSheetAt(0);
        int startRowIndex = 2;

        for (int i = 0, size = problemSchedules.size(); i < size; i++) {
            JkwwProblemSchedule problemSchedule = problemSchedules.get(i);
            XSSFRow row = sheet.getRow(startRowIndex);
            if (row == null) {
                row = sheet.createRow(startRowIndex);
            }
            ++ startRowIndex;

            XSSFCell cell1 = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell1, problemSchedule.getLsylwt());

            XSSFCell cell2 = row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell2, problemSchedule.getFljltzqk());

            XSSFCell cell3 = row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell3, problemSchedule.getGzjzyxqk());

            XSSFCell cell4 = row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell4, problemSchedule.getZdfljjhxggzjhqk());

            XSSFCell cell5 = row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell5, problemSchedule.getCtxgzcqk());

            XSSFCell cell6 = row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell6, problemSchedule.getZxjzqk());

            XSSFCell cell7 = row.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell7, problemSchedule.getYjhsyqk());

            XSSFCell cell8 = row.getCell(7, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell8, problemSchedule.getCzwtjdc());

            XSSFCell cell9 = row.getCell(8, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell9, problemSchedule.getTbr());

            XSSFCell cell10 = row.getCell(9, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell10, problemSchedule.getLxdh());
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }

    /**
     * 获取汇总的《居住区配套设施建设移交历史遗留问题台账》的Excel二级制数据
     * @param deptName 统计的部门名称，为空则统计所有。
     * @param countRate 统计周期
     * @return byte[]
     */
    public byte[] downloadEduProblemScheduleSummary(String deptName, String countRate)
            throws IOException, InvalidFormatException {
        List<JkwwResidenceLedger> residenceLedgers =
                prjDataSummaryService.queryResidenceLedger(deptName, countRate);
        return createResidenceLedgerExcel(residenceLedgers);
    }

    /**
     * 生成《居住区配套设施建设移交历史遗留问题台账》Excel表格
     * @param residenceLedgers 数据
     * @return XSSFWorkbook
     */
    private byte[] createResidenceLedgerExcel(List<JkwwResidenceLedger> residenceLedgers)
            throws IOException, InvalidFormatException {
        String templateFilePath = "/templates/jkww/ExportTemplates/居住区配套设施建设移交历史遗留问题台账.xlsx";
        InputStream in = getClass().getResourceAsStream(templateFilePath);
        File tmpFile = new File(System.getProperty("java.io.tmpdir") + File.separatorChar
                + UUID.randomUUID().toString() +".xlsx");
        FileUtils.copyInputStreamToFile(in, tmpFile);
        XSSFWorkbook workbook = new XSSFWorkbook(tmpFile);
        XSSFSheet sheet = workbook.getSheetAt(0);
        int startRowIndex = 5;

        for (int i = 0, size = residenceLedgers.size(); i < size; i++) {
            JkwwResidenceLedger residenceLedger = residenceLedgers.get(i);
            XSSFRow row = sheet.getRow(startRowIndex);
            if (row == null) {
                row = sheet.createRow(startRowIndex);
            }
            ++ startRowIndex;

            XSSFCell cell1 = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell1, residenceLedger.getTypeName());

            XSSFCell cell2 = row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell2, residenceLedger.getTotal());

            XSSFCell cell3 = row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell3, residenceLedger.getYxq());

            XSSFCell cell4 = row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell4, residenceLedger.getHzq());

            XSSFCell cell5 = row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell5, residenceLedger.getLwq());

            XSSFCell cell6 = row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell6, residenceLedger.getThq());

            XSSFCell cell7 = row.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell7, residenceLedger.getByq());

            XSSFCell cell8 = row.getCell(7, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell8, residenceLedger.getHpq());

            XSSFCell cell9 = row.getCell(8, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell9, residenceLedger.getHdq());

            XSSFCell cell10 = row.getCell(9, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell10, residenceLedger.getPyq());

            XSSFCell cell11 = row.getCell(10, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell11, residenceLedger.getNsq());

            XSSFCell cell12 = row.getCell(11, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell12, residenceLedger.getChq());

            XSSFCell cell13 = row.getCell(12, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            CellValueUtils.setValue(cell13, residenceLedger.getZcq());
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }

}
