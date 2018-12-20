package cn.gz.rd.datacollection.controller;

import cn.gz.rd.datacollection.controller.response.ControllerResponse;
import cn.gz.rd.datacollection.model.*;
import cn.gz.rd.datacollection.service.JkwwFileUpDownloadService;
import cn.gz.rd.datacollection.service.JkwwPrjDataSummaryService;
import cn.gz.rd.datacollection.service.JkwwwSubjectService;
import cn.gz.rd.datacollection.utils.CountQuarterUtils;
import cn.gz.rd.datacollection.utils.DownloadFileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 汇总数据相关的接口
 * @author Peng Xiaodong
 */
@Controller
@RequestMapping("/jkwww/summary")
public class JkwwSummaryDataCtrl extends BaseCtrl {

    private final Logger logger = LoggerFactory.getLogger(JkwwSummaryDataCtrl.class);

    @Autowired
    private JkwwPrjDataSummaryService summaryService;

    @Autowired
    private JkwwwSubjectService subjectService;

    @Autowired
    private JkwwFileUpDownloadService fileUpDownloadService;

    @Autowired
    private JkwwFileUpDownloadService fileService;

    @ResponseBody
    @RequestMapping(value = "/query/v1", method = RequestMethod.GET)
    public ControllerResponse query(
            String className, String status, String mainDeptName,
            String ownerName, String projectName, String countRate,
            Integer pageNum, Integer pageSize, String subjectCode) {

        logger.debug("查询汇总数据：请求参数 className = {}, status = {}, " +
                "mainDeptName = {}, ownerName = {}, projectName = {}, " +
                "countRate = {}, pageNum = {}, pageSize = {}, subjectCode = {}",
                className, status, mainDeptName, ownerName, projectName,
                countRate, pageNum, pageSize, subjectCode);

        if (StringUtils.equals(className, "0")) {
            className = null;
        }
        if (StringUtils.equals(status, "0")) {
            status = null;
        }
        if (StringUtils.equals(mainDeptName, "0")) {
            mainDeptName = null;
        }
        ControllerResponse response = new ControllerResponse();
        Map<String, Object> data = new HashMap<>();
        response.setData(data);
        try {
            if ("ZTBM_JKWW_0006".equals(subjectCode)) {

                //“十三五”时期市本级社会民生基础设施年度计划表
                List<JkwwYearlyPlan> yearlyPlans = summaryService.queryYearlyPlanByCondition(
                        className, status, mainDeptName, ownerName, projectName, countRate,
                        pageSize * (pageNum - 1), pageSize);
                int count = summaryService.countYearlyPlanByCondition(
                        className, status, mainDeptName, ownerName, projectName, countRate);
                data.put("list", yearlyPlans);
                data.put("count", count);

            } else if ("ZTBM_JKWW_0007".equals(subjectCode)) {

                //“十三五”时期市本级社会民生基础设施计划执行情况表
                List<JkwwExeCondition> projectInfos = subjectService.selectExePlan(
                        className, status, mainDeptName, ownerName, projectName, countRate,
                        pageSize * (pageNum - 1), pageSize);
                int count = subjectService.countExePlan(
                        className, status, mainDeptName, ownerName, projectName, countRate);
                data.put("list", projectInfos);
                data.put("count", count);

            } else if ("PROJECT_SUMMARY".equals(subjectCode)) {

                //“十三五”时期市本级社会民生基础设施建设项目进度总表

                //先检查上一季度的数据是否齐备
                String previousQuarter = CountQuarterUtils.getPreviousQuarter(countRate);
                boolean previousSubmitAll = subjectService.isSubmitAll(
                        previousQuarter, "ZTBM_JKWW_0066");
                if (previousSubmitAll) {
                    List<JkwwPrjPlanSummeryStr> prjPlanSummeryStrs =
                            summaryService.queryPrjScheduleSummary(countRate);
                    data.put("list", prjPlanSummeryStrs);

                } else {
                    response.error("上一个季度数据不齐备，无法进行汇总统计！");
                    return response;
                }

            } else if ("ZTBM_JKWW_0066".equals(subjectCode)) {

                //“十三五”时期市本级社会民生基础设施建设项目进度表
                List<JkwwProjectScheduleSummary> scheduleSummaries = summaryService.queryScheduleSummaryByCondition(
                        className, status, mainDeptName, ownerName, projectName, countRate,
                        null, pageSize * (pageNum - 1), pageSize);
                int count = summaryService.countScheduleSummaryByCondition(
                        className, status, mainDeptName, ownerName, projectName, countRate, null);
                data.put("list", scheduleSummaries);
                data.put("count", count);

            } else if ("ZDMSJCSSXMJDB".equals(subjectCode)) {

                //重点民生基础设施项目进度表
                List<JkwwProjectScheduleSummary> scheduleSummaries =
                        summaryService.queryScheduleSummaryByCondition(
                        className, status, mainDeptName, ownerName, projectName, countRate,
                        true, pageSize * (pageNum - 1), pageSize);
                int count = summaryService.countScheduleSummaryByCondition(
                        className, status, mainDeptName, ownerName, projectName,
                        countRate, true);
                data.put("list", scheduleSummaries);
                data.put("count", count);

            } else if ("ZTBM_JKWW_0069".equals(subjectCode)) {

                //民生基础设施布局规划及其实施方案制定实施情况表
                List<JkwwProjectPlanAndImplement> planAndImplements =
                        summaryService.queryPrjPlanAndImpl(countRate);
                data.put("list", planAndImplements);

            } else if ("ZTBM_JKWW_0072".equals(subjectCode)) {

                //解决历史遗留问题进度表
                List<JkwwProblemSchedule> problemSchedules =
                        summaryService.queryHistoryProblemSchedule(countRate);
                data.put("list", problemSchedules);

            } else if ("RESIDENCE_ALL".equals(subjectCode)) {

                //居住区配套设施建设移交历史遗留问题台账（总表）
                List<JkwwResidenceLedger> ledgers =
                        summaryService.queryResidenceLedger(null, countRate);
                data.put("list", ledgers);

            } else if ("RESIDENCE_EDU".equals(subjectCode)) {

                //居住区配套教育设施建设移交历史遗留问题台账
                List<JkwwResidenceLedger> ledgers =
                        summaryService.queryResidenceLedger("教育局", countRate);
                data.put("list", ledgers);

            } else if ("RESIDENCE_CULTURE".equals(subjectCode)) {

                //居住区配套公共文化服务设施建设移交历史遗留问题台账
                List<JkwwResidenceLedger> ledgers =
                        summaryService.queryResidenceLedger("市文广新局", countRate);
                data.put("list", ledgers);

            } else if ("RESIDENCE_HEALTH".equals(subjectCode)) {

                //居住区配套卫生服务设施建设移交历史遗留问题台账
                List<JkwwResidenceLedger> ledgers =
                        summaryService.queryResidenceLedger("市卫计委", countRate);
                data.put("list", ledgers);

            } else {
                response.error();
                response.setMessage("无法识别的主题编号：" + subjectCode);
                return response;
            }
            response.success();
        } catch (Exception e) {
            logger.error("汇总数据查询接口执行出现异常", e);
            response.error(e);
        }
        return response;
    }

