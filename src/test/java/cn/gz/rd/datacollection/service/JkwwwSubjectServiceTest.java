package cn.gz.rd.datacollection.service;

import cn.gz.rd.datacollection.DataCenterApplicationTests;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Peng Xiaodong
 */
public class JkwwwSubjectServiceTest extends DataCenterApplicationTests {

    @Autowired
    private JkwwwSubjectService subjectService;

    @Test
    public void testIsAllUpload() {
        Assert.assertEquals(false, subjectService.isAllUpload("2018Q1"));
    }

}
