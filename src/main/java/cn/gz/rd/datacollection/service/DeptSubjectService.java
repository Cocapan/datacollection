package cn.gz.rd.datacollection.service;

import cn.gz.rd.datacollection.dao.DeptSubjectMapper;
import cn.gz.rd.datacollection.model.DeptSubject;
import cn.gz.rd.datacollection.model.SubjectDeptInfoVO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DeptSubjectService {

    private final Logger logger = LoggerFactory.getLogger(DeptSubjectService.class);

    @Autowired
    private DeptSubjectMapper deptSubjectMapper;

    public void saveAll(String[] deptCodes, String subjectCode) {
        for (String deptCode : deptCodes) {
            DeptSubject deptSubject = new DeptSubject();
            deptSubject.setBmbh(deptCode);
            deptSubject.setZtbh(subjectCode);
            deptSubject.setCjsj(new Date());
            deptSubjectMapper.insert(deptSubject);
        }
    }

    public int deleteAll(String subjectCode) {
        int deleteCount = deptSubjectMapper.deleteAllDeptCode(subjectCode);
        logger.info("根据主题编号删除所有‘主题部门关联表’数据，subjectCode = {}, delete count = {}",
                subjectCode, deleteCount);
        return deleteCount;
    }

    public void deleteAndSaveAll(String[] deptCodes, String subjectCode) {
        if (deptCodes != null && deptCodes.length > 0
                && StringUtils.isNotBlank(subjectCode)) {
            deleteAll(subjectCode);
            saveAll(deptCodes, subjectCode);
        }
    }

    public int saveDeptSubject(DeptSubject deptSubject) {
        return deptSubjectMapper.insert(deptSubject);
    }

    public List<SubjectDeptInfoVO> selectDeptInfoBySubjectCode(List<String> subjectCodes) {
        if (CollectionUtils.isEmpty(subjectCodes)) {
            return new ArrayList<>();
        }
        return deptSubjectMapper.selectDeptInfoBySubjectCode(subjectCodes);
    }

}
