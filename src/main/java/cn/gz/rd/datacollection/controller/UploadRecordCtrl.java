package cn.gz.rd.datacollection.controller;

import cn.gz.rd.datacollection.controller.request.PageRequest;
import cn.gz.rd.datacollection.controller.request.UploadRecordRequest;
import cn.gz.rd.datacollection.controller.response.ControllerResponse;
import cn.gz.rd.datacollection.service.UploadRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author panxuan
 * 上传记录控制层
 */
@Controller
@RequestMapping("/api/uploadRecord")
public class UploadRecordCtrl {

    private static Logger logger = LoggerFactory.getLogger(UploadRecordCtrl.class);

    /**
     * 上传记录服务接口
     */
    private UploadRecordService uploadRecordService;

    @Autowired
    public UploadRecordCtrl(UploadRecordService uploadRecordService){
        this.uploadRecordService = uploadRecordService;
    }

    /**
     * 获取上传记录列表
     * @param session HttpSession 保存了用户的相关信息
     * @param request 请求参数, 包括页码及每页展示数
     * @return 响应结果
     */
    @RequestMapping(value = "/list/v1", method = RequestMethod.POST)
    @ResponseBody
    public ControllerResponse uploadRecord(HttpSession session, @RequestBody PageRequest request){
        ControllerResponse response = new ControllerResponse();
        try {
            Map<String, Object> resultMap = uploadRecordService.getUploadRecordList(session, request);
            response.setData(resultMap);
            response.success();
        } catch (Exception e){
            logger.error("Exception", e);
            response.setMessage(e.getMessage());
            response.error();
        }
        return response;
    }

    /**
     * 条件查询获取上传记录列表
     * @param session HttpSession 保存了用户的相关信息
     * @param request 请求参数, 包括部门名, 主题名, 上报频度, 日期数组页码及每页展示数
     * @return 上传记录列表
     */
    @RequestMapping(value = "/queryByCondition/v1", method = RequestMethod.POST)
    @ResponseBody
    public ControllerResponse queryByCondition(HttpSession session, @RequestBody UploadRecordRequest request){
        ControllerResponse response = new ControllerResponse();
        try {
            Map<String, Object> resultMap = uploadRecordService.getUploadRecordListByCondition(session, request);
            response.setData(resultMap);
            response.success();
        } catch (Exception e){
            logger.error("Exception", e);
            response.setMessage(e.getMessage());
            response.error();
        }
        return response;
    }

    /**
     * 获取上报频度列表
     * @param session HttpSession 保存了用户的相关信息
     * @return 上传记录列表
     */
    @RequestMapping(value = "/uploadPeriod/v1", method = RequestMethod.POST)
    @ResponseBody
    public ControllerResponse getUploadPeriod(HttpSession session){
        ControllerResponse response = new ControllerResponse();
        try {
            Map<String, Object> resultMap = uploadRecordService.getUploadPeriods(session);
            response.setData(resultMap);
            response.success();
        } catch (Exception e){
            logger.error("Exception", e);
            response.setMessage(e.getMessage());
            response.error();
        }
        return response;
    }

}
