package cn.gz.rd.datacollection.service.imp;

import cn.gz.rd.datacollection.controller.request.DepartmentRequest;
import cn.gz.rd.datacollection.controller.request.PageRequest;
import cn.gz.rd.datacollection.dao.DepartmentMapper;
import cn.gz.rd.datacollection.dao.WorkDeptRelationTblMapper;
import cn.gz.rd.datacollection.model.*;
import cn.gz.rd.datacollection.service.DepartmentService;
import cn.gz.rd.datacollection.utils.ResultMapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * @author panxuan
 * 部门服务实现类
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {

    /**
     * 部门数据信息访问
     */
    private DepartmentMapper departmentMapper;

    /**
     * 工委部门关联数据信息访问
     */
    private WorkDeptRelationTblMapper workDeptRelationTblMapper;

    @Autowired
    public DepartmentServiceImpl(DepartmentMapper departmentMapper, WorkDeptRelationTblMapper workDeptRelationTblMapper){
        this.departmentMapper = departmentMapper;
        this.workDeptRelationTblMapper = workDeptRelationTblMapper;
    }

    @Override
    public Map<String, Object> getDepartmentNames(HttpSession session, String departmentName){
        //部门名列表
        List<String> departNameList = new ArrayList<>();
        //从session中获取用户信息
        User user = (User) session.getAttribute("user");
        //从用户信息中角色信息
        String role = user.getRole();
        if (role.equals("工委")){
            //从用户信息中工委编码
            String workingCommitteeId = user.getWorkingCommitteeId();
            departNameList = departmentMapper.selectDepartmentNamesByWorkCommitteeIdLikeDepartmentName(workingCommitteeId, departmentName);
        }
        if (role.equals("部门")){
            //从用户信息中部门编码
            String departmentId = user.getDepartmentId();
            departNameList = departmentMapper.selectDepartmentNamesByDepartmentId(departmentId);
        }
        if (role.equals("管理员")){
            departNameList = departmentMapper.selectDepartmentNamesLikeDepartmentName(departmentName);
        }
        return ResultMapUtils.getResultMap(departNameList);
    }

    @Override
    public Map<String, Object> getDepartmentIds(HttpSession session, String departmentId) {
        List<String> departmentIds = new ArrayList<>();
        //从session中获取用户信息
        User user = (User) session.getAttribute("user");
        //从用户信息中获取角色信息
        String role = user.getRole();
        if (role.equals("工委")){
            //从用户信息中获取工委编码
            String workingCommitteeId = user.getWorkingCommitteeId();
            departmentIds = departmentMapper.selectByWorkingCommitteeIdLikeDepartmentId(workingCommitteeId, departmentId);
        }
        if (role.equals("管理员")){
            departmentIds = departmentMapper.selectLikeDepartmentId(departmentId);
        }
        return ResultMapUtils.getResultMap(departmentIds);
    }

    @Override
    public Map<String, Object> getShowDepartments(HttpSession session, PageRequest request) {
        List<ShowDepartment> showDepartments = new ArrayList<>();
        //从请求参数中获取页码
        int pageNum = request.getPageNum();
        //从请求参数中获取每页展示数
        int pageSize = request.getPageSize();
        int startSize = pageSize * (pageNum -1);
        int count = 0;
        //从session中获取用户信息
        User user = (User) session.getAttribute("user");
        //从用户信息中获取角色信息
        String role = user.getRole();
        if (role.equals("工委")) {
            //从用户信息中获取工委编码
            String workingCommitteeId = user.getWorkingCommitteeId();
            showDepartments = departmentMapper.pageQueryAllByWorkingCommitteeId(workingCommitteeId, startSize, pageSize);
            count = departmentMapper.countAllByWorkingCommitteeId(workingCommitteeId);
            handleWorkingCommitteeName(showDepartments);
        }
        if (role.equals("管理员")){
            showDepartments = departmentMapper.pageQueryAll(startSize, pageSize);
            count = departmentMapper.countAll();
            handleWorkingCommitteeName(showDepartments);
        }
        return ResultMapUtils.getResultMap(showDepartments, count);
    }

    @Override
    public Map<String, Object> getShowDepartmentsByCondition(DepartmentRequest request) {
        String departmentId = request.getDepartmentId();
        String departmentName = request.getDepartmentName();
        //从请求参数中获取页码
        int pageNum = request.getPageNum();
        //从请求参数中获取每页展示数
        int pageSize = request.getPageSize();
        int startSize = pageSize * (pageNum -1);
        List<ShowDepartment> showDepartments = departmentMapper.pageQueryByCondition(departmentId, departmentName, startSize, pageSize);
        int count = departmentMapper.countAllByCondition(departmentId, departmentName);
        handleWorkingCommitteeName(showDepartments);
        return ResultMapUtils.getResultMap(showDepartments, count);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> addDepartment(HttpSession session, DepartmentRequest request) throws CommonException {
        //从session中获取用户信息
        User user = (User) session.getAttribute("user");
        //部门编码前缀
        String prefix = "BMBM_";
        Department department = new Department();
        //设置新增部门的部门名
        department.setDepartmentName(request.getDepartmentName());
        //设置新增部门的创建时间
        department.setCreateDate(new Date());
        department.setCreateUser(user.getUserName());
        department.setModifyUser(user.getUserName());
        String lastDepartmentId = departmentMapper.selectLastDepartmentId();
        //获取最新的部门编码序号
        int lastId = Integer.parseInt(lastDepartmentId.substring(prefix.length(), lastDepartmentId.length()));
        //例如: 部门编码可能为BMBM_008或者BMBM_088, 所以需要根据情况在编码序号之前补0
        //部门编码为BMBM_00X的情况
        if (lastId < 9){
            prefix += "00";
        }
        //部门编码为BMBM_0XX的情况
        if (lastId >= 9 && lastId < 99){
            prefix += "0";
        }
        //设置新增部门的部门编码
        department.setDepartmentId(prefix + (lastId+1));
        int result = departmentMapper.insert(department);
        if (result == 0){
            throw new CommonException("新增部门失败!");
        }
        //获取新增的部门编码
        String departmentId = department.getDepartmentId();
        //获取工委编码数组
        String[] workingCommitteeIdArray = request.getWorkingCommitteeIdArray();
        if (workingCommitteeIdArray != null && workingCommitteeIdArray.length != 0){
            for (String workingCommitteeId : workingCommitteeIdArray){
                WorkDeptRelationTbl workDeptRelationTbl = new WorkDeptRelationTbl();
                workDeptRelationTbl.setWorkingCommitteeId(workingCommitteeId);
                workDeptRelationTbl.setDepartmentId(departmentId);
                workDeptRelationTbl.setCreateDate(new Date());
                result = workDeptRelationTblMapper.insert(workDeptRelationTbl);
                if (result == 0){
                    throw new CommonException("新增部门所属工委失败!");
                }
            }
        }
        return ResultMapUtils.getResultMap("departmentId", departmentId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void alterDepartment(HttpSession session, DepartmentRequest request) throws CommonException {
        //从session中获取用户信息
        User user = (User) session.getAttribute("user");
        String departmentId = request.getDepartmentId();
        String departmentName = request.getDepartmentName();
        String remark = request.getRemark();
        List<String> newWorkingCommitteeIds = Arrays.asList(request.getWorkingCommitteeIdArray());
        Department department = new Department();
        department.setDepartmentId(departmentId);
        department.setDepartmentName(departmentName);
        department.setRemark(remark);
        department.setModifyUser(user.getUserName());
        //更新部门信息
        int result = departmentMapper.updateByPrimaryKey(department);
        if (result == 0){
            throw new CommonException("部门信息修改失败!");
        }
        //更新部门所属工委信息
        List<String> oldWorkingCommitteeIds = workDeptRelationTblMapper.selectWorkingCommitteeIdsByDepartmentId(departmentId);
        List<String> removeWorkingCommitteeIds = new ArrayList<>(oldWorkingCommitteeIds);
        List<String> addWorkingCommitteeIds = new ArrayList<>(newWorkingCommitteeIds);
        addWorkingCommitteeIds.removeAll(oldWorkingCommitteeIds);
        removeWorkingCommitteeIds.removeAll(newWorkingCommitteeIds);
        for (String removeWorkingCommitteeId : removeWorkingCommitteeIds){
            //先删除部门所属工委
            result = workDeptRelationTblMapper.deleteByWorkingCommitteeIdAndDeptId(removeWorkingCommitteeId, departmentId);
            if (result == 0){
                throw new CommonException("部门所属工委删除失败!");
            }
        }
        //遍历插入部门所属工委
        for (String addWorkingCommitteeId : addWorkingCommitteeIds){
            WorkDeptRelationTbl workDeptRelationTbl = new WorkDeptRelationTbl();
            workDeptRelationTbl.setDepartmentId(departmentId);
            workDeptRelationTbl.setWorkingCommitteeId(addWorkingCommitteeId);
            workDeptRelationTbl.setCreateDate(new Date());
            result = workDeptRelationTblMapper.insert(workDeptRelationTbl);
            if (result == 0){
                throw new CommonException("部门所属工委插入失败!");
            }
        }
    }

    @Override
    public Department getByDepartmentName(DepartmentRequest request) throws CommonException {
        String departmentName = request.getDepartmentName();
        //判断工委名称是否为空
        if (StringUtils.isEmpty(departmentName)){
            throw new CommonException("部门名称不能为空!");
        }
        return departmentMapper.selectByDepartmentName(departmentName);
    }

    @Override
    public Map<String, Object> getDepartmentsByWorkingCommitteeId(String workingCommitteeId) {
        List<Department> departments = departmentMapper.selectDepartmentsByWorkingCommitteeId(workingCommitteeId);
        return ResultMapUtils.getResultMap(departments);
    }

    @Override
    public Map<String, Object> getDepartments() {
        List<Department> departments = departmentMapper.selectDepartments();
        return ResultMapUtils.getResultMap(departments);
    }

    /**
     * 获取每个部门的所属工委列表及菜单权限列表, 用于展示
     * @param showDepartments 部门信息列表
     */
    private void handleWorkingCommitteeName(List<ShowDepartment> showDepartments){
        String workingCommitteeName;
        //遍历展示部门信息, 拼接处理工委名
        for(ShowDepartment showDepartment : showDepartments){
            //获取部门编码
            String departmentId = showDepartment.getDepartmentId();
            //处理属性信息, 拼接属性(本函数为工委名)
            List<String> workingCommitteeNames = departmentMapper.selectWorkingCommitteeNameByDepartmentId(departmentId);
            showDepartment.setWorkingCommitteeIds(departmentMapper.getWorkingCommitteeIdsByDeptId(departmentId));
            if (workingCommitteeNames.size() != 0){
                workingCommitteeName = workingCommitteeNames.toString();
                workingCommitteeName = workingCommitteeName.substring(workingCommitteeName.indexOf("[") + 1, workingCommitteeName.lastIndexOf("]"));
            }else {
                workingCommitteeName = "";
            }
            showDepartment.setWorkingCommitteeName(workingCommitteeName);
        }
    }

}
