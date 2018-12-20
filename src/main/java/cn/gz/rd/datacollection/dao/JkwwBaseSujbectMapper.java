package cn.gz.rd.datacollection.dao;

import cn.gz.rd.datacollection.model.JkwwBaseSujbect;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface JkwwBaseSujbectMapper {

    int deleteByPrimaryKey(Integer xh);

    int insert(JkwwBaseSujbect record);

    int insertSelective(JkwwBaseSujbect record);

    JkwwBaseSujbect selectByPrimaryKey(Integer xh);

    int updateByPrimaryKeySelective(JkwwBaseSujbect record);

    int updateByPrimaryKey(JkwwBaseSujbect record);

    List<JkwwBaseSujbect> selectAll();
}