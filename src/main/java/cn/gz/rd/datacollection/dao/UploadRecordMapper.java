package cn.gz.rd.datacollection.dao;

import cn.gz.rd.datacollection.model.UploadRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author panxuan
 * 上传记录数据访问层
 */
@Mapper
@Component(value = "uploadRecordMapper")
public interface UploadRecordMapper {

    /**
     * 插入上传记录
     * @param uploadRecord 上传记录信息
     * @return 1 插入成功, 0 插入失败
     */
    int insert(UploadRecord uploadRecord);

    /**
     * 通过部门编码, 获取本月部门上传记录列表
     * @param startTime 本月第一天
     * @param endTime 本月最后一天
     * @param statisticsPeriods 统计周期
     * @param departmentId 部门编码
     * @return 本月部门上传记录列表
     */
    List<UploadRecord> selectThisMonthRecordByDepartmentId(@Param("startTime") Date startTime,
                                                   @Param("endTime") Date endTime,
                                                   @Param("statisticsPeriods") String statisticsPeriods,
                                                   @Param("departmentId") String departmentId);

    /**
     * 通过工委编码, 获取本月部门上传记录列表
     * @param startTime 本月第一天
     * @param endTime 本月最后一天
     * @param statisticsPeriods 统计周期
     * @param workingCommitteeId 工委编码
     * @return 本月部门上传记录列表
     */
    List<UploadRecord> selectThisMonthRecordByWorkingCommitteeId(@Param("startTime") Date startTime,
                                                           @Param("endTime") Date endTime,
                                                           @Param("statisticsPeriods") String statisticsPeriods,
                                                           @Param("workingCommitteeId") String workingCommitteeId);

    /**
     * 根据部门编码, 获取上传记录列表
     * @param departmentId 部门编码
     * @return 上传记录列表
     */
    List<UploadRecord> selectByDepartmentId(@Param("departmentId") String departmentId);

    /**
     * 根据工委编码, 获取上传记录列表
     * @param workingCommitteeId 工委编码
     * @return 上传记录列表
     */
    List<UploadRecord> selectByWorkingCommitteeId(@Param("workingCommitteeId") String workingCommitteeId);

    /**
     * 根据部门编码及统计周期, 获取上传记录列表
     * @param departmentId 部门编码
     * @param statisticsPeriods 统计周期
     * @return 上传记录列表
     */
    List<UploadRecord> selectByDepartmentIdAndStatisticsPeriod(@Param("departmentId") String departmentId, @Param("statisticsPeriods") String statisticsPeriods);

    /**
     * 根据工委编码及统计周期, 获取上传记录列表
     * @param workingCommitteeId 工委编码
     * @param statisticsPeriods 统计周期
     * @return 上传记录列表
     */
    List<UploadRecord> selectByWorkingCommitteeIdAndStatisticsPeriod(@Param("workingCommitteeId") String workingCommitteeId, @Param("statisticsPeriods") String statisticsPeriods);

    /**
     * 根据工委编码, 条件查询获取上传记录列表
     * @param workingCommitteeId 工委编码
     * @param departmentName 部门名
     * @param topicName 主题名
     * @param uploadPeriod 上报频度
     * @param startDate 开始日期
     * @param endDate 截止日期
     * @return 上传记录列表
     */
    List<UploadRecord> conditionQueryByWorkingCommitteeId(@Param("workingCommitteeId") String workingCommitteeId,  @Param("departmentName") String departmentName, @Param("topicName") String topicName, @Param("uploadPeriod") String uploadPeriod, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * 根据部门编码, 条件查询获取上传记录列表
     * @param departmentId 部门编码
     * @param departmentName 部门名
     * @param topicName 主题名
     * @param uploadPeriod 上报频度
     * @param startDate 开始日期
     * @param endDate 截止日期
     * @return 上传记录列表
     */
    List<UploadRecord> conditionQueryByDepartmentId(@Param("departmentId") String departmentId,  @Param("departmentName") String departmentName, @Param("topicName") String topicName, @Param("uploadPeriod") String uploadPeriod, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * 根据工委编码, 查询上报频度列表
     * @param workingCommitteeId 工委编码
     * @return 上报频度列表
     */
    List<String> selectUploadPeriodByWorkingCommitteeId(@Param("workingCommitteeId") String workingCommitteeId);

    /**
     * 根据部门编码, 查询上报频度列表
     * @param departmentId 部门编码
     * @return 上报频度列表
     */
    List<String> selectUploadPeriodByDepartmentId(@Param("departmentId") String departmentId);



}
