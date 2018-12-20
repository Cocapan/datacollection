package cn.gz.rd.datacollection.service.imp;

import cn.gz.rd.datacollection.controller.request.PageRequest;
import cn.gz.rd.datacollection.controller.request.TopicRequest;
import cn.gz.rd.datacollection.dao.TopicMapper;
import cn.gz.rd.datacollection.dao.UploadRecordMapper;
import cn.gz.rd.datacollection.dao.WorkDeptRelationTblMapper;
import cn.gz.rd.datacollection.model.*;
import cn.gz.rd.datacollection.service.*;
import cn.gz.rd.datacollection.utils.DateUtlis;
import cn.gz.rd.datacollection.utils.ExcelUtils;
import cn.gz.rd.datacollection.utils.ResultMapUtils;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author panxuan
 * 主题服务实现类
 */
@Service
public class TopicServiceImpl implements TopicService {

    /**
     * 主题数据访问
     */
    private TopicMapper topicMapper;

    /**
     * 上传记录访问
     */
    private UploadRecordMapper uploadRecordMapper;

    private WorkDeptRelationTblMapper workDeptRelationTblMapper;

    /**
     * 假期数据访问
     */
    private HolidayService holidayService;

    /**
     * 通用采集功能
     */
    private CommonCollectionService commonCollectionService;

    /**
     * SFTP服务功能
     */
    private SFTPService sftpService;

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

    /**
     * 采集系统开始天数
     */
    @Value("${data.collection.start.day}")
    private int startDay;

    /**
     * 上传文件的保存目录前缀
     */
    @Value("${uploadfile.new.save.root.dir}")
    private String uploadNewFileSaveDir;

    /**
     * 上传文件的历史保存目录前缀
     */
    @Value("${uploadfile.history.save.root.dir}")
    private String uploadHisFileSaveDir;

    @Autowired
    public TopicServiceImpl(TopicMapper topicMapper, UploadRecordMapper uploadRecordMapper, HolidayService holidayService, CommonCollectionService commonCollectionService, SFTPService sftpService, WorkDeptRelationTblMapper workDeptRelationTblMapper){
        this.topicMapper = topicMapper;
        this.uploadRecordMapper = uploadRecordMapper;
        this.holidayService = holidayService;
        this.commonCollectionService = commonCollectionService;
        this.sftpService = sftpService;
        this.workDeptRelationTblMapper = workDeptRelationTblMapper;
    }

    @Override
    public Map<String, Object> getToUploadTopics(HttpSession session, PageRequest request) {
        //本月需上传主题
        List<ShowTopic> toUploadTopics = new ArrayList<>();
        //本月上传记录
        List<UploadRecord> uploadRecords = new ArrayList<>();
        int pageNum = request.getPageNum();
        int pageSize = request.getPageSize();
        User user = (User) session.getAttribute("user");
        //用户角色信息
        String role = user.getRole();
        boolean isCXJW = false;
        if (role.equals("工委")){
            String workingCommitteeId = user.getWorkingCommitteeId();
            if (workingCommitteeId.equals("GWBM_01")){
                isCXJW = true;
            }
            //获取本月的上报频度
            String currentUploadPeriod = getUploadPeriods(DateUtlis.getCurrentMonth());
            toUploadTopics = topicMapper.selectByWorkingCommitteeIdAndUploadPeriod(workingCommitteeId, currentUploadPeriod);
            //拼接处理主题名
            handleTopicName(toUploadTopics);
            //查询本月的上传记录
            uploadRecords = uploadRecordMapper.selectThisMonthRecordByWorkingCommitteeId(
                    DateUtlis.getFistDayInMonth(DateUtlis.getCurrentYear(), DateUtlis.getCurrentMonth()),
                    DateUtlis.getLastDayInMonth(DateUtlis.getCurrentYear(), DateUtlis.getCurrentMonth()),
                    getStatisticsPeriods(DateUtlis.getMonthDate(0)),
                    workingCommitteeId);

        }
        if (role.equals("部门")){
            String departmentId = user.getDepartmentId();
            List<String> workingCommitteeIds = workDeptRelationTblMapper.selectWorkingCommitteeIdsByDepartmentId(departmentId);
            if (workingCommitteeIds.contains("GWBM_01")){
                isCXJW = true;
            }
            //获取本月的上报频度
            String currentUploadPeriod = getUploadPeriods(DateUtlis.getCurrentMonth());
            toUploadTopics = topicMapper.selectByDepartmentIdAndUploadPeriod(departmentId, currentUploadPeriod);
            //拼接处理主题名
            handleTopicName(toUploadTopics);
            //查询本月的上传记录
            uploadRecords = uploadRecordMapper.selectThisMonthRecordByDepartmentId(
                    DateUtlis.getFistDayInMonth(DateUtlis.getCurrentYear(), DateUtlis.getCurrentMonth()),
                    DateUtlis.getLastDayInMonth(DateUtlis.getCurrentYear(), DateUtlis.getCurrentMonth()),
                    getStatisticsPeriods(DateUtlis.getMonthDate(0)),
                    departmentId);
        }
        //设置上传时间
        List<ShowTopic> topics = setUploadTime(uploadRecords, setCurrentStatisticsPeriod(toUploadTopics), isCXJW);
        if (user.isBatchUpload()){
            handleSeasonTopic(topics);
        }
        return ResultMapUtils.getPageResultMap(topics, pageNum, pageSize);
    }

