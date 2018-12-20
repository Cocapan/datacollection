package cn.gz.rd.datacollection.dao;

import cn.gz.rd.datacollection.model.DeptSubjectInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface DeptSubjectInfoMapper {

    int deleteByPrimaryKey(Integer ztztid);

    int insert(DeptSubjectInfo record);

    int insertSelective(DeptSubjectInfo record);

    DeptSubjectInfo selectByPrimaryKey(Integer ztztid);

    int updateByPrimaryKeySelective(DeptSubjectInfo record);

    int updateByPrimaryKey(DeptSubjectInfo record);

    List<DeptSubjectInfo> selectDeptSubject(DeptSubjectInfo record);

    List<DeptSubjectInfo> selectManySubjectAtCountRate(
            @Param("subjectStatusIds") List<Integer> subjectStatusIds,
            @Param("countRate") String countRate);
}