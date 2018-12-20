package cn.gz.rd.datacollection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ActiveProfiles({"dev"})
@SpringBootTest
public class TestControllerBase {

    @Autowired
    public WebApplicationContext wac;

    /**
     * 测试模拟类
     */
    public MockMvc mockMvc;

    public MockHttpSession session;

    public JsonObject assertSuccResult(String jsonResult) {
        JsonObject jsonObj = new JsonParser().parse(jsonResult).getAsJsonObject();
        boolean isSucc = jsonObj.get("result").getAsBoolean();
        assertTrue("执行失败！", isSucc);
        return jsonObj;
    }

    public void testLogin(String userName) throws Exception {
        testLogin(userName, "gzrd1234");
    }

    public void testLogin(String userName, String passwd) throws Exception {

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("account", userName);
        paramMap.put("password", passwd);
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
        String jsonParam = gson.toJson(paramMap);

        MvcResult result = mockMvc.perform(
                post("/api/login")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(jsonParam)
        )
                .andExpect(status().isOk())
                .andReturn();
        String jsonResult = result.getResponse().getContentAsString();
        System.out.println(jsonResult);
    }

}
