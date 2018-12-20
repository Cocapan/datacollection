package cn.gz.rd.datacollection.dao;

import cn.gz.rd.datacollection.model.JkwwProjectScheduleSummary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface JkwwProjectScheduleSummaryMapper {

    int insert(JkwwProjectScheduleSummary record);

    int insertSelective(JkwwProjectScheduleSummary record);

    int deleteDeptData(@Param("deptCode") String deptCode, @Param("countRate") String countRate);

    List<JkwwProjectScheduleSummary> selectDeptData(
            @Param("deptCode") String deptCode,
            @Param("countRate") String countRate);

    /**
     * 查询重点项目进度数据
     * @param countRate 统计周期
     * @return List<JkwwProjectScheduleSummary> 重点项目进度数据
     */
    List<JkwwProjectScheduleSummary> selectKeyPrjScheduleSummary(String countRate);

    List<JkwwProjectScheduleSummary> selectScheduleSummary(
            @Param("className") String className,
            @Param("status") String status,
            @Param("mainDeptName") String mainDeptName,
            @Param("ownerName") String ownerName,
            @Param("projectName") String projectName,
            @Param("countRate") String countRate,
            @Param("isKeyPrj") Boolean isKeyPrj,
            @Param("startRow") Integer startRow,
            @Param("pageSize") Integer pageSize);

    int countScheduleSummary(
            @Param("className") String className,
            @Param("status") String status,
            @Param("mainDeptName") String mainDeptName,
            @Param("ownerName") String ownerName,
            @Param("projectName") String projectName,
            @Param("countRate") String countRate,
            @Param("isKeyPrj") Boolean isKeyPrj);
}