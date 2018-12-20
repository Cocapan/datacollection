package cn.gz.rd.datacollection.controller;

import cn.gz.rd.datacollection.controller.request.PageRequest;
import cn.gz.rd.datacollection.controller.response.ControllerResponse;
import cn.gz.rd.datacollection.model.WorkingCommittee;
import cn.gz.rd.datacollection.service.WorkingCommitteeService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author panxuan
 * 工委控制层
 */
@Controller
@RequestMapping("/api/workingCommittee")
public class WorkingCommitteeCtrl {

    private static Logger logger = LoggerFactory.getLogger(WorkingCommitteeCtrl.class);

    /**
     * 工委服务接口
     */
    private WorkingCommitteeService workingCommitteeService;

    @Autowired
    public WorkingCommitteeCtrl(WorkingCommitteeService workingCommitteeService){
        this.workingCommitteeService = workingCommitteeService;
    }

    /**
     * 分页查询工委列表
     * @param request 请求参数, 包括页码及每页展示数
     * @return 响应结果
     */
    @RequestMapping("/list/v1")
    @ResponseBody
    public ControllerResponse listWorkingCommittee(@RequestBody PageRequest request) {
        ControllerResponse response = new ControllerResponse();
        try {
            Map<String, Object> resultMap = workingCommitteeService.getWorkingCommitteeList(request);
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
     * 增加工委
     * @param workingCommittee 工委
     * @return 响应结果
     */
    @RequestMapping("/insert/v1")
    @ResponseBody
    public ControllerResponse addWorkingCommittee(HttpSession session, @RequestBody WorkingCommittee workingCommittee) {
        ControllerResponse response = new ControllerResponse();
        try {
            Map<String, Object> resultMap = workingCommitteeService.addWorkingCommittee(session, workingCommittee);
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
     * 修改工委信息
     * @param workingCommittee 工委
     * @return 响应结果
     */
    @RequestMapping("/update/v1")
    @ResponseBody
    public ControllerResponse updateWorkingCommittee(HttpSession session, @RequestBody WorkingCommittee workingCommittee) {
        ControllerResponse response = new ControllerResponse();
        try {
            int result = workingCommitteeService.alterWorkingCommittee(session, workingCommittee);
            if (result == 0){
                response.error();
            }else {
                response.success();
            }
        } catch (Exception e) {
            logger.error("Exception", e);
            response.error();
            response.setMessage(e.getMessage());
        }
        return response;
    }

    /**
     * 验证工委名是否存在
     * @param request 请求参数, 包括工委名
     * @return 响应结果
     */
    @RequestMapping("/verifyName/v1")
    @ResponseBody
    public ControllerResponse verifyWorkingCommitteeName(@RequestBody WorkingCommittee request) {
        ControllerResponse response = new ControllerResponse();
        try {
            WorkingCommittee workingCommittee = workingCommitteeService.getByWorkingCommitteeName(request);
            if (workingCommittee == null){
                response.success();
                response.setMessage("验证通过!");
            }else {
                if (StringUtils.isEmpty(request.getWorkingCommitteeId()) || !workingCommittee.getWorkingCommitteeId().equals(request.getWorkingCommitteeId())){
                    response.error();
                    response.setMessage("该工委名已存在!");
                }else {
                    response.success();
                }
            }
        } catch (Exception e) {
            logger.error("Exception", e);
            response.error();
            response.setMessage(e.getMessage());
        }
        return response;
    }

    /**
     * 获取工委列表
     * @return 响应结果
     */
    @RequestMapping("/names/v1")
    @ResponseBody
    public ControllerResponse getWorkingCommittees() {
        ControllerResponse response = new ControllerResponse();
        try {
            Map<String, Object> resultMap = workingCommitteeService.getWorkingCommittees();
            response.setData(resultMap);
            response.success();
        } catch (Exception e) {
            logger.error("Exception", e);
            response.error();
            response.setMessage(e.getMessage());
        }
        return response;
    }

}
