package cn.gz.rd.datacollection.dao;

import cn.gz.rd.datacollection.model.JkwwProblemSchedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface JkwwProblemScheduleMapper {

    int insert(JkwwProblemSchedule record);

    int insertSelective(JkwwProblemSchedule record);

    int deleteDeptData(@Param("deptCode") String deptCode, @Param("countRate") String countRate);

    List<JkwwProblemSchedule> selectDeptData(
            @Param("deptCode") String deptCode,
            @Param("countRate") String countRate);
}