package cn.gz.rd.datacollection.controller;

import cn.gz.rd.datacollection.constant.CommitteeConstant;
import cn.gz.rd.datacollection.controller.request.UpdateUsableRequest;
import cn.gz.rd.datacollection.controller.response.ControllerResponse;
import cn.gz.rd.datacollection.model.FileDataVO;
import cn.gz.rd.datacollection.model.SubjectInfo;
import cn.gz.rd.datacollection.service.SubjectInfoService;
import cn.gz.rd.datacollection.utils.DownloadFileUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统主题管理
 * @author Peng Xiaodong
 */
@Controller
@RequestMapping("/jkwww/sysSubject")
public class SubjectInfoCtrl extends BaseCtrl {

    public final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SubjectInfoService subjectInfoService;

    @RequestMapping(value = "/save/v1", method = RequestMethod.POST)
    @ResponseBody
    public ControllerResponse save(SubjectInfo subjectInfo, MultipartFile file) {
        logger.debug("保存主题：请求参数信息=" + subjectInfo.toString());
        ControllerResponse response = new ControllerResponse();
        try {
            String committeeCode = subjectInfo.getCommitteeCode();
            if (StringUtils.isBlank(committeeCode)) {
                response.error("保存失败，未获取到工委编号！");
                return response;
            }
            String subjectType = subjectInfo.getSubjectType();
            if (StringUtils.isBlank(subjectType)) {
                response.error("保存失败，主题类型为必填项！");
                return response;
            }
            if (StringUtils.equals(subjectType, "数据表") && file == null) {
                response.error("保存失败，数据表类型的主题必须上传模板！");
                return response;
            }
            String[] deptCodes = subjectInfo.getDeptCodes();
            boolean isNeedDept = isNeedDept(subjectInfo);
            if (isNeedDept && (deptCodes == null || deptCodes.length == 0)) {
                response.error("保存失败，部门为必选项！");
                return response;
            }
            if (!isNeedDept) {
                subjectInfo.setDeptCodes(null);
            }
            String userCode = getUserCodeFromSession();
            subjectInfo.setCreateDate(new Date());
            subjectInfo.setCreateUserCode(userCode);
            subjectInfo.setEnableFlag(1);//默认可用
            subjectInfo.setUsableFlag(1);//默认启用
            subjectInfoService.saveSubject(subjectInfo, file);
            response.setData(subjectInfo.getSubjectCode());
            response.success();
        } catch (Exception e) {
            logger.error("保存主题信息出现异常！", e);
            response.error(e);
        }
        return response;
    }

    /**
     * 判断是否需要部门信息
     * @return
     */
    private boolean isNeedDept(SubjectInfo subjectInfo) {
        String committeeCode = subjectInfo.getCommitteeCode();
        String subjectType = subjectInfo.getSubjectType();

        if (StringUtils.equals(committeeCode, CommitteeConstant.JKWW_COMMITTEE_CODE)
                && StringUtils.equals(subjectType, "目录")) {
            return false;
        } else {
            return true;
        }
    }

    @RequestMapping(value = "/update/v1", method = RequestMethod.POST)
    @ResponseBody
    public ControllerResponse update(SubjectInfo subjectInfo, MultipartFile file) {
        logger.debug("更新主题：请求参数信息=" + subjectInfo.toString());
        ControllerResponse response = new ControllerResponse();
        try {
            String[] deptCodes = subjectInfo.getDeptCodes();
            boolean isNeedDept = isNeedDept(subjectInfo);
            if (isNeedDept && (deptCodes == null || deptCodes.length == 0)) {
                response.error("保存失败，部门为必选项！");
                return response;
            }
            if (!isNeedDept) {
                subjectInfo.setDeptCodes(null);
            }
            String userCode = getUserCodeFromSession();
            subjectInfo.setUpdateDate(new Date());
            subjectInfo.setUpdateUserCode(userCode);
            subjectInfoService.updateSubject(subjectInfo, file);
            response.success();
        } catch (Exception e) {
            logger.error("更新主题信息出现异常！", e);
            response.error(e);
        }
        return response;
    }

    @RequestMapping(value = "/disable/v1", method = RequestMethod.POST)
    @ResponseBody
    public ControllerResponse disable(String subjectCode) {
        logger.debug("禁用主题：请求参数信息 subjectCode=" + subjectCode);
        ControllerResponse response = new ControllerResponse();
        try {
            boolean isSucc = subjectInfoService.disableSubject(subjectCode);
            if (isSucc) {
                response.success();
            } else {
                response.error("禁用主题出现问题，请联系管理员！");
            }
        } catch (Exception e) {
            logger.error("禁用主题信息出现异常！", e);
            response.error(e);
        }
        return response;
    }

