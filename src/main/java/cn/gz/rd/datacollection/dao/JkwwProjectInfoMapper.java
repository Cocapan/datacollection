package cn.gz.rd.datacollection.dao;

import cn.gz.rd.datacollection.model.JkwwProjectInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface JkwwProjectInfoMapper {

    int deleteByPrimaryKey(Integer xmxh);

    int deleteAll();

    int insert(JkwwProjectInfo record);

    int insertSelective(JkwwProjectInfo record);

    JkwwProjectInfo selectByPrimaryKey(Integer xmxh);

    int updateByPrimaryKeySelective(JkwwProjectInfo record);

    int updateByPrimaryKey(JkwwProjectInfo record);

}