package cn.gz.rd.datacollection.controller;

import cn.gz.rd.datacollection.TestControllerBase;
import cn.gz.rd.datacollection.controller.request.UpdateUsableRequest;
import cn.gz.rd.datacollection.model.SubjectInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Peng Xiaodong
 */
public class SubjectInfoCtrlTest extends TestControllerBase {

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        this.session = new MockHttpSession();
    }

    @Test
    public void testSave() throws Exception {
        testLogin("sfgw", "123456");

        MvcResult result = mockMvc.perform(
                post("/jkwww/sysSubject/save/v1")
                        .session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("subjectCode", "")
                        .param("subjectName", "测试主题")
                        .param("parentSubjectCode", "ZTBM_JKWW_0001")
                        .param("level", "2")
                        .param("subjectType", "文件")
                        .param("saveLocation", "path")
                        .param("uploadFrequency", "季")
                        .param("uploadDeadline", "9")
                        .param("templatePath", "templatePath")
                        //map.put("dataStartRow", "4");
                        //map.put("columnNum", "9");
                        .param("deptCodes", "BMBM_001")
                        .param("deptCodes", "BMBM_002")
                        .param("committeeCode", "GWBM_03")
                        .param("comment", "comment")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();
        String jsonResult = result.getResponse().getContentAsString();
        System.out.println(jsonResult);
        assertSuccResult(jsonResult);
    }

    @Test
    public void testUpdate() throws Exception {
        testLogin("sfgw", "123456");

        MvcResult result = mockMvc.perform(
                post("/jkwww/sysSubject/update/v1")
                        .session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("subjectCode", "ZTBM_JKWW_0092")
                        .param("subjectName", "测试主题")
                        .param("parentSubjectCode", "ZTBM_JKWW_0001")
                        .param("level", "2")
                        .param("subjectType", "文件")
                        .param("saveLocation", "path")
                        .param("uploadFrequency", "季")
                        .param("uploadDeadline", "9")
                        .param("templatePath", "templatePath")
                        //map.put("dataStartRow", "4");
                        //map.put("columnNum", "9");
                        .param("deptCodes", "BMBM_001")
                        .param("committeeCode", "GWBM_03")
                        .param("comment", "comment")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();
        String jsonResult = result.getResponse().getContentAsString();
        System.out.println(jsonResult);
        assertSuccResult(jsonResult);
    }

    @Test
    public void testDisable() throws Exception {
        testLogin("sfgw", "123456");

        MvcResult result = mockMvc.perform(
                get("/jkwww/sysSubject/disable/v1")
                        .session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("subjectCode", "ZTBM_JKWWW_0004")
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
        testLogin("sfgw", "123456");

        MvcResult result = mockMvc.perform(
                get("/jkwww/sysSubject/query/v1")
                        .session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("subjectCode", "")
                        .param("subjectName", "")
                        .param("deptCode", "BMBM_003")
                        .param("committeeCode", "")
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
    public void testUpdateUsable() throws Exception {
        testLogin("sfgw", "123456");
        UpdateUsableRequest request = new UpdateUsableRequest();
        List<String> subjectCodes = new ArrayList<>();
        subjectCodes.add("ZTBH_JJYXW_0001");
        subjectCodes.add("ZTBH_JJYXW_0002");
        request.setSubjectCodes(subjectCodes);
        request.setUsable(false);
        String reqestJson = new Gson().toJson(request);
        System.out.println(reqestJson);
        MvcResult result = mockMvc.perform(
                post("/jkwww/sysSubject/updateUsable/v1")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(reqestJson)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();
        String jsonResult = result.getResponse().getContentAsString();
        System.out.println(jsonResult);
        assertSuccResult(jsonResult);
    }


}
