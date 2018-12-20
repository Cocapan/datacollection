package cn.gz.rd.datacollection.controller;

import cn.gz.rd.datacollection.controller.response.ControllerResponse;
import cn.gz.rd.datacollection.model.*;
import cn.gz.rd.datacollection.service.JkwwwSubjectService;
import cn.gz.rd.datacollection.utils.CountQuarterUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/jkwww/subject")
public class JkwwwSubjectCtrl extends BaseCtrl {

    private static final Logger logger = LoggerFactory.getLogger(JkwwwSubjectCtrl.class);

    @Autowired
    private JkwwwSubjectService subjectService;

    /**
     * 查询主题信息
     * @param countRate 统计周期
     */
    @RequestMapping(value = "/query/v1", method = RequestMethod.GET)
    @ResponseBody
    public ControllerResponse querySubjectInfo(String countRate, Integer pageNum, Integer pageSize) {
        ControllerResponse response = new ControllerResponse();
        logger.debug("查询主题信息：请求参数 countRate = {}, pageNum = {}, pageSize = {}",
                countRate, pageNum, pageSize);
        try {
            String queryDeptCode = getDeptCodeFromSession();
            String uploadRate = CountQuarterUtils.getUploadRate(countRate);

            List<JkwwwSubjectInfo> subjectInfos = subjectService.querySubjectInfo(
                    queryDeptCode, countRate, null, null, uploadRate, pageNum, pageSize);
            int count = subjectService.countSubjectInfo(
                    queryDeptCode, countRate, null, null, uploadRate);

            Map<String, Object> data = new HashMap<>();
            data.put("list", subjectInfos);
            data.put("totalCount", count);
            response.setData(data);
            response.success();
        } catch (Exception e) {
            logger.error("Exception", e);
            response.error(e);
        }
        return response;
    }

    /**
     * 查询要审核的主题信息
     * @param countRate 统计周期
     * @param uploadStatus 是否上传
     * @param statusCode 审核状态
     * @param deptCode 部门编码
     */
    @RequestMapping(value = "/queryCheck/v1", method = RequestMethod.GET)
    @ResponseBody
    public ControllerResponse queryCheckSubjectInfo(
            String countRate, String deptCode, Integer uploadStatus,
            Integer statusCode, Integer pageNum, Integer pageSize) {
        ControllerResponse response = new ControllerResponse();

        Boolean isNDRC = getIsNDRCFromSession();
        String workingCommitteeCode = getWorkingCommitteeCodeFromSession();
        logger.debug("查询要审核的主题信息：请求参数 countRate = {}, uploadStatus = {}, " +
                "statusCode = {}, pageNum = {}, pageSize = {}, isNDRC = {}",
                countRate, uploadStatus, statusCode, pageNum, pageSize, isNDRC);

        String uploadRate = CountQuarterUtils.getUploadRate(countRate);

        if (StringUtils.isBlank(deptCode) || StringUtils.equals(deptCode, "0")) {
            deptCode = null;
        }

        if (statusCode != null && statusCode == 999) {//全部状态
            statusCode = null;
        }

        Boolean isUpload = null;
        if (uploadStatus == null || uploadStatus == 0) {
            isUpload = null;
        } else if (uploadStatus == 1) {
            isUpload = true;
        } else if (uploadStatus == 2) {
            isUpload = false;
        }

        try {
            int unCheckStatusCode = 0; //是‘发改委待审核状态编号’还是‘政府办公厅待审核状态编号’
            List<JkwwwSubjectInfo> subjectInfos = null;
            int count = 0;

            if (isNDRC != null) {//发改委和政府办公厅角色

                if (isNDRC) {
                    unCheckStatusCode = 0;

                    List<Integer> statusCodes = new ArrayList<>();
                    statusCodes.add(statusCode);
                    if (statusCode == null) {
                        statusCodes = null;
                    }
                    subjectInfos = subjectService.querySubjectInfo(
                            deptCode, countRate, statusCodes, isUpload,
                            uploadRate, pageNum, pageSize);
                    count = subjectService.countSubjectInfo(
                            deptCode, countRate, statusCodes, isUpload, uploadRate);

                } else {

                    unCheckStatusCode = 6;
                    List<Integer> statusCodes = new ArrayList<>();
                    if (statusCode == null) {
                        statusCodes.add(6);
                        statusCodes.add(7);
                        statusCodes.add(8);
                    } else {
                        statusCodes.add(statusCode);
                    }
                    subjectInfos = subjectService.querySubjectInfo(
                            deptCode, countRate, statusCodes, isUpload, uploadRate, pageNum, pageSize);
                    count = subjectService.countSubjectInfo(
                            deptCode, countRate, statusCodes, isUpload, uploadRate);
                }

            } else {//政府部门角色
                if (workingCommitteeCode.equals("GWBM_03")){
                    List<Integer> statusCodes = new ArrayList<>();
                    statusCodes.add(statusCode);
                    if (statusCode == null) {
                        statusCodes = null;
                    }
                    subjectInfos = subjectService.querySubjectInfo(
                            deptCode, countRate, statusCodes, isUpload,
                            uploadRate, pageNum, pageSize);
                    count = subjectService.countSubjectInfo(
                            deptCode, countRate, statusCodes, isUpload, uploadRate);
                }else {
                    response.error("用户权限不足！");
                    return response;
                }
            }

            List<JkwwwSubjectInfo> allSubjectInfos = subjectService.querySubjectInfo(
                    deptCode, countRate, null, null, uploadRate, null, null);
            int unSubmitNum = 0;
            int unCheckNum = 0;
            for (JkwwwSubjectInfo subjectInfo : allSubjectInfos) {
                if (subjectInfo.getStatusCode() == null) {
                    ++ unSubmitNum;
                } else if (subjectInfo.getStatusCode() == unCheckStatusCode) {
                    ++ unCheckNum;
                }
            }
            Map<String, Object> data = new HashMap<>();
            data.put("list", subjectInfos);
            data.put("totalCount", count);
            data.put("unSubmitNum", unSubmitNum);
            data.put("unCheckNum", unCheckNum);
            boolean allSubmit = subjectService.isAllSubmit(countRate, isNDRC, uploadRate);
            data.put("isAllSubmit", allSubmit);
            response.setData(data);
            response.success();
        } catch (Exception e) {
            logger.error("Exception", e);
            response.error(e);
        }
        return response;
    }

