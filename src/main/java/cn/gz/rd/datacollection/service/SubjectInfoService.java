package cn.gz.rd.datacollection.service;

import cn.gz.rd.datacollection.dao.SubjectInfoMapper;
import cn.gz.rd.datacollection.model.FileDataVO;
import cn.gz.rd.datacollection.model.SubjectDeptInfoVO;
import cn.gz.rd.datacollection.model.SubjectInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

/**
 * 主题服务
 * @author Peng Xiaodong
 */
@Service
public class SubjectInfoService {

    private final Logger logger = LoggerFactory.getLogger(SubjectInfoService.class);

    @Autowired
    private DeptSubjectService deptSubjectService;

    @Autowired
    private SubjectInfoMapper subjectInfoMapper;

    @Value("${template.save.root.dir}")
    private String templateSaveRootDir;

    @Transactional(rollbackFor = Exception.class)
    public void saveSubject(SubjectInfo subjectInfo, MultipartFile templateFile)
            throws IOException {
        logger.debug("保存主题：参数=" + subjectInfo);
        String subjectCode = generateSubjectCode(subjectInfo.getCommitteeCode());
        subjectInfo.setSubjectCode(subjectCode);
        if (subjectInfo.getEnableFlag() == null) {
            subjectInfo.setEnableFlag(0);
        }
        if (templateFile != null) {
            String templateUploadPath = uploadFile(subjectInfo.getCommitteeName(), templateFile);
            subjectInfo.setTemplatePath(templateUploadPath);
        }
        subjectInfoMapper.insert(subjectInfo);

        String[] deptCodes = subjectInfo.getDeptCodes();
        deptSubjectService.saveAll(deptCodes, subjectCode);
    }

