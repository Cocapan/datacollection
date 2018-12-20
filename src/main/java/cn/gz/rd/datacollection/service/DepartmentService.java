package cn.gz.rd.datacollection.service;

import cn.gz.rd.datacollection.controller.request.DepartmentRequest;
import cn.gz.rd.datacollection.controller.request.PageRequest;
import cn.gz.rd.datacollection.model.CommonException;
import cn.gz.rd.datacollection.model.Department;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author panxuan
 * 部门服务接口
 */
public interface DepartmentService {

    /**
     * 模糊匹配部门名
     * @param session HttpSession 保存了用户的相关信息
     * @param departmentName 部门名
     * @return 返回部门名列表
     */
    Map<String, Object> getDepartmentNames(HttpSession session, String departmentName);

    /**
     * 模糊匹配部门编码
     * @param session HttpSession 保存了用户的相关信息
     * @param departmentId 部门编码
     * @return 返回展示部门信息, 包括部门名及部门编码
     */
    Map<String, Object> getDepartmentIds(HttpSession session, String departmentId);

    /**
     * 获取展示部门列表
     * @param session HttpSession 保存了用户的相关信息
     * @param request 查询所需参数, 页码及每页展示数
     * @return 返回展示部门信息, 包括部门编码, 部门名, 所属工委, 所属权限
     */
    Map<String, Object> getShowDepartments(HttpSession session, PageRequest request);

    /**
     * 根据条件获取展示部门列表
     * @param request 查询所需参数, 包括页码, 每页展示数, 部门编码, 部门名
     * @return 返回展示部门信息, 包括部门编码, 部门名, 所属工委, 所属权限
     */
    Map<String, Object> getShowDepartmentsByCondition(DepartmentRequest request);

    /**
     * 增加部门信息
     * @param request 请求参数, 包括部门名, 工委编码数组, 菜单编码数组
     * @return 新增的部门编码
     */
    Map<String, Object> addDepartment(HttpSession session, DepartmentRequest request) throws CommonException;

    /**
     * 修改部门信息
     * @param request 请求参数, 包括部门编码, 部门编码, 备注, 工委编码数组, 菜单编码数组
     */
    void alterDepartment(HttpSession session, DepartmentRequest request) throws CommonException;

    /**
     * 根据部门名查询部门
     * @param request 请求参数, 包括部门名, 部门编码
     * @return 返回部门信息
     * @throws CommonException 部门名为空异常
     */
    Department getByDepartmentName(DepartmentRequest request) throws CommonException;

    /**
     * 获取部门信息列表
     * @return 返回部门信息列表
     */
    Map<String, Object> getDepartmentsByWorkingCommitteeId(String workingCommitteeId);

    /**
     * 获取部门信息列表
     * @return 返回部门信息列表
     */
    Map<String, Object> getDepartments();

}