    @RequestMapping(value = "/query/v1", method = RequestMethod.GET)
    @ResponseBody
    public ControllerResponse query(
            String subjectCode, String subjectName, String deptCode,
            String committeeCode, Boolean isUsable,
            Integer pageNum, Integer pageSize) {
        logger.debug("查询主题：请求参数信息 subjectCode = {}, subjectName = {}, " +
                        "deptCode = {}, committeeCode = {}, pageNum = {}, pageSize = {}",
                subjectCode, subjectName, deptCode, committeeCode, pageNum, pageSize);
        ControllerResponse response = new ControllerResponse();
        try {
            if (StringUtils.isBlank(deptCode) || StringUtils.equals(deptCode, "0")) {
                deptCode = null;
            }
            if (StringUtils.isBlank(committeeCode) || StringUtils.equals(committeeCode, "0")) {
                committeeCode = null;
            }

            List<SubjectInfo> subjectInfos = subjectInfoService.querySubject(
                    subjectCode, subjectName, deptCode, committeeCode, isUsable, pageNum, pageSize);
            int count = subjectInfoService.countSubject(
                    subjectCode, subjectName, deptCode, committeeCode, isUsable);
            Map<String, Object> data = new HashMap<>();
            data.put("dataList", subjectInfos);
            data.put("count", count);
            response.setData(data);
            response.success();
        } catch (Exception e) {
            logger.error("查询主题信息出现异常！", e);
            response.error(e);
        }
        return response;
    }

    /**
     * 查询主题信息
     * @param pageNum 页码
     * @param pageNum 每页显示数
     */
    @RequestMapping(value = "/verify/query/v1", method = RequestMethod.GET)
    @ResponseBody
    public ControllerResponse queryVerifySubjectInfo(Integer pageNum, Integer pageSize) {
        ControllerResponse response = new ControllerResponse();
        logger.debug("查询主题信息：请求参数 pageNum = {}, pageSize = {}",
                pageNum, pageSize);
        try {

            List<SubjectInfo> subjectInfos = subjectInfoService.getVerifySubjectInfo(pageNum, pageSize);
            int count = subjectInfoService.countVerifySubjectInfo();
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

    @RequestMapping(value = "/downloadTemplate/v1", method = RequestMethod.GET)
    public ResponseEntity<byte[]> downloadTemplate(
            @RequestHeader("User-Agent") String userAgent, String subjectCode) {
        logger.debug("主题模板下载： 请求参数 subjectCode = " + subjectCode);
        ResponseEntity<byte[]> responseEntity = null;
        try {
            FileDataVO fileDataVO = subjectInfoService.downloadTemplate(subjectCode);
            String fileName = DownloadFileUtils.getFileNameByExplore(userAgent, fileDataVO.getFileName());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName);
            responseEntity = new ResponseEntity<>(
                    fileDataVO.getFileData(),
                    headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return responseEntity;
    }

    @RequestMapping(value = "/updateUsable/v1", method = RequestMethod.POST)
    @ResponseBody
    public ControllerResponse updateUsable(@RequestBody UpdateUsableRequest request) {
        logger.debug("主题启用与否：请求参数信息 = " + request);
        ControllerResponse response = new ControllerResponse();
        try {
            List<String> subjectCodes = request.getSubjectCodes();
            Boolean isUsable = request.getUsable();
            if (request == null) {
                response.error("操作失败，未获取到请求参数！");
                return response;
            }
            if (CollectionUtils.isEmpty(subjectCodes)) {
                response.error("操作失败，未获取到主题编号！");
                return response;
            }
            if (isUsable == null) {
                response.error("操作失败，未获取到‘可用标志’！");
                return response;
            }
            int updateCount = subjectInfoService.updateUsableFlag(subjectCodes, isUsable);
            response.success("操作成功，共更新" + updateCount + "条数据。");
        } catch (Exception e) {
            logger.error("主题启用与否接口执行出现异常", e);
            response.error(e);
        }
        return response;
    }

    @RequestMapping(value = "/queryParent/v1", method = RequestMethod.GET)
    @ResponseBody
    public ControllerResponse getParentSubject(String committeeCode) {
        logger.debug("查询主题：请求参数信息 committeeCode = {}",
                committeeCode);
        ControllerResponse response = new ControllerResponse();
        try {
            if (StringUtils.isBlank(committeeCode) || StringUtils.equals(committeeCode, "0")) {
                committeeCode = null;
            }
            List<SubjectInfo> subjectInfos = subjectInfoService.getParentSubject(committeeCode);
            Map<String, Object> data = new HashMap<>();
            data.put("dataList", subjectInfos);
            response.setData(data);
            response.success();
        } catch (Exception e) {
            logger.error("查询主题信息出现异常！", e);
            response.error(e);
        }
        return response;
    }

}