    /**
     * 查询汇总主题信息
     * @param countRate 统计周期
     * @return ControllerResponse
     */
    @ResponseBody
    @RequestMapping(value = "/completeInfo/v1", method = RequestMethod.GET)
    public ControllerResponse queryCompleteInfo(String countRate) {
        ControllerResponse response = new ControllerResponse();
        try {
            String queryDeptCode = getDeptCodeFromSession();
            List<SubjectCompleteInfo> completeInfos =
                    subjectService.selectSubjectCompleteInfo(countRate, queryDeptCode);
            Map<String, Object> data = new HashMap<>();
            data.put("list", completeInfos);
            response.setData(data);
            response.success();
        } catch (Exception e) {
            logger.error("Exception", e);
            response.error(e);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/statInfo/v1", method = RequestMethod.GET)
    public ControllerResponse queryStatInfo(String countRate) {
        ControllerResponse response = new ControllerResponse();
        try {
            List<JkwwExeCondition> projectInfos = subjectService.selectStatInfo(countRate);
            Map<String, Object> data = new HashMap<>();
            data.put("list", projectInfos);
            response.setData(data);
            response.success();
        } catch (Exception e) {
            logger.error("Exception", e);
            response.error(e);
        }
        return response;
    }

    @RequestMapping(value = "/downloadStat/v1", method = RequestMethod.GET)
    public ResponseEntity<byte[]> downloadProjectStatExcel(
            @RequestHeader("User-Agent") String userAgent, String countRate) {
        ResponseEntity<byte[]> responseEntity = null;
        try {
            byte[] data = fileUpDownloadService.downloadPrjStatInfo(countRate);
            String fileName = "“十三五”时期市本级社会民生基础设施分领域统计表.xlsx";
            // IE内核的浏览器特殊处理
            if (StringUtils.containsAny(userAgent, "MSIE", "Trident")) {
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } else {
                fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName);
            responseEntity = new ResponseEntity<>(data, headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return responseEntity;
    }

    @RequestMapping(value = "/download/v1", method = RequestMethod.GET)
    public ResponseEntity<byte[]> download(
            @RequestHeader("User-Agent") String userAgent,
            String className, String status, String mainDeptName,
            String ownerName, String projectName, String countRate,
            String subjectCode) {

        logger.debug("下载汇总数据：请求参数 className = {}, status = {}, " +
                        "mainDeptName = {}, ownerName = {}, projectName = {}, " +
                        "countRate = {}, subjectCode = {}",
                className, status, mainDeptName, ownerName, projectName,
                countRate, subjectCode);

        if (StringUtils.equals(className, "0")) {
            className = null;
        }
        if (StringUtils.equals(status, "0")) {
            status = null;
        }
        if (StringUtils.equals(mainDeptName, "0")) {
            mainDeptName = null;
        }

        ResponseEntity<byte[]> responseEntity = null;
        byte[] fileBytes = null;
        try {
            String fileName = null;
            if ("ZTBM_JKWW_0006".equals(subjectCode)) {

                fileName = "“十三五”时期市本级社会民生基础设施年度计划表.xlsx";
                fileBytes = fileService.downloadYearlyPlanSummary(
                        className, status, mainDeptName, ownerName, projectName, countRate);

            } else if ("ZTBM_JKWW_0007".equals(subjectCode)) {

                fileName = "“十三五”时期市本级社会民生基础设施计划执行情况表.xlsx";
                fileBytes = fileUpDownloadService.downloadPrjDetailInfo(
                        className, status, mainDeptName, ownerName, projectName, countRate);

            } else if ("PROJECT_SUMMARY".equals(subjectCode)) {

                fileName = "“十三五”时期市本级社会民生基础设施建设项目进度总表.xlsx";
                fileBytes = fileUpDownloadService.downloadScheduleSummary(countRate);

            } else if ("ZTBM_JKWW_0066".equals(subjectCode)) {

                fileName = "“十三五”时期市本级社会民生基础设施建设项目进度表.xlsx";
                fileBytes = fileService.downloadSchedule(
                        className, status, mainDeptName, ownerName, projectName, countRate, null);

            } else if ("ZDMSJCSSXMJDB".equals(subjectCode)) {

                fileName = "“十三五”时期市本级社会民生基础设施建设重点项目进度表.xlsx";
                fileBytes = fileService.downloadSchedule(
                        className, status, mainDeptName, ownerName, projectName, countRate, true);

            } else if ("ZTBM_JKWW_0069".equals(subjectCode)) {

                fileName = "民生基础设施布局规划及其实施方案制定实施情况表.xlsx";
                fileBytes = fileUpDownloadService.downloadPlanAndImplSummary(countRate);

            } else if ("ZTBM_JKWW_0072".equals(subjectCode)) {

                fileName = "解决历史遗留问题进度表.xlsx";
                fileBytes = fileUpDownloadService.downloadProblemScheduleSummary(countRate);

            }  else if ("RESIDENCE_ALL".equals(subjectCode)) {

                fileName = "居住区配套设施建设移交历史遗留问题台账（总表）.xlsx";
                fileBytes = fileUpDownloadService.downloadEduProblemScheduleSummary(null, countRate);

            } else if ("RESIDENCE_EDU".equals(subjectCode)) {

                fileName = "居住区配套教育设施建设移交历史遗留问题台账.xlsx";
                fileBytes = fileUpDownloadService.downloadEduProblemScheduleSummary("教育局", countRate);

            } else if ("RESIDENCE_CULTURE".equals(subjectCode)) {

                fileName = "居住区配套公共文化服务设施建设移交历史遗留问题台账.xlsx";
                fileBytes = fileUpDownloadService.downloadEduProblemScheduleSummary("市文广新局", countRate);

            } else if ("RESIDENCE_HEALTH".equals(subjectCode)) {

                fileName = "居住区配套卫生服务设施建设移交历史遗留问题台账.xlsx";
                fileBytes = fileUpDownloadService.downloadEduProblemScheduleSummary("市卫计委", countRate);

            } else {
                logger.error("无法识别的主题编号：" + subjectCode);
                return responseEntity;
            }

            fileName = DownloadFileUtils.getFileNameByExplore(userAgent, fileName);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName);
            responseEntity = new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("汇总数据下载接口执行出现异常", e);
        }
        return responseEntity;
    }

    /**
     * 查询主题是否齐备
     */
    @ResponseBody
    @RequestMapping(value = "/isComplete/v1", method = RequestMethod.GET)
    public ControllerResponse isComplete(String countRate, String subjectCode) {
        ControllerResponse response = new ControllerResponse();
        try {
            if (StringUtils.isBlank(countRate)) {
                response.error("统计周期不能为空！");
                return response;
            }
            if (StringUtils.isBlank(subjectCode)) {
                response.error("主题编码不能为空！");
                return response;
            }
            boolean isComplete = subjectService.isComplete(countRate, subjectCode);
            response.setData(isComplete);
            response.success();
        } catch (Exception e) {
            logger.error("Exception", e);
            response.error(e);
        }
        return response;
    }
}
