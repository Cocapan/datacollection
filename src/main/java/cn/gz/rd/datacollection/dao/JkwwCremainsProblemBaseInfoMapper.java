package cn.gz.rd.datacollection.dao;

import cn.gz.rd.datacollection.model.JkwwCremainsProblemBaseInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface JkwwCremainsProblemBaseInfoMapper {

    int deleteByPrimaryKey(Long xmbh);

    int deleteAll();

    int insert(JkwwCremainsProblemBaseInfo record);

    int insertSelective(JkwwCremainsProblemBaseInfo record);

    JkwwCremainsProblemBaseInfo selectByPrimaryKey(Long xmbh);

    int updateByPrimaryKeySelective(JkwwCremainsProblemBaseInfo record);

    int updateByPrimaryKey(JkwwCremainsProblemBaseInfo record);
}