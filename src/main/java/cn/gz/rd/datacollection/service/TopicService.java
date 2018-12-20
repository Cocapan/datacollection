package cn.gz.rd.datacollection.service;

import cn.gz.rd.datacollection.controller.request.PageRequest;
import cn.gz.rd.datacollection.controller.request.TopicRequest;
import cn.gz.rd.datacollection.model.CommonException;
import com.jcraft.jsch.SftpException;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

/**
 * 主题服务接口
 * @author panxuan
 */
public interface TopicService {

    /**
     * 获取本月需上传主题列表
     * @param session HttpSession 保存了用户的相关信息
     * @param request 查询所需参数, 包括页码及每页展示数
     * @return 查询结果, 包括分页查询主题列表及列表总个数
     */
    Map<String, Object> getToUploadTopics(HttpSession session, PageRequest request);

    /**
     * 获取超时未上传主题列表
     * @param session HttpSession 保存了用户的相关信息
     * @param request 查询所需参数, 包括页码及每页展示数
     * @return 查询结果, 包括分页查询主题列表及列表总个数
     */
    Map<String, Object> getNotYetUploadTopics(HttpSession session, PageRequest request);

    /**
     * 获取上传主题列表
     * @param session HttpSession 保存了用户的相关信息
     * @param request 请求参数, 包括页码及每页展示数
     * @return 查询结果, 包括分页查询主题列表及列表总个数
     */
    Map<String, Object> getUploadTopics(HttpSession session, PageRequest request);

    /**
     * 条件查询上传主题列表
     * @param session HttpSession 保存了用户的相关信息
     * @param request 请求参数, 包括部门名, 主题类型, 页码, 每页展示数, 年份, 月份
     * @return 查询结果, 包括分页查询主题列表及列表总个数
     */
    Map<String, Object> getUploadTopicsByCondition(HttpSession session, TopicRequest request);

    /**
     * 获取主题名列表
     * @param session HttpSession 保存了用户的相关信息
     * @return 主题名列表
     */
    Map<String, Object> getTopicNames(HttpSession session);

    /**
     * 上传主题文件
     * @param workingCommitteeId 工委编码
     * @param departmentId 部门编码
     * @param topicId 主题编码
     * @param StatisticsPeriod 统计周期
     * @param uploadPeriod 上报频度
     * @param topicType 主题类型
     * @param storageTableName 存储表名
     * @param userName 用户名
     * @param uploadTime 上传时间
     * @throws ParseException 解析异常
     * @throws CommonException 自定义异常
     * @throws IOException 流异常
     */
    void uploadFile(String workingCommitteeId, String departmentId, String topicId, String StatisticsPeriod, String uploadPeriod, String topicType, String storageTableName, String userName, String uploadTime, String previewUrl, String filePath) throws Exception;

    /**
     * 获取年份列表
     * @return 年份列表
     */
    Map<String, Object> getYears();

    /**
     * 获取月份列表
     * @return 年份列表
     */
    Map<String, Object> getMonths(int year);

    /**
     * 获取数据类型列表
     * @return 数据类型列表
     */
    Map<String, Object> getTopicTypes(HttpSession session);

    void uploadFile(HttpSession session, int year, int month, MultipartFile uploadFile) throws Exception;

    Map<String, Object> onlinePreview(HttpSession session, String statisticsPeriod, String topicId, String topicType, String uploadTime, String departmentId, boolean isJkww, MultipartFile uploadFile) throws Exception;

    void deletePreviewUrl(String previewUrl, String filePath) throws SftpException;

    boolean isAllUpload(String workingCommitteeId, String departmentId, String uploadPeriod, String statisticPeriod);

}
