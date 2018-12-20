package cn.gz.rd.datacollection.dao;

import cn.gz.rd.datacollection.model.Holiday;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author panxuan
 * 假期数据访问
 */
@Mapper
@Component
public interface HolidayMapper {

    /**
     * 插入假期记录
     * @param holiday 假期
     * @return 1 插入成功, 0 插入失败
     */
    int insert(Holiday holiday);

    /**
     * 更新假期记录
     * @param holiday 假期
     * @return 1 更新成功, 0 更新失败
     */
    int updateByPrimaryKey(Holiday holiday);

    /**
     * 通过主键, 删除记录
     * @param id 主键唯一标识
     * @return 1 删除成功, 0 删除失败
     */
    int deleteByPrimaryKey(int id);

    /**
     * 分页查询, 获取假期列表
     * @param startSize 起始数
     * @param pageSize 每页展示数
     * @return 假期列表
     */
    List<Holiday> pageQueryAll(
            @Param(value = "startSize") int startSize,
            @Param(value = "pageSize") int pageSize
    );

    /**
     * 条件分页查询, 获取假期列表
     * @param year 年份
     * @param startSize 起始数
     * @param pageSize 每页展示数
     * @return 假期列表
     */
    List<Holiday> pageQueryByCondition(
            @Param(value = "year") int year,
            @Param(value = "startSize") int startSize,
            @Param(value = "pageSize") int pageSize
    );

    /**
     * 查询假期列表总数
     * @return 总数
     */
    int countAll();

    /**
     * 条件查询假期列表总数
     * @return 总数
     */
    int countByCondition(@Param(value = "year") int year);

    /**
     * 获取假期列表
     * @return 假期列表
     */
    List<Holiday> queryAll();

    /**
     * 条件分页查询, 获取假期列表
     * @param year 年份
     * @param month 月份
     * @return 假期列表
     */
    List<Holiday> getExtraWorkDateByYearAndMonth(
            @Param(value = "year") int year,
            @Param(value = "month") int month
    );

    List<String> getYearList();

}