    @Override
    public Map<String, Object> getNotYetUploadTopics(HttpSession session, PageRequest request) {
        User user = (User) session.getAttribute("user");
        String role = user.getRole();
        int pageNum = request.getPageNum();
        int pageSize = request.getPageSize();
        //未上传主题列表
        List<ShowTopic> notYetUploadTopics = new ArrayList<>();
        //获取本月月份数
        int currentMonth = DateUtlis.getCurrentMonth();
        //获取本年年数
        int currentYear = DateUtlis.getCurrentYear();
        //总月份数
        int monthCount=0;
        //2018-04 2018-06 总月份数为: 6-4+1=3
        if (startYear == currentYear){
            monthCount = currentMonth - startMonth + 1;
        }
        //2017-06 2018-06 总月份数为: (12-6+1)+6=13
        if ((currentYear - startYear) == 1){
            monthCount = (12 - startMonth + 1) + currentMonth;
        }
        //2016-06 2018-06 总月份数为: (12-6+1)+12*(2018-2016-1)+6=25
        if ((currentYear - startYear) > 1){
            monthCount = (12 - startMonth + 1) + 12*(currentYear - startYear -1) + currentMonth;
        }
        //遍历递增总月份数
        for (int i=0;i<monthCount;i++){
            //获取上传频度, 如当前为6月, 则依次获取6 5 4 3 2 1月的上报频度
            String uploadPeriod = getUploadPeriods(DateUtlis.getMonth(-(i-1)));
            //获取统计周期, 如当前为6月, 则依次获取5 4 3 2 1月的统计周期
            String statisticsPeriod = getStatisticsPeriods(DateUtlis.getMonthDate(-i));
            //获取统计周期集合
            Map<String, String> statisticsPeriodMap = getStatisticsPeriodMap(DateUtlis.getMonthDate(-i));
            //已上传主题列表
            List<ShowTopic> alreadyUploadTopics = new ArrayList<>();
            //重复主题列表
            List<ShowTopic> repeatUploadTopics = new ArrayList<>();
            //需上传主题列表
            List<ShowTopic> toUploadTopics = new ArrayList<>();
            //按日报送主题列表
            List<ShowTopic> dayTopics = new ArrayList<>();
            List<ShowTopic> weekTopics = new ArrayList<>();
            //上传记录列表
            List<UploadRecord> uploadRecords = new ArrayList<>();
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            Date monthDate = DateUtlis.getMonthDate(-i);
            String dateString = format.format(monthDate);
            int year = Integer.parseInt(dateString.substring(0, 4));
            int month = Integer.parseInt(dateString.substring(4,6));
            List<String> workDayMap = holidayService.getWorkDayMap(year, month);
            int maxUploadDeadLine = topicMapper.selectMaxUploadDeadLine();
            holidayService.getWorkDayMap(workDayMap, year, month, workDayMap.size() + maxUploadDeadLine);
            if (role.equals("工委")) {
                String workingCommitteeId = user.getWorkingCommitteeId();
                toUploadTopics = topicMapper.selectByWorkingCommitteeIdAndUploadPeriod(workingCommitteeId, uploadPeriod);
                //拼接处理主题名
                handleTopicName(toUploadTopics);
                uploadRecords = uploadRecordMapper.selectByWorkingCommitteeIdAndStatisticsPeriod(workingCommitteeId, statisticsPeriod);
            }
            if (role.equals("部门")) {
                String departmentId = user.getDepartmentId();
                toUploadTopics = topicMapper.selectByDepartmentIdAndUploadPeriod(departmentId, uploadPeriod);
                //拼接处理主题名
                handleTopicName(toUploadTopics);
                uploadRecords = uploadRecordMapper.selectByDepartmentIdAndStatisticsPeriod(departmentId, statisticsPeriod);
            }
            //遍历需上传主题列表
            for (ShowTopic toUploadTopic : toUploadTopics) {
                switch (toUploadTopic.getUploadPeriod()) {
                    case "日":
                        handleDayStatisticPeriod(i, toUploadTopic, repeatUploadTopics, dayTopics);
                        break;
                    case "周":
                        weekTopics.addAll(handleWeekStatisticPeriod(i, toUploadTopic, repeatUploadTopics));
                        break;
                    default:
                        //设置统计周期字段
                        toUploadTopic.setStatisticsPeriod(statisticsPeriodMap.get(toUploadTopic.getUploadPeriod()));
                        if (toUploadTopic.getUploadDeadLine() != 0) {
                            int uploadDeadLine = toUploadTopic.getUploadDeadLine();
                            toUploadTopic.setUploadDeadLineDate(workDayMap.get(uploadDeadLine - 1));
                        }
                        break;
                }
            }
            toUploadTopics.removeAll(repeatUploadTopics);
            toUploadTopics.addAll(dayTopics);
            toUploadTopics.addAll(weekTopics);
            for(ShowTopic toUploadTopic : toUploadTopics){
                String currentDateString = DateUtlis.getCurrentDateString();
                String uploadDeadLineDate = toUploadTopic.getUploadDeadLineDate();
                if (uploadDeadLineDate != null){
                    if (Integer.parseInt(currentDateString) < Integer.parseInt(toUploadTopic.getUploadDeadLineDate())){
                        alreadyUploadTopics.add(toUploadTopic);
                    }
                }
                //遍历上传记录表
                for (UploadRecord uploadRecord : uploadRecords){
                    //如果该主题已上传, 添加至已上传主题列表
                    if (toUploadTopic.getTopicId().equals(uploadRecord.getTopicId()) && toUploadTopic.getStatisticsPeriod().equals(uploadRecord.getStatisticsPeriod())){
                        alreadyUploadTopics.add(toUploadTopic);
                    }
                }
            }
            //移除已上传主题, 留下未上传主题列表
            toUploadTopics.removeAll(alreadyUploadTopics);
            //添加需上传而未上传的主题列表
            notYetUploadTopics.addAll(toUploadTopics);
        }
        if (user.isBatchUpload()){
            handleSeasonTopic(notYetUploadTopics);
        }
        return ResultMapUtils.getPageResultMap(notYetUploadTopics, pageNum, pageSize);
    }

