package cn.gz.rd.datacollection.dao;

import cn.gz.rd.datacollection.model.ShowTopic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author panxuan
 * 主题数据访问层
 */
@Mapper
@Component(value = "topicMapper")
public interface TopicMapper {

    /**
     * 通过工委编码及上报频度查询主题列表
     * @param workingCommitteeId 工委编码
     * @param uploadPeriod 上报频度
     * @return 返回主题列表
     */
    List<ShowTopic> selectByWorkingCommitteeIdAndUploadPeriod(@Param("workingCommitteeId") String workingCommitteeId, @Param("uploadPeriod") String uploadPeriod);

    /**
     * 通过工委编码查询主题列表
     * @param workingCommitteeId 工委编码
     * @return 返回主题列表
     */
    List<ShowTopic> selectByWorkingCommitteeId(@Param("workingCommitteeId") String workingCommitteeId);

    /**
     * 根据工委编码及上报频度查询上级主题列表
     * @param workingCommitteeId 工委编码
     * @param uploadPeriod 上报频度
     * @return 返回主题列表
     */
    List<ShowTopic> selectSuperiorTopicByWorkingCommitteeIdAndUploadPeriod(@Param("workingCommitteeId") String workingCommitteeId, @Param("uploadPeriod") String uploadPeriod);

    /**
     * 根据工委编码查询上级主题列表
     * @param workingCommitteeId 工委编码
     * @return 返回主题列表
     */
    List<ShowTopic> selectSuperiorTopicByWorkingCommitteeId(@Param("workingCommitteeId") String workingCommitteeId);

    /**
     * 通过部门编码及上报频度查询主题列表
     * @param departmentId 部门编码
     * @param uploadPeriod 上报频度
     * @return 返回主题列表
     */
    List<ShowTopic> selectByDepartmentIdAndUploadPeriod(@Param("departmentId")String departmentId, @Param("uploadPeriod")String uploadPeriod);

    /**
     * 通过部门编码查询主题列表
     * @param departmentId 部门编码
     * @return 返回主题列表
     */
    List<ShowTopic> selectByDepartmentId(@Param("departmentId")String departmentId);

    /**
     * 根据部门编码及上报频度查询上级主题列表
     * @param departmentId 部门编码
     * @param uploadPeriod 上报频度
     * @return 返回主题列表
     */
    List<ShowTopic> selectSuperiorTopicByDepartmentIdAndUploadPeriod(@Param("departmentId")String departmentId, @Param("uploadPeriod")String uploadPeriod);

    /**
     * 通过部门编码查询上级主题列表
     * @param departmentId 部门编码
     * @return 返回主题列表
     */
    List<ShowTopic> selectSuperiorTopicByDepartmentId(@Param("departmentId")String departmentId);

    /**
     * 通过条件查询主题列表
     * @param workingCommitteeId 工委编码
     * @param departmentName 部门名
     * @param topicType 主题类型
     * @param uploadPeriod 上报频度
     * @return 返回主题列表
     */
    List<ShowTopic> conditionQueryByWorkingCommitteeId(@Param("workingCommitteeId") String workingCommitteeId, @Param("departmentName") String departmentName, @Param("topicType") String topicType, @Param("uploadPeriod") String uploadPeriod);

    /**
     *
     * @param departmentId 部门编码
     * @param departmentName 部门名
     * @param topicType 主题类型
     * @param uploadPeriod 上报频度
     * @return 返回主题列表
     */
    List<ShowTopic> conditionQueryByDepartmentId(@Param("departmentId") String departmentId, @Param("departmentName") String departmentName, @Param("topicType") String topicType, @Param("uploadPeriod") String uploadPeriod);

    /**
     * 通过统计周期删除指定表中的记录
     * @param storageTableName 表名
     * @param statisticsPeriod 统计周期
     * @return 1 成功, 0 失败
     */
    int deleteByStatisticsPeriod(@Param("storageTableName") String storageTableName, @Param("statisticsPeriod") String statisticsPeriod);

    /**
     * 通过主题编码获取制定主题
     * @param topicId 主题编码
     * @return 主题
     */
    ShowTopic selectByTopicId(String topicId);

    /**
     * 通过工委编码, 获取主题类型列表
     * @param workingCommitteeId 工委编码
     * @return 主题类型列表
     */
    List<String> selectTopicTypesByWorkingCommitteeId(@Param("workingCommitteeId") String workingCommitteeId);

    /**
     * 通过部门编码, 获取主题类型列表
     * @param departmentId 部门编码
     * @return 主题类型列表
     */
    List<String> selectTopicTypesByDepartmentId(@Param("departmentId") String departmentId);

    int countByStatisticsPeriod(@Param("storageTableName") String storageTableName, @Param("statisticsPeriod") String statisticsPeriod);

    String getAbsolutePathByTopicId(String topicId);

    int selectMaxUploadDeadLineByUploadPeriod(String uploadPeriod);

    int selectMaxUploadDeadLineByWorkingCommitteeIdUploadPeriod(@Param("workingCommitteeId") String workingCommitteeId, @Param("uploadPeriod") String uploadPeriod);

    int selectMaxUploadDeadLine();

    int countNotUploadTopic(@Param("workingCommitteeId") String workingCommitteeId, @Param("departmentId") String departmentId, @Param("uploadPeriod") String uploadPeriod, @Param("statisticsPeriod") String statisticsPeriod);

    int selectMaxUploadDeadLineByDepartmentIdAndUploadPeriod(@Param("departmentId") String departmentId, @Param("uploadPeriod") String uploadPeriod);

}
