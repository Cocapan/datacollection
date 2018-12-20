package cn.gz.rd.datacollection.controller;

import cn.gz.rd.datacollection.controller.request.DepartmentRequest;
import cn.gz.rd.datacollection.controller.request.PageRequest;
import cn.gz.rd.datacollection.controller.response.ControllerResponse;
import cn.gz.rd.datacollection.model.Department;
import cn.gz.rd.datacollection.service.DepartmentService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author panxuan
 * 部门控制层
 */
@Controller
@RequestMapping("/api/department")
public class DepartmentCtrl {

    private static Logger logger = LoggerFactory.getLogger(DepartmentCtrl.class);

    /**
     * 部门服务接口
     */
    private DepartmentService departmentService;

    @Autowired
    public DepartmentCtrl(DepartmentService departmentService){
        this.departmentService = departmentService;
    }

    /**
     * 通过工委编码/部门编码及部门名, 模糊匹配部门名
     * @param session HttpSession, 保存了用户的相关信息
     * @param departmentName 请求参数, 部门名
     * @return 响应结果
     */
    @RequestMapping("/names/v1")
    @ResponseBody
    public ControllerResponse getDepartmentNames(HttpSession session, @RequestParam(value = "departmentName") String departmentName) {
        ControllerResponse response = new ControllerResponse();
        try {
            Map<String, Object> resultMap = departmentService.getDepartmentNames(session, departmentName);
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
     * 通过部门编码, 模糊查询部门
     * @param session HttpSession, 保存了用户的相关信息
     * @param departmentId 请求参数, 部门编码
     * @return 响应结果
     */
    @RequestMapping("/ids/v1")
    @ResponseBody
    public ControllerResponse getDepartmentIds(HttpSession session, @RequestParam(value = "departmentId") String departmentId) {
        ControllerResponse response = new ControllerResponse();
        try {
            Map<String, Object> resultMap = departmentService.getDepartmentIds(session, departmentId);
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
     * 分页查询展示部门信息
     * @param session HttpSession, 保存了用户的相关信息
     * @param request 请求参数, 包括页码及每页展示数
     * @return 响应结果
     */
    @RequestMapping("/list/v1")
    @ResponseBody
    public ControllerResponse getShowDepartments(HttpSession session, @RequestBody PageRequest request) {
        ControllerResponse response = new ControllerResponse();
        try {
            Map<String, Object> resultMap = departmentService.getShowDepartments(session, request);
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
     * 条件查询展示部门信息
     * @param request 请求参数, 包括页码, 每页展示数, 部门编码, 部门名
     * @return 响应结果
     */
    @RequestMapping("/conditionQuery/v1")
    @ResponseBody
    public ControllerResponse conditionQuery(@RequestBody DepartmentRequest request) {
        ControllerResponse response = new ControllerResponse();
        try {
            Map<String, Object> resultMap = departmentService.getShowDepartmentsByCondition(request);
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
     * 增加部门信息
     * @param request 请求参数, 包括部门名, 工委编码数组, 菜单编码数组
     * @return 响应结果
     */
    @RequestMapping("/insert/v1")
    @ResponseBody
    public ControllerResponse addDepartment(HttpSession session, @RequestBody DepartmentRequest request) {
        ControllerResponse response = new ControllerResponse();
        try {
            Map<String, Object> resultMap = departmentService.addDepartment(session, request);
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
     * 修改部门信息
     * @param request 请求参数, 包括部门编码, 部门编码, 备注, 工委编码数组, 菜单编码数组
     * @return 响应结果
     */
    @RequestMapping("/update/v1")
    @ResponseBody
    public ControllerResponse alterDepartment(HttpSession session, @RequestBody DepartmentRequest request) {
        ControllerResponse response = new ControllerResponse();
        try {
            departmentService.alterDepartment(session, request);
            response.success();
        } catch (Exception e) {
            logger.error("Exception", e);
            response.error();
            response.setMessage(e.getMessage());
        }
        return response;
    }

    /**
     * 验证部门名是否存在
     * @param request 请求参数, 包括部门名, 部门编码
     * @return 响应结果
     */
    @RequestMapping("/verifyName/v1")
    @ResponseBody
    public ControllerResponse verifyDepartmentName(@RequestBody DepartmentRequest request) {
        ControllerResponse response = new ControllerResponse();
        try {
            Department department = departmentService.getByDepartmentName(request);
            if (department == null){
                response.success();
                response.setMessage("验证通过!");
            }else {
                if (StringUtils.isEmpty(request.getDepartmentId()) || !department.getDepartmentId().equals(request.getDepartmentId())){
                    response.error();
                    response.setMessage("该部门名已存在!");
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
     * 获取部门列表
     * @return 响应结果
     */
    @RequestMapping("/nameIds/v1")
    @ResponseBody
    public ControllerResponse getDepartmentsByWorkingCommitteeId(@RequestParam(value = "workingCommitteeId") String workingCommitteeId) {
        ControllerResponse response = new ControllerResponse();
        try {
            Map<String, Object> resultMap = departmentService.getDepartmentsByWorkingCommitteeId(workingCommitteeId);
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
     * 获取部门列表
     * @return 响应结果
     */
    @RequestMapping("/nameAndIds/all/v1")
    @ResponseBody
    public ControllerResponse getDepartments() {
        ControllerResponse response = new ControllerResponse();
        try {
            Map<String, Object> resultMap = departmentService.getDepartments();
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