    /**
     * 审核
     * @param subjectStatusIds 主题状态ID，多个使用逗号分隔
     */
    @ResponseBody
    @RequestMapping(value = "/checkAudit/v1", method = RequestMethod.POST)
    public ControllerResponse checkAudit(
            String subjectStatusIds) {
        logger.debug("审核校验接口：请求参数 subjectStatusIds = {}",
                subjectStatusIds);
        ControllerResponse response = new ControllerResponse();
        String[] split = StringUtils.split(subjectStatusIds, ",");
        for (String statusId : split) {
            if (statusId.equals("null")){
                response.setMessage("请确保所有审核主题数据已上传！");
                response.error();
                return response;
            }
        }
        String userCode = getUserCodeFromSession();
        Boolean isNDRC = getIsNDRCFromSession();
        logger.debug("当前用户信息： userCode = {}, isNDRC = {}", userCode, isNDRC);
        if (isNDRC == null) {
            response.error();
            response.setMessage("审核失败，只有发改委和市政府办公厅可以审核！");
            return response;
        }else {
            response.success();
        }
        return response;
    }

    /**
     * 审核
     * @param subjectStatusIds 主题状态ID，多个使用逗号分隔
     * @param countRate 统计周期
     * @param isPass 审核是否通过
     */
    @ResponseBody
    @RequestMapping(value = "/check/v1", method = RequestMethod.POST)
    public ControllerResponse checkSubject(
            String subjectStatusIds, String countRate, Boolean isPass) {
        logger.debug("审核接口：请求参数 subjectStatusIds = {}, countRate = {}, isPass = {}",
                subjectStatusIds, countRate, isPass);
        ControllerResponse response = new ControllerResponse();
        String[] split = StringUtils.split(subjectStatusIds, ",");

        List<Integer> statusIds = new ArrayList<>();
        for (String statusId : split) {
            if (statusId.equals("null")){
                response.setMessage("请确保所有审核主题数据已上传！");
                response.error();
                return response;
            }
            statusIds.add(Integer.valueOf(statusId));
        }

        String userCode = getUserCodeFromSession();
        Boolean isNDRC = getIsNDRCFromSession();
        logger.debug("当前用户信息： userCode = {}, isNDRC = {}", userCode, isNDRC);

        if (isNDRC == null) {
            response.error();
            response.setMessage("审核失败，只有发改委和市政府办公厅可以审核！");
            return response;
        }

        try {
            subjectService.check(statusIds, countRate, isPass, userCode, isNDRC);
            response.success();
        } catch (Exception e) {
            logger.error("Exception", e);
            response.error(e);
        }
        return response;
    }

