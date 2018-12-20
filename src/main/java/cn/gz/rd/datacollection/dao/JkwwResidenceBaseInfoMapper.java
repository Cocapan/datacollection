package cn.gz.rd.datacollection.dao;

import cn.gz.rd.datacollection.model.JkwwResidenceBaseInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface JkwwResidenceBaseInfoMapper {

    int insert(JkwwResidenceBaseInfo record);

    int insertSelective(JkwwResidenceBaseInfo record);

    int deleteAll();
}