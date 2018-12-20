package cn.gz.rd.datacollection.dao;

import cn.gz.rd.datacollection.model.WorkingCommittee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author panxuan
 * 工委数据访问层
 */
@Mapper
@Component(value = "workingCommitteeMapper")
public interface WorkingCommitteeMapper {

    /**
     * 插入工委信息
     * @param workingCommittee 工委信息
     * @return 1 插入成功, 0 插入失败
     */
    int insert(WorkingCommittee workingCommittee);

    /**
     * 更新工委信息
     * @param workingCommittee 工委信息
     * @return 1 更新成功, 0 更新失败
     */
    int updateByPrimaryKey(WorkingCommittee workingCommittee);

    /**
     * 获取工委信息表的记录总数
     * @return 工委信息表的记录总数
     */
    int countAll();

    /**
     * 通过工委名, 获取工委信息
     * @param workingCommitteeName 工委名
     * @return 工委信息
     */
    WorkingCommittee selectByWorkingCommitteeName(String workingCommitteeName);

    /**
     * 通过分页查询, 获取工委信息列表
     * @param startSize 起始数
     * @param pageSize 查询数量
     * @return 工委信息列表
     */
    List<WorkingCommittee> pageQueryAll(
            @Param(value = "startSize") int startSize,
            @Param(value = "pageSize") int pageSize);

    /**
     * 获取最新的工委编码
     * @return 最新的工委编码
     */
    String getLastWorkingCommitteeId();

    /**
     * 获取工委名列表
     * @return 工委名列表
     */
    List<WorkingCommittee> selectWorkingCommittees();

}
