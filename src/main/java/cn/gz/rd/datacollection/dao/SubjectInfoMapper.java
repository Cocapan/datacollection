package cn.gz.rd.datacollection.dao;

import cn.gz.rd.datacollection.model.SubjectInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface SubjectInfoMapper {

    int deleteByPrimaryKey(String subjectCode);

    int insert(SubjectInfo record);

    int insertSelective(SubjectInfo record);

    SubjectInfo selectByPrimaryKey(String subjectCode);

    int updateByPrimaryKeySelective(SubjectInfo record);

    int updateByPrimaryKey(SubjectInfo record);

    Integer selectMaxNum(String subjectCode);

    int updateSubjectToDisable(String subjectCode);

    List<SubjectInfo> selectSubject(
            @Param("subjectCode") String subjectCode,
            @Param("subjectName") String subjectName,
            @Param("deptCode") String deptCode,
            @Param("committeeCode") String committeeCode,
            @Param("isUsable") Boolean isUsable,
            @Param("startRow") Integer startRow,
            @Param("pageSize") Integer pageSize);

    int countSubject(
            @Param("subjectCode") String subjectCode,
            @Param("subjectName") String subjectName,
            @Param("deptCode") String deptCode,
            @Param("committeeCode") String committeeCode,
            @Param("isUsable") Boolean isUsable);

    int updateUsableFlag(
            @Param("subjectCodes") List<String> subjectCodes,
            @Param("usableFlag") int usableFlag);

    List<SubjectInfo> selectParentSubject(@Param("committeeCode") String committeeCode);

    List<SubjectInfo> getVerifySubject(@Param("startRow") Integer startRow,
                                       @Param("pageSize") Integer pageSize);

    int countVerifySubject();

    String queryTableNameBySubjectCode(@Param("subjectCode") String subjectCode);

}