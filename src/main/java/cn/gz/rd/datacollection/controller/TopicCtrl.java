package cn.gz.rd.datacollection.controller;

import cn.gz.rd.datacollection.controller.request.PageRequest;
import cn.gz.rd.datacollection.controller.request.TopicRequest;
import cn.gz.rd.datacollection.controller.response.ControllerResponse;
import cn.gz.rd.datacollection.model.CommonException;
import cn.gz.rd.datacollection.model.User;
import cn.gz.rd.datacollection.service.TopicService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

/**
 * @author panxuan
 * 主题控制层
 */
@Controller
@RequestMapping("/api/topic")
public class TopicCtrl {

    private static Logger logger = LoggerFactory.getLogger(TopicCtrl.class);

    /**
     * 主题服务接口
     */
    private TopicService topicService;

    @Autowired
    public TopicCtrl(TopicService topicService){
        this.topicService = topicService;
    }

    /**
     * 获取本月需上传主题
     * @param session HttpSession 保存了用户的相关信息
     * @param request 请求参数, 包括页码及每页展示数
     * @return 响应结果
     */
    @RequestMapping("/toUpload/page/v1")
    @ResponseBody
    public ControllerResponse getToUploadTopics(HttpSession session, @RequestBody PageRequest request) {
        ControllerResponse response = new ControllerResponse();
        try {
            Map<String, Object> resultMap = topicService.getToUploadTopics(session, request);
            response.setData(resultMap);
            response.success();
        } catch (Exception e) {
            logger.error("Exception", e);
            response.error();
            response.setMessage(e.getMessage());
        }
        return response;
    }


    /**
     * 获取超时未上传主题列表
     * @param session HttpSession 保存了用户的相关信息
     * @param request 请求参数, 包括页码及每页展示数
     * @return 响应结果
     */
    @RequestMapping("/notYetUpload/page/v1")
    @ResponseBody
    public ControllerResponse getNotYetUploadTopics(HttpSession session, @RequestBody PageRequest request) {
        ControllerResponse response = new ControllerResponse();
        try {
            Map<String, Object> resultMap = topicService.getNotYetUploadTopics(session, request);
            response.setData( resultMap);
            response.success();
        } catch (Exception e) {
            logger.error("Exception", e);
            response.error();
            response.setMessage(e.getMessage());
        }
        return response;
    }

    /**
     * 上传主题文件
     * @param session HttpSession 保存了用户的相关信息
     * @param workingCommitteeId 工委编码
     * @param departmentId 部门编码
     * @param topicId 主题编码
     * @param statisticsPeriod 统计周期
     * @param uploadPeriod 上报频度
     * @param topicType 主题类型
     * @param storageTableName 存储表名
     * @param uploadTime 上传时间
     * @param previewUrl 文件在线预览路径
     * @param filePath 文件路径
     * @return 响应结果
     */
    @RequestMapping("/upload/v1")
    @ResponseBody
    public ControllerResponse uploadTopic(HttpSession session,
                                          @RequestParam("workingCommitteeId") String workingCommitteeId,
                                          @RequestParam("departmentId") String departmentId,
                                          @RequestParam("topicId") String topicId,
                                          @RequestParam("statisticsPeriod") String statisticsPeriod,
                                          @RequestParam("uploadPeriod") String uploadPeriod,
                                          @RequestParam("topicType") String topicType,
                                          @RequestParam("storageTableName") String storageTableName,
                                          @RequestParam("uploadTime") String uploadTime,
                                          @RequestParam("previewUrl") String previewUrl,
                                          @RequestParam("filePath") String filePath) {
        ControllerResponse response = new ControllerResponse();
        User user = (User) session.getAttribute("user");
        String userName = user.getUserName();
        if (StringUtils.isEmpty(userName)){
            response.error();
            response.setMessage("用户名为空");
        }
        try {
            topicService.uploadFile(workingCommitteeId, departmentId, topicId, statisticsPeriod, uploadPeriod, topicType, storageTableName, userName, uploadTime, previewUrl, filePath);
            response.success();
        } catch (CommonException e) {
            logger.error("CommonException", e);
            response.error();
            response.setMessage(e.getMessage());
        } catch (Exception e){
            logger.error("CommonException", e);
            response.error();
            response.setMessage("上传失败!");
        }

        return response;
    }

    /**
     * 获取上传主题列表
     * @param session HttpSession 保存了用户的相关信息
     * @param request 请求参数, 包括页码及每页展示数
     * @return 响应结果
     */
    @RequestMapping("/uploadTopic/list/v1")
    @ResponseBody
    public ControllerResponse getUploadTopics(HttpSession session, @RequestBody PageRequest request) {
        ControllerResponse response = new ControllerResponse();
        try {
            Map<String, Object> resultMap = topicService.getUploadTopics(session, request);
            response.setData(resultMap);
            response.success();
        } catch (Exception e) {
            logger.error("Exception", e);
            response.error();
            response.setMessage(e.getMessage());
        }
        return response;
    }

    /**
     * 条件查询上传主题列表
     * @param session HttpSession 保存了用户的相关信息
     * @param request 请求参数, 包括部门名, 主题类型, 页码, 每页展示数, 年份, 月份
     * @return 响应结果
     */
    @RequestMapping("/uploadTopic/query/v1")
    @ResponseBody
    public ControllerResponse getUploadTopicsByCondition(HttpSession session, @RequestBody TopicRequest request) {
        ControllerResponse response = new ControllerResponse();
        try {
            Map<String, Object> resultMap = topicService.getUploadTopicsByCondition(session, request);
            response.setData(resultMap);
            response.success();
        } catch (Exception e) {
            logger.error("Exception", e);
            response.error();
            response.setMessage(e.getMessage());
        }
        return response;
    }

