package cn.gz.rd.datacollection.service.imp;

import cn.gz.rd.datacollection.dao.JkwwProjectInfoMapper;
import cn.gz.rd.datacollection.model.JkwwProjectInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 科教文卫工委数据采集服务
 */
@Service
public class JkwwCollectionServiceImp {

    @Resource
    private JkwwProjectInfoMapper projectInfoMapper;

    @Autowired
    private JkwwParseExcelServiceImp parseExcelService;

    @Transactional(rollbackFor = Exception.class)
    public void parseAndSaveProjectInfo(String fileName, InputStream is) throws IOException {

        List<JkwwProjectInfo> projectInfos = parseExcelService.parseProjectInfo(fileName, is);
        for (JkwwProjectInfo projectInfo : projectInfos) {
            projectInfoMapper.updateByPrimaryKey(projectInfo);
        }

    }


}
