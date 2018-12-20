package cn.gz.rd.datacollection.controller;

import cn.gz.rd.datacollection.TestControllerBase;
import cn.gz.rd.datacollection.model.User;
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

public class TopicTest extends TestControllerBase{

        private final String uploadFileDir = "C:\\Users\\panxuan\\Desktop\\畜牧业生产情况_采集模板(2万行).xlsx";

        @Before
        public void setup() {
            mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
            this.session = new MockHttpSession();
        }

        public void testOnlinePreview(
                String userName, String filePath,
                String topicId, String statisticsPeriod
                )
                throws Exception {
            testLogin(userName);
            User user = (User) session.getAttribute("user");
            MvcResult result = mockMvc.perform(
                    MockMvcRequestBuilders.fileUpload("/api/topic/onlinePreview/v1")
                            .file(new MockMultipartFile("file",
                                    FilenameUtils.getName(filePath),
                                    "application/vnd.ms-excel",
                                    new FileInputStream(filePath)))
                            .session(session)
                            .param("topicId", topicId)
                            .param("statisticsPeriod", statisticsPeriod)
                            .param("uploadTime", "2018-7-17 11:00:00")
                            .param("topicType", "数据表")
            )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                    .andReturn();
            String jsonResult = result.getResponse().getContentAsString();
            System.out.println(jsonResult);
            assertSuccResult(jsonResult);
        }

    @Test
    public void testUploadExcelFile() throws Exception {
        testOnlinePreview("stjj", uploadFileDir, "ZTBM_NCNYW_0002", "2017");
    }

}
