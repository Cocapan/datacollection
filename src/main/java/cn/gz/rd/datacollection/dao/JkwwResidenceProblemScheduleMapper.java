package cn.gz.rd.datacollection.dao;

import cn.gz.rd.datacollection.model.JkwwResidenceLedger;
import cn.gz.rd.datacollection.model.JkwwResidenceProblemSchedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface JkwwResidenceProblemScheduleMapper {

    int insert(JkwwResidenceProblemSchedule record);

    int insertSelective(JkwwResidenceProblemSchedule record);

    int deleteDeptData(
            @Param("deptCode") String deptCode,
            @Param("countRate")String countRate);

    List<JkwwResidenceProblemSchedule> selectDeptData(
            @Param("deptCode") String deptCode,
            @Param("countRate") String countRate);

    /**
     * 获取《居住区配套设施建设移交历史遗留问题台账》数据
     * @param deptName 部门名称，为空则取所有。
     * @param countRate 统计周期
     * @return List<JkwwResidenceLedger>
     */
    List<JkwwResidenceLedger> selectResidenceLedger(
            @Param("deptName") String deptName,
            @Param("countRate") String countRate);
}