package cn.gz.rd.datacollection.dao;

import cn.gz.rd.datacollection.model.CommonExcelConf;
import cn.gz.rd.datacollection.model.DbColumnInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface CommonExcelConfMapper {

    CommonExcelConf selectByPrimaryKey(String confId);

    /**
     * 查询表的字段信息
     * @param tableName 表名称
     * @return 字段信息
     */
    List<DbColumnInfo> selectTableColumnInfo(String tableName);

    int batchInsertList(
            @Param(value = "tableName") String tableName,
            @Param(value = "rowValueList") List<List<Object>> rowValueList);

    int updateIsUsable(@Param(value = "tableName") String tableName, @Param(value = "countRate") String countRate);
}