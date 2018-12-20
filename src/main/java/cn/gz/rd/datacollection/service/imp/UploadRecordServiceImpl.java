package cn.gz.rd.datacollection.service.imp;

import cn.gz.rd.datacollection.controller.request.PageRequest;
import cn.gz.rd.datacollection.controller.request.UploadRecordRequest;
import cn.gz.rd.datacollection.dao.TopicMapper;
import cn.gz.rd.datacollection.dao.UploadRecordMapper;
import cn.gz.rd.datacollection.model.UploadRecord;
import cn.gz.rd.datacollection.model.User;
import cn.gz.rd.datacollection.service.UploadRecordService;
import cn.gz.rd.datacollection.utils.DateUtlis;
import cn.gz.rd.datacollection.utils.ResultMapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author panxuan
 * 上传记录服务实现类
 */
@Service
public class UploadRecordServiceImpl implements UploadRecordService {

    /**
     * 主题数据访问
     */
    private TopicMapper topicMapper;

    /**
     * 上传记录数据访问
     */
    private UploadRecordMapper uploadRecordMapper;

    /**
     * 采集系统开始年份
     */
    @Value("${data.collection.start.year}")
    private int startYear;

    /**
     * 采集系统开始月份
     */
    @Value("${data.collection.start.month}")
    private int startMonth;

    @Autowired
    public UploadRecordServiceImpl(UploadRecordMapper uploadRecordMapper, TopicMapper topicMapper){
        this.uploadRecordMapper = uploadRecordMapper;
        this.topicMapper = topicMapper;
    }

    @Override
    public Map<String, Object> getUploadRecordList(HttpSession session, PageRequest request) {
        int pageNum = request.getPageNum();
        int pageSize = request.getPageSize();
        User user = (User) session.getAttribute("user");
        String role = user.getRole();
        //上传记录列表
        List<UploadRecord> uploadRecords = new ArrayList<>();
        if (role.equals("工委")) {
            String workingCommitteeId = user.getWorkingCommitteeId();
            uploadRecords = uploadRecordMapper.selectByWorkingCommitteeId(workingCommitteeId);
        }
        if (role.equals("部门")) {
            String departmentId = user.getDepartmentId();
            uploadRecords = uploadRecordMapper.selectByDepartmentId(departmentId);
        }
        //拼接处理主题名并去除重复上级主题
        handleTopicName(uploadRecords);
        return ResultMapUtils.getPageResultMap(uploadRecords, pageNum, pageSize);
    }

    private void handleTopicName(List<UploadRecord> uploadRecords){
        //遍历展示主题信息列表
        for (UploadRecord uploadRecord : uploadRecords){
            //如果展示主题的主题编码与上级主题的主题编码相同, 则拼接处理主题名并添加至重复主题列表中
            if (!StringUtils.isEmpty(uploadRecord.getSuperiorTopicId())) {
                String topicName = topicMapper.getAbsolutePathByTopicId(uploadRecord.getTopicId());
                uploadRecord.setTopicName(topicName);
            }
        }
    }

    @Override
    public Map<String, Object> getUploadRecordListByCondition(HttpSession session, UploadRecordRequest request) throws ParseException {
        //查询条件 部门名
        String departmentName = request.getDepartmentName();
        //查询条件 日期数组
        String[] dateArray = request.getDateArray();
        //查询条件主题名
        String topicName = request.getTopicName();
        //查询条件上报频度
        String uploadPeriod = request.getUploadPeriod();
        int pageNum = request.getPageNum();
        int pageSize = request.getPageSize();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        //上传记录列表
        List<UploadRecord> uploadRecords = new ArrayList<>();
        //起始日期
        Date startDate = DateUtlis.getFistDayInMonth(startYear, startMonth);
        //结束日期
        Date endDate = new Date();
        //判断查询时间范围数组是否存在
        if (dateArray != null && dateArray.length == 2){
            startDate = format.parse(dateArray[0]);
            endDate = DateUtlis.getLastTimeInDay(format.parse(dateArray[1]));

        }
        //获取数据主题目录数组
        String[] topicNamePath = topicName.split("/");
        User user = (User) session.getAttribute("user");
        String role = user.getRole();
        if (role.equals("工委")) {
            String workingCommitteeId = user.getWorkingCommitteeId();
            if (uploadPeriod.equals("全部")){
                uploadPeriod = "";
            }
            uploadRecords = uploadRecordMapper.conditionQueryByWorkingCommitteeId(workingCommitteeId, departmentName, topicNamePath[topicNamePath.length-1], uploadPeriod, startDate, endDate);
        }
        if (role.equals("部门")) {
            String departmentId = user.getDepartmentId();
            if (uploadPeriod.equals("全部")){
                uploadPeriod = "";
            }
            uploadRecords = uploadRecordMapper.conditionQueryByDepartmentId(departmentId, departmentName, topicNamePath[topicNamePath.length-1], uploadPeriod, startDate, endDate);
        }
        //拼接处理主题名并去除重复上级主题
        handleTopicName(uploadRecords);
        return ResultMapUtils.getPageResultMap(uploadRecords, pageNum, pageSize);
    }

    @Override
    public Map<String, Object> getUploadPeriods(HttpSession session) {
        List<String> uploadPeriods = new ArrayList<>();
        uploadPeriods.add("全部");
        User user = (User) session.getAttribute("user");
        String role = user.getRole();
        if (role.equals("工委")) {
            String workingCommitteeId = user.getWorkingCommitteeId();
            uploadPeriods.addAll(uploadRecordMapper.selectUploadPeriodByWorkingCommitteeId(workingCommitteeId));
            if (uploadPeriods.size() == 2){
                uploadPeriods.remove(0);
            }
        }
        if (role.equals("部门")) {
            String departmentId = user.getDepartmentId();
            uploadPeriods.addAll(uploadRecordMapper.selectUploadPeriodByDepartmentId(departmentId));
            if (uploadPeriods.size() == 2){
                uploadPeriods.remove(0);
            }
        }
        return ResultMapUtils.getResultMap(uploadPeriods);
    }

}
