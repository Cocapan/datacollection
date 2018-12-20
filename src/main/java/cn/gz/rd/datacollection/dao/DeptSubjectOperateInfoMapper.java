package cn.gz.rd.datacollection.dao;

import cn.gz.rd.datacollection.model.DeptSubjectOperateInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface DeptSubjectOperateInfoMapper {

    int deleteByPrimaryKey(Integer jlid);

    int insert(DeptSubjectOperateInfo record);

    int insertSelective(DeptSubjectOperateInfo record);

    DeptSubjectOperateInfo selectByPrimaryKey(Integer jlid);

    int updateByPrimaryKeySelective(DeptSubjectOperateInfo record);

    int updateByPrimaryKey(DeptSubjectOperateInfo record);
}