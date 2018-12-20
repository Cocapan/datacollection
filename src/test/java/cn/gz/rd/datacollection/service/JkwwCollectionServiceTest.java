package cn.gz.rd.datacollection.service;

import cn.gz.rd.datacollection.DataCenterApplicationTests;
import cn.gz.rd.datacollection.service.imp.JkwwCollectionServiceImp;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class JkwwCollectionServiceTest extends DataCenterApplicationTests {

    @Autowired
    private JkwwCollectionServiceImp collectionService;

    @Test
    public void testParseAndSaveProjectInfo() throws IOException {
        File file = new File("D:\\Document\\广州人大\\文档管理\\智慧人大\\7_教育科学文化卫生联网监督系统、代表大会议案办理监督系统\\5清洗后的数据\\民生基础设施项目基础信息表.xlsx");
        collectionService.parseAndSaveProjectInfo(file.getName(), new FileInputStream(file));
    }

}
