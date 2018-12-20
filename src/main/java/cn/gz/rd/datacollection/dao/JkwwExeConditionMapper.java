package cn.gz.rd.datacollection.dao;

import cn.gz.rd.datacollection.model.JkwwExeCondition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface JkwwExeConditionMapper {

    int insert(JkwwExeCondition record);

    int insertSelective(JkwwExeCondition record);

    int deleteDeptData(
            @Param("deptCode") String deptCode,
            @Param("countRate") String countRate);

    List<JkwwExeCondition> selectExePlan(
            @Param("className") String className,
            @Param("status") String status,
            @Param("mainDeptName") String mainDeptName,
            @Param("ownerName") String ownerName,
            @Param("projectName") String projectName,
            @Param("countRate") String countRate,
            @Param("startRow") Integer startRow,
            @Param("pageSize") Integer pageSize);

    int countExePlan(
            @Param("className") String className,
            @Param("status") String status,
            @Param("mainDeptName") String mainDeptName,
            @Param("ownerName") String ownerName,
            @Param("projectName") String projectName,
            @Param("countRate") String countRate);

    List<JkwwExeCondition> selectStatInfo(String countRate);

    List<JkwwExeCondition> selectDeptData(
            @Param("deptCode") String deptCode,
            @Param("countRate") String countRate);
}