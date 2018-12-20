package cn.gz.rd.datacollection.dao;

import cn.gz.rd.datacollection.model.JkwwPrjSummaryDb;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface JkwwPrjSummaryDbMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(JkwwPrjSummaryDb record);

    int insertSelective(JkwwPrjSummaryDb record);

}