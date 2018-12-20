package cn.gz.rd.datacollection.controller;

import cn.gz.rd.datacollection.TestControllerBase;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;
import java.util.Collection;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Peng Xiaodong
 */
public class JkwwSummaryDataCtrlTest extends TestControllerBase {

    private String urlHead = "/jkwww/summary/";

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        this.session = new MockHttpSession();
    }

    @Test
    public void testQueryCompleteInfo() throws Exception {
        testLogin("sfgw");
        MvcResult result = mockMvc.perform(
                get(urlHead + "completeInfo/v1")
                        .session(session)
                        .param("countRate", "2018Q2")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        System.out.println(jsonResult);
        assertSuccResult(jsonResult);
    }

    @Test
    public void testQueryYearlyPlan() throws Exception {
        MvcResult result = mockMvc.perform(
                post("/jkwww/summary/query/yearlyPlan/v1")
                        .session(session)
                        .param("className", "教育设施项目")
                        .param("status", "前期准备项目")
                        .param("mainDeptName", "广州医科大学")
                        .param("ownerName", "大学")
                        .param("projectName", "大学")
                        .param("countRate", "2018Q1")
                        .param("pageNum", "1")
                        .param("pageSize", "100")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        System.out.println(jsonResult);
        assertSuccResult(jsonResult);
    }

    @Test
    public void testQueryScheduleSummary() throws Exception {
        testLogin("sfgw");
        MvcResult result = mockMvc.perform(
                get(urlHead + "query/v1")
                        .session(session)
                        .param("subjectCode", "PROJECT_SUMMARY")
                        .param("countRate", "2018Q2")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        System.out.println(jsonResult);
        assertSuccResult(jsonResult);
    }

    @Test
    public void testExportExcel() throws Exception {
        testLogin("sfgw");
        MvcResult result = mockMvc.perform(
                get(urlHead + "download/v1")
                        .session(session)
                        .header("User-Agent", "Windows NT,MSIE")
                        .param("countRate", "2018Q2")
                        .param("subjectCode", "ZTBM_JKWW_0007")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        String contentType = response.getContentType();
        Collection<String> headerNames = response.getHeaderNames();
        for (String headerName : headerNames) {
            System.out.println(headerName + " --> " + response.getHeader(headerName));
        }
        System.out.println(contentType);

        FileUtils.writeByteArrayToFile(new File("C:\\Users\\Shadon\\Desktop\\pxd.xlsx"),
                response.getContentAsByteArray());
    }

    @Test
    public void testExportExcelStatInfo() throws Exception {
        testLogin("sfgw");
        MvcResult result = mockMvc.perform(
                get(urlHead + "downloadStat/v1")
                        .session(session)
                        .header("User-Agent", "Windows NT,MSIE")
                        .param("countRate", "2018Q2")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        String contentType = response.getContentType();
        Collection<String> headerNames = response.getHeaderNames();
        for (String headerName : headerNames) {
            System.out.println(headerName + " --> " + response.getHeader(headerName));
        }
        System.out.println(contentType);

        FileUtils.writeByteArrayToFile(new File("C:\\Users\\Shadon\\Desktop\\pxd.xlsx"),
                response.getContentAsByteArray());
    }

    @Test
    public void testIsComplete() throws Exception {
        testLogin("sfgw");
        MvcResult result = mockMvc.perform(
                get(urlHead + "isComplete/v1")
                        .session(session)
                        .param("subjectCode", "PROJECT_SUMMARY")
                        .param("countRate", "2018Q1")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        System.out.println(jsonResult);
        assertSuccResult(jsonResult);
    }



}