    @Override
    public Map<String, Object> getUploadTopics(HttpSession session, PageRequest request) {
        int pageNum = request.getPageNum();
        int pageSize = request.getPageSize();
        int thisYear = DateUtlis.getCurrentYear();
        int thisMonth = DateUtlis.getCurrentMonth();
        //小于采集开始年月的返回空数据集合
        if (thisYear == startYear && thisMonth < startMonth){
            return ResultMapUtils.getEmptyResultMap();
        }
        //获取该月的上报频度
        String uploadPeriods = getUploadPeriods(thisMonth);
        //上传主题列表
        List<ShowTopic> uploadTopics = new ArrayList<>();
        //上传记录列表
        List<UploadRecord> uploadRecords = new ArrayList<>();
        User user = (User) session.getAttribute("user");
        //用户角色
        String role = user.getRole();
        boolean isCXJW = false;
        if (role.equals("工委")){
            String workingCommitteeId = user.getWorkingCommitteeId();
            if (workingCommitteeId.equals("GWBM_01")){
                isCXJW = true;
            }
            //该月需上传主题列表
            uploadTopics = topicMapper.selectByWorkingCommitteeIdAndUploadPeriod(workingCommitteeId, uploadPeriods);
            //拼接处理主题名
            handleTopicName(uploadTopics);
            //查询该月的上传记录
            uploadRecords = uploadRecordMapper.selectThisMonthRecordByWorkingCommitteeId(
                    DateUtlis.getFistDayInMonth(thisYear, thisMonth),
                    DateUtlis.getLastDayInMonth(thisYear, thisMonth),
                    getStatisticsPeriods(DateUtlis.getFistDayInMonth(thisYear, thisMonth)),
                    workingCommitteeId);
        }
        if (role.equals("部门")){
            String departmentId = user.getDepartmentId();
            List<String> workingCommitteeIds = workDeptRelationTblMapper.selectWorkingCommitteeIdsByDepartmentId(departmentId);
            if (workingCommitteeIds.contains("GWBM_01")){
                isCXJW = true;
            }
            //该月需上传主题列表
            uploadTopics = topicMapper.selectByDepartmentIdAndUploadPeriod(departmentId, uploadPeriods);
            //拼接处理主题名
            handleTopicName(uploadTopics);
            //查询该月的上传记录
            uploadRecords = uploadRecordMapper.selectThisMonthRecordByDepartmentId(
                    DateUtlis.getFistDayInMonth(thisYear, thisMonth),
                    DateUtlis.getLastDayInMonth(thisYear, thisMonth),
                    getStatisticsPeriods(DateUtlis.getFistDayInMonth(thisYear, thisMonth)),
                    departmentId);
        }
        //设置上传时间
        List<ShowTopic> topics = setUploadTime(uploadRecords, setCurrentStatisticsPeriod(uploadTopics), isCXJW);
        if (user.isBatchUpload()){
            handleSeasonTopic(topics);
        }
        return ResultMapUtils.getPageResultMap(topics, pageNum, pageSize);
    }

    @Override
    public Map<String, Object> getUploadTopicsByCondition(HttpSession session, TopicRequest request) {
        //查询条件年份
        String year = request.getYear();
        //查询条件月份
        String month = request.getMonth();
        //查询条件部门名
        String departmentName = request.getDepartmentName();
        //查询条件主题类型
        String topicType = request.getTopicType();
        int pageNum = request.getPageNum();
        int pageSize = request.getPageSize();
        int thisYear;
        int thisMonth;
        //如果年份为空的话, 默认为当前年, 否则 为请求参数年份
        if (StringUtils.isEmpty(year)){
            thisYear = DateUtlis.getCurrentYear();
        }else {
            thisYear = Integer.parseInt(year);
        }
        //如果月份为空的话, 默认为当前月, 否则 为请求参数月份
        if (StringUtils.isEmpty(month)){
            thisMonth = DateUtlis.getCurrentMonth();
        }else {
            thisMonth = Integer.parseInt(month);
        }
        //获取该月的上报频度
        String uploadPeriods = getUploadPeriods(thisMonth);
        //该月需上传主题列表
        List<ShowTopic> uploadTopics = new ArrayList<>();
        //查询该月的上传记录
        List<UploadRecord> uploadRecords = new ArrayList<>();
        User user = (User) session.getAttribute("user");
        String role = user.getRole();
        boolean isCXJW = false;
        if (role.equals("工委")){
            String workingCommitteeId = user.getWorkingCommitteeId();
            if (workingCommitteeId.equals("GWBM_01")){
                isCXJW = true;
            }
            uploadTopics = topicMapper.conditionQueryByWorkingCommitteeId(workingCommitteeId, departmentName, getTopicTypes(topicType), uploadPeriods);
            //拼接处理主题名
            handleTopicName(uploadTopics);
            uploadRecords = uploadRecordMapper.selectByWorkingCommitteeIdAndStatisticsPeriod(
                    workingCommitteeId,
                    getStatisticsPeriods(DateUtlis.getFistDayInMonth(thisYear, thisMonth)));
        }
        if (role.equals("部门")) {
            String departmentId = user.getDepartmentId();
            List<String> workingCommitteeIds = workDeptRelationTblMapper.selectWorkingCommitteeIdsByDepartmentId(departmentId);
            if (workingCommitteeIds.contains("GWBM_01")){
                isCXJW = true;
            }
            uploadTopics = topicMapper.conditionQueryByDepartmentId(departmentId, departmentName, getTopicTypes(topicType), uploadPeriods);
            //拼接处理主题名
            handleTopicName(uploadTopics);
            uploadRecords = uploadRecordMapper.selectByDepartmentIdAndStatisticsPeriod(
                    departmentId,
                    getStatisticsPeriods(DateUtlis.getFistDayInMonth(thisYear, thisMonth))
            );
        }
        //设置上传时间
        List<ShowTopic> topics = setUploadTime(uploadRecords, setStatisticsPeriod(uploadTopics, DateUtlis.getMonthDifference(thisYear,thisMonth)), isCXJW);
        if (user.isBatchUpload()){
            handleSeasonTopic(topics);
        }
        return ResultMapUtils.getPageResultMap(topics, pageNum, pageSize);
    }

