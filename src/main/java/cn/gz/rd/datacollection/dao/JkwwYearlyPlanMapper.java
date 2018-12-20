package cn.gz.rd.datacollection.dao;

import cn.gz.rd.datacollection.model.JkwwYearlyPlan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface JkwwYearlyPlanMapper {

    int insert(JkwwYearlyPlan record);

    int insertSelective(JkwwYearlyPlan record);

    int deleteDeptData(
            @Param("deptCode") String deptCode,
            @Param("countRate") String countRate);

    List<JkwwYearlyPlan> selectDeptData(
            @Param("deptCode") String deptCode,
            @Param("countRate") String countRate);

    List<JkwwYearlyPlan> selectYearlyPlan(
            @Param("className") String className,
            @Param("status") String status,
            @Param("mainDeptName") String mainDeptName,
            @Param("ownerName") String ownerName,
            @Param("projectName") String projectName,
            @Param("countRate") String countRate,
            @Param("startRow") Integer startRow,
            @Param("pageSize") Integer pageSize);

    int countYearlyPlan(
            @Param("className") String className,
            @Param("status") String status,
            @Param("mainDeptName") String mainDeptName,
            @Param("ownerName") String ownerName,
            @Param("projectName") String projectName,
            @Param("countRate") String countRate);


}