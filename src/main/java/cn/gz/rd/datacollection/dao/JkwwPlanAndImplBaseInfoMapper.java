package cn.gz.rd.datacollection.dao;

import cn.gz.rd.datacollection.model.JkwwPlanAndImplBaseInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface JkwwPlanAndImplBaseInfoMapper {

    int deleteByPrimaryKey(Integer xh);

    int insert(JkwwPlanAndImplBaseInfo record);

    int insertSelective(JkwwPlanAndImplBaseInfo record);

    JkwwPlanAndImplBaseInfo selectByPrimaryKey(Integer xh);

    int updateByPrimaryKeySelective(JkwwPlanAndImplBaseInfo record);

    int updateByPrimaryKey(JkwwPlanAndImplBaseInfo record);

    int deleteAll();
}