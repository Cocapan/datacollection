package cn.gz.rd.datacollection.dao;

import cn.gz.rd.datacollection.model.JkwwCremainsProblemSchedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface JkwwCremainsProblemScheduleMapper {

    int insert(JkwwCremainsProblemSchedule record);

    int insertSelective(JkwwCremainsProblemSchedule record);

    int deleteDeptData(@Param("deptCode") String deptCode, @Param("countRate") String countRate);

    List<JkwwCremainsProblemSchedule> selectDeptData(
            @Param("deptCode") String deptCode,
            @Param("countRate") String countRate);
}