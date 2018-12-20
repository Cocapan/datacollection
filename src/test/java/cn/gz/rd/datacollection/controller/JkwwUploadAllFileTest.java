package cn.gz.rd.datacollection.controller;

import cn.gz.rd.datacollection.TestControllerBase;
import org.apache.commons.io.FilenameUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.FileInputStream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class JkwwUploadAllFileTest extends TestControllerBase {

    private final String uploadFileDir = "F:\\Document\\广州人大\\7教科文卫\\数据导入模板\\2018Q1\\";

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        this.session = new MockHttpSession();
    }

    public void testUploadFile(
            String userName, String filePath,
            String subjectCode, String statPeriod)
            throws Exception {
        testLogin(userName);
        String deptCode = (String) session.getAttribute("deptCode");
        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.fileUpload("/jkwww/file/uploadFile/v1")
                        .file(new MockMultipartFile("file",
                                FilenameUtils.getName(filePath),
                                "application/vnd.ms-excel",
                                new FileInputStream(filePath)))
                        .session(session)
                        .param("subjectCode", subjectCode)
                        .param("countRate", statPeriod)
                        .param("deptCode", deptCode)
                        .param("dataType", "数据表")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();
        String jsonResult = result.getResponse().getContentAsString();
        System.out.println(jsonResult);
        assertSuccResult(jsonResult);
    }

    public void testUploadFileByYearlyPlann(String statPeriod) throws Exception {
        String subjectCode = "ZTBM_JKWW_0006";
        String subjectName = "“十三五”时期市本级社会民生基础设施年度计划表 - ";
        testUploadFile("sfl",
                uploadFileDir + subjectName + "市妇联.xls",
                subjectCode, statPeriod);
        testUploadFile("sjyj",
                uploadFileDir + subjectName + "市教育局.xls",
                subjectCode, statPeriod);
        testUploadFile("smzj",
                uploadFileDir + subjectName + "市民政局.xls",
                subjectCode, statPeriod);
        testUploadFile("styj",
                uploadFileDir + subjectName + "市体育局.xls",
                subjectCode, statPeriod);
        testUploadFile("swsjsw",
                uploadFileDir + subjectName + "市卫生计生委.xls",
                subjectCode, statPeriod);
        testUploadFile("swhgdxwcbj",
                uploadFileDir + subjectName + "市文化广电新闻出版局.xls",
                subjectCode, statPeriod);
        testUploadFile("szgh",
                uploadFileDir + subjectName + "市总工会.xls",
                subjectCode, statPeriod);
        testUploadFile("tsw",
                uploadFileDir + subjectName + "团市委.xls",
                subjectCode, statPeriod);
    }

    public void testUploadFileByExeSituation(String statPeriod) throws Exception {
        String subjectCode = "ZTBM_JKWW_0007";
        String subjectName = "“十三五”时期市本级社会民生基础设施计划执行情况表 - ";
        testUploadFile("sfl",
                uploadFileDir + subjectName + "市妇联.xls",
                subjectCode, statPeriod);
        testUploadFile("sjyj",
                uploadFileDir + subjectName + "市教育局.xls",
                subjectCode, statPeriod);
        testUploadFile("smzj",
                uploadFileDir + subjectName + "市民政局.xls",
                subjectCode, statPeriod);
        testUploadFile("styj",
                uploadFileDir + subjectName + "市体育局.xls",
                subjectCode, statPeriod);
        testUploadFile("swsjsw",
                uploadFileDir + subjectName + "市卫生计生委.xls",
                subjectCode, statPeriod);
        testUploadFile("swhgdxwcbj",
                uploadFileDir + subjectName + "市文化广电新闻出版局.xls",
                subjectCode, statPeriod);
        testUploadFile("szgh",
                uploadFileDir + subjectName + "市总工会.xls",
                subjectCode, statPeriod);
        testUploadFile("tsw",
                uploadFileDir + subjectName + "团市委.xls",
                subjectCode, statPeriod);
    }

    public void testUploadFileByPrjProgress(String statPeriod) throws Exception {
        String subjectCode = "ZTBM_JKWW_0066";
        String subjectName = "“十三五”时期市本级社会民生基础设施建设项目进度表 - ";
        testUploadFile("sfl",
                uploadFileDir + subjectName + "市妇联.xlsx",
                subjectCode, statPeriod);
        testUploadFile("sjyj",
                uploadFileDir + subjectName + "市教育局.xlsx",
                subjectCode, statPeriod);
        testUploadFile("smzj",
                uploadFileDir + subjectName + "市民政局.xlsx",
                subjectCode, statPeriod);
        testUploadFile("styj",
                uploadFileDir + subjectName + "市体育局.xlsx",
                subjectCode, statPeriod);
        testUploadFile("swsjsw",
                uploadFileDir + subjectName + "市卫生计生委.xlsx",
                subjectCode, statPeriod);
        testUploadFile("swhgdxwcbj",
                uploadFileDir + subjectName + "市文化广电新闻出版局.xlsx",
                subjectCode, statPeriod);
        testUploadFile("szgh",
                uploadFileDir + subjectName + "市总工会.xlsx",
                subjectCode, statPeriod);
        testUploadFile("tsw",
                uploadFileDir + subjectName + "团市委.xlsx",
                subjectCode, statPeriod);
    }

    public void testUploadFileByImplSituation(String statPeriod) throws Exception {
        String subjectCode = "ZTBM_JKWW_0069";
        String subjectName = "民生基础设施布局规划及其实施方案制定实施情况表 - ";
        testUploadFile("sfl",
                uploadFileDir + subjectName + "市妇联.xls",
                subjectCode, statPeriod);
        testUploadFile("sjyj",
                uploadFileDir + subjectName + "市教育局.xls",
                subjectCode, statPeriod);
        testUploadFile("smzj",
                uploadFileDir + subjectName + "市民政局.xls",
                subjectCode, statPeriod);
        testUploadFile("styj",
                uploadFileDir + subjectName + "市体育局.xls",
                subjectCode, statPeriod);
        testUploadFile("swsjsw",
                uploadFileDir + subjectName + "市卫生计生委.xls",
                subjectCode, statPeriod);
        testUploadFile("swhgdxwcbj",
                uploadFileDir + subjectName + "市文化广电新闻出版局.xls",
                subjectCode, statPeriod);
        testUploadFile("szgh",
                uploadFileDir + subjectName + "市总工会.xls",
                subjectCode, statPeriod);
        testUploadFile("tsw",
                uploadFileDir + subjectName + "团市委.xls",
                subjectCode, statPeriod);
    }

    public void testUploadFileByHisProblem(String statPeriod) throws Exception {
        String cremainsProblemFileName = "解决公益性骨灰安放设施历史遗留问题工作进度表 - 市民政局.xls";
        String oldageProblemFileName = "解决养老机构历史遗留问题（消防问题）工作进度表 - 市民政局.xls";
        String eduProblemFileName = "解决农村和“村改居”地区教育设施移交历史遗留问题的工作进度表 - 市教育局.xls";
        String residenceProblemFileName = "解决居住区配套设施建设移交历史遗留问题工作进度表 - 市住建委.xlsx";

        String hisZJWFileName = "解决历史遗留问题进度表 - 住建委.xls";
        String hisJYJFileName = "解决历史遗留问题进度表 - 教育局.xls";
        String hisMZJFileName = "解决历史遗留问题进度表 - 民政局.xls";

        testUploadFile("smzj",
                uploadFileDir + cremainsProblemFileName,
                "ZTBM_JKWW_0076", statPeriod);
        testUploadFile("smzj",
                uploadFileDir + oldageProblemFileName,
                "ZTBM_JKWW_0075", statPeriod);

        testUploadFile("szjw",
                uploadFileDir + residenceProblemFileName,
                "ZTBM_JKWW_0074", statPeriod);
        testUploadFile("sjyj",
                uploadFileDir + eduProblemFileName,
                "ZTBM_JKWW_0073", statPeriod);

        testUploadFile("szjw",
                uploadFileDir + hisZJWFileName,
                "ZTBM_JKWW_0072", statPeriod);
        testUploadFile("sjyj",
                uploadFileDir + hisJYJFileName,
                "ZTBM_JKWW_0072", statPeriod);

        testUploadFile("smzj",
                uploadFileDir + hisMZJFileName,
                "ZTBM_JKWW_0072", statPeriod);

    }

    /**
     * 上传所有Excel类型的主题数据
     */
    @Test
    public void testUploadExcelFile() throws Exception {
        String statPeriod = "2018Q1";

        testUploadFileByYearlyPlann(statPeriod);
        testUploadFileByExeSituation(statPeriod);
        testUploadFileByPrjProgress(statPeriod);
        testUploadFileByImplSituation(statPeriod);
        testUploadFileByHisProblem(statPeriod);
    }

























}
