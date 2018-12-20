package cn.gz.rd.datacollection.dao;

import cn.gz.rd.datacollection.model.AccessoryFile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface JkwwAccessoryMapper {

    /**
     * 插入附件文件信息
     * @param fileName 附件文件名
     * @param originalName 原始附件文件名
     * @param statisticPeriod 统计周期
     * @param storageUrl 存储路径
     * @param previewUrl 预览路径
     * @param deptCode 部门编码
     */
    void insert(@Param("fileName") String fileName
                ,@Param("originalName") String originalName
                ,@Param("statisticPeriod") String statisticPeriod
                ,@Param("storageUrl") String storageUrl
                ,@Param("previewUrl") String previewUrl
                ,@Param("deptCode") String deptCode);


    List<AccessoryFile> selectByCountRateAndDeptCode(@Param("statisticPeriod") String statisticPeriod, @Param("deptCode") String deptCode);

    int deleteById(int id);

    String selectStorageUrlById(int id);

    int updateByStorageUrl(AccessoryFile accessoryFile);

    AccessoryFile selectByFileNameCountRateAndDeptCode(@Param("fileName")String fileName, @Param("statisticPeriod") String statisticPeriod, @Param("deptCode") String deptCode);

}
