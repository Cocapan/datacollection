package cn.gz.rd.datacollection.service.imp;

import cn.gz.rd.datacollection.controller.request.PageRequest;
import cn.gz.rd.datacollection.dao.WorkingCommitteeMapper;
import cn.gz.rd.datacollection.model.CommonException;
import cn.gz.rd.datacollection.model.User;
import cn.gz.rd.datacollection.model.WorkingCommittee;
import cn.gz.rd.datacollection.service.WorkingCommitteeService;
import cn.gz.rd.datacollection.utils.ResultMapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author panxuan
 * 工委服务实现类
 */
@Service
public class WorkingCommitteeServiceImpl implements WorkingCommitteeService {

    /**
     * 工委数据访问
     */
    private WorkingCommitteeMapper workingCommitteeMapper;

    @Autowired
    public WorkingCommitteeServiceImpl(WorkingCommitteeMapper workingCommitteeMapper){
        this.workingCommitteeMapper = workingCommitteeMapper;
    }

    @Override
    public Map<String, Object> getWorkingCommitteeList(PageRequest request) {
        int pageNum = request.getPageNum();
        int pageSize = request.getPageSize();
        int startSize = (pageNum - 1) * pageSize;
        return ResultMapUtils.getResultMap(workingCommitteeMapper.pageQueryAll(startSize, pageSize), workingCommitteeMapper.countAll());
    }

    @Override
    public Map<String, Object> addWorkingCommittee(HttpSession session, WorkingCommittee workingCommittee) throws CommonException {
        User user = (User) session.getAttribute("user");
        String workingCommitteeName = workingCommittee.getWorkingCommitteeName();
        if (StringUtils.isEmpty(workingCommitteeName)){
            throw new CommonException("工委名不能为空!");
        }
        //工委编码前缀
        String prefix = "GWBM_";
        //设置创建时间为当前日期
        workingCommittee.setCreateDate(new Date());
        //获取最新的工委编码
        String lastWorkingCommitteeId = workingCommitteeMapper.getLastWorkingCommitteeId();
        //获取最新的工委编码序号
        int lastId = Integer.parseInt(lastWorkingCommitteeId.substring(prefix.length(), lastWorkingCommitteeId.length()));
        //例如: 工委编码可能为GWBM_08, 所以需要根据情况在编码序号之前补0
        //工委编码为GWBM_0X的情况
        if (lastId < 9){
            prefix += "0";
        }
        //设置新增工委的工委编码
        workingCommittee.setWorkingCommitteeId(prefix + (lastId+1));
        workingCommittee.setCreateUser(user.getUserName());
        workingCommittee.setModifyUser(user.getUserName());
        int result = workingCommitteeMapper.insert(workingCommittee);
        if (result == 0){
            throw new CommonException("新增部门失败!");
        }
        return ResultMapUtils.getResultMap("workingCommitteeId", workingCommittee.getWorkingCommitteeId());
    }

    @Override
    public int alterWorkingCommittee(HttpSession session, WorkingCommittee workingCommittee) throws CommonException {
        User user = (User) session.getAttribute("user");
        workingCommittee.setModifyUser(user.getUserName());
        String workingCommitteeName = workingCommittee.getWorkingCommitteeName();
        if (StringUtils.isEmpty(workingCommitteeName)){
            throw new CommonException("工委名不能为空!");
        }
        return workingCommitteeMapper.updateByPrimaryKey(workingCommittee);
    }

    @Override
    public WorkingCommittee getByWorkingCommitteeName(WorkingCommittee request) throws CommonException {
        String workingCommitteeName = request.getWorkingCommitteeName();
        //判断工委名称是否为空
        if (StringUtils.isEmpty(workingCommitteeName)){
            throw new CommonException("工委名称不能为空!");
        }
        return workingCommitteeMapper.selectByWorkingCommitteeName(workingCommitteeName);
    }

    @Override
    public Map<String, Object> getWorkingCommittees() {
        List<WorkingCommittee> workingCommittees = workingCommitteeMapper.selectWorkingCommittees();
        return ResultMapUtils.getResultMap(workingCommittees);
    }
}
