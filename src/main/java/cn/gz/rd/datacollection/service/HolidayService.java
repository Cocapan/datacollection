package cn.gz.rd.datacollection.service;

import cn.gz.rd.datacollection.controller.request.HolidayRequest;
import cn.gz.rd.datacollection.controller.request.PageRequest;
import cn.gz.rd.datacollection.model.CommonException;
import cn.gz.rd.datacollection.model.Holiday;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * @author panxuan
 * 假期服务接口
 */
public interface HolidayService {

    /**
     * 添加假期
     * @param holiday 假期
     */
    Map<String, Object> addHoliday(HttpSession session, Holiday holiday) throws CommonException;

    /**
     * 修改假期
     * @param holiday 假期
     */
    void alterHoliday(HttpSession session, Holiday holiday) throws CommonException;

    /**
     * 删除假期
     * @param id 假期唯一标识
     */
    void deleteHoliday(int id) throws CommonException;

    /**
     * 获取假期列表
     * @return 假期列表
     */
    Map<String, Object> getHolidayList(PageRequest request);

    /**
     * 条件查询假期列表
     * @param request 查询条件
     * @return 假期列表
     */
    Map<String, Object> getHolidayByCondition(HolidayRequest request);

    /**
     * 获取年份列表
     * @return 年份列表
     */
    Map<String, Object> getYears();

    List<String> getWorkDayMap(int year, int month);

    void getWorkDayMap(List<String> workDatMap, int year, int month, int uploadDeadLine);

}