    /**
     * 整体提交校验
     * @param countRate 统计周期
     */
    @ResponseBody
    @RequestMapping(value = "/checkAllSubmit/v1", method = RequestMethod.POST)
    public ControllerResponse checkAll(String countRate) {
        logger.debug("整体提交检查，请求参数=" + countRate);
        ControllerResponse response = new ControllerResponse();
        Boolean isNDRC = getIsNDRCFromSession();
        try {
            if (StringUtils.isBlank(countRate)) {
                response.error("统计周期不能为空！");
                return response;
            }
            String uploadRate = CountQuarterUtils.getUploadRate(countRate);
            if (isNDRC == null){
                response.setMessage("只有市发改委及市府办公厅可以整体提交！");
                response.error();
                return response;
            }
            boolean isCheck = subjectService.isAllCheck(countRate, isNDRC, uploadRate);
            if (isCheck) {
                boolean allSubmit = subjectService.isAllSubmit(countRate, isNDRC, uploadRate);
                if (!allSubmit){
                    response.success();
                }else {
                    response.setMessage("请勿重复整体提交！");
                    response.error();
                }
            } else {
                response.setMessage("请确保所有主题数据审核通过，并且还未整体提交！");
                response.error();
            }
        } catch (Exception e) {
            logger.error("Exception", e);
            response.error(e);
        }
        return response;
    }

    /**
     * 整体提交
     * @param countRate 统计周期
     */
    @ResponseBody
    @RequestMapping(value = "/submitAll/v1", method = RequestMethod.POST)
    public ControllerResponse submitAll(String countRate) {
        logger.debug("整体提交，请求参数=" + countRate);
        ControllerResponse response = new ControllerResponse();
        String userCode = getUserCodeFromSession();
        Boolean isNDRC = getIsNDRCFromSession();
        try {
            if (StringUtils.isBlank(countRate)) {
                response.error("统计周期不能为空！");
                return response;
            }
            String uploadRate = CountQuarterUtils.getUploadRate(countRate);
            boolean isCheck = subjectService.isAllCheck(countRate, isNDRC, uploadRate);
            if (isCheck) {
                subjectService.submitAll(countRate, userCode, isNDRC);
                response.success();
            } else {
                response.setMessage("请确保所有主题数据审核通过，并且还未整体提交！");
                response.error();
            }
        } catch (Exception e) {
            logger.error("Exception", e);
            response.error(e);
        }
        return response;
    }

    /**
     * 查询部门信息
     */
    @ResponseBody
    @RequestMapping(value = "/queryDept/v1", method = RequestMethod.GET)
    public ControllerResponse queryDeptInfo() {
        ControllerResponse response = new ControllerResponse();
        try {
            List<JkwwDeptInfo> deptInfos = subjectService.queryJkwwDeptInfo();
            Map<String, Object> data = new HashMap<>();
            data.put("list", deptInfos);
            response.setData(data);
            response.success();
        } catch (Exception e) {
            logger.error("Exception", e);
            response.error(e);
        }
        return response;
    }

    /**
     * 发改委是否整体提交了
     * @param countRate 统计周期
     */
    @ResponseBody
    @RequestMapping(value = "/isSubmitAll/v1", method = RequestMethod.GET)
    public ControllerResponse isSubmitAll(String countRate) {
        ControllerResponse response = new ControllerResponse();
        try {
            if (!CountQuarterUtils.isValidQuarter(countRate)) {
                response.error("未获取到合法的统计周期参数，请仔细检查请求参数！");
                return response;
            }
            String uploadRate = CountQuarterUtils.getUploadRate(countRate);
            boolean isSubmitAll = subjectService.isSubmitAllByNDRC(countRate, uploadRate);
            response.setData(isSubmitAll);
            response.success();
        } catch (Exception e) {
            logger.error("Exception", e);
            response.error(e);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/queryCountRate/v1", method = RequestMethod.GET)
    public ControllerResponse queryCountRate() {
        ControllerResponse response = new ControllerResponse();

        List<CountRate> allCountRates = new ArrayList<>();

        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();

        if (month >= 7 && month <= 12) {
            if (year != 2018){
                allCountRates.add( new CountRate(year + "上半年", year + "Q2"));
            }
            if (year == 2018){
                allCountRates.add( new CountRate(year + "Q4", year + "Q4"));
            }
        }
        -- year;
        while (year >= 2018) {
            if (year == 2018){
                allCountRates.add( new CountRate(year + "Q4", year + "Q4"));
            }else {
                allCountRates.add( new CountRate(year + "下半年", year + "Q4"));
            }
            if (year != 2018){
                allCountRates.add( new CountRate(year + "上半年", year + "Q2"));
            }
            -- year;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("list", allCountRates);
        data.put("now", allCountRates.get(0));
        response.setData(data);
        response.success();
        return response;
    }

}
