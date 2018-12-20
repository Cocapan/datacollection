package cn.gz.rd.datacollection.controller;

import cn.gz.rd.datacollection.controller.response.ControllerResponse;
import cn.gz.rd.datacollection.model.AccessoryFile;
import cn.gz.rd.datacollection.model.JkwwPrjImgInfo;
import cn.gz.rd.datacollection.model.SubjectInfo;
import cn.gz.rd.datacollection.model.UploadFileResultVO;
import cn.gz.rd.datacollection.service.*;
import cn.gz.rd.datacollection.utils.DownloadFileUtils;
import com.alibaba.fastjson.JSON;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
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

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 科教文卫文件处理相关的接口：文件上传、文件下载
 */
@Controller
@RequestMapping("/jkwww/file")
public class JkwwFileCtrl extends BaseCtrl {

    private static final Logger logger = LoggerFactory.getLogger(JkwwFileCtrl.class);

    @Autowired
    private JkwwFileUpDownloadService fileUpDownloadService;

    @Autowired
    private JkwwwSubjectService subjectService;

    @Autowired
    private SubjectInfoService subjectInfoService;

    @Autowired
    private SFTPService sftpService;

    @Autowired
    private JkwwPrjImgInfoService prjImgInfoService;

    @RequestMapping(value = "/downloadTemplate/v1", method = RequestMethod.GET)
    public ResponseEntity<byte[]> downloadTemplate(
            @RequestHeader("User-Agent") String userAgent, String subjectCode) {

        ResponseEntity<byte[]> responseEntity = null;

        SubjectInfo subjectInfo = subjectInfoService.selectBySubjectCode(subjectCode);
        if (subjectInfo == null) {
            logger.error("没有找到相应的主题信息，subjectCode=" + subjectCode);
            return responseEntity;
        }
        String templatePath = subjectInfo.getTemplatePath();
        try {
            logger.debug("模板文件的路径：" + templatePath);
            byte[] bytes = FileUtils.readFileToByteArray(new File(templatePath));
            String fileName = FilenameUtils.getName(templatePath);

            fileName = DownloadFileUtils.getFileNameByExplore(userAgent, fileName);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName);
            responseEntity = new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return responseEntity;
    }

    @RequestMapping(value = "/downloadCodeTable/v1", method = RequestMethod.GET)
    public ResponseEntity<byte[]> downloadCodeTable(
            @RequestHeader("User-Agent") String userAgent, String url) {
        ResponseEntity<byte[]> responseEntity = null;
        try {
            logger.debug("模板文件的路径：" + url);
            byte[] bytes = FileUtils.readFileToByteArray(new File(url));
            String fileName = FilenameUtils.getName(url);
            fileName = DownloadFileUtils.getFileNameByExplore(userAgent, fileName);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName);
            responseEntity = new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return responseEntity;
    }

    /**
     * 文件上传
     * @param subjectCode 主题编号
     * @param countRate 统计周期
     * @param dataType 数据类型
     * @param file 上传的文件
     * @param deptCode 数据所属的部门
     */
    @RequestMapping(value = "/uploadFile/v1", method = RequestMethod.POST)
    @ResponseBody
    public ControllerResponse uploadFile(
            String subjectCode, String countRate, String dataType,
            MultipartFile file, Integer subjectStatusId, String deptCode, String previewPath) {
        logger.debug("文件上传接口，请求参数：subjectCode = {}, countRate = {}, " +
                        "dataType = {}, file = {}, subjectStatusId = {}, dataDeptCode = {}, previewPath = {}",
                subjectCode, countRate, dataType, file, subjectStatusId, deptCode, previewPath);
        String userCode = getUserCodeFromSession();
        ControllerResponse response = new ControllerResponse();
        try {
            if (StringUtils.isBlank(countRate)) {
                response.error("统计周期不能为空！");
                return response;
            }
            SubjectInfo subjectInfo = subjectInfoService.selectBySubjectCode(subjectCode);
            UploadFileResultVO resultVO = fileUpDownloadService.uploadFile(userCode, subjectInfo,
                    countRate, deptCode, dataType, file, subjectStatusId, previewPath);
            response.success();
            response.setData(resultVO);
        } catch (Exception e) {
            logger.error("文件上传接口调用异常", e);
            response.error("上传出现错误，错误信息：" + e.getMessage(), e);
        }
        return response;
    }

