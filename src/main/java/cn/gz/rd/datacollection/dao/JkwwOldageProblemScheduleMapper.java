package cn.gz.rd.datacollection.dao;

import cn.gz.rd.datacollection.model.JkwwOldageProblemSchedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface JkwwOldageProblemScheduleMapper {

    int insert(JkwwOldageProblemSchedule record);

    int insertSelective(JkwwOldageProblemSchedule record);

    int deleteDeptData(@Param("deptCode") String deptCode, @Param("countRate") String countRate);

    List<JkwwOldageProblemSchedule> selectDeptData(
            @Param("deptCode") String deptCode,
            @Param("countRate") String countRate);
}