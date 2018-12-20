package cn.gz.rd.datacollection.controller;

import cn.gz.rd.datacollection.TestControllerBase;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Peng Xiaodong
 */
public class JkwwBaseInfoCtrlTest extends TestControllerBase {

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        this.session = new MockHttpSession();
    }

    //1、“十三五”时期市本级社会民生基础设施建设项目基础信息表
    //2、历史遗留问题--公益性骨灰安放设施基础信息表
    //3、历史遗留问题--养老机构基础信息表
    //4、历史遗留问题--居住区配套设施基础信息表
    //5、历史遗留问题--农村和“村改居”地区教育设施基础信息表

    @Test
    public void testUpload() throws Exception {
        testLogin("sfgw");

        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.fileUpload("/jkwww/baseInfo/upload/v1")
                        .file(new MockMultipartFile("file",
                                "历史遗留问题--公益性骨灰安放设施基础信息表.xlsx",
                                "application/vnd.ms-excel",
                                new FileInputStream(
                                        "D:\\workspace\\DataCollection\\src" +
                                                "\\main\\resources\\templates\\jkww\\" +
                                                "历史遗留问题--公益性骨灰安放设施基础信息表.xlsx")))
                        .session(session)
                        .param("subjectNo", "2")
                        .param("previewPath", "/jkww_base_info/1532331330758.html")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        System.out.println(jsonResult);
        assertSuccResult(jsonResult);
    }

    @Test
    public void testQuery() throws Exception {
        testLogin("sfgw");

        MvcResult result = mockMvc.perform(
                get("/jkwww/baseInfo/query/v1")
                        .session(session)
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
                get("/jkwww/baseInfo/download/v1")
                        .session(session)
                        .header("User-Agent", "MSIE")
                        .param("subjectNo", "2")
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

        FileUtils.writeByteArrayToFile(new File("C:\\Users\\Shadon\\Desktop\\a.xlsx"),
                response.getContentAsByteArray());
    }

    @Test
    public void testDownloadTemplateFile() throws Exception {
        testLogin("sfgw");
        MvcResult result = mockMvc.perform(
                get("/jkwww/baseInfo/downloadTemplate/v1")
                        .session(session)
                        .header("User-Agent", "MSIE")
                        .param("subjectNo", "1")
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

        FileUtils.writeByteArrayToFile(new File("C:\\Users\\Shadon\\Desktop\\a.xlsx"),
                response.getContentAsByteArray());
    }

    @Test
    public void testPreview() throws Exception {

        testLogin("sfgw", "123456");

        //上传文件主题数据
        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.fileUpload("/jkwww/baseInfo/preview/v1")
                        .file(new MockMultipartFile("file",
                                "历史遗留问题--公益性骨灰安放设施基础信息表.xlsx",
                                "application/vnd.ms-excel",
                                new FileInputStream(
                                        "D:\\workspace\\DataCollection\\src" +
                                                "\\main\\resources\\templates\\jkww\\" +
                                                "历史遗留问题--公益性骨灰安放设施基础信息表.xlsx")))
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
