package cn.gz.rd.datacollection.service;

import org.springframework.stereotype.Service;


@Service
public interface CommonAutoNoticeJobService {

    void notUploadTopicAutoNotice(String workingCommitteeId, String uploadPeriod, String noticeMessage, String statisticPeriod);

    void seasonNotUploadTopicAutoNotice(String workingCommitteeId, String uploadPeriod, String noticeMessage);

    void sentUploadNoticeMsgToDepartment(String workingCommitteeId, String uploadPeriod, String noticeMessage);

    void weekNotUploadTopicAutoNotice(String workingCommitteeId, String noticeMessage);

    String getUploadDeadLineDateString(String workingCommitteeId, String uploadPeriod);

}