    private String uploadFile(String committeeName, MultipartFile templateFile)
            throws IOException {
        logger.debug("保存主题模板：上传的模板文件名称={}, 文件大小={}",
                templateFile.getOriginalFilename(), templateFile.getSize());
        String uploadFileName = templateFile.getOriginalFilename();
        String templateUploadPath = templateSaveRootDir + committeeName + File.separatorChar + uploadFileName;
        File dir = new File(templateSaveRootDir + committeeName + File.separatorChar);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        FileUtils.copyInputStreamToFile(templateFile.getInputStream(), new File(templateUploadPath));
        return templateUploadPath;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateSubject(SubjectInfo subjectInfo, MultipartFile templateFile)
            throws IOException {
        logger.debug("修改主题：参数=" + subjectInfo);
        String subjectCode = subjectInfo.getSubjectCode();
        SubjectInfo dbSubject = subjectInfoMapper.selectByPrimaryKey(subjectCode);
        String dbSubjectTemplatePath = dbSubject.getTemplatePath();

        String templateUploadPath;
        if (templateFile != null) {//如果上传文件，则替换；否则还是使用原来的模板文件。

            FileUtils.deleteQuietly(new File(dbSubjectTemplatePath));
            templateUploadPath = uploadFile(subjectInfo.getCommitteeName(), templateFile);

        } else {
            templateUploadPath = dbSubjectTemplatePath;
        }

        subjectInfo.setTemplatePath(templateUploadPath);
        subjectInfoMapper.updateByPrimaryKeySelective(subjectInfo);

        String[] deptCodes = subjectInfo.getDeptCodes();
        deptSubjectService.deleteAndSaveAll(deptCodes, subjectCode);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean disableSubject(String subjectCode) {
        logger.debug("禁用主题：参数subjectCode=" + subjectCode);

        int deleteCount = subjectInfoMapper.updateSubjectToDisable(subjectCode);
        if (deleteCount == 1) {
            return true;
        } else {
            logger.error("主题禁用失败");
            return false;
        }
    }

    public List<SubjectInfo> querySubject(
            String subjectCode, String subjectName, String deptCode,
            String committeeCode, Boolean isUsable,
            Integer pageNum, Integer pageSize) {
        if (StringUtils.isBlank(subjectCode)) {
            subjectCode = null;
        }
        if (StringUtils.isBlank(subjectName)) {
            subjectName = null;
        }
        if (StringUtils.isBlank(deptCode)) {
            deptCode = null;
        }
        if (StringUtils.isBlank(committeeCode)) {
            committeeCode = null;
        }
        if (subjectCode != null) {
            subjectCode = "%" + subjectCode + "%";
        }
        if (subjectName != null) {
            subjectName = "%" + subjectName + "%";
        }
        List<SubjectInfo> subjectInfos = subjectInfoMapper.selectSubject(
                subjectCode, subjectName, deptCode, committeeCode,
                isUsable, pageSize * (pageNum - 1), pageSize);
        logger.debug("查询到的主题个数=" + subjectInfos.size());
        setAllDeptInfo(subjectInfos);

        return subjectInfos;
    }

    public List<SubjectInfo> getVerifySubjectInfo(Integer pageNum, Integer pageSize) {
        List<SubjectInfo> subjectInfos = subjectInfoMapper.getVerifySubject(pageSize * (pageNum - 1), pageSize);
        logger.debug("查询到的主题个数=" + subjectInfos.size());
        setAllDeptInfo(subjectInfos);
        return subjectInfos;
    }

    public int countVerifySubjectInfo() {
        return subjectInfoMapper.countVerifySubject();
    }

    /**
     * 将每个主题的部门信息设置到主题信息中，每个主题可能存在多个部门与之关联。
     * @param subjectInfos 主题
     */
    private void setAllDeptInfo(List<SubjectInfo> subjectInfos) {
        List<String> subjectCodes = new ArrayList<>();
        for (SubjectInfo subjectInfo : subjectInfos) {
            subjectCodes.add(subjectInfo.getSubjectCode());
        }

        List<SubjectDeptInfoVO> subjectDeptInfoVOs =
                deptSubjectService.selectDeptInfoBySubjectCode(subjectCodes);
        Map<String, List<SubjectDeptInfoVO>> subjectDeptInfoVOMap = new HashMap<>();
        for (SubjectDeptInfoVO subjectDeptInfoVO : subjectDeptInfoVOs) {
            String subjectCodeKey = subjectDeptInfoVO.getSubjectCode();
            if (subjectDeptInfoVOMap.containsKey(subjectCodeKey)) {
                List<SubjectDeptInfoVO> subjectDeptInfoVOS =
                        subjectDeptInfoVOMap.get(subjectCodeKey);
                subjectDeptInfoVOS.add(subjectDeptInfoVO);

            } else {
                List<SubjectDeptInfoVO> subjectDeptInfoVOS = new ArrayList<>();
                subjectDeptInfoVOS.add(subjectDeptInfoVO);
                subjectDeptInfoVOMap.put(subjectCodeKey, subjectDeptInfoVOS);
            }
        }

        for (SubjectInfo subjectInfo : subjectInfos) {
            String subjectCodeKey = subjectInfo.getSubjectCode();
            List<SubjectDeptInfoVO> subjectDeptInfoVOS = subjectDeptInfoVOMap.get(subjectCodeKey);

            if (CollectionUtils.isNotEmpty(subjectDeptInfoVOS)) {
                int size = subjectDeptInfoVOS.size();
                String[] deptCodes = new String[size];
                String[] deptNames = new String[size];
                for (int i = 0; i < size; i++) {
                    SubjectDeptInfoVO deptInfoVO = subjectDeptInfoVOS.get(i);
                    deptCodes[i] = deptInfoVO.getDeptCode();
                    deptNames[i] = deptInfoVO.getDeptName();
                }
                subjectInfo.setDeptCodes(deptCodes);
                subjectInfo.setDeptNames(deptNames);
            }

        }
    }

    public int countSubject(
            String subjectCode, String subjectName, String deptCode,
            String committeeCode, Boolean isUsable) {
        if (StringUtils.isBlank(subjectCode)) {
            subjectCode = null;
        }
        if (StringUtils.isBlank(subjectName)) {
            subjectName = null;
        }
        if (StringUtils.isBlank(deptCode)) {
            deptCode = null;
        }
        if (StringUtils.isBlank(committeeCode)) {
            committeeCode = null;
        }
        if (subjectCode != null) {
            subjectCode = "%" + subjectCode + "%";
        }
        if (subjectName != null) {
            subjectName = "%" + subjectName + "%";
        }
        int countSubject = subjectInfoMapper.countSubject(
                subjectCode, subjectName, deptCode, committeeCode, isUsable);
        logger.debug("主题总个数=" + countSubject);
        return countSubject;
    }

    /**
     * 生成主题编号
     * @param committeeCode 工委编号
     * @return 自增的主题编号
     */
    public String generateSubjectCode(String committeeCode) {

        String subjectCode = "ZTBM_";
        if (StringUtils.equals(committeeCode, "GWBM_01")) {
            subjectCode += "CXJW_";
        } else if (StringUtils.equals(committeeCode, "GWBM_02")) {
            subjectCode += "NCNYW_";
        } else if (StringUtils.equals(committeeCode, "GWBM_03")) {
            subjectCode += "JKWW_";
        } else if (StringUtils.equals(committeeCode, "GWBM_04")) {
            subjectCode += "HQWSW_";
        } else if (StringUtils.equals(committeeCode, "GWBM_05")) {
            subjectCode += "YSW_";
        } else if (StringUtils.equals(committeeCode, "GWBM_06")) {
            subjectCode += "JJW_";
        } else if (StringUtils.equals(committeeCode, "GWBM_07")) {
            subjectCode += "NSW_";
        } else {
            subjectCode += committeeCode + "_";
        }

        Integer maxNum = subjectInfoMapper.selectMaxNum(subjectCode + "%");
        if (maxNum == null) {
            maxNum = 1;
        } else {
            ++ maxNum;
        }
        String format = String.format("%04d", maxNum);
        return subjectCode + format;
    }

    public FileDataVO downloadTemplate(String subjectCode) throws IOException {
        SubjectInfo subjectInfo = subjectInfoMapper.selectByPrimaryKey(subjectCode);
        String templatePath = subjectInfo.getTemplatePath();
        FileInputStream fileInputStream = new FileInputStream(templatePath);
        byte[] fileData = new byte[fileInputStream.available()];
        fileInputStream.read(fileData);
        fileInputStream.close();
        FileDataVO fileDataVO = new FileDataVO();
        fileDataVO.setFileData(fileData);
        fileDataVO.setFileName(FilenameUtils.getName(templatePath));
        return fileDataVO;
    }

    @Transactional(rollbackFor = Exception.class)
    public int updateUsableFlag(List<String> subjectCodes, boolean isUsable) {
        int usableFlag;
        if (isUsable) {
            usableFlag = 1;
        } else {
            usableFlag = 0;
        }
        return subjectInfoMapper.updateUsableFlag(subjectCodes, usableFlag);
    }

    public SubjectInfo selectBySubjectCode(String subjectCode) {
        return subjectInfoMapper.selectByPrimaryKey(subjectCode);
    }

    public List<SubjectInfo> getParentSubject(String committeeCode){
        return subjectInfoMapper.selectParentSubject(committeeCode);
    }

}
