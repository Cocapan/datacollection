package cn.gz.rd.datacollection.dao;


import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * @author panxuan
 * 部门数据访问层
 */
@Mapper
@Component
public interface CodeParallelTableMapper {

    String selectUrlByCode(String code);

}
