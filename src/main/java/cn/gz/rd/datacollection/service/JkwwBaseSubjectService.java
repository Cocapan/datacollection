package cn.gz.rd.datacollection.service;

import cn.gz.rd.datacollection.dao.*;
import cn.gz.rd.datacollection.model.*;
import cn.gz.rd.datacollection.utils.CellValueUtils;
import cn.gz.rd.datacollection.utils.ExcelUtils;
import cn.gz.rd.datacollection.utils.PoiUtils;
import com.jcraft.jsch.SftpException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 基础主题服务类
 * @author Peng Xiaodong
 */
@Service
public class JkwwBaseSubjectService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JkwwCremainsProblemBaseInfoMapper cremainsMapper;

    @Autowired
    private JkwwEduProblemBaseInfoMapper eduMapper;

    @Autowired
    private JkwwOldageProblemBaseInfoMapper oldageMapper;

    @Autowired
    private JkwwResidenceBaseInfoMapper residenceMapper;

    @Autowired
    private JkwwProjectInfoMapper prjInfoMapper;

    @Autowired
    private JkwwPlanAndImplBaseInfoMapper planAndImplBaseInfoMapper;

    @Autowired
    private JkwwBaseSujbectMapper baseSujbectMapper;

    @Autowired
    private SFTPService sftpService;

    @Autowired
    private DeptSubjectInfoService deptSubjectInfoService;

    @Value("${uploadfile.jkww.baseinfo.dir}")
    private String jkwwBaseInfoSaveDir;

    @Transactional(rollbackFor = Exception.class)
    public void uploadFile(
            String userName, String previewPath,
            Integer subjectNo, MultipartFile file) throws IOException {

        if (file == null || file.isEmpty() || file.getSize() == 0) {
            logger.info("上传的文件为空！");
            throw new IllegalArgumentException("上传文件不能为空！");
        }

        if (StringUtils.isBlank(previewPath)) {
            throw new IllegalArgumentException("预览路径不能为空！");
        }

        if (subjectNo == null) {
            throw new IllegalArgumentException("主题序号不能为空！");
        }

        JkwwBaseSujbect baseSujbect = baseSujbectMapper.selectByPrimaryKey(subjectNo);
        if (baseSujbect == null) {
            throw new IllegalArgumentException("主题不存在，主题编号：" + subjectNo);
        }

        InputStream inputStream = file.getInputStream();
        Workbook workbook = ExcelUtils.getWorkBookByFileName(
                file.getOriginalFilename(), inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        if (sheet == null) {
            throw new IllegalArgumentException("上传的Excel文件不存在工作表！");
        }

        if (subjectNo == 1) {
            List<JkwwProjectInfo> projectInfos = parsePrjBaseInfo(sheet);
            prjInfoMapper.deleteAll();
            for (JkwwProjectInfo prjInfo: projectInfos) {
                prjInfoMapper.insert(prjInfo);
            }

        } else if (subjectNo == 2) {
            List<JkwwCremainsProblemBaseInfo> cremainsInfos = parseCremainsBaseInfo(sheet);
            cremainsMapper.deleteAll();
            for (JkwwCremainsProblemBaseInfo cremainsInfo : cremainsInfos) {
                cremainsMapper.insert(cremainsInfo);
            }

        } else if (subjectNo == 3) {
            List<JkwwOldageProblemBaseInfo> oldageInfos = parseOldageBaseInfo(sheet);
            oldageMapper.deleteAll();
            for (JkwwOldageProblemBaseInfo oldageInfo : oldageInfos) {
                oldageMapper.insert(oldageInfo);
            }

        } else if (subjectNo == 4) {
            List<JkwwResidenceBaseInfo> residenceInfos = parseResidenceBaseInfo(sheet);
            residenceMapper.deleteAll();
            for (JkwwResidenceBaseInfo residenceInfo : residenceInfos) {
                residenceMapper.insert(residenceInfo);
            }

        } else if (subjectNo == 5) {
            List<JkwwEduProblemBaseInfo> eduBaseInfos = parseEduBaseInfo(sheet);
            eduMapper.deleteAll();
            for (JkwwEduProblemBaseInfo eduBaseInfo : eduBaseInfos) {
                eduMapper.insert(eduBaseInfo);
            }
        } else if (subjectNo == 6) {
            List<JkwwPlanAndImplBaseInfo> planAndImplBaseInfos = parsePlanAndImplBaseInfo(sheet);
            planAndImplBaseInfoMapper.deleteAll();
            for (JkwwPlanAndImplBaseInfo planAndImplBaseInfo : planAndImplBaseInfos) {
                planAndImplBaseInfoMapper.insert(planAndImplBaseInfo);
            }
        } else if (subjectNo == 7) {

        } else {
            throw new IllegalArgumentException("无法识别的基础主题编号：" + subjectNo);
        }

        SFTPClient sftpClient = null;
        String savePath;
        try {
            sftpClient = sftpService.createClientAndLgoin();
            savePath = sftpService.uploadNewFile(
                    sftpClient, jkwwBaseInfoSaveDir,
                    file.getOriginalFilename(), file.getInputStream());
        } finally {
            sftpService.close(sftpClient);
        }

        baseSujbect.setYllj(previewPath);
        baseSujbect.setCclj(savePath);
        baseSujbect.setGxr(userName);

        baseSujbectMapper.updateByPrimaryKeySelective(baseSujbect);

        DeptSubjectOperateInfo operateInfo = new DeptSubjectOperateInfo();
        operateInfo.setZtztid(subjectNo);
        operateInfo.setCzlx("更新基础数据");
        operateInfo.setCzsj(new Date());
        operateInfo.setSjcclj(savePath);
        operateInfo.setCzyhbh(userName);
        operateInfo.setWjmc(file.getOriginalFilename());
        deptSubjectInfoService.saveDeptSubjectOperateInfo(operateInfo);
    }

    public File download(Integer subjectNo) {
        JkwwBaseSujbect baseSujbect = baseSujbectMapper.selectByPrimaryKey(subjectNo);
        if (baseSujbect == null) {
            throw new IllegalArgumentException("主题序号不存在：" + subjectNo);
        }

        String savePath = baseSujbect.getCclj();

        SFTPClient sftpClient = null;
        File file = null;
        try {
            sftpClient = sftpService.createClientAndLgoin();
            String saveDir = FilenameUtils.getFullPath(savePath);
            String saveFileName = FilenameUtils.getName(savePath);
            byte[] bytes = sftpClient.download(saveDir, saveFileName);
            if (bytes != null) {
                file = new File(System.getProperty("java.io.tmpdir") + saveFileName);
                FileUtils.writeByteArrayToFile(file, bytes);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        } finally {
            sftpService.close(sftpClient);
        }
        return file;
    }

    public String preview(MultipartFile uploadFile) {
        String uploadPath;
        String fileName = uploadFile.getOriginalFilename();
        SFTPClient sftpClient = null;
        try {
            Workbook workbook = ExcelUtils.getWorkBookByFileName(fileName, uploadFile.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            String htmlText = PoiUtils.getExcelInfo(sheet, true);
            File htmlFile = new File(
                    System.getProperty("java.io.tmpdir") + System.currentTimeMillis() + ".html");
            FileUtils.writeStringToFile(htmlFile, htmlText);

            sftpClient = sftpService.createClientAndLgoin();
            String saveDir = File.separatorChar + "jkww_base_info" + File.separatorChar;
            sftpService.uploadHisFile(sftpClient, saveDir,
                    htmlFile.getName(), new FileInputStream(htmlFile));
            uploadPath = File.separatorChar + "history" + saveDir + htmlFile.getName();
        } catch (Exception e) {
            logger.error("", e);
            throw new RuntimeException("预览出现问题！错误信息：" + e.getMessage(), e);
        } finally {
            sftpService.close(sftpClient);
        }
        return uploadPath;
    }

    public List<JkwwBaseSujbect> queryAll() {
        List<JkwwBaseSujbect> sujbects = baseSujbectMapper.selectAll();
        return sujbects;
    }

    private List<JkwwProjectInfo> parsePrjBaseInfo(Sheet sheet) {

        List<JkwwProjectInfo> projectInfoList = new ArrayList<>();

        int firstDataRow = 6;
        int lastDataRow = sheet.getLastRowNum();

        for (int i = firstDataRow; i <= lastDataRow; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }

            Cell cell1 = row.getCell(0);
            if (CellValueUtils.isEmpty(cell1)) {
                break;//如果项目序号字段为空，则不解析后面的数据了。
            }
            Cell cell2 = row.getCell(1);
            Cell cell3 = row.getCell(2);
            Cell cell4 = row.getCell(3);
            Cell cell5 = row.getCell(4);
            Cell cell6 = row.getCell(5);
            Cell cell7 = row.getCell(6);
            Cell cell8 = row.getCell(7);
            Cell cell9 = row.getCell(8);
            Cell cell10 = row.getCell(9);
            Cell cell11 = row.getCell(10);
            Cell cell12 = row.getCell(11);
            Cell cell13 = row.getCell(12);
            Cell cell14 = row.getCell(13);
            Cell cell15 = row.getCell(14);
            Cell cell16 = row.getCell(15);
            Cell cell17 = row.getCell(16);
            Cell cell18 = row.getCell(17);
            Cell cell19 = row.getCell(18);

            JkwwProjectInfo projectInfo = new JkwwProjectInfo();
            projectInfo.setXmxh(CellValueUtils.getInt(cell1));
            projectInfo.setXmbm(CellValueUtils.getString(cell2));
            projectInfo.setSfwzdxm(CellValueUtils.getString(cell3));
            projectInfo.setZdxmxh(CellValueUtils.getInt(cell4));
            projectInfo.setSfwgcbzxm(CellValueUtils.getString(cell5));
            projectInfo.setGcbzxmxh(CellValueUtils.getInt(cell6));
            projectInfo.setFl(CellValueUtils.getString(cell7));
            projectInfo.setZt(CellValueUtils.getString(cell8));
            projectInfo.setSsq(CellValueUtils.getString(cell9));
            projectInfo.setXmmc(CellValueUtils.getString(cell10));
            projectInfo.setZgbm(CellValueUtils.getString(cell11));
            projectInfo.setXmyz(CellValueUtils.getString(cell12));
            projectInfo.setXmgmhjsnr(CellValueUtils.getString(cell13));
            projectInfo.setJhkgsjn(CellValueUtils.getInt(cell14));
            projectInfo.setJhkgsjy(CellValueUtils.getInt(cell15));
            projectInfo.setYjjcsjn(CellValueUtils.getInt(cell16));
            projectInfo.setYjjcsjy(CellValueUtils.getInt(cell17));
            projectInfo.setXmztzze(CellValueUtils.getDouble(cell18));
            projectInfo.setXmztzczzj(CellValueUtils.getDouble(cell19));

            projectInfoList.add(projectInfo);
        }
        return projectInfoList;
    }

    private List<JkwwCremainsProblemBaseInfo> parseCremainsBaseInfo(Sheet sheet) {

        List<JkwwCremainsProblemBaseInfo> projectInfoList = new ArrayList<>();

        int firstDataRow = 5;
        int lastDataRow = sheet.getLastRowNum();

        for (int i = firstDataRow; i <= lastDataRow; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }

            Cell cell1 = row.getCell(0);
            if (CellValueUtils.isEmpty(cell1)) {
                break;//如果项目序号字段为空，则不解析后面的数据了。
            }
            Cell cell2 = row.getCell(1);
            Cell cell3 = row.getCell(2);
            Cell cell4 = row.getCell(3);
            Cell cell5 = row.getCell(4);
            Cell cell6 = row.getCell(5);
            Cell cell7 = row.getCell(6);
            Cell cell8 = row.getCell(7);
            Cell cell9 = row.getCell(8);
            Cell cell10 = row.getCell(9);
            Cell cell11 = row.getCell(10);
            Cell cell12 = row.getCell(11);
            Cell cell13 = row.getCell(12);
            Cell cell14 = row.getCell(13);
            Cell cell15 = row.getCell(14);
            Cell cell16 = row.getCell(15);
            Cell cell17 = row.getCell(16);
            Cell cell18 = row.getCell(17);
            Cell cell19 = row.getCell(18);
            Cell cell20 = row.getCell(19);
            Cell cell21 = row.getCell(20);
            Cell cell22 = row.getCell(21);
            Cell cell23 = row.getCell(22);
            Cell cell24 = row.getCell(23);
            Cell cell25 = row.getCell(24);
            Cell cell26 = row.getCell(25);
            Cell cell27 = row.getCell(26);
            Cell cell28 = row.getCell(27);
            Cell cell29 = row.getCell(28);
            Cell cell30 = row.getCell(29);
            Cell cell31 = row.getCell(30);
            Cell cell32 = row.getCell(31);

            JkwwCremainsProblemBaseInfo cremainsBaseInfo = new JkwwCremainsProblemBaseInfo();
            cremainsBaseInfo.setXmbh(CellValueUtils.getInt(cell1));
            cremainsBaseInfo.setQ(CellValueUtils.getString(cell2));
            cremainsBaseInfo.setjZ(CellValueUtils.getString(cell3));
            cremainsBaseInfo.setcSq(CellValueUtils.getString(cell4));
            cremainsBaseInfo.setJgmc(CellValueUtils.getString(cell5));
            cremainsBaseInfo.setJb(CellValueUtils.getString(cell6));
            cremainsBaseInfo.setDwxz(CellValueUtils.getString(cell7));
            cremainsBaseInfo.setZjlyqd(CellValueUtils.getString(cell8));
            cremainsBaseInfo.setClsj(CellValueUtils.getString(cell9));
            cremainsBaseInfo.setTrsysj(CellValueUtils.getString(cell10));
            cremainsBaseInfo.setGhmj(CellValueUtils.getString(cell11));
            cremainsBaseInfo.setZdmj(CellValueUtils.getString(cell12));
            cremainsBaseInfo.setZjzmj(CellValueUtils.getString(cell13));
            cremainsBaseInfo.setGhghgwMw(CellValueUtils.getString(cell14));
            cremainsBaseInfo.setYsjghgwMw(CellValueUtils.getString(cell15));
            cremainsBaseInfo.setYsyghgwMw(CellValueUtils.getString(cell16));
            cremainsBaseInfo.setYcfgh(CellValueUtils.getString(cell17));
            cremainsBaseInfo.setSyghgw(CellValueUtils.getString(cell18));
            cremainsBaseInfo.setPzwjhGt(CellValueUtils.getString(cell19));
            cremainsBaseInfo.setPzwjhGh(CellValueUtils.getString(cell20));
            cremainsBaseInfo.setPzwjhMz(CellValueUtils.getString(cell21));
            cremainsBaseInfo.setFwjcsj(CellValueUtils.getString(cell22));
            cremainsBaseInfo.setFwyfczZyfc(CellValueUtils.getString(cell23));
            cremainsBaseInfo.setFwyfczZlfc(CellValueUtils.getString(cell24));
            cremainsBaseInfo.setFwwfczYJsydghxkzJsgcghxkzXcghxkz(CellValueUtils.getString(cell25));
            cremainsBaseInfo.setFwwfczWJsydghxkzJsgcghxkzXcghxkz(CellValueUtils.getString(cell26));
            cremainsBaseInfo.setXfyshghbafhyq(CellValueUtils.getString(cell27));
            cremainsBaseInfo.setXfyshbabfhyq(CellValueUtils.getString(cell28));
            cremainsBaseInfo.setSfblhjyxbgbhdjb(CellValueUtils.getString(cell29));
            cremainsBaseInfo.setSfblfwjzgcjgysba(CellValueUtils.getString(cell30));
            cremainsBaseInfo.setSfblfwjzgcjgyshgzm(CellValueUtils.getString(cell31));
            cremainsBaseInfo.setYwjgyshezmhbass(CellValueUtils.getString(cell32));

            projectInfoList.add(cremainsBaseInfo);
        }
        return projectInfoList;
    }

    private List<JkwwEduProblemBaseInfo> parseEduBaseInfo(Sheet sheet) {

        List<JkwwEduProblemBaseInfo> projectInfoList = new ArrayList<>();

        int firstDataRow = 5;
        int lastDataRow = sheet.getLastRowNum();

        for (int i = firstDataRow; i <= lastDataRow; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }

            Cell cell1 = row.getCell(0);
            if (CellValueUtils.isEmpty(cell1)) {
                break;//如果项目序号字段为空，则不解析后面的数据了。
            }
            Cell cell2 = row.getCell(1);
            Cell cell3 = row.getCell(2);
            Cell cell4 = row.getCell(3);
            Cell cell5 = row.getCell(4);
            Cell cell6 = row.getCell(5);
            Cell cell7 = row.getCell(6);
            Cell cell8 = row.getCell(7);
            Cell cell9 = row.getCell(8);
            Cell cell10 = row.getCell(9);
            Cell cell11 = row.getCell(10);
            Cell cell12 = row.getCell(11);
            Cell cell13 = row.getCell(12);
            Cell cell14 = row.getCell(13);
            Cell cell15 = row.getCell(14);
            Cell cell16 = row.getCell(15);
            Cell cell17 = row.getCell(16);
            Cell cell18 = row.getCell(17);
            Cell cell19 = row.getCell(18);
            Cell cell20 = row.getCell(19);
            Cell cell21 = row.getCell(20);
            Cell cell22 = row.getCell(21);
            Cell cell23 = row.getCell(22);
            Cell cell24 = row.getCell(23);
            Cell cell25 = row.getCell(24);
            Cell cell26 = row.getCell(25);
            Cell cell27 = row.getCell(26);
            Cell cell28 = row.getCell(27);
            Cell cell29 = row.getCell(28);
            Cell cell30 = row.getCell(29);
            Cell cell31 = row.getCell(30);
            Cell cell32 = row.getCell(31);
            Cell cell33 = row.getCell(32);

            JkwwEduProblemBaseInfo eduBaseInfo = new JkwwEduProblemBaseInfo();
            eduBaseInfo.setXmbh(CellValueUtils.getInt(cell1));
            eduBaseInfo.setXxmc(CellValueUtils.getString(cell2));
            eduBaseInfo.setXmmc(CellValueUtils.getString(cell3));
            eduBaseInfo.setSsq(CellValueUtils.getString(cell4));
            eduBaseInfo.setDz(CellValueUtils.getString(cell5));
            eduBaseInfo.setYdlypw(CellValueUtils.getString(cell6));
            eduBaseInfo.setXjxxxgh(CellValueUtils.getString(cell7));
            eduBaseInfo.setJsydghxkz(CellValueUtils.getString(cell8));
            eduBaseInfo.setJsydpzs(CellValueUtils.getString(cell9));
            eduBaseInfo.setTysytzs(CellValueUtils.getString(cell10));
            eduBaseInfo.setTddjqkAh(CellValueUtils.getString(cell11));
            eduBaseInfo.setTddjqkCqr(CellValueUtils.getString(cell12));
            eduBaseInfo.setTddjqkYdmj(CellValueUtils.getString(cell13));
            eduBaseInfo.setGhbjwh(CellValueUtils.getString(cell14));
            eduBaseInfo.setZjzmj(CellValueUtils.getString(cell15));
            eduBaseInfo.setGhyshgzwh(CellValueUtils.getString(cell16));
            eduBaseInfo.setWfjzjgclAh(CellValueUtils.getString(cell17));
            eduBaseInfo.setWjjzmj(CellValueUtils.getString(cell18));
            eduBaseInfo.setYdchAh(CellValueUtils.getString(cell19));
            eduBaseInfo.setFwchAh(CellValueUtils.getString(cell20));
            eduBaseInfo.setFwchJjmj(CellValueUtils.getString(cell21));
            eduBaseInfo.setFwchJzmj(CellValueUtils.getString(cell22));
            eduBaseInfo.setDjzg(CellValueUtils.getString(cell23));
            eduBaseInfo.setCqr(CellValueUtils.getString(cell24));
            eduBaseInfo.setChqk(CellValueUtils.getString(cell25));
            eduBaseInfo.setXxfl(CellValueUtils.getString(cell26));
            eduBaseInfo.setLxr(CellValueUtils.getString(cell27));
            eduBaseInfo.setYdzl(CellValueUtils.getString(cell28));
            eduBaseInfo.setGtz(CellValueUtils.getString(cell29));
            eduBaseInfo.setBj(CellValueUtils.getString(cell30));
            eduBaseInfo.setYs(CellValueUtils.getString(cell31));
            eduBaseInfo.setSfyjssjsscqk(CellValueUtils.getString(cell32));
            eduBaseInfo.setHcqk(CellValueUtils.getString(cell33));

            projectInfoList.add(eduBaseInfo);
        }
        return projectInfoList;
    }

    private List<JkwwOldageProblemBaseInfo> parseOldageBaseInfo(Sheet sheet) {

        List<JkwwOldageProblemBaseInfo> projectInfoList = new ArrayList<>();

        int firstDataRow = 3;
        int lastDataRow = sheet.getLastRowNum();

        for (int i = firstDataRow; i <= lastDataRow; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }

            Cell cell1 = row.getCell(0);
            if (CellValueUtils.isEmpty(cell1)) {
                break;//如果项目序号字段为空，则不解析后面的数据了。
            }
            Cell cell2 = row.getCell(1);
            Cell cell3 = row.getCell(2);
            Cell cell4 = row.getCell(3);
            Cell cell5 = row.getCell(4);
            Cell cell6 = row.getCell(5);
            Cell cell7 = row.getCell(6);
            Cell cell8 = row.getCell(7);

            JkwwOldageProblemBaseInfo oldageBaseInfo = new JkwwOldageProblemBaseInfo();
            oldageBaseInfo.setXmbh(CellValueUtils.getInt(cell1));
            oldageBaseInfo.setQ(CellValueUtils.getString(cell2));
            oldageBaseInfo.setJgmc(CellValueUtils.getString(cell3));
            oldageBaseInfo.setCws(CellValueUtils.getString(cell4));
            oldageBaseInfo.setClsj(CellValueUtils.getString(cell5));
            oldageBaseInfo.setZjzmj(CellValueUtils.getString(cell6));
            oldageBaseInfo.setCzwt(CellValueUtils.getString(cell7));
            oldageBaseInfo.setGzjh(CellValueUtils.getString(cell8));

            projectInfoList.add(oldageBaseInfo);
        }
        return projectInfoList;
    }

    private List<JkwwResidenceBaseInfo> parseResidenceBaseInfo(Sheet sheet) {

        List<JkwwResidenceBaseInfo> projectInfoList = new ArrayList<>();

        int firstDataRow = 3;
        int lastDataRow = sheet.getLastRowNum();

        for (int i = firstDataRow; i <= lastDataRow; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }

            Cell cell1 = row.getCell(0);
            if (CellValueUtils.isEmpty(cell1)) {
                break;//如果项目序号字段为空，则不解析后面的数据了。
            }
            Cell cell2 = row.getCell(1);
            Cell cell3 = row.getCell(2);
            Cell cell4 = row.getCell(3);
            Cell cell5 = row.getCell(4);
            Cell cell6 = row.getCell(5);
            Cell cell7 = row.getCell(6);
            Cell cell8 = row.getCell(7);
            Cell cell9 = row.getCell(8);
            Cell cell10 = row.getCell(9);
            Cell cell11 = row.getCell(10);

            JkwwResidenceBaseInfo residenceBaseInfo = new JkwwResidenceBaseInfo();
            residenceBaseInfo.setXmbh(CellValueUtils.getInt(cell1));
            residenceBaseInfo.setZgbm(CellValueUtils.getString(cell2));
            residenceBaseInfo.setXqmc(CellValueUtils.getString(cell3));
            residenceBaseInfo.setSsq(CellValueUtils.getString(cell4));
            residenceBaseInfo.setDz(CellValueUtils.getString(cell5));
            residenceBaseInfo.setKfqy(CellValueUtils.getString(cell6));
            residenceBaseInfo.setSsmc(CellValueUtils.getString(cell7));
            residenceBaseInfo.setXz(CellValueUtils.getString(cell8));
            residenceBaseInfo.setCzwt(CellValueUtils.getString(cell9));
            residenceBaseInfo.setLx(CellValueUtils.getString(cell10));
            residenceBaseInfo.setJjslhjhwcsj(CellValueUtils.getString(cell11));
            projectInfoList.add(residenceBaseInfo);
        }
        return projectInfoList;
    }

    private List<JkwwPlanAndImplBaseInfo> parsePlanAndImplBaseInfo(Sheet sheet) {

        List<JkwwPlanAndImplBaseInfo> projectInfoList = new ArrayList<>();

        int firstDataRow = 3;
        int lastDataRow = sheet.getLastRowNum();

        for (int i = firstDataRow; i <= lastDataRow; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }

            Cell cell1 = row.getCell(0);
            if (CellValueUtils.isEmpty(cell1)) {
                break;//如果项目序号字段为空，则不解析后面的数据了。
            }
            Cell cell2 = row.getCell(1);
            Cell cell3 = row.getCell(2);
            Cell cell4 = row.getCell(3);
            Cell cell5 = row.getCell(4);
            Cell cell6 = row.getCell(5);
            Cell cell7 = row.getCell(6);

            JkwwPlanAndImplBaseInfo planAndImplBaseInfo = new JkwwPlanAndImplBaseInfo();
            planAndImplBaseInfo.setXh(CellValueUtils.getInt(cell1));
            planAndImplBaseInfo.setGzrw(CellValueUtils.getString(cell2));
            planAndImplBaseInfo.setJhctsj(CellValueUtils.getString(cell3));
            planAndImplBaseInfo.setLy(CellValueUtils.getString(cell4));
            planAndImplBaseInfo.setGzjd(CellValueUtils.getString(cell5));
            planAndImplBaseInfo.setWcqk(CellValueUtils.getString(cell6));
            planAndImplBaseInfo.setBz(CellValueUtils.getString(cell7));

            projectInfoList.add(planAndImplBaseInfo);
        }
        return projectInfoList;
    }

    public String getPhoneBookPreviewUrl(Integer subjectNo){
        JkwwBaseSujbect jkwwBaseSujbect = baseSujbectMapper.selectByPrimaryKey(subjectNo);
        return jkwwBaseSujbect.getYllj();
    }

}
