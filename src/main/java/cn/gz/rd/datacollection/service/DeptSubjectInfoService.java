package cn.gz.rd.datacollection.service;

import cn.gz.rd.datacollection.dao.DeptSubjectInfoMapper;
import cn.gz.rd.datacollection.dao.DeptSubjectOperateInfoMapper;
import cn.gz.rd.datacollection.model.DeptSubject;
import cn.gz.rd.datacollection.model.DeptSubjectInfo;
import cn.gz.rd.datacollection.model.DeptSubjectOperateInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DeptSubjectInfoService {

    @Autowired
    private DeptSubjectInfoMapper deptSubjectInfoMapper;

    @Autowired
    private DeptSubjectOperateInfoMapper deptSubjectOperateInfoMapper;

    public boolean hasDeptSubjectInfo(String subjectCode, String deptCode, String countRate) {
        DeptSubjectInfo subjectInfo = selectOnlyOneDeptSubject(subjectCode, deptCode, countRate);
        if (subjectInfo == null) {
            return false;
        } else {
            return true;
        }
    }

    public DeptSubjectInfo selectBySubjectStatusId(Integer subjectStatusId) {
        return deptSubjectInfoMapper.selectByPrimaryKey(subjectStatusId);
    }

    public DeptSubjectInfo selectOnlyOneDeptSubject(String subjectCode, String deptCode,String countRate) {
        DeptSubjectInfo condition = new DeptSubjectInfo();
        condition.setBmbh(deptCode);
        condition.setZtbh(subjectCode);
        condition.setSjtjzq(countRate);
        List<DeptSubjectInfo> deptSubjectInfos = deptSubjectInfoMapper.selectDeptSubject(condition);
        if (deptSubjectInfos.size() == 0) {
            return null;
        } else {
            return deptSubjectInfos.get(0);
        }
    }

    public void saveDeptSubjectInfo(DeptSubjectInfo deptSubjectInfo) {
        deptSubjectInfoMapper.insertSelective(deptSubjectInfo);
    }

    public void saveDeptSubjectOperateInfo(DeptSubjectOperateInfo info) {
        deptSubjectOperateInfoMapper.insertSelective(info);
    }

    public List<DeptSubjectInfo> selectManySubjectAtCountRate(List<Integer> subjectStatusIds, String countRate) {
        return deptSubjectInfoMapper.selectManySubjectAtCountRate(subjectStatusIds, countRate);
    }

    public int updateDeptSubjectInfo(DeptSubjectInfo deptSubjectInfo) {
        return deptSubjectInfoMapper.updateByPrimaryKey(deptSubjectInfo);
    }

    public List<DeptSubjectInfo> selectAllSubjectByCountRate(String countRate) {
        DeptSubjectInfo conn = new DeptSubjectInfo();
        conn.setSjtjzq(countRate);
        return deptSubjectInfoMapper.selectDeptSubject(conn);
    }

}
