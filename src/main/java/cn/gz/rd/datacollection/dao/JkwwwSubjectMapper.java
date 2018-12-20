package cn.gz.rd.datacollection.dao;

import cn.gz.rd.datacollection.model.JkwwDeptInfo;
import cn.gz.rd.datacollection.model.JkwwUnCompleteDeptVO;
import cn.gz.rd.datacollection.model.JkwwwSubjectInfo;
import cn.gz.rd.datacollection.model.SubjectCompleteInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 教科文卫主题信息DAO
 */
@Mapper
@Component
public interface JkwwwSubjectMapper {

    /**
     * 查询主题信息
     * @param deptCode 部门编码
     * @param countRate 统计周期
     * @param statusCodes 状态编码，可以多个
     * @return JkwwwSubjectInfo
     */
    List<JkwwwSubjectInfo> selectSubjectInfo(
            @Param("bmbm") String deptCode,
            @Param("tjzq") String countRate,
            @Param("statusCodes") List<Integer> statusCodes,
            @Param("isUpload") Boolean isUpload,
            @Param("uploadRate") String uploadRate,
            @Param("startRow") Integer startRow,
            @Param("pageSize") Integer pageSize
    );

    Integer countSubjectInfo(
            @Param("bmbm") String deptCode,
            @Param("tjzq") String countRate,
            @Param("statusCodes") List<Integer> statusCodes,
            @Param("isUpload") Boolean isUpload,
            @Param("uploadRate") String uploadRate);

    /**
     * 查询科教文卫工委相关的政府部门信息
     * @return JkwwDeptInfo
     */
    List<JkwwDeptInfo> selectJkwwDeptInfo();

    /**
     * 查询主题的完备性（主题相关的数据是否全部收集齐全）
     * @param countRate 统计周期
     * @return SubjectCompleteInfo
     */
    List<SubjectCompleteInfo> selectSubjectCompleteInfo(
            @Param("countRate") String countRate,
            @Param("subjectCodes") List<String> subjectCodes);

    /**
     * 查询各个主题中未提交信息的部门
     */
    List<JkwwUnCompleteDeptVO> selectUnCompleteDept(
            @Param("countRate") String countRate,
            @Param("subjectCodes") List<String> subjectCodes);

    /**
     * 查询未提交主题数据的条数
     * @param statPeriod 统计周期
     * @param uploadRate 上传频率
     * @return 未提交主题数据的条数
     */
    int countUnUploadSubject(
            @Param("statPeriod") String statPeriod,
            @Param("uploadRate") String uploadRate
    );

}
