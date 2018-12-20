package cn.gz.rd.datacollection.dao;

import cn.gz.rd.datacollection.model.DeptSubject;
import cn.gz.rd.datacollection.model.SubjectDeptInfoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface DeptSubjectMapper {

    int deleteByPrimaryKey(Integer xh);

    int deleteAllDeptCode(String subjectCode);

    int insert(DeptSubject record);

    int insertSelective(DeptSubject record);

    DeptSubject selectByPrimaryKey(Integer xh);

    int updateByPrimaryKeySelective(DeptSubject record);

    int updateByPrimaryKey(DeptSubject record);

    List<SubjectDeptInfoVO> selectDeptInfoBySubjectCode(
            @Param("subjectCodes") List<String> subjectCodes);

}