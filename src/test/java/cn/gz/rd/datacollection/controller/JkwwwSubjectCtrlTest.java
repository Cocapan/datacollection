package cn.gz.rd.datacollection.controller;

import cn.gz.rd.datacollection.TestControllerBase;
import cn.gz.rd.datacollection.model.User;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class JkwwwSubjectCtrlTest extends TestControllerBase {

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        this.session = new MockHttpSession();
    }

    /**
     * 政府部门的主题查询
     */
    @Test
    public void testQuerySubject() throws Exception {

        testLogin("sfgw", "gzrd1234");
        MvcResult result = mockMvc.perform(
                get("/jkwww/subject/query/v1")
                        .session(session)
                        .param("countRate", "2018Q1")
                        .param("pageNum", "1")
                        .param("pageSize", "9999")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();
        String jsonResult = result.getResponse().getContentAsString();
        System.out.println(jsonResult);
        assertSuccResult(jsonResult);
    }

    /**
     * 发改委的主题查询
     */
    @Test
    public void testQuerySubjectInfoByNDRC() throws Exception {
        testLogin("sfgw", "123456");
        MvcResult result = mockMvc.perform(
                get("/jkwww/subject/queryCheck/v1")
                        .session(session)
                        .param("countRate", "2018Q1")
                        .param("deptCode", "")
                        .param("isUpload", "")
                        .param("statusCode", "")
                        .param("pageNum", "1")
                        .param("pageSize", "10")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();


        String jsonResult = result.getResponse().getContentAsString();
        System.out.println(jsonResult);
        assertSuccResult(jsonResult);

    }

    @Test
    public void testDownloadTemplate() throws Exception {

        MvcResult result = mockMvc.perform(
                get("/jkwww/file/downloadTemplate/v1")
                        .session(session)
                        .param("subjectCode", "ZTBM_JKWW_0007")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        Collection<String> headerNames = response.getHeaderNames();
        for (String headerName : headerNames) {
            System.out.println(headerName + " --> " + response.getHeader(headerName));
        }
        FileUtils.writeByteArrayToFile(
                new File("C:\\Users\\Shadon\\Desktop\\ZTBM_JKWW_0007.xlsx"),
                response.getContentAsByteArray());
    }

    @Test
    public void testUploadFile() throws Exception {

        testLogin("sfgw", "123456");

        //上传文件主题数据
        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.fileUpload("/jkwww/file/uploadFile/v1")
                        .file(new MockMultipartFile("file",
                                "市本级民生基础设施项目建设进展情况报告_2018Q1.doc",
                                "application/msword",
                                new FileInputStream(
                                "D:\\Document\\广州人大\\文档管理\\智慧人大\\" +
                                        "7_教育科学文化卫生联网监督系统、代表大会议案办理监督系统\\" +
                                        "5_清洗后的数据\\社会民生基础设施项目工程推进情况\\" +
                                        "市本级民生基础设施项目建设进展情况报告_2018Q1.doc")))
                        .session(session)
                        .param("subjectCode", "ZTBM_JKWW_0003")
                        .param("countRate", "2017Q1")
                        .param("deptCode", "BMBM_005")
                        //.param("subjectStatusId", "48")
                        .param("dataType", "文件") //文件 数据表
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        System.out.println(jsonResult);
        assertSuccResult(jsonResult);

    }

    /**
     * 测试文件上传--年度计划表
     */
    @Test
    public void testUploadFileYearlyPlann() throws Exception {
        testLogin("sjyj");

        MvcResult result = mockMvc.perform(
            MockMvcRequestBuilders.fileUpload("/jkwww/file/uploadFile/v1")
                .file(new MockMultipartFile("file",
                        "“十三五”时期市本级社会民生基础设施年度计划表--教育部.xls",
                        "application/vnd.ms-excel",
                        new FileInputStream(
                                "D:\\Document\\广州人大\\文档管理\\智慧人大\\" +
                                        "7_教育科学文化卫生联网监督系统、代表大会议案办理监督系统\\" +
                                        "数据导入模板\\2018Q2\\" +
                                        "“十三五”时期市本级社会民生基础设施年度计划表 - 市教育局.xls")))
                .session(session)
                .param("subjectCode", "ZTBM_JKWW_0006")
                .param("countRate", "2018Q2")
                .param("deptCode", "BMBM_012")
                .param("dataType", "数据表") //文件 数据表
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();
        String jsonResult = result.getResponse().getContentAsString();
        System.out.println(jsonResult);
        assertSuccResult(jsonResult);
    }

    /**
     * 测试文件上传--执行情况表
     */
    @Test
    public void testUploadFileExcSituation() throws Exception {
        session.putValue("deptCode", "BMBM_012");
        session.putValue("userCode", "PXD");

        MvcResult result = mockMvc.perform(
            MockMvcRequestBuilders.fileUpload("/jkwww/file/uploadFile/v1")
                .file(new MockMultipartFile("file",
                        "“十三五”时期市本级社会民生基础设施计划执行情况表--教育部.xls",
                        "application/vnd.ms-excel",
                        new FileInputStream(
                                "src\\test\\resources\\jkww\\uploadFile\\" +
                                        "“十三五”时期市本级社会民生基础设施计划执行情况表--教育部.xls")))
                .session(session)
                .param("subjectCode", "ZTBM_JKWW_0007")
                .param("countRate", "2018Q1")
                .param("dataType", "数据表") //文件 数据表
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();
        String jsonResult = result.getResponse().getContentAsString();
        System.out.println(jsonResult);
        assertSuccResult(jsonResult);
    }

    /**
     * 测试文件上传--进度总表
     */
    @Test
    public void testUploadFileProgressSummary() throws Exception {
        session.putValue("deptCode", "BMBM_012");
        session.putValue("userCode", "PXD");

        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.fileUpload("/jkwww/file/uploadFile/v1")
                        .file(new MockMultipartFile("file",
                                "“十三五”时期市本级社会民生基础设施建设项目进度总表--教育部.xlsx",
                                "application/vnd.ms-excel",
                                new FileInputStream(
                                        "src\\test\\resources\\jkww\\uploadFile\\" +
                                                "“十三五”时期市本级社会民生基础设施建设项目进度总表--教育部.xlsx")))
                        .session(session)
                        .param("subjectCode", "ZTBM_JKWW_0066")
                        .param("countRate", "2018Q1")
                        .param("dataType", "数据表") //文件 数据表
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();
        String jsonResult = result.getResponse().getContentAsString();
        System.out.println(jsonResult);
        assertSuccResult(jsonResult);
    }

    /**
     * 测试文件上传--实施情况表
     */
    @Test
    public void testUploadFileImplSituation() throws Exception {
        session.putValue("deptCode", "BMBM_012");
        session.putValue("userCode", "PXD");

        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.fileUpload("/jkwww/file/uploadFile/v1")
                        .file(new MockMultipartFile("file",
                                "民生基础设施布局规划及其实施方案制定实施情况表--教育部.xls",
                                "application/vnd.ms-excel",
                                new FileInputStream(
                                        "src\\test\\resources\\jkww\\uploadFile\\" +
                                                "民生基础设施布局规划及其实施方案制定实施情况表--教育部.xls")))
                        .session(session)
                        .param("subjectCode", "ZTBM_JKWW_0069")
                        .param("countRate", "2018Q1")
                        .param("dataType", "数据表") //文件 数据表
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();
        String jsonResult = result.getResponse().getContentAsString();
        System.out.println(jsonResult);
        assertSuccResult(jsonResult);
    }

    /**
     * 测试文件上传--解决历史遗留问题进度表
     */
    @Test
    public void testUploadFileProblemProgress() throws Exception {
        session.putValue("deptCode", "BMBM_012");
        session.putValue("userCode", "PXD");

        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.fileUpload("/jkwww/file/uploadFile/v1")
                        .file(new MockMultipartFile("file",
                                "解决历史遗留问题进度表--教育部.xls",
                                "application/vnd.ms-excel",
                                new FileInputStream(
                                        "src\\test\\resources\\jkww\\uploadFile\\" +
                                                "解决历史遗留问题进度表--教育部.xls")))
                        .session(session)
                        .param("subjectCode", "ZTBM_JKWW_0072")
                        .param("countRate", "2018Q1")
                        .param("dataType", "数据表") //文件 数据表
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();
        String jsonResult = result.getResponse().getContentAsString();
        System.out.println(jsonResult);
        assertSuccResult(jsonResult);
    }

    /**
     * 测试文件上传--解决农村和“村改居”地区教育设施移交历史遗留问题的工作进度表
     */
    @Test
    public void testUploadFileEduWorkProgress() throws Exception {
        session.putValue("deptCode", "BMBM_012");
        session.putValue("userCode", "PXD");

        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.fileUpload("/jkwww/file/uploadFile/v1")
                        .file(new MockMultipartFile("file",
                                "解决农村和“村改居”地区教育设施移交历史遗留问题的工作进度表--教育局.xls",
                                "application/vnd.ms-excel",
                                new FileInputStream(
                                        "src\\test\\resources\\jkww\\uploadFile\\" +
                                                "解决农村和“村改居”地区教育设施移交历史遗留问题的工作进度表--教育局.xls")))
                        .session(session)
                        .param("subjectCode", "ZTBM_JKWW_0073")
                        .param("countRate", "2018Q1")
                        .param("dataType", "数据表") //文件 数据表
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();
        String jsonResult = result.getResponse().getContentAsString();
        System.out.println(jsonResult);
        assertSuccResult(jsonResult);
    }

    /**
     * 测试文件上传--居住区
     */
    @Test
    public void testResidenceProblem() throws Exception {
        testLogin("szjw", "123456");

        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.fileUpload("/jkwww/file/uploadFile/v1")
                        .file(new MockMultipartFile("file",
                                "解决居住区配套设施建设移交历史遗留问题工作进度表.xlsx",
                                "application/vnd.ms-excel",
                                new FileInputStream(
                                        "D:\\Document\\广州人大\\" +
                                                "文档管理\\智慧人大\\" +
                                                "7_教育科学文化卫生联网监督系统、代表大会议案办理监督系统\\" +
                                                "5_清洗后的数据\\" +
                                                "历史遗留问题解决情况\\" +
                                                "解决居住区配套设施建设移交历史遗留问题工作进度表.xlsx")))
                        .session(session)
                        .param("subjectCode", "ZTBM_JKWW_0074")
                        .param("countRate", "2018Q1")
                        .param("deptCode", "BMBM_026")
                        .param("subjectStatusId", "221")
                        .param("dataType", "数据表") //文件 数据表
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();
        String jsonResult = result.getResponse().getContentAsString();
        System.out.println(jsonResult);
        assertSuccResult(jsonResult);
    }

    /**
     * 测试文件上传--图片
     */
    @Test
    public void testUploadFileImg() throws Exception {
        testLogin("sjyj");
        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.fileUpload("/jkwww/file/uploadFile/v1")
                        .file(new MockMultipartFile("file",
                                "1广州市聋人学校新校建设项目现场进度情况.png",
                                "application/vnd.ms-excel",
                                new FileInputStream("F:\\Document\\广州人大\\7教科文卫\\5_清洗后的数据\\" +
                                        "社会民生基础设施项目工程推进情况\\项目进度照片\\2018Q1\\" +
                                        "1广州市聋人学校新校建设项目现场进度情况.png")))
                        .session(session)
                        .param("subjectCode", "ZTBM_JKWW_0008")
                        .param("countRate", "2018Q1")
                        .param("dataType", "文件") //文件 数据表
                        .param("subjectStatusId", "259")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();
        String jsonResult = result.getResponse().getContentAsString();
        System.out.println(jsonResult);
        assertSuccResult(jsonResult);
    }

    @Test
    public void testQueryDeptInfo() throws Exception {

        MvcResult result = mockMvc.perform(
                get("/jkwww/subject/queryDept/v1")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        System.out.println(jsonResult);
        assertSuccResult(jsonResult);
    }

    @Test
    public void testCheckSubject() throws Exception {

        session.putValue("isNDRC", true);
        session.putValue("userCode", "PXD");
        MvcResult result = mockMvc.perform(
                post("/jkwww/subject/check/v1")
                .session(session)
                .param("countRate", "2018Q1")
                .param("isPass", "false")
                .param("subjectCodes", "ZTBM_JKWW_0005,ZTBM_JKWW_0006")

        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        System.out.println(jsonResult);
        assertSuccResult(jsonResult);
    }

    @Test
    public void testSubmitAll() throws Exception {

        testLogin("sfgw");
        MvcResult result = mockMvc.perform(
                post("/jkwww/subject/submitAll/v1")
                        .session(session)
                        .param("countRate", "2018Q1")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        System.out.println(jsonResult);
        assertSuccResult(jsonResult);
    }

    @Test
    public void testIsSubmitAll() throws Exception {
        testLogin("office", "123456");
        MvcResult result = mockMvc.perform(
                get("/jkwww/subject/isSubmitAll/v1")
                        .session(session)
                        .param("countRate", "2018Q1")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        System.out.println(jsonResult);
        assertSuccResult(jsonResult);
    }

    @Test
    public void testDownloadFile() throws Exception {
        testLogin("sfgw");
        MvcResult result = mockMvc.perform(
                get("/jkwww/file/downloadFile/v1")
                        .session(session)
                        .header("User-Agent", "MSIE")
                        .param("subjectStatusIds", "48")
                        .param("countRate", "2018Q1")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        Collection<String> headerNames = response.getHeaderNames();
        for (String headerName : headerNames) {
            System.out.println(headerName + " --> " + response.getHeader(headerName));
        }
        String contentType = response.getContentType();
        System.out.println(contentType);

        FileUtils.writeByteArrayToFile(new File("C:\\Users\\Shadon\\Desktop\\a.zip"),
                response.getContentAsByteArray());
    }

    @Test
    public void testQueryCompleteInfo() throws Exception {

        MvcResult result = mockMvc.perform(
            get("/jkwww/summary/completeInfo/v1")
                .session(session)
                .param("countRate", "2018Q1")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        System.out.println(jsonResult);
        assertSuccResult(jsonResult);
    }

    @Test
    public void testQueryProjectDetailInfo() throws Exception {
        MvcResult result = mockMvc.perform(
                get("/jkwww/subject/detailInfo/v1")
                        .param("className", "教育设施项目")
                        .param("status", "前期准备项目")
                        .param("mainDeptName", "广州医科大学")
                        .param("ownerName", "大学")
                        .param("projectName", "大学")
                        .param("countRate", "2018Q1")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        System.out.println(jsonResult);
        assertSuccResult(jsonResult);
    }

    @Test
    public void testQueryStatInfo() throws Exception {
        MvcResult result = mockMvc.perform(
                get("/jkwww/subject/statInfo/v1")
                        .param("countRate", "2018Q1")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        System.out.println(jsonResult);
        assertSuccResult(jsonResult);
    }

    @Test
    public void testDownloadProjectDetailExcel() throws Exception {

        MvcResult result = mockMvc.perform(
                get("/jkwww/file/downloadDetail/v1")
                        .session(session)
                        .param("className", "教育设施项目")
                        .param("status", "前期准备项目")
                        .param("mainDeptName", "广州医科大学")
                        .param("ownerName", "大学")
                        .param("projectName", "大学")
                        .param("countRate", "2018Q1")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        Collection<String> headerNames = response.getHeaderNames();
        for (String headerName : headerNames) {
            System.out.println(headerName + " --> " + response.getHeader(headerName));
        }
        String contentType = response.getContentType();
        System.out.println(contentType);

        FileUtils.writeByteArrayToFile(new File("C:\\Users\\Shadon\\Desktop\\pxd.xlsx"),
                response.getContentAsByteArray());
    }

    @Test
    public void testDownloadProjectStatExcel() throws Exception {

        MvcResult result = mockMvc.perform(
                get("/jkwww/file/downloadStat/v1")
                        .session(session)
                        .param("countRate", "2018Q1")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        Collection<String> headerNames = response.getHeaderNames();
        for (String headerName : headerNames) {
            System.out.println(headerName + " --> " + response.getHeader(headerName));
        }
        String contentType = response.getContentType();
        System.out.println(contentType);

        FileUtils.writeByteArrayToFile(new File("C:\\Users\\Shadon\\Desktop\\downloadStat.xlsx"),
                response.getContentAsByteArray());
    }

    @Test
    public void testViewDataSubject() throws Exception {
        MvcResult result = mockMvc.perform(
                get("/jkwww/view/data/v1")
                        .param("countRate", "2018Q1")
                        .param("subjectCode", "ZDMSJCSSXMJDB")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        System.out.println(jsonResult);
        assertSuccResult(jsonResult);
    }

    /**
     * “十三五”时期市本级社会民生基础设施年度计划表
     */
    @Test
    public void testViewDataYearlyPlan() throws Exception {
        MvcResult result = mockMvc.perform(
                get("/jkwww/view/data/v1")
                        .session(session)
                        .param("countRate", "2018Q1")
                        .param("subjectCode", "ZTBM_JKWW_0066")
                        .param("deptCode", "BMBM_012")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        System.out.println(jsonResult);
        assertSuccResult(jsonResult);
    }

    //“十三五”时期市本级社会民生基础设施建设项目进度总表
    //民生基础设施布局规划及其实施方案制定实施情况表
    //解决历史遗留问题进度表
    //市人大2号议案解决农村和“村改居”地区教育设施移交历史遗留问题的工作台账一览表
    //居住区配套设施建设移交历史遗留问题台账
    //居住区配套教育设施建设移交历史遗留问题台账（详细表）
    //公益性骨灰安放设施台账
    //广州市需进一步协调解决历史遗留问题（消防问题）养老机构一览表
    //
    //“十三五”时期市本级社会民生基础设施建设项目进度总表
    //重点民生基础设施项目进度表

    /**
     * “十三五”时期市本级社会民生基础设施计划执行情况表
     */
    @Test
    public void testViewExeSituation() throws Exception {
        MvcResult result = mockMvc.perform(
                get("/jkwww/view/data/v1")
                        .session(session)
                        .param("countRate", "2018Q1")
                        .param("subjectCode", "ZTBM_JKWW_0007")
                        //.param("deptCode", "")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        System.out.println(jsonResult);
        assertSuccResult(jsonResult);
    }

    @Test
    public void testQueryCountRate() throws Exception {
        testLogin("sfgw");
        MvcResult result = mockMvc.perform(
                get("/jkwww/subject/queryCountRate/v1")
                        .session(session)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        System.out.println(jsonResult);
        assertSuccResult(jsonResult);
    }

}
