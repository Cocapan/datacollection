package cn.gz.rd.datacollection.dao;

import cn.gz.rd.datacollection.model.JkwwEduProblemSchedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface JkwwEduProblemScheduleMapper {

    int insert(JkwwEduProblemSchedule record);

    int insertSelective(JkwwEduProblemSchedule record);

    int deleteDeptData(@Param("deptCode") String deptCode, @Param("countRate") String countRate);

    List<JkwwEduProblemSchedule> selectDeptData(
            @Param("deptCode") String deptCode,
            @Param("countRate") String countRate);
}