    /**
     * 附件文件上传
     * @param countRate 统计周期
     * @param file 上传的附件文件
     */
    @RequestMapping(value = "/uploadAccessoryFile/v1", method = RequestMethod.POST)
    @ResponseBody
    public ControllerResponse uploadAccessoryFile(String countRate,  MultipartFile file) {
        logger.debug("附件上传接口，请求参数：countRate = {}, file = {}", countRate, file);
        String deptCode = getDeptCodeFromSession();
        ControllerResponse response = new ControllerResponse();
        try {
            if (StringUtils.isBlank(countRate)) {
                response.error("统计周期不能为空！");
                return response;
            }
            if (file.isEmpty()){
                response.error("上传文件为空文件!");
                return response;
            }
            AccessoryFile accessoryFile = fileUpDownloadService.uploadAccessoryFile(countRate, deptCode, file);
            response.success();
            response.setData(accessoryFile);
        } catch (Exception e) {
            logger.error("附件上传接口调用异常", e);
            response.error("上传出现错误，错误信息：" + e.getMessage(), e);
        }
        return response;
    }

    /**
     * 附件文件列表
     * @param countRate 统计周期
     */
    @RequestMapping(value = "/listAccessoryFile/v1")
    @ResponseBody
    public ControllerResponse listAccessoryFile(@RequestParam(value = "countRate") String countRate) {
        logger.debug("附件文件列表接口，请求参数：countRate = {}", countRate);
        String deptCode = getDeptCodeFromSession();
        ControllerResponse response = new ControllerResponse();
        Map<String, Object> resultMap = new HashMap<>();
        try {
            if (StringUtils.isBlank(countRate)) {
                response.error("统计周期不能为空！");
                return response;
            }
            List<AccessoryFile> accessoryFiles = fileUpDownloadService.listAccessoryFiles(countRate, deptCode);
            response.success();
            resultMap.put("dataList", accessoryFiles);
            response.setData(resultMap);
        } catch (Exception e) {
            logger.error("附件文件列表接口调用异常", e);
            response.error("查询附件文件列表出现错误，错误信息：" + e.getMessage(), e);
        }
        return response;
    }

    /**
     * 删除附件文件
     * @param id 附件文件唯一标识
     */
    @RequestMapping(value = "/deleteAccessoryFile/v1")
    @ResponseBody
    public ControllerResponse deleteAccessoryFile(@RequestParam(value = "id") int id) {
        logger.debug("删除附件文件接口，请求参数：id = {}", id);
        ControllerResponse response = new ControllerResponse();
        try {
            fileUpDownloadService.deleteAccessoryFiles(id);
            response.success();
        } catch (Exception e) {
            logger.error("删除附件文件接口调用异常", e);
            response.error("删除附件文件出现错误，错误信息：" + e.getMessage(), e);
        }
        return response;
    }

