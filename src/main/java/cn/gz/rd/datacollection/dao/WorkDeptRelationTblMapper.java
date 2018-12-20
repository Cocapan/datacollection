package cn.gz.rd.datacollection.dao;

import cn.gz.rd.datacollection.model.WorkDeptRelationTbl;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author panxuan
 * 工委部门管理表数据访问层
 */
@Mapper
@Component
public interface WorkDeptRelationTblMapper {

    /**
     * 插入工委部门管理信息
     * @param workDeptRelationTbl 工委部门管理信息
     * @return 1 插入成功, 0 插入失败
     */
    int insert(WorkDeptRelationTbl workDeptRelationTbl);

    /**
     * 通过工委编码, 删除工委部门管理信息
     * @param workingCommitteeId 工委编码
     * @return 1 删除成功, 0 删除失败
     */
    int deleteByWorkingCommitteeIdAndDeptId(@Param(value = "workingCommitteeId") String workingCommitteeId, @Param(value = "departmentId")String departmentId);

    int selectBudgetWorkByDeptId(String departmentId);

    List<String> selectWorkingCommitteeIdsByDepartmentId(String departmentId);
}