    /**
     * 获取主题名列表
     * @param session HttpSession 保存了用户的相关信息
     * @return 主题名列表
     */
    @RequestMapping("/name/list/v1")
    @ResponseBody
    public ControllerResponse getTopicNames(HttpSession session) {
        ControllerResponse response = new ControllerResponse();
        try {
            Map<String, Object> resultMap = topicService.getTopicNames(session);
            response.setData(resultMap);
            response.success();
        } catch (Exception e) {
            logger.error("Exception", e);
            response.error();
            response.setMessage(e.getMessage());
        }
        return response;
    }

    /**
     * 获取年份列表
     * @return 年份列表
     */
    @RequestMapping("/year/list/v1")
    @ResponseBody
    public ControllerResponse getYears() {
        ControllerResponse response = new ControllerResponse();
        try {
            Map<String, Object> resultMap = topicService.getYears();
            response.setData(resultMap);
            response.success();
        } catch (Exception e) {
            logger.error("Exception", e);
            response.error();
            response.setMessage(e.getMessage());
        }
        return response;
    }

    /**
     * 根据年份获取月份列表
     * @param year 年份
     * @return 月份列表
     */
    @RequestMapping("/month/list/v1")
    @ResponseBody
    public ControllerResponse getMonths(@RequestParam(value = "year") int year) {
        ControllerResponse response = new ControllerResponse();
        try {
            Map<String, Object> resultMap = topicService.getMonths(year);
            response.setData(resultMap);
            response.success();
        } catch (Exception e) {
            logger.error("Exception", e);
            response.error();
            response.setMessage(e.getMessage());
        }
        return response;
    }

    /**
     * 获取数据类型列表
     * @return 数据类型列表
     */
    @RequestMapping("/topicType/list/v1")
    @ResponseBody
    public ControllerResponse getTopicTypes(HttpSession session) {
        ControllerResponse response = new ControllerResponse();
        try {
            Map<String, Object> resultMap = topicService.getTopicTypes(session);
            response.setData(resultMap);
            response.success();
        } catch (Exception e) {
            logger.error("Exception", e);
            response.error();
            response.setMessage(e.getMessage());
        }
        return response;
    }

    /**
     * 批量导入主题数据表
     * @param session HttpSession 保存了用户的相关信息
     * @param year 年份
     * @param month 月份
     * @param uploadFile 上传文件
     * @return 响应结果
     */
    @RequestMapping("/batchUpload/v1")
    @ResponseBody
    public ControllerResponse uploadTopics(HttpSession session,
                                           @RequestParam("year") int year,
                                           @RequestParam("month") int month,
                                           @RequestParam("file") MultipartFile uploadFile) {
        ControllerResponse response = new ControllerResponse();
        try {
            topicService.uploadFile(session, year, month, uploadFile);
            response.success();
        } catch (CommonException e) {
            logger.error("CommonException", e);
            response.error();
            response.setMessage(e.getMessage());
        } catch (Exception e){
            logger.error("Exception", e);
            response.error();
            response.setMessage("上传失败!");
        }
        return response;
    }

    /**
     * 在线预览
     * @param session HttpSession 保存了用户的相关信息
     * @param statisticsPeriod 统计周期
     * @param topicId 主题编码
     * @param uploadFile 上传文件
     * @return 响应结果
     */
    @RequestMapping("/onlinePreview/v1")
    @ResponseBody
    public ControllerResponse onlinePreview(HttpSession session,@RequestParam("statisticsPeriod") String statisticsPeriod, @RequestParam("topicId") String topicId, @RequestParam("topicType") String topicType, @RequestParam("uploadTime") String uploadTime, @RequestParam("departmentId") String departmentId, @RequestParam("isJkww") boolean isJkww, @RequestParam("file") MultipartFile uploadFile) {
        ControllerResponse response = new ControllerResponse();
        try {
            Map<String, Object> resultMap = topicService.onlinePreview(session, statisticsPeriod, topicId, topicType, uploadTime, departmentId, isJkww, uploadFile);
            response.success();
            response.setData(resultMap);
        } catch (CommonException e) {
            logger.error("CommonException", e);
            response.error();
            response.setMessage(e.getMessage());
        }catch (Exception e){
            logger.error("Exception", e);
            response.error();
            response.setMessage("在线预览文件构建失败!");
        }
        return response;
    }

    /**
     * 在线预览pdf接口
     * @param previewUrl 预览路径
     * @param request http请求
     * @param response http响应
     */
    @RequestMapping("/onlinePreview/pdf/v1")
    @ResponseBody
    public void onlinePreview(@RequestParam(value = "previewUrl") String previewUrl, HttpServletRequest request, HttpServletResponse response) {
        String pre = "/data/file";
        File file = new File(pre + previewUrl);
        byte[] data;
        try {
            FileInputStream inputStream = new FileInputStream(file);
            data = new byte[inputStream.available()];
            inputStream.read(data);
            response.getOutputStream().write(data);
            inputStream.close();
        }catch (Exception e){
            logger.error("pdf文件处理异常：", e);
        }
    }

    /**
     * 删除在线预览文件
     * @param previewUrl 预览文件路径
     * @param filePath 原文件路径
     * @return 响应结果
     */
    @RequestMapping("/onlinePreview/delete/v1")
    @ResponseBody
    public ControllerResponse deletePreviewUrl(@RequestParam("previewUrl") String previewUrl, @RequestParam("filePath") String filePath) {
        ControllerResponse response = new ControllerResponse();
        try {
            topicService.deletePreviewUrl(previewUrl, filePath);
            response.success();
        } catch (Exception e){
            logger.error("Exception", e);
            response.error();
            response.setMessage("预览文件删除失败!");
        }
        return response;
    }


}
