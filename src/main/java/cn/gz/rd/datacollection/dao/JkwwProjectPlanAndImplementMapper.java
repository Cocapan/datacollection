package cn.gz.rd.datacollection.dao;

import cn.gz.rd.datacollection.model.JkwwProjectPlanAndImplement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface JkwwProjectPlanAndImplementMapper {

    int deleteDeptData(
            @Param("deptCode") String deptCode,
            @Param("countRate") String countRate);

    List<JkwwProjectPlanAndImplement> selectDeptData(
            @Param("deptCode") String deptCode,
            @Param("countRate") String countRate);
}