    /**
     * 文件下载
     * @param countRate 统计周期
     * @param subjectStatusIds 主题状态ID，多个使用逗号分隔
     */
    @RequestMapping(value = "/downloadFile/v1", method = RequestMethod.GET)
    public ResponseEntity<byte[]> downloadFile(
            @RequestHeader("User-Agent") String userAgent,
            String countRate, String subjectStatusIds) {
        logger.debug("文件下载：请求参数 userAgent = {}, countRate = {}, subjectStatusIds = {}",
                userAgent, countRate, subjectStatusIds);
        String[] split = StringUtils.split(subjectStatusIds, ",");
        List<Integer> statusIds = new ArrayList<>();
        for (String statusId : split) {
            statusIds.add(Integer.valueOf(statusId));
        }
        ResponseEntity<byte[]> responseEntity = null;
        try {
            File file = fileUpDownloadService.downloadFile(statusIds, countRate);
            String fileName = DownloadFileUtils.getFileNameByExplore(userAgent, file.getName());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName);
            responseEntity = new ResponseEntity<>(FileUtils.readFileToByteArray(file),
                    headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return responseEntity;
    }

    /**
     * 查看图片
     * @param imgSavePath 图片所在的HDFS路径
     * @param imgFileName 图片文件名称
     */
    @RequestMapping(value = "/viewImg/v1", method = RequestMethod.GET)
    public ResponseEntity<byte[]> viewImg(
            @RequestHeader("User-Agent") String userAgent,
            String imgSavePath, String imgFileName) {
        logger.debug("查看图片：请求参数 imgPath = {}, imgFileName = {}", imgSavePath, imgFileName);
        ResponseEntity<byte[]> responseEntity = null;
        SFTPClient sftpClient;
        try {
            sftpClient = sftpService.createClientAndLgoin();
            String imgSaveDir = FilenameUtils.getFullPath(imgSavePath);
            String imgSaveFileName = FilenameUtils.getName(imgSavePath);
            byte[] data = sftpClient.download(imgSaveDir, imgSaveFileName);

            String fileName = imgFileName;
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

    /**
     * 查看图片
     * @param infoId 图片文件ID
     */
    @RequestMapping(value = "/viewImgById/v1", method = RequestMethod.GET)
    public ResponseEntity<byte[]> viewImg(
            @RequestHeader("User-Agent") String userAgent, Integer infoId) {
        logger.info("查看图片接口： 请求参数 infoId=" + infoId);
        if (infoId == null) {
            logger.error("查看图片接口： infoId = null");
            return null;
        }
        JkwwPrjImgInfo prjImgInfo = prjImgInfoService.selectByPrimaryKey(infoId);
        if (prjImgInfo == null) {
            logger.error("查看图片接口：没有找到图片信息。infoId = " + infoId);
            return null;
        }
        ResponseEntity<byte[]> responseEntity = viewImg(userAgent,
                prjImgInfo.getImgSavePath(), prjImgInfo.getImgFileName());
        return responseEntity;
    }

    /**
     * 查看图片
     * @param subjectCode 主题编码
     * @param countRate 统计周期
     */
    @RequestMapping(value = "/listImg/v1", method = RequestMethod.GET)
    @ResponseBody
    public ControllerResponse listImg(String subjectCode, String countRate) {
        ControllerResponse response = new ControllerResponse();
        logger.info("查询图片接口： 请求参数 countRate=" + countRate + ",subjectCode=" + subjectCode);
        try{
            List<JkwwPrjImgInfo> jkwwPrjImgInfos = subjectService.queryPrjImgInfos(subjectCode, countRate);
            response.setData(jkwwPrjImgInfos);
            response.success();
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return response;
    }

    /**
     * 删除图片
     * @param infoId 文件ID
     */
    @ResponseBody
    @RequestMapping(value = "/delImg/v1", method = RequestMethod.POST)
    public ControllerResponse delImg(Integer infoId, Integer subjectStatusId) {
        logger.debug("删除图片：请求参数 infoId = {}", infoId);
        ControllerResponse response = new ControllerResponse();
        if (infoId == null) {
            response.error();
            response.setMessage("删除图片时，文件ID不能为空！");
            return response;
        }
        try {
            String userCode = getUserCodeFromSession();
            subjectService.deleteImgInfo(infoId, userCode, subjectStatusId);
            response.success();
        } catch (Exception e) {
            logger.error("Exception", e);
            response.error(e);
        }
        return response;
    }

}
