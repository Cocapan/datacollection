package cn.gz.rd.datacollection.dao;

import cn.gz.rd.datacollection.model.JkwwOldageProblemBaseInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface JkwwOldageProblemBaseInfoMapper {

    int deleteByPrimaryKey(Integer xmbh);

    int deleteAll();

    int insert(JkwwOldageProblemBaseInfo record);

    int insertSelective(JkwwOldageProblemBaseInfo record);

    JkwwOldageProblemBaseInfo selectByPrimaryKey(Integer xmbh);

    int updateByPrimaryKeySelective(JkwwOldageProblemBaseInfo record);

    int updateByPrimaryKey(JkwwOldageProblemBaseInfo record);
}