package cn.gz.rd.datacollection.dao;

import cn.gz.rd.datacollection.model.JkwwPrjImgInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface JkwwPrjImgInfoMapper {

    int deleteByPrimaryKey(Integer infoId);

    int insert(JkwwPrjImgInfo record);

    int insertSelective(JkwwPrjImgInfo record);

    JkwwPrjImgInfo selectByPrimaryKey(Integer infoId);

    List<JkwwPrjImgInfo> selectByPrjStatusId(Integer projectStatusId);

    int updateByPrimaryKeySelective(JkwwPrjImgInfo record);

    int updateByPrimaryKey(JkwwPrjImgInfo record);

    List<JkwwPrjImgInfo> selectByFileNameAndSubjectCode(@Param(value = "imgFileName") String imgFileName, @Param(value = "subjectCode") String subjectCode, @Param(value = "statisticPeriod") String statisticPeriod);

    int selectImgIdBySubjectCode(String subjectCode);

    Integer selectPrjStatusIdByCountRateAndSubjectCode(@Param(value = "subjectCode") String subjectCode, @Param(value = "countRate") String countRate);

}