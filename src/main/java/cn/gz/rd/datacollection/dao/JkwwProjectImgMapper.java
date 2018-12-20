package cn.gz.rd.datacollection.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface JkwwProjectImgMapper {

    String selectProjectCodeBySubjectCode(String subjectCode);

}