    @Override
    public Map<String, Object> getTopicNames(HttpSession session) {
        //主题名列表
        ArrayList<String> names = new ArrayList<>();
        User user = (User) session.getAttribute("user");
        String role = user.getRole();
        //主题列表
        List<ShowTopic> topics = new ArrayList<>();
        if (role.equals("工委")) {
            String workingCommitteeId = user.getWorkingCommitteeId();
            topics = topicMapper.selectByWorkingCommitteeId(workingCommitteeId);
        }
        if (role.equals("部门")) {
            String departmentId = user.getDepartmentId();
            topics = topicMapper.selectByDepartmentId(departmentId);
        }
        //拼接处理主题名
        handleTopicName(topics);
        //遍历主题列表, 获取主题名添加到主题名列表中
        for (Topic topic: topics){
            names.add(topic.getTopicName());
        }
        return ResultMapUtils.getResultMap(names);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadFile(String workingCommitteeId, String departmentId, String topicId, String statisticsPeriod, String uploadPeriod, String topicType, String storageTableName, String userName, String uploadTime, String previewUrl, String filePath) throws Exception {
        int result;
        File uploadFile = new File(filePath);
        InputStream inputStream = new FileInputStream(uploadFile);
        //获取文件名
        String fileName = uploadFile.getName();
        String originalFileName = fileName.substring(fileName.indexOf("_") + 1, fileName.length());
        SFTPClient sftpClient = sftpService.createClientAndLgoin();
        //判断文件类型是否为文件
        if (StringUtils.equals("文件", topicType)) {
            if (sftpClient.exist(uploadNewFileSaveDir+storageTableName)){
                Vector<LsEntry> files = sftpClient.listFiles(uploadNewFileSaveDir + storageTableName);
                for (LsEntry entry : files){
                    if (entry.getFilename().equals(originalFileName)){
                        sftpService.deleteNewFile(sftpClient, storageTableName + "/", originalFileName);
                    }
                }
            }
            sftpService.uploadNewFile(sftpClient, storageTableName, originalFileName, inputStream);
        }
        //判断文件类型是否为数据表
        if (StringUtils.equals("数据表", topicType)) {
            //Excel数据文件
            int count = topicMapper.countByStatisticsPeriod(storageTableName, statisticsPeriod);
            if (count != 0){
                //根据统计周期删除已上传的数据库记录
                result = topicMapper.deleteByStatisticsPeriod(storageTableName, statisticsPeriod);
                if (result == 0){
                    throw new CommonException("上传失败!");
                }
            }
            Workbook workbook = ExcelUtils.getWorkBookByFileName(originalFileName, inputStream);
            //将表格数据导入到数据库中
            commonCollectionService.uploadExcelData(topicId,
                    false, departmentId, statisticsPeriod, workbook.getSheetAt(0));
        }
        //插入上传记录
        UploadRecord uploadRecord = new UploadRecord();
        uploadRecord.setTopicId(topicId);
        uploadRecord.setDepartmentId(departmentId);
        uploadRecord.setWorkingCommitteeId(workingCommitteeId);
        uploadRecord.setFileName(originalFileName);
        uploadRecord.setStoragePath(storageTableName);
        uploadRecord.setPreviewPath(previewUrl);
        uploadRecord.setUploadTime(new Date());
        uploadRecord.setStatisticsPeriod(statisticsPeriod);
        uploadRecord.setUploadPeriod(uploadPeriod);
        uploadRecord.setUploadUser(userName);
        result = uploadRecordMapper.insert(uploadRecord);
        if (result == 0){
            throw new CommonException("上传记录插入失败!");
        }

    }

    @Override
    public Map<String, Object> getYears() {
        return ResultMapUtils.getResultMap(DateUtlis.getYearList(startYear, DateUtlis.getCurrentYear()));
    }

    @Override
    public Map<String, Object> getMonths(int year) {
        Map<Integer, List<Integer>> yearMonthListMap = DateUtlis.getMonthList(startYear, startMonth, DateUtlis.getCurrentYear(), DateUtlis.getCurrentMonth());
        return ResultMapUtils.getResultMap(yearMonthListMap.get(year));
    }

    @Override
    public Map<String, Object> getTopicTypes(HttpSession session) {
        User user = (User) session.getAttribute("user");
        String role = user.getRole();
        List<String> topicTypes = new ArrayList<>();
        topicTypes.add("全部");
        if (role.equals("工委")) {
            String workingCommitteeId = user.getWorkingCommitteeId();
            topicTypes.addAll(topicMapper.selectTopicTypesByWorkingCommitteeId(workingCommitteeId));
            if (topicTypes.size() == 2){
                topicTypes.remove(0);
            }
        }
        if (role.equals("部门")) {
            String departmentId = user.getDepartmentId();
            topicTypes.addAll(topicMapper.selectTopicTypesByDepartmentId(departmentId));
            if (topicTypes.size() == 2){
                topicTypes.remove(0);
            }
        }
        return ResultMapUtils.getResultMap(topicTypes);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadFile(HttpSession session, int year, int month, MultipartFile uploadFile) throws Exception {
        User user = (User) session.getAttribute("user");
        String role = user.getRole();
        String userName = user.getUserName();
        //获取文件名
        String originalFileName = uploadFile.getOriginalFilename();
        SFTPClient sftpClient = sftpService.createClientAndLgoin();
        String saveFileName = System.currentTimeMillis() + "_" + originalFileName;
        //判断文件类型是否为文件
        String filePath = sftpService.uploadHisFile(sftpClient, "/" + userName + "/" + "预算专工委/", saveFileName, uploadFile);
        Map<String, String> topicIdFilePvwMap = commonCollectionService.uploadExcelToNginx(filePath);
        List<ShowTopic> toUploadTopics = new ArrayList<>();
        for (String topicId : topicIdFilePvwMap.keySet()) {
            ShowTopic showTopic = topicMapper.selectByTopicId(topicId);
            if (showTopic != null) {
                toUploadTopics.add(showTopic);
            }else {
                throw new CommonException("sheet名" + topicId + "主题编码错误, 请检查sheet名!");
            }
        }
        List<UploadRecord> uploadRecords = new ArrayList<>();
        boolean isCXJW = false;
        if (role.equals("工委")){
            String workingCommitteeId = user.getWorkingCommitteeId();
            if (workingCommitteeId == "GWBM_01"){
                isCXJW = true;
            }
            uploadRecords = uploadRecordMapper.selectThisMonthRecordByWorkingCommitteeId(
                    DateUtlis.getFistDayInMonth(year, month),
                    DateUtlis.getLastDayInMonth(year, month),
                    getStatisticsPeriods(DateUtlis.getFistDayInMonth(year, month)),
                    workingCommitteeId);
        }
        if (role.equals("部门")) {
            String departmentId = user.getDepartmentId();
            uploadRecords = uploadRecordMapper.selectThisMonthRecordByDepartmentId(
                    DateUtlis.getFistDayInMonth(year, month),
                    DateUtlis.getLastDayInMonth(year, month),
                    getStatisticsPeriods(DateUtlis.getFistDayInMonth(year, month)),
                    departmentId);
        }
        List<ShowTopic> topics = setUploadTime(uploadRecords, setStatisticsPeriod(toUploadTopics, DateUtlis.getMonthDifference(year,month)), isCXJW);
        int result;
        for (ShowTopic toUploadTopic: topics) {
            String topicId = toUploadTopic.getTopicId();
            String storageTableName = toUploadTopic.getStorageTableName();
            String statisticsPeriod = toUploadTopic.getStatisticsPeriod();
            int count = topicMapper.countByStatisticsPeriod(storageTableName, statisticsPeriod);
            if (count != 0){
                //根据统计周期删除已上传的数据库记录
                result = topicMapper.deleteByStatisticsPeriod(storageTableName, statisticsPeriod);
                if (result == 0){
                    throw new CommonException("上传失败!");
                }
            }
            Workbook workbook = ExcelUtils.getWorkBookByFileName(originalFileName, uploadFile.getInputStream());
            //将表格数据导入到数据库中
            commonCollectionService.uploadExcelData(topicId,
                    false, toUploadTopic.getDepartmentId(), statisticsPeriod, workbook.getSheet(topicId));
            UploadRecord uploadRecord = new UploadRecord();
            uploadRecord.setTopicId(topicId);
            uploadRecord.setDepartmentId(toUploadTopic.getDepartmentId());
            uploadRecord.setWorkingCommitteeId(toUploadTopic.getWorkingCommitteeId());
            uploadRecord.setFileName(originalFileName);
            uploadRecord.setStoragePath(toUploadTopic.getTopicFileStoragePath());
            uploadRecord.setPreviewPath(topicIdFilePvwMap.get(topicId));
            uploadRecord.setUploadTime(new Date());
            uploadRecord.setStatisticsPeriod(statisticsPeriod);
            uploadRecord.setUploadPeriod(toUploadTopic.getUploadPeriod());
            uploadRecord.setUploadUser(user.getUserName());
            result = uploadRecordMapper.insert(uploadRecord);
            if (result == 0){
                throw new CommonException("上传记录插入失败!");
            }
        }
    }

    @Override
    public Map<String, Object> onlinePreview(HttpSession session, String statisticsPeriod, String topicId, String topicType, String uploadTime, String departmentId, boolean isJkww, MultipartFile uploadFile) throws Exception{
        boolean isDataTable = false;
        User user = (User) session.getAttribute("user");
        String originalFileName = uploadFile.getOriginalFilename();
        String[] originalFilePath = originalFileName.split("\\\\");
        String fileName = originalFilePath[originalFilePath.length-1];
        SFTPClient sftpClient = sftpService.createClientAndLgoin();
        String saveFileName = System.currentTimeMillis() + "_" + fileName;
        String filePath = sftpService.uploadHisFile(sftpClient, "/" + user.getUserName() + "/", saveFileName, uploadFile);
        if (topicType.equals("数据表")){
            isDataTable = true;
        }
        //上传文件到文件服务器, 提供在线预览
        Map<String, Object> resultMap = commonCollectionService.uploadFileToNginx(filePath, statisticsPeriod, topicId, departmentId, isDataTable, isJkww);
        resultMap.put("filePath", filePath);
        return resultMap;
    }

    @Override
    public void deletePreviewUrl(String previewUrl, String filePath) {
        File file;
        SFTPClient sftpClient = sftpService.createClientAndLgoin();
        if (previewUrl != null){
            file = new File(uploadNewFileSaveDir, previewUrl);
            deleteFile(sftpClient, file);
        }
        if (filePath != null){
            file = new File(filePath);
            deleteFile(sftpClient, file);
        }
    }

    private void deleteFile(SFTPClient sftpClient, File file) {
        String parent = file.getParent();
        String name = file.getName();
        sftpClient.delete(parent + "/", name);
    }

    /*private void isOverTime(String uploadTime) throws ParseException, CommonException {
        if (!uploadTime.equals( "null") && !uploadTime.equals("未上传")){
            if (uploadTime.contains(":")){
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date uploadDate = format.parse(uploadTime);
                //获取当前时间与上传时间的小时差
                long hours = DateUtlis.getTimeDifference(uploadDate, new Date());
                if (hours >= 24){
                    throw new CommonException("离首次上传已过24小时！");
                }
            }
        }
    }*/

    private void handleDayStatisticPeriod(int index, ShowTopic toUploadTopic, List<ShowTopic> repeatUploadTopics, List<ShowTopic> dayTopics){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date monthDate = DateUtlis.getMonthDate(-index);
        String dateString = format.format(monthDate);
        int year = Integer.parseInt(dateString.substring(0, 4));
        int month = Integer.parseInt(dateString.substring(4,6));
        List<String> workDayMap = holidayService.getWorkDayMap(year, month);
        List<String> monthDays = DateUtlis.getMonthDayList(year, month);
        Date startDate;
        Date endDate;
        if (index == 0){
            if (year == startYear && month == startMonth){
                startDate = DateUtlis.getDate(startYear, startMonth, startDay);
                endDate = monthDate;

            }else {
                startDate = DateUtlis.getFistDayInMonth(year,month);
                endDate = monthDate;
            }
        }else {
            endDate = DateUtlis.getLastDayInMonth(year, month);
            if (year == startYear && month == startMonth){
                startDate = DateUtlis.getDate(startYear, startMonth, startDay);
            }else {
                startDate = DateUtlis.getFistDayInMonth(year,month);
            }
        }
        int startDayIndex = monthDays.indexOf(DateUtlis.getDateString(startDate));
        long dayCount = DateUtlis.getDayDifference(startDate, endDate) + 1;
        if (dayCount == 0){
            repeatUploadTopics.add(toUploadTopic);
            return;
        }
        int maxUploadDeadLine = topicMapper.selectMaxUploadDeadLineByUploadPeriod("日");
        holidayService.getWorkDayMap(workDayMap, year, month, workDayMap.size() + maxUploadDeadLine);
        for (int j=startDayIndex;j<dayCount;j++){
            ShowTopic showTopic = new ShowTopic();
            showTopic.setTopicId(toUploadTopic.getTopicId());
            showTopic.setUploadTime(toUploadTopic.getUploadTime());
            showTopic.setDepartmentId(toUploadTopic.getDepartmentId());
            showTopic.setDepartmentName(toUploadTopic.getDepartmentName());
            showTopic.setWorkingCommitteeName(toUploadTopic.getWorkingCommitteeName());
            showTopic.setColllectWay(toUploadTopic.getColllectWay());
            showTopic.setComment(toUploadTopic.getComment());
            showTopic.setStorageTableName(toUploadTopic.getStorageTableName());
            showTopic.setSuperiorTopicId(toUploadTopic.getSuperiorTopicId());
            showTopic.setTopicFileStoragePath(toUploadTopic.getTopicFileStoragePath());
            showTopic.setTopicLevel(toUploadTopic.getTopicLevel());
            showTopic.setTopicName(toUploadTopic.getTopicName());
            showTopic.setUploadDeadLine(toUploadTopic.getUploadDeadLine());
            showTopic.setWorkingCommitteeId(toUploadTopic.getWorkingCommitteeId());
            showTopic.setTopicType(toUploadTopic.getTopicType());
            showTopic.setUploadPeriod(toUploadTopic.getUploadPeriod());
            showTopic.setStatisticsPeriod(monthDays.get((int) (dayCount-j-1)));
            int uploadDeadLine = toUploadTopic.getUploadDeadLine();
            showTopic.setUploadDeadLineDate(workDayMap.get(workDayMap.indexOf(showTopic.getStatisticsPeriod()) + uploadDeadLine));
            repeatUploadTopics.add(toUploadTopic);
            dayTopics.add(showTopic);
        }
    }

    private  List<ShowTopic> handleWeekStatisticPeriod(int index, ShowTopic toUploadTopic, List<ShowTopic> repeatUploadTopics){
        List<ShowTopic> weekTopics = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date monthDate = DateUtlis.getMonthDate(-index);
        String dateString = format.format(monthDate);
        int year = Integer.parseInt(dateString.substring(0, 4));
        int month = Integer.parseInt(dateString.substring(4,6));
        Date startDate = getStartDate(year, month);
        Date endDate = getEndDate(index, year, month, monthDate);
        List<String> workDayMap = holidayService.getWorkDayMap(year, month);
        Map<String, Integer> weekMap = getWeekMap(startDate, endDate);
        int maxUploadDeadLine = topicMapper.selectMaxUploadDeadLineByUploadPeriod("周");
        holidayService.getWorkDayMap(workDayMap, year, month, workDayMap.size() + maxUploadDeadLine);
        Integer startWeek = weekMap.get("startWeek");
        Integer endWeek = weekMap.get("endWeek");
        for (int j=startWeek;j<=endWeek;j++){
            ShowTopic showTopic = new ShowTopic();
            showTopic.setTopicId(toUploadTopic.getTopicId());
            showTopic.setUploadTime(toUploadTopic.getUploadTime());
            showTopic.setDepartmentId(toUploadTopic.getDepartmentId());
            showTopic.setDepartmentName(toUploadTopic.getDepartmentName());
            showTopic.setWorkingCommitteeName(toUploadTopic.getWorkingCommitteeName());
            showTopic.setColllectWay(toUploadTopic.getColllectWay());
            showTopic.setComment(toUploadTopic.getComment());
            showTopic.setStorageTableName(toUploadTopic.getStorageTableName());
            showTopic.setSuperiorTopicId(toUploadTopic.getSuperiorTopicId());
            showTopic.setTopicFileStoragePath(toUploadTopic.getTopicFileStoragePath());
            showTopic.setTopicLevel(toUploadTopic.getTopicLevel());
            showTopic.setTopicName(toUploadTopic.getTopicName());
            showTopic.setUploadDeadLine(toUploadTopic.getUploadDeadLine());
            showTopic.setWorkingCommitteeId(toUploadTopic.getWorkingCommitteeId());
            showTopic.setTopicType(toUploadTopic.getTopicType());
            showTopic.setUploadPeriod(toUploadTopic.getUploadPeriod());
            showTopic.setStatisticsPeriod(dateString.substring(0, 4)+"W"+j);
            int uploadDeadLine = toUploadTopic.getUploadDeadLine();
            List<String> dayListByWeek = DateUtlis.getDayListByWeek(j);
            for (String day : dayListByWeek){
                if (workDayMap.contains(day)){
                    showTopic.setUploadDeadLineDate(workDayMap.get(workDayMap.indexOf(day) + uploadDeadLine));
                    break;
                }
            }
            repeatUploadTopics.add(toUploadTopic);
            weekTopics.add(showTopic);
        }
        if (DateUtlis.isRepeatWeek(startDate)){
            weekTopics.remove(0);
        }
        return weekTopics;
    }

    /**
     * 根据上级主题拼接处理主题名, 并去除重复主题
     * @param topics 主题列表
     */
    private void handleTopicName(List<ShowTopic> topics){
        //遍历展示主题信息列表
        for (ShowTopic topic : topics){
            //如果展示主题的主题编码与上级主题的主题编码相同, 则拼接处理主题名并添加至重复主题列表中
            if (!StringUtils.isEmpty(topic.getSuperiorTopicId())) {
                String topicName = topicMapper.getAbsolutePathByTopicId(topic.getTopicId());
                topic.setTopicName(topicName);
            }
        }
    }

    /**
     * 获取主题类型
     * @param topicType 主题类型
     * @return 主题类型
     */
    private String getTopicTypes(String topicType){
        switch (topicType){
            case "文件":
                return "文件|目录";
            case "数据表":
                return "数据表";
            case "全部":
                return "文件|数据表|目录";
            default:
                return "文件|数据表|目录";
        }
    }

    /**
     * 通过上传记录设置上传时间
     * @param uploadRecords 上传记录表
     * @param topics 主题表
     * @return 设置上传时间后的主题表
     */
    private List<ShowTopic> setUploadTime(List<UploadRecord> uploadRecords, List<ShowTopic> topics, boolean isCXJW){
        //遍历本月需上传主题
        for (ShowTopic topic : topics){
            topic.setUploadTime("未上传");
            //遍历本月的上传记录
            for(UploadRecord uploadRecord : uploadRecords){
                //获取本月已上传的主题名
                String topicId = uploadRecord.getTopicId();
                //如果该主题已上传, 设置上传时间; 未上传, 不作处理
                if (topic.getTopicId().equals(topicId) && topic.getStatisticsPeriod().equals(uploadRecord.getStatisticsPeriod())){
                    if (!topic.getUploadTime().equals("未上传")){
                        break;
                    }
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String uploadTime = format.format(uploadRecord.getUploadTime());
                    topic.setUploadTime(uploadTime);
                    if (isCXJW){
                        //获取当前时间与上传时间的小时差
                        long hours = DateUtlis.getTimeDifference(uploadRecord.getUploadTime(), new Date());
                        if (hours >= 24){
                            topic.setOverTime(true);
                        }
                    }
                }
            }
        }
        return topics;
    }

    /**
     * 处理季度主题
     * @param topics 主题表
     */
    private void handleSeasonTopic(List<ShowTopic> topics){
        List<ShowTopic> removeTopics = new ArrayList<>();
        //遍历本月需上传主题
        for (ShowTopic topic : topics){
            if (topic.getUploadPeriod().equals("季")){
                if (topic.getStatisticsPeriod().contains("Q1") && !topic.getTopicName().contains("第一季度")){
                    removeTopics.add(topic);
                }
                if (topic.getStatisticsPeriod().contains("Q2") && !topic.getTopicName().contains("第二季度")){
                    removeTopics.add(topic);
                }
                if (topic.getStatisticsPeriod().contains("Q3") && !topic.getTopicName().contains("第三季度")){
                    removeTopics.add(topic);
                }
                if (topic.getStatisticsPeriod().contains("Q4") && !topic.getTopicName().contains("第四季度")){
                    removeTopics.add(topic);
                }
            }
        }
        topics.removeAll(removeTopics);
    }

    /**
     * 设置统计周期
     * @param topics 主题列表
     * @param index 索引, -1表示上上月 0表示上月, 1表示本月
     * @return 设置统计周期后的主题列表
     */
    private List<ShowTopic> setStatisticsPeriod(List<ShowTopic> topics, int index){
        Date monthDate = DateUtlis.getMonthDate(-index);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String dateString = format.format(monthDate);
        int year = Integer.parseInt(dateString.substring(0, 4));
        int month = Integer.parseInt(dateString.substring(4,6));
        List<String> workDayMap = holidayService.getWorkDayMap(year, month);
        //获取上月统计周期集合
//        Map<String, String> statisticsPeriodMap = getStatisticsPeriodMap(DateUtlis.getMonthDate(0));
        Map<String, String> statisticsPeriodMap = getStatisticsPeriodMap(monthDate);
        List<ShowTopic> repeatTopics = new ArrayList<>();
        List<ShowTopic> dayTopics = new ArrayList<>();
        List<ShowTopic> weekTopics = new ArrayList<>();
        //遍历本月需上传主题
        for (ShowTopic topic : topics){
            switch (topic.getUploadPeriod()) {
                case "日":
                    handleDayStatisticPeriod(index, topic, repeatTopics, dayTopics);
                    break;
                case "周":
                    weekTopics.addAll(handleWeekStatisticPeriod(index, topic, repeatTopics));
                    break;
                default:
                    topic.setStatisticsPeriod(statisticsPeriodMap.get(topic.getUploadPeriod()));
                    if (topic.getUploadDeadLine() != 0) {
                        int uploadDeadLine = topic.getUploadDeadLine();
                        holidayService.getWorkDayMap(workDayMap, year, month, uploadDeadLine);
                        topic.setUploadDeadLineDate(workDayMap.get(uploadDeadLine));
                    }
                    break;
            }
        }
        topics.removeAll(repeatTopics);
        topics.addAll(dayTopics);
        topics.addAll(weekTopics);
        return topics;
    }

    /**
     * 设置上月统计周期
     * @param topics 主题列表
     * @return 设置统计周期后的主题列表
     */
    private List<ShowTopic> setCurrentStatisticsPeriod(List<ShowTopic> topics){
        return setStatisticsPeriod(topics,0);
    }

    /**
     * 根据月份数获取上报频度
     * @param month 月份数
     * @return 上报频度
     */
    private String getUploadPeriods(int month){
        String uploadPeriod;
        switch (month){
            case 1:
                uploadPeriod = "日|周|年|季|月|半年";
                break;
            case 4:
                uploadPeriod = "日|周|季|月";
                break;
            case 7:
                uploadPeriod = "日|周|季|月|半年";
                break;
            case 10:
                uploadPeriod = "日|周|季|月";
                break;
            default:
                uploadPeriod = "日|周|月";
                break;
        }
        return uploadPeriod;
    }

    /**
     * 根据日期获取统计周期集合
     * @param date 日期
     * @return 统计周期集合
     */
    private Map<String, String> getStatisticsPeriodMap(Date date){
        HashMap<String, String> statisticsPeriodMap = new HashMap<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        //获取上月的年月
        String dateString = format.format(DateUtlis.getLastMonthDate(date));
        String[] yearAndMonth = dateString.split("-");
        int year = Integer.parseInt(yearAndMonth[0]);
        int month = Integer.parseInt(yearAndMonth[1]);
        long dayDifference = DateUtlis.getDayDifference(DateUtlis.getFistDayInMonth(year, month+1), DateUtlis.getLastDayInMonth(year, month+1)) + 1;
        for (int j=0;j<dayDifference;j++){
            statisticsPeriodMap.put(DateUtlis.getDay(j, DateUtlis.getLastDayInMonth(year, month+1)), "日");
        }
        Map<String, Integer> weekMap = getWeekMap(getStartDate(year, month+1), DateUtlis.getLastDayInMonth(year, month+1));
        Integer startWeek = weekMap.get("startWeek");
        Integer endWeek = weekMap.get("endWeek");
        for (int j=startWeek;j<=endWeek;j++){
            statisticsPeriodMap.put(year+"W"+j, "周");
        }
        switch (month){
            case 3:
                statisticsPeriodMap.put("季", year+"Q1");
                statisticsPeriodMap.put("月", dateString.replace("-", ""));
                break;
            case 6:
                statisticsPeriodMap.put("半年", year+"Y1");
                statisticsPeriodMap.put("季", year+"Q2");
                statisticsPeriodMap.put("月", dateString.replace("-", ""));
                break;
            case 9:
                statisticsPeriodMap.put("季", year+"Q3");
                statisticsPeriodMap.put("月", dateString.replace("-", ""));
                break;
            case 12:
                statisticsPeriodMap.put("年", String.valueOf(year));
                statisticsPeriodMap.put("半年", year+"Y2");
                statisticsPeriodMap.put("季", year+"Q4");
                statisticsPeriodMap.put("月", dateString.replace("-", ""));
                break;
            default:
                statisticsPeriodMap.put("月", dateString.replace("-", ""));
                break;
        }
        return statisticsPeriodMap;
    }

    /**
     * 根据日期获取统计周期
     * @param date 日期
     * @return 统计周期
     */
    private String getStatisticsPeriods(Date date){
        //获取该日期的统计周期集合
        Map<String, String> statisticsPeriodMap = getStatisticsPeriodMap(date);
        //获取上报频度集合
        Set<String> uploadPeriodSet = statisticsPeriodMap.keySet();
        StringBuilder statisticsPeriod = new StringBuilder();
        int size = uploadPeriodSet.size();
        int i = 0;
        //遍历上报频度集合, 拼接统计周期
        for (String uploadPeriod : uploadPeriodSet){
            i++;
            switch (statisticsPeriodMap.get(uploadPeriod)) {
                case "日":
                    statisticsPeriod.append(uploadPeriod);
                    break;
                case "周":
                    statisticsPeriod.append(uploadPeriod);
                    break;
                default:
                    statisticsPeriod.append(statisticsPeriodMap.get(uploadPeriod));
                    break;
            }
            if (i != size){
                statisticsPeriod.append("|");
            }
        }
        return statisticsPeriod.toString();
    }



    private Map<String, Integer> getWeekMap(Date startDate, Date endDate){
       return DateUtlis.getWeekMap(startDate, endDate);
    }

    private Date getStartDate(int year, int month){
        Date startDate;
        if (year == startYear && month == startMonth){
            startDate = DateUtlis.getDate(startYear, startMonth, startDay);
        }else {
            startDate = DateUtlis.getFistDayInMonth(year,month);
        }
        return startDate;
    }

    private Date getEndDate(int index, int year, int month, Date monthDate){
        Date endDate;
        if (index == 0) {
            endDate = monthDate;
        }else{
            endDate = DateUtlis.getLastDayInMonth(year, month);
        }
        return endDate;
    }

    @Override
    public boolean isAllUpload(String workingCommitteeId, String departmentId, String uploadPeriod, String statisticPeriod) {
        int notUploadTopics = topicMapper.countNotUploadTopic(workingCommitteeId, departmentId, uploadPeriod, statisticPeriod);
        return notUploadTopics != 0;
    }

}
