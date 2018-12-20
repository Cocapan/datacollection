package cn.gz.rd.datacollection.controller;

import cn.gz.rd.datacollection.controller.response.ControllerResponse;
import cn.gz.rd.datacollection.model.JkwwBaseSujbect;
import cn.gz.rd.datacollection.service.JkwwBaseSubjectService;
import cn.gz.rd.datacollection.utils.DownloadFileUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author Peng Xiaodong
 */
@Controller
@RequestMapping("/jkwww/baseInfo")
public class JkwwBaseInfoCtrl extends BaseCtrl {

    private final Logger logger = LoggerFactory.getLogger(JkwwBaseInfoCtrl.class);

    @Autowired
    private JkwwBaseSubjectService baseSubjectService;

    @ResponseBody
    @RequestMapping(value = "/upload/v1", method = RequestMethod.POST)
    public ControllerResponse upload(Integer subjectNo, String previewPath, MultipartFile file) {
        logger.info("教科文卫基础信息上传，请求参数：subjectNo = {}, previewPath = {}, file = {}",
                subjectNo, previewPath, file.getOriginalFilename());
        ControllerResponse response = new ControllerResponse();

        if (subjectNo == null) {
            response.error("主题序号不能为空！");
            return response;
        }
        if (previewPath == null) {
            response.error("预览路径不能为空！");
            return response;
        }
        if (file == null || file.isEmpty()) {
            response.error("上传文件不能为空！");
            return response;
        }

        String userName = getUserCodeFromSession();
        try {
            baseSubjectService.uploadFile(userName, previewPath, subjectNo, file);
            response.success();
        } catch (Exception e) {
            logger.error("教科文卫基础信息上传失败！", e);
            response.error(e.getMessage(), e);
        }
        return response;
    }

    @RequestMapping(value = "/download/v1", method = RequestMethod.GET)
    public ResponseEntity<byte[]> download(
            @RequestHeader("User-Agent") String userAgent, Integer subjectNo) {
        logger.info("教科文卫基础信息下载请求参数：subjectNo = " + subjectNo);
        ResponseEntity<byte[]> responseEntity = null;
        try {
            File file = baseSubjectService.download(subjectNo);
            if (file == null) {
                return null;
            }
            String fileName = DownloadFileUtils.getFileNameByExplore(userAgent, file.getName());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName);
            responseEntity = new ResponseEntity<>(FileUtils.readFileToByteArray(file),
                    headers, HttpStatus.OK);
        } catch (IOException e) {
            logger.error("Exception", e);
        }
        return responseEntity;
    }

    @ResponseBody
    @RequestMapping(value = "/preview/v1", method = RequestMethod.POST)
    public ControllerResponse preview(MultipartFile file) {
        ControllerResponse response = new ControllerResponse();
        try {
            String previewPath = baseSubjectService.preview(file);
            response.setData(previewPath);
            response.success();
        } catch (Exception e) {
            logger.error("预览失败", e);
            response.error("预览出现问题，错误信息：" + e.getMessage(), e);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/query/v1", method = RequestMethod.GET)
    public ControllerResponse query() {
        ControllerResponse response = new ControllerResponse();
        try {
            List<JkwwBaseSujbect> baseSujbects = baseSubjectService.queryAll();
            if (CollectionUtils.isNotEmpty(baseSujbects)) {
                response.setData(baseSujbects);
                logger.debug("基础主题查询情况：size = " + baseSujbects.size());
            } else {
                logger.warn("查询到的基础主题为空，请检查！");
            }
            response.success();
        } catch (Exception e) {
            response.error(e.getMessage());
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/phoneBook/previewUrl/v1", method = RequestMethod.GET)
    public ControllerResponse getPhoneBookPreviewUrl(Integer subjectNo) {
        ControllerResponse response = new ControllerResponse();
        try {
            String previewUrl = baseSubjectService.getPhoneBookPreviewUrl(subjectNo);
            if (StringUtils.isNotEmpty(previewUrl)) {
                response.setData(previewUrl);
                logger.debug("通讯录预览路径：previewUrl = " + previewUrl);
            } else {
                logger.warn("查询到的预览为空，请检查！");
            }
            response.success();
        } catch (Exception e) {
            response.error(e.getMessage());
        }
        return response;
    }

}
