package cn.gz.rd.datacollection.service;

import cn.gz.rd.datacollection.controller.request.PageRequest;
import cn.gz.rd.datacollection.model.CommonException;
import cn.gz.rd.datacollection.model.WorkingCommittee;

import javax.servlet.http.HttpSession;
import java.util.Map;

public interface WorkingCommitteeService {

    /**
     * 分页查询工委列表
     * @param request 请求参数, 包括页码及每页展示数
     * @return 工委列表
     */
    Map<String, Object> getWorkingCommitteeList(PageRequest request);

    /**
     * @param workingCommittee 工委
     * @return 新增工委编码
     */
    Map<String, Object> addWorkingCommittee(HttpSession session, WorkingCommittee workingCommittee) throws CommonException;

    /**
     * 修改工委信息
     * @param workingCommittee 工委信息
     * @return 响应结果
     */
    int alterWorkingCommittee(HttpSession session, WorkingCommittee workingCommittee) throws CommonException;

    /**
     * 根据工委名查询工委
     * @param workingCommittee 请求参数, 包括工委名
     * @return 返回工委信息
     * @throws CommonException 工委名为空异常
     */
    WorkingCommittee getByWorkingCommitteeName(WorkingCommittee workingCommittee) throws CommonException;

    /**
     * @return 获取工委列表
     */
    Map<String, Object> getWorkingCommittees();

}
