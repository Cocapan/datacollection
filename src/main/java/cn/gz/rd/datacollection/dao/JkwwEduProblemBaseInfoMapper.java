package cn.gz.rd.datacollection.dao;

import cn.gz.rd.datacollection.model.JkwwEduProblemBaseInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface JkwwEduProblemBaseInfoMapper {

    int deleteByPrimaryKey(Integer xmbh);

    int deleteAll();

    int insert(JkwwEduProblemBaseInfo record);

    int insertSelective(JkwwEduProblemBaseInfo record);

    JkwwEduProblemBaseInfo selectByPrimaryKey(Integer xmbh);

    int updateByPrimaryKeySelective(JkwwEduProblemBaseInfo record);

    int updateByPrimaryKey(JkwwEduProblemBaseInfo record);
}