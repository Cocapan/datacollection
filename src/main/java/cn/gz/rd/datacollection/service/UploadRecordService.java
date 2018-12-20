package cn.gz.rd.datacollection.service;

import cn.gz.rd.datacollection.controller.request.PageRequest;
import cn.gz.rd.datacollection.controller.request.UploadRecordRequest;

import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.util.Map;

/**
 * 上传记录服务接口
 * @author panxuan
 */
public interface UploadRecordService {

    /**
     * 获取上传记录列表
     * @param session HttpSession 保存了用户的相关信息
     * @param request 请求参数, 包括页码及每页展示数
     * @return 查询结果, 包括上传记录列表及列表总个数
     */
    Map<String, Object> getUploadRecordList(HttpSession session, PageRequest request);

    /**
     * 条件查询获取上传记录列表
     * @param session HttpSession 保存了用户的相关信息
     * @param request 请求参数, 包括部门名, 主题名, 上报频度, 日期数组页码及每页展示数
     * @return 查询结果, 包括上传记录列表及列表总个数
     */
    Map<String, Object> getUploadRecordListByCondition(HttpSession session, UploadRecordRequest request) throws ParseException;

    /**
     * 获取上报频度列表
     * @param session HttpSession 保存了用户的相关信息
     * @return 查询结果, 包括上报频度列表
     */
    Map<String, Object> getUploadPeriods(HttpSession session);

}
