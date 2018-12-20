package cn.gz.rd.datacollection.controller;

import cn.gz.rd.datacollection.TestControllerBase;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserCtrlTest extends TestControllerBase {

    private String urlHead = "/api/user";

    @Before
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        this.session = new MockHttpSession();
    }

    @Test
    public void testInsertUser() throws Exception {
        MvcResult result = mockMvc.perform(
                post(urlHead + "/insert/v1")
                        .session(session)
                        .param("userId", "")
                        .param("userName", "tt")
                        .param("nickName", "tt")
                        .param("passwd", "tt")
                        .param("deptCode", "tt")
                        .param("committeeCode", "tt")
                        .param("role", "tt")
                        .param("phoneNum", "tt")
                        .param("email", "tt")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();
        String jsonResult = result.getResponse().getContentAsString();
        System.out.println(jsonResult);
        assertSuccResult(jsonResult);
    }

    @Test
    public void testUpdateUser() throws Exception {

        MvcResult result = mockMvc.perform(
                post(urlHead + "/update/v1")
                        .session(session)
                        .param("userId", "35")
                        .param("userName", "pxd2")
                        .param("nickName", "shadon2")
                        .param("passwd", "1234562")
                        .param("deptCode", "BMBM_0412")
                        .param("committeeCode", "2")
                        .param("role", "部门2")
                        .param("phoneNum", "177393932")
                        .param("email", "344064802@qq.com2")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();
        String jsonResult = result.getResponse().getContentAsString();
        System.out.println(jsonResult);
        assertSuccResult(jsonResult);
    }

    @Test
    public void testDeleteUser() throws Exception {

        MvcResult result = mockMvc.perform(
                post(urlHead + "/delete/v1")
                        .session(session)
                        .param("userId", "35")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();
        String jsonResult = result.getResponse().getContentAsString();
        System.out.println(jsonResult);
        assertSuccResult(jsonResult);
    }

    @Test
    public void testQueryUser() throws Exception {

        MvcResult result = mockMvc.perform(
                get(urlHead + "/query/v1")
                        .session(session)
                        .param("userName", "j")
                        .param("nickName", "局")
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
    public void testChgPasswd() throws Exception {
        testLogin("sfl", "654321");
        MvcResult result = mockMvc.perform(
                post(urlHead + "/chgPasswd/v1")
                        .session(session)
                        .param("oldPasswd", "654321")
                        .param("newPasswd", "123456")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();
        String jsonResult = result.getResponse().getContentAsString();
        System.out.println(jsonResult);
        assertSuccResult(jsonResult);
    }




}
