package cn.gz.rd.datacollection.service;

import cn.gz.rd.datacollection.dao.*;
import cn.gz.rd.datacollection.model.*;
import cn.gz.rd.datacollection.utils.CountQuarterUtils;
import com.jcraft.jsch.SftpException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class JkwwwSubjectService {

    private static final Logger logger = LoggerFactory.getLogger(JkwwwSubjectService.class);

    @Autowired
    JkwwPrjSummaryDbMapper prjSummaryDbMapper;

    @Autowired
    private JkwwwSubjectMapper ztMapper;

    @Autowired
    private DeptSubjectInfoService deptSubjectInfoService;

    @Autowired
    private DeptSubjectInfoMapper deptSubjectInfoMapper;

    @Autowired
    private JkwwCremainsProblemScheduleMapper cremainsProblemScheduleMapper;

    @Autowired
    private JkwwEduProblemScheduleMapper eduProblemScheduleMapper;

    @Autowired
    private JkwwExeConditionMapper exeConditionMapper;

    @Autowired
    private JkwwOldageProblemScheduleMapper oldageProblemScheduleMapper;

    @Autowired
    private JkwwProblemScheduleMapper problemScheduleMapper;

    @Autowired
    private JkwwProjectPlanAndImplementMapper projectPlanAndImplementMapper;

    @Autowired
    private JkwwProjectScheduleSummaryMapper projectScheduleSummaryMapper;

    @Autowired
    private JkwwResidenceProblemScheduleMapper residenceProblemScheduleMapper;

    @Autowired
    private JkwwYearlyPlanMapper yearlyPlanMapper;

    @Autowired
    private JkwwPrjDataSummaryService dataSummaryService;

    @Autowired
    private JkwwPrjImgInfoMapper prjImgInfoMapper;

    @Autowired
    private SMSService smsService;

    @Value("${jkww.jkwwStaffPhone}")
    private String jkwwStaffPhone;

    @Autowired
    private SFTPService sftpService;

    @Autowired
    private SubjectInfoMapper subjectInfoMapper;

    @Autowired
    private CommonExcelConfMapper commonExcelConfMapper;

    public int countSubjectInfo(
            String deptCode, String countRate, List<Integer> statusCode,
            Boolean isUpload, String uploadRate) {
        return ztMapper.countSubjectInfo(deptCode, countRate, statusCode, isUpload, uploadRate);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteImgInfo(Integer imgInfoId, String userCode, Integer subjectStatusId) {
        JkwwPrjImgInfo imgInfo = prjImgInfoMapper.selectByPrimaryKey(imgInfoId);
        Integer statusId = imgInfo.getProjectStatusId();
        String imgNewSavePath = imgInfo.getImgNewSavePath();

        SFTPClient sftpClient = null;
        try {
            sftpClient = sftpService.createClientAndLgoin();
            String dirPath = FilenameUtils.getFullPathNoEndSeparator(imgNewSavePath);
            String fileName = FilenameUtils.getName(imgNewSavePath);
            sftpClient.delete(dirPath, fileName);
        } finally {
            sftpService.close(sftpClient);
        }

        prjImgInfoMapper.deleteByPrimaryKey(imgInfoId);
        DeptSubjectOperateInfo operateInfo = new DeptSubjectOperateInfo();
        operateInfo.setZtztid(statusId);
        operateInfo.setCzlx("删除图片");
        operateInfo.setCzsj(new Date());
        operateInfo.setSjcclj(imgNewSavePath);
        operateInfo.setCzyhbh(userCode);
        operateInfo.setWjmc(imgInfo.getImgFileName());
        deptSubjectInfoService.saveDeptSubjectOperateInfo(operateInfo);
        List<JkwwPrjImgInfo> jkwwPrjImgInfos = queryPrjImgInfos(subjectStatusId);
        if (jkwwPrjImgInfos == null || jkwwPrjImgInfos.size() == 0){
            deptSubjectInfoMapper.deleteByPrimaryKey(subjectStatusId);
        }
    }

    /**
     * 获取主题信息
     * @param deptCode 部门编码
     * @param countRate 统计周期
     * @return List<JkwwwSubjectInfo>
     */
    public List<JkwwwSubjectInfo> querySubjectInfo(
            String deptCode, String countRate,
            List<Integer> statusCode, Boolean isUpload,
            String uploadRate, Integer pageNum, Integer pageSize) {
        if (pageNum != null) {
            pageNum = pageSize * (pageNum - 1);
        }
        List<JkwwwSubjectInfo> subjectInfos = ztMapper.selectSubjectInfo(
                deptCode, countRate, statusCode, isUpload, uploadRate, pageNum, pageSize);
        for (int i = 0, size = subjectInfos.size(); i < size; i++) {
            JkwwwSubjectInfo subjectInfo = subjectInfos.get(i);
            subjectInfo.setNo(i + 1);
            Integer statusCode1 = subjectInfo.getStatusCode();
            if (statusCode1 == null) {
                subjectInfo.setIsUpload("否");
                subjectInfo.setCheckStatus("");
            } else if (statusCode1 == 0) {
                subjectInfo.setIsUpload("是");
                subjectInfo.setCheckStatus("发改委待审核");
            } else if (statusCode1 == 1) {
                subjectInfo.setIsUpload("是");
                subjectInfo.setCheckStatus("发改委审核通过");
            } else if (statusCode1 == 2) {
                subjectInfo.setIsUpload("是");
                subjectInfo.setCheckStatus("发改委审核未通过");
            } else if (statusCode1 == 5) {
                subjectInfo.setIsUpload("是");
                subjectInfo.setCheckStatus("政府办公厅审核未通过");
            } else if (statusCode1 == 6) {
                subjectInfo.setIsUpload("是");
                subjectInfo.setCheckStatus("政府办公厅待审核");
            } else if (statusCode1 == 7) {
                subjectInfo.setIsUpload("是");
                subjectInfo.setCheckStatus("政府办公厅审核通过");
            } else if (statusCode1 == 8) {
                subjectInfo.setIsUpload("是");
                subjectInfo.setCheckStatus("政府办公厅已提交");
            }

            //为了预览图片方便，这里把主题相关的所有图片信息发给前端。
            if (StringUtils.contains(subjectInfo.getSubjectName(), "项目进展照片")) {
                Integer subjectStatusId = subjectInfo.getSubjectStatusId();
                List<JkwwPrjImgInfo> prjImgInfos;
                if (subjectStatusId != null) {
                    prjImgInfos = prjImgInfoMapper.selectByPrjStatusId(subjectStatusId);
                } else {
                    prjImgInfos = new ArrayList<>(0);
                }
                subjectInfo.setImgInfoList(prjImgInfos);
            }
        }
        return subjectInfos;
    }

    public List<JkwwPrjImgInfo> queryPrjImgInfos(String subjectCode, String countRate) {
        Integer subjectStatusId = prjImgInfoMapper.selectPrjStatusIdByCountRateAndSubjectCode(subjectCode, countRate);
        return queryPrjImgInfos(subjectStatusId);
    }

    public List<JkwwPrjImgInfo> queryPrjImgInfos(Integer subjectStatusId) {
        List<JkwwPrjImgInfo> prjImgInfos;
        if (subjectStatusId != null) {
            prjImgInfos = prjImgInfoMapper.selectByPrjStatusId(subjectStatusId);
        } else {
            prjImgInfos = new ArrayList<>(0);
        }
        return prjImgInfos;
    }

    /**
     * 删除该主题下、该部门下、该统计周期下上传的业务数据
     * @param deptCode 部门编号
     * @param countRate 统计周期
     * @param subjectCode 主题编号
     * @return 删除的数据量
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteDeptData(String deptCode, String countRate, String subjectCode) {

        if ("ZTBM_JKWW_0006".equals(subjectCode)) {
            return yearlyPlanMapper.deleteDeptData(deptCode, countRate);
        } else if ("ZTBM_JKWW_0007".equals(subjectCode)) {
            return exeConditionMapper.deleteDeptData(deptCode, countRate);
        } else if ("ZTBM_JKWW_0066".equals(subjectCode)) {
            return projectScheduleSummaryMapper.deleteDeptData(deptCode, countRate);
        } else if ("ZTBM_JKWW_0069".equals(subjectCode)) {
            return projectPlanAndImplementMapper.deleteDeptData(deptCode, countRate);
        } else if ("ZTBM_JKWW_0072".equals(subjectCode)) {
            return problemScheduleMapper.deleteDeptData(deptCode, countRate);
        } else if ("ZTBM_JKWW_0073".equals(subjectCode)) {
            return eduProblemScheduleMapper.deleteDeptData(deptCode, countRate);
        } else if ("ZTBM_JKWW_0074".equals(subjectCode)) {
            return residenceProblemScheduleMapper.deleteDeptData(deptCode, countRate);
        } else if ("ZTBM_JKWW_0075".equals(subjectCode)) {
            return oldageProblemScheduleMapper.deleteDeptData(deptCode, countRate);
        } else if ("ZTBM_JKWW_0076".equals(subjectCode)) {
            return cremainsProblemScheduleMapper.deleteDeptData(deptCode, countRate);
        } else {
            logger.error("无法识别的主题编号：" + subjectCode);
            return 0;
        }

    }

    /**
     * 审核
     * @param subjectStatusIds 审核的主题状态ID
     * @param countRate 统计周期
     * @param isPass 是否通过审核
     * @param isNDRC true 发改委 ，false 市政府办公厅
     */
    @Transactional(rollbackFor = Exception.class)
    public void check(List<Integer> subjectStatusIds, String countRate, Boolean isPass,
                      String userCode, Boolean isNDRC) {
        List<DeptSubjectInfo> deptSubjectInfos =
                deptSubjectInfoService.selectManySubjectAtCountRate(subjectStatusIds, countRate);

        for (int i = 0, size = deptSubjectInfos.size(); i < size; i++) {
            DeptSubjectInfo deptSubjectInfo = deptSubjectInfos.get(i);
            DeptSubjectOperateInfo operateInfo = new DeptSubjectOperateInfo();
            operateInfo.setZtztid(deptSubjectInfo.getZtztid());

            if (isNDRC) {
                if (isPass) {
                    deptSubjectInfo.setSjzt(1);
                    operateInfo.setCzlx("发改委审核通过");
                } else {
                    deptSubjectInfo.setSjzt(2);
                    operateInfo.setCzlx("发改委审核不通过");
                }
            } else {
                if (isPass) {
                    deptSubjectInfo.setSjzt(7);
                    operateInfo.setCzlx("市政府办公厅审核通过");
                } else {
                    deptSubjectInfo.setSjzt(5);
                    operateInfo.setCzlx("市政府办公厅审核不通过");
                }
            }
            operateInfo.setCzsj(new Date());
            operateInfo.setCzyhbh(userCode);

            deptSubjectInfoService.saveDeptSubjectOperateInfo(operateInfo);
            deptSubjectInfoService.updateDeptSubjectInfo(deptSubjectInfo);
        }
    }

    /**
     * 是否已经全部通过审核
     * @param countRate 统计周期
     * @param isNDRC true 发改委 ，false 市政府办公厅
     * @return true 全部审核通过,false 未全部审核通过
     */
    @Transactional(readOnly = true)
    public boolean isAllCheck(String countRate, Boolean isNDRC, String uploadRate) {
        List<JkwwwSubjectInfo> subjectInfos = ztMapper.selectSubjectInfo(
                null, countRate, null, null, uploadRate, null, null);
        boolean isCheck = true;
        for (int i = 0, size = subjectInfos.size(); i < size; i++) {
            JkwwwSubjectInfo subjectInfo = subjectInfos.get(i);
            Integer statusCode = subjectInfo.getStatusCode();
            if (isNDRC == null){
                if ( statusCode == null || statusCode == 0 ||
                        statusCode == 2 || statusCode == 5 || statusCode < 7){
                    isCheck = false;
                    break;
                }
            }else {
                if (isNDRC && (statusCode == null || statusCode == 0 ||
                        statusCode == 2 || statusCode == 5)) {//发改委
                    isCheck = false;
                    break;
                } else if (!isNDRC && (statusCode == null || statusCode < 7)) {//市政府办公厅
                    isCheck = false;
                    break;
                }
            }
        }
        return isCheck;
    }

    /**
     * 是否已经整体
     * @param countRate 统计周期
     * @param isNDRC true 发改委 ，false 市政府办公厅
     * @return true 全部审核通过,false 未全部审核通过
     */
    @Transactional(readOnly = true)
    public boolean isAllSubmit(String countRate, Boolean isNDRC, String uploadRate) {
        List<JkwwwSubjectInfo> subjectInfos = ztMapper.selectSubjectInfo(
                null, countRate, null, null, uploadRate, null, null);
        boolean isAllSubmit = true;
        for (int i = 0, size = subjectInfos.size(); i < size; i++) {
            JkwwwSubjectInfo subjectInfo = subjectInfos.get(i);
            Integer statusCode = subjectInfo.getStatusCode();
            if (statusCode == null){
                isAllSubmit = false;
            }else {
                if (isNDRC == null){
                    if (statusCode ==6 || statusCode == 8){
                        isAllSubmit = true;
                    }
                }else {
                    if (isNDRC && (statusCode < 6)) {//发改委
                        isAllSubmit = false;
                        break;
                    } else if (!isNDRC && (statusCode != 8)) {//市政府办公厅
                        isAllSubmit = false;
                        break;
                    }
                }

            }
        }
        return isAllSubmit;
    }

    /**
     * 整体提交
     * @param countRate 统计周期
     * @param userCode 用户编号
     * @param isNDRC true 发改委 ，false 市政府办公厅
     */
    @Transactional(rollbackFor = Exception.class)
    public void submitAll(String countRate, String userCode, Boolean isNDRC) {
        List<DeptSubjectInfo> deptSubjectInfos = deptSubjectInfoService.selectAllSubjectByCountRate(countRate);
        for (int i = 0, size = deptSubjectInfos.size(); i < size; i++) {
            DeptSubjectInfo deptSubjectInfo = deptSubjectInfos.get(i);
            Integer subjectStatus = deptSubjectInfo.getSjzt();
            DeptSubjectOperateInfo operateInfo = new DeptSubjectOperateInfo();
            operateInfo.setZtztid(deptSubjectInfo.getZtztid());
            if (isNDRC && subjectStatus == 1) {
                deptSubjectInfo.setSjzt(6);
                operateInfo.setCzlx("发改委整体提交");
            } else if (!isNDRC && subjectStatus == 7) {
                deptSubjectInfo.setSjzt(8);
                operateInfo.setCzlx("市政府办公厅整体提交");
                String tableName = subjectInfoMapper.queryTableNameBySubjectCode(deptSubjectInfo.getZtbh());
                if (tableName != null && !tableName.equals("")){
                    commonExcelConfMapper.updateIsUsable(tableName, countRate);
                }
            } else {
                continue;
            }
            operateInfo.setCzsj(new Date());
            operateInfo.setCzyhbh(userCode);

            deptSubjectInfoService.saveDeptSubjectOperateInfo(operateInfo);
            deptSubjectInfoService.updateDeptSubjectInfo(deptSubjectInfo);
        }
        calSummaryData(countRate);
        if (!isNDRC) {
            sendSMSByOfficeSubmitAll(countRate);
        }
    }

    private void sendSMSByOfficeSubmitAll(String countRate) {
        String[] split = StringUtils.split(countRate, "Q");
        String txt = split[0] + "年第" + split[1] + "季度2号议案数据采集已经完成，请留意查阅。";
        smsService.send(jkwwStaffPhone, txt);
    }

    private void calSummaryData(String countRate) {
        //计算汇总数据，并存入数据库中。
        List<JkwwPrjPlanSummeryStr> prjPlanSummeryStrs = new ArrayList<>();
        try {
            prjPlanSummeryStrs = dataSummaryService.queryPrjScheduleSummary(countRate);
        } catch (Exception e) {
            logger.error("计算汇总数据出现异常", e);
        }
        for (JkwwPrjPlanSummeryStr summeryStr : prjPlanSummeryStrs) {
            JkwwPrjSummaryDb prjSummaryDb = new JkwwPrjSummaryDb();
            prjSummaryDb.setClassname(summeryStr.getClassName());
            prjSummaryDb.setZdsgzxfa(summeryStr.getZdsgzxfa());
            prjSummaryDb.setZdqk(summeryStr.getZdqk());
            prjSummaryDb.setQdjssgxkzqk(summeryStr.getQdjssgxkzqk());
            prjSummaryDb.setYdzbqk(summeryStr.getYdzbqk());
            prjSummaryDb.setYspfqk(summeryStr.getYspfqk());
            prjSummaryDb.setZtbqk(summeryStr.getZtbqk());
            prjSummaryDb.setDjdw(summeryStr.getDjdw());
            prjSummaryDb.setTjjdqk(summeryStr.getTjjdqk());
            prjSummaryDb.setNsgcqk(summeryStr.getNsgcqk());
            prjSummaryDb.setPtgchjgcqk(summeryStr.getPtgchjgcqk());
            prjSummaryDb.setYsyjsyqk(summeryStr.getYsyjsyqk());

            prjSummaryDb.setZdsgzxfaNum(summeryStr.getZdsgzxfaNum());
            prjSummaryDb.setZdqkNum(summeryStr.getZdqkNum());
            prjSummaryDb.setQdjssgxkzqkNum(summeryStr.getQdjssgxkzqkNum());
            prjSummaryDb.setYdzbqkNum(summeryStr.getYdzbqkNum());
            prjSummaryDb.setYspfqkNum(summeryStr.getYspfqkNum());
            prjSummaryDb.setZtbqkNum(summeryStr.getZtbqkNum());
            prjSummaryDb.setDjdwNum(summeryStr.getDjdwNum());
            prjSummaryDb.setTjjdqkNum(summeryStr.getTjjdqkNum());
            prjSummaryDb.setNsgcqkNum(summeryStr.getNsgcqkNum());
            prjSummaryDb.setPtgchjgcqkNum(summeryStr.getPtgchjgcqkNum());
            prjSummaryDb.setYsyjsyqkNum(summeryStr.getYsyjsyqkNum());

            prjSummaryDb.setCzwtjdc(summeryStr.getCzwtjdc());
            prjSummaryDb.setTjzq(countRate);

            prjSummaryDbMapper.insertSelective(prjSummaryDb);
        }
    }

    /**
     * 查询科教文卫工委相关的部门信息
     * @return JkwwDeptInfo
     */
    @Transactional(readOnly = true)
    public List<JkwwDeptInfo> queryJkwwDeptInfo() {
        return ztMapper.selectJkwwDeptInfo();
    }

    /**
     * 发改委是否整体提交
     * @param countRate 统计周期
     * @return true 是   false 否
     */
    public boolean isSubmitAllByNDRC(String countRate, String uploadRate) {
        List<JkwwwSubjectInfo> subjectInfos = ztMapper.selectSubjectInfo(
                null, countRate, null, null, uploadRate, null, null);
        boolean isCheck = true;
        for (int i = 0, size = subjectInfos.size(); i < size; i++) {
            JkwwwSubjectInfo subjectInfo = subjectInfos.get(i);
            Integer statusCode = subjectInfo.getStatusCode();
            if (statusCode == null || statusCode < 5) {
                isCheck = false;
                break;
            }
        }
        return isCheck;
    }

    /**
     * 查询汇总的主题是否准备齐备
     * @param countRate 统计周期
     * @param deptCode 部门编号
     * @return List<SubjectCompleteInfo>
     */
    public List<SubjectCompleteInfo> selectSubjectCompleteInfo(String countRate, String deptCode) {

        List<SubjectCompleteInfo> completeInfos = new ArrayList<>();
        Map<String, List<String>> unCompleteInfos = new HashMap<>();

        if (!StringUtils.equalsIgnoreCase("BMBM_013", deptCode)) {//不是住建委

            List<String> subjectCodes = new ArrayList<>();
            subjectCodes.add("ZTBM_JKWW_0006");
            subjectCodes.add("ZTBM_JKWW_0007");
            subjectCodes.add("ZTBM_JKWW_0066");
            subjectCodes.add("ZTBM_JKWW_0069");
            subjectCodes.add("ZTBM_JKWW_0072");
            subjectCodes.add("ZTBM_JKWW_0074");
            completeInfos = ztMapper.selectSubjectCompleteInfo(countRate, subjectCodes);
            unCompleteInfos = selectUnCompleteDeptInfos(countRate, subjectCodes);
            for (SubjectCompleteInfo completeInfo : completeInfos) {
                String subjectCode = completeInfo.getSubjectCode();
                List<String> unCompleteDeptNames = unCompleteInfos.get(subjectCode);
                completeInfo.setUnCompleteDeptNames(unCompleteDeptNames);
            }

            Integer isComplete = isCompleteKeySubject(completeInfos);

            //增加虚拟主题（因为这个主题是计算出来的）：《重点民生基础设施项目进度表》
            SubjectCompleteInfo keyProject = new SubjectCompleteInfo();
            keyProject.setSubjectCode("ZDMSJCSSXMJDB");
            keyProject.setSubjectName("重点民生基础设施项目进度表");
            keyProject.setSubjectStatusId(0);
            keyProject.setIsComplete(isComplete);
            keyProject.setUnCompleteDeptNames(unCompleteInfos.get("ZTBM_JKWW_0066"));
            completeInfos.add(keyProject);

            SubjectCompleteInfo scheduleSummaryInfo = new SubjectCompleteInfo();
            scheduleSummaryInfo.setSubjectCode("PROJECT_SUMMARY");
            scheduleSummaryInfo.setSubjectName("“十三五”时期市本级社会民生基础设施建设项目进度总表");
            scheduleSummaryInfo.setSubjectStatusId(0);
            scheduleSummaryInfo.setIsComplete(isCompleteScheduleSummary(countRate));
            scheduleSummaryInfo.setUnCompleteDeptNames(unCompleteInfos.get("ZTBM_JKWW_0066"));
            completeInfos.add(scheduleSummaryInfo);

        } else {//是住建委

            List<String> subjectCodes = new ArrayList<>();
            subjectCodes.add("ZTBM_JKWW_0074");
            completeInfos = ztMapper.selectSubjectCompleteInfo(countRate, subjectCodes);
            unCompleteInfos = selectUnCompleteDeptInfos(countRate, subjectCodes);

        }

        //增加虚拟主题
        //居住区配套设施建设移交历史遗留问题台账（总表）
        //居住区配套教育设施建设移交历史遗留问题台账
        //居住区配套公共文化服务设施建设移交历史遗留问题台账
        //居住区配套卫生服务设施建设移交历史遗留问题台账

        Integer residenceLedgerCompleteStatus = isCompleteResidenceLedger(completeInfos);
        List<String> residenceLedgerUnCompleteDeptNames = unCompleteInfos.get("ZTBM_JKWW_0074");

        SubjectCompleteInfo allLedger = new SubjectCompleteInfo();
        allLedger.setSubjectCode("RESIDENCE_ALL");
        allLedger.setSubjectName("居住区配套设施建设移交历史遗留问题台账（总表）");
        allLedger.setSubjectStatusId(0);
        allLedger.setIsComplete(residenceLedgerCompleteStatus);
        allLedger.setUnCompleteDeptNames(residenceLedgerUnCompleteDeptNames);
        completeInfos.add(allLedger);

        SubjectCompleteInfo eduLedger = new SubjectCompleteInfo();
        eduLedger.setSubjectCode("RESIDENCE_EDU");
        eduLedger.setSubjectName("居住区配套教育设施建设移交历史遗留问题台账");
        eduLedger.setSubjectStatusId(1);
        eduLedger.setIsComplete(residenceLedgerCompleteStatus);
        eduLedger.setUnCompleteDeptNames(residenceLedgerUnCompleteDeptNames);
        completeInfos.add(eduLedger);

        SubjectCompleteInfo cultureLedger = new SubjectCompleteInfo();
        cultureLedger.setSubjectCode("RESIDENCE_CULTURE");
        cultureLedger.setSubjectName("居住区配套公共文化服务设施建设移交历史遗留问题台账");
        cultureLedger.setSubjectStatusId(3);
        cultureLedger.setIsComplete(residenceLedgerCompleteStatus);
        cultureLedger.setUnCompleteDeptNames(residenceLedgerUnCompleteDeptNames);
        completeInfos.add(cultureLedger);

        SubjectCompleteInfo healthLedger = new SubjectCompleteInfo();
        healthLedger.setSubjectCode("RESIDENCE_HEALTH");
        healthLedger.setSubjectName("居住区配套卫生服务设施建设移交历史遗留问题台账");
        healthLedger.setSubjectStatusId(2);
        healthLedger.setIsComplete(residenceLedgerCompleteStatus);
        healthLedger.setUnCompleteDeptNames(residenceLedgerUnCompleteDeptNames);
        completeInfos.add(healthLedger);

        //移除这个主题：ZTBM_JKWW_0074
        Iterator<SubjectCompleteInfo> it = completeInfos.iterator();
        while(it.hasNext()) {
            SubjectCompleteInfo x = it.next();
            String subjectCode = x.getSubjectCode();
            if(StringUtils.equals("ZTBM_JKWW_0074", subjectCode)) {
                it.remove();
            }
        }

        return completeInfos;
    }

    public Map<String, List<String>> selectUnCompleteDeptInfos(String countRate, List<String> subjectCodes) {
        Map<String, List<String>> unCompleteMap = new HashMap<>();
        if (StringUtils.isEmpty(countRate) || CollectionUtils.isEmpty(subjectCodes)) {
            return unCompleteMap;
        }
        List<JkwwUnCompleteDeptVO> unCompleteDepts = ztMapper.selectUnCompleteDept(countRate, subjectCodes);

        for (JkwwUnCompleteDeptVO unCompleteDeptVO : unCompleteDepts) {
            String subjectCode = unCompleteDeptVO.getSubjectCode();
            String deptName = unCompleteDeptVO.getDeptName();
            if (unCompleteMap.containsKey(subjectCode)) {
                List<String> deptNames = unCompleteMap.get(subjectCode);
                deptNames.add(deptName);
            } else {
                List<String> deptNames = new ArrayList<>();
                deptNames.add(deptName);
                unCompleteMap.put(subjectCode, deptNames);
            }
        }
        return unCompleteMap;
    }

    public boolean isComplete(String statPeriod, String subjectCode) {

        List<String> subjectCodes = new ArrayList<>();
        if ("ZTBM_JKWW_0006".equals(subjectCode)) {

            //“十三五”时期市本级社会民生基础设施年度计划表
            subjectCodes.add("ZTBM_JKWW_0006");

        } else if ("ZTBM_JKWW_0007".equals(subjectCode)) {

            //“十三五”时期市本级社会民生基础设施计划执行情况表
            subjectCodes.add("ZTBM_JKWW_0007");

        } else if ("PROJECT_SUMMARY".equals(subjectCode)) {

            //“十三五”时期市本级社会民生基础设施建设项目进度总表
            return isCompleteScheduleSummary(statPeriod) == 1 ? true : false;

        } else if ("ZTBM_JKWW_0066".equals(subjectCode)) {

            //“十三五”时期市本级社会民生基础设施建设项目进度表
            subjectCodes.add("ZTBM_JKWW_0066");

        } else if ("ZDMSJCSSXMJDB".equals(subjectCode)) {

            //重点民生基础设施项目进度表
            subjectCodes.add("ZTBM_JKWW_0066");

        } else if ("ZTBM_JKWW_0069".equals(subjectCode)) {

            //民生基础设施布局规划及其实施方案制定实施情况表
            subjectCodes.add("ZTBM_JKWW_0069");

        } else if ("ZTBM_JKWW_0072".equals(subjectCode)) {

            //解决历史遗留问题进度表
            subjectCodes.add("ZTBM_JKWW_0072");

        } else if ("RESIDENCE_ALL".equals(subjectCode)) {

            //居住区配套设施建设移交历史遗留问题台账（总表）
            subjectCodes.add("ZTBM_JKWW_0074");

        } else if ("RESIDENCE_EDU".equals(subjectCode)) {

            //居住区配套教育设施建设移交历史遗留问题台账
            subjectCodes.add("ZTBM_JKWW_0074");

        } else if ("RESIDENCE_CULTURE".equals(subjectCode)) {

            //居住区配套公共文化服务设施建设移交历史遗留问题台账
            subjectCodes.add("ZTBM_JKWW_0074");

        } else if ("RESIDENCE_HEALTH".equals(subjectCode)) {

            //居住区配套卫生服务设施建设移交历史遗留问题台账
            subjectCodes.add("ZTBM_JKWW_0074");

        } else {
            logger.error("无法识别的主题编号：" + subjectCode);
            return false;
        }

        List<SubjectCompleteInfo> completeInfos =
                ztMapper.selectSubjectCompleteInfo(statPeriod, subjectCodes);
        if (CollectionUtils.isEmpty(completeInfos)) {
            return false;
        }

        SubjectCompleteInfo completeInfo = completeInfos.get(0);
        if (completeInfo != null && completeInfo.getIsComplete() == 1) {
            return true;
        }
        return false;
    }

    /**
     * 进度总表是否齐备，只有当前季度和上一个季度都齐备才能算真正齐备。
     * @param statPeriod 统计周期
     * @return 0:否，1:是
     */
    public int isCompleteScheduleSummary(String statPeriod) {
        String previousQuarter = CountQuarterUtils.getPreviousQuarter(statPeriod);
        boolean isPreviousComplete = false;
        if(previousQuarter.equals("2018Q3")){
            isPreviousComplete = true;
        }
        List<String> subjectCodes = new ArrayList<>();
        subjectCodes.add("ZTBM_JKWW_0066");
        List<SubjectCompleteInfo> completeInfos =
                ztMapper.selectSubjectCompleteInfo(statPeriod, subjectCodes);
        List<SubjectCompleteInfo> preCompleteInfos =
                ztMapper.selectSubjectCompleteInfo(previousQuarter, subjectCodes);
        if (CollectionUtils.isEmpty(completeInfos) || !isPreviousComplete || CollectionUtils.isEmpty(preCompleteInfos)) {
            return 0;
        }
        SubjectCompleteInfo completeInfo = completeInfos.get(0);
        SubjectCompleteInfo preCompleteInfo = preCompleteInfos.get(0);
        if (completeInfo != null && completeInfo.getIsComplete() == 1 && isPreviousComplete){
            return 1;
        }
        if (completeInfo != null && completeInfo.getIsComplete() == 1
                && preCompleteInfo != null && preCompleteInfo.getIsComplete() == 1) {
            return 1;
        }
        return 0;
    }

    /**
     * 检查《居住区配套设施建设移交历史遗留问题台账》的数据是否准备齐备
     * @param completeInfos 主题齐备性数据
     * @return 0：未齐备, 1: 齐备
     */
    private Integer isCompleteResidenceLedger(List<SubjectCompleteInfo> completeInfos) {
        for (SubjectCompleteInfo subjectCompleteInfo : completeInfos) {
            String subjectCode = subjectCompleteInfo.getSubjectCode();
            if ("ZTBM_JKWW_0074".equals(subjectCode)) {
                return subjectCompleteInfo.getIsComplete();
            }
        }
        return 0;
    }

    /**
     * 检查《重点民生基础设施项目进度表》的数据是否准备齐备
     * @param completeInfos 主题数据齐备性数据
     * @return 0：未齐备, 1: 齐备
     */
    private Integer isCompleteKeySubject(List<SubjectCompleteInfo> completeInfos) {
        for (SubjectCompleteInfo subjectCompleteInfo : completeInfos) {
            String subjectCode = subjectCompleteInfo.getSubjectCode();
            if ("ZTBM_JKWW_0066".equals(subjectCode)) {
                return subjectCompleteInfo.getIsComplete();
            }
        }
        return 0;
    }

    public List<JkwwExeCondition> selectExePlan(
            String className, String status,
            String mainDeptName, String ownerName,
            String projectName, String countRate,
            Integer startRow, Integer pageSize) {
        if (StringUtils.isBlank(className)) {
            className = null;
        }
        if (StringUtils.isBlank(status)) {
            status = null;
        }
        if (StringUtils.isBlank(mainDeptName)) {
            mainDeptName = null;
        } else {
            mainDeptName = "%" + mainDeptName + "%";
        }
        if (StringUtils.isBlank(ownerName)) {
            ownerName = null;
        } else {
            ownerName = "%" + ownerName + "%";
        }
        if (StringUtils.isBlank(projectName)) {
            projectName = null;
        } else {
            projectName = "%" + projectName + "%";
        }
        if (StringUtils.isBlank(countRate)) {
            countRate = null;
        }

        return exeConditionMapper.selectExePlan(
                className, status, mainDeptName,
                ownerName, projectName, countRate, startRow, pageSize);
    }

    public int countExePlan(
            String className, String status,
            String mainDeptName, String ownerName,
            String projectName, String countRate) {
        if (StringUtils.isBlank(className)) {
            className = null;
        }
        if (StringUtils.isBlank(status)) {
            status = null;
        }
        if (StringUtils.isBlank(mainDeptName)) {
            mainDeptName = null;
        } else {
            mainDeptName = "%" + mainDeptName + "%";
        }
        if (StringUtils.isBlank(ownerName)) {
            ownerName = null;
        } else {
            ownerName = "%" + ownerName + "%";
        }
        if (StringUtils.isBlank(projectName)) {
            projectName = null;
        } else {
            projectName = "%" + projectName + "%";
        }
        if (StringUtils.isBlank(countRate)) {
            countRate = null;
        }

        return exeConditionMapper.countExePlan(
                className, status, mainDeptName, ownerName, projectName, countRate);
    }


    public List<JkwwExeCondition> selectStatInfo(String countRate) {
        return exeConditionMapper.selectStatInfo(countRate);
    }

    /**
     * 查看数据主题的数据
     * @param subjectCode 主题编码
     * @param deptCode 部门编码
     * @param countRate 统计周期
     */
    public Map<String, Object> viewDataSubject(String subjectCode, String deptCode, String countRate) {

        Map<String, Object> data = new HashMap<>();
        if ("ZTBM_JKWW_0006".equals(subjectCode)) {

            List<JkwwYearlyPlan> jkwwYearlyPlans = yearlyPlanMapper.selectDeptData(deptCode, countRate);
            data.put("list", jkwwYearlyPlans);

        } else if ("ZTBM_JKWW_0007".equals(subjectCode)) {

            List<JkwwExeCondition> jkwwExeConditions = exeConditionMapper.selectDeptData(deptCode, countRate);
            data.put("list", jkwwExeConditions);

        } else if ("ZTBM_JKWW_0066".equals(subjectCode) && StringUtils.isNotBlank(deptCode)) {

            List<JkwwProjectScheduleSummary> scheduleSummaries =
                    projectScheduleSummaryMapper.selectDeptData(deptCode, countRate);
            data.put("list", scheduleSummaries);

        } else if ("ZTBM_JKWW_0069".equals(subjectCode)) {

            List<JkwwProjectPlanAndImplement> planAndImplements =
                    projectPlanAndImplementMapper.selectDeptData(deptCode, countRate);
            data.put("list", planAndImplements);

        } else if ("ZTBM_JKWW_0072".equals(subjectCode)) {

            List<JkwwProblemSchedule> problemSchedules =
                    problemScheduleMapper.selectDeptData(deptCode, countRate);
            data.put("list", problemSchedules);

        } else if ("ZTBM_JKWW_0073".equals(subjectCode)) {

            List<JkwwEduProblemSchedule> eduProblemSchedules =
                    eduProblemScheduleMapper.selectDeptData(deptCode, countRate);
            data.put("list", eduProblemSchedules);

        } else if ("ZTBM_JKWW_0074".equals(subjectCode)) {

            List<JkwwResidenceProblemSchedule> residenceProblemSchedules =
                    residenceProblemScheduleMapper.selectDeptData(deptCode, countRate);
            data.put("list", residenceProblemSchedules);

        } else if ("ZTBM_JKWW_0075".equals(subjectCode)) {

            List<JkwwOldageProblemSchedule> oldageProblemSchedules =
                    oldageProblemScheduleMapper.selectDeptData(deptCode, countRate);
            data.put("list", oldageProblemSchedules);

        } else if ("ZTBM_JKWW_0076".equals(subjectCode)) {

            List<JkwwCremainsProblemSchedule> cremainsProblemSchedules =
                    cremainsProblemScheduleMapper.selectDeptData(deptCode, countRate);
            data.put("list", cremainsProblemSchedules);

        }
        return data;
    }

    /**
     * 查询某一主题的数据是否全部齐备
     */
    public boolean isSubmitAll(String countRate, String subjectCode) {
        List<String> subjectCodes = new ArrayList<>();
        subjectCodes.add(subjectCode);
        List<SubjectCompleteInfo> completeInfoList = ztMapper.selectSubjectCompleteInfo(countRate, subjectCodes);
        if (countRate.equals("2018Q3")){
            return true;
        }
        if (CollectionUtils.isNotEmpty(completeInfoList) && completeInfoList.size() == 1) {

            SubjectCompleteInfo completeInfo = completeInfoList.get(0);
            Integer isComplete = completeInfo.getIsComplete();
            return isComplete == 1 ? true : false;

        } else {
            logger.error("一个主题不可能存在多个‘完备信息’，请检查！");
            return false;
        }
    }

    /**
     * 在一个统计周期下，所有主题数据是否已经上传数据。
     * @param statPeriod
     * @return
     */
    public boolean isAllUpload(String statPeriod) {
        String uploadRate = CountQuarterUtils.getUploadRate(statPeriod);
        int countUnUploadSubject = ztMapper.countUnUploadSubject(statPeriod, uploadRate);
        if (countUnUploadSubject == 0) {
            return true;
        } else {
            return false;
        }
    }
}
