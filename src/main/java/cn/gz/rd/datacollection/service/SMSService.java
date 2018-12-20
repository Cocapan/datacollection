package cn.gz.rd.datacollection.service;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 短信接口
 * @author Peng Xiaodong
 */
@Service
public class SMSService {

    private final Logger logger = LoggerFactory.getLogger(SMSService.class);

    @Value("${sms.url}")
    private String smsUrl;

    @Value("${sms.masIp}")
    private String masIp;

    @Value("${sms.username}")
    private String username;

    @Value("${sms.password}")
    private String password;

    @Value("${sms.code}")
    private String code;

    public void send(String phoneNum, String txt) {
        logger.debug("发送短信接口，请求参数：phoneNum = {}, txt = {}", phoneNum, txt);

        if (StringUtils.isEmpty(phoneNum)) {
            logger.warn("短信发送失败，手机号码为空！");
            return;
        }
        if (StringUtils.isEmpty(txt)) {
            logger.warn("短信发送失败，短信内容为空！");
            return;
        }

        WebClient client = WebClient.create(smsUrl);
        Form form = new Form()
            .param("ip", masIp)
            .param("username", username)
            .param("password", password)
            .param("code", code)
            .param("mobiles", phoneNum)
            .param("content", txt);
        Response response = client.post(form);
        String value = "";
        try {
            value = IOUtils.toString((InputStream) response.getEntity());
        } catch (IOException e) {
            logger.error("短信接口的响应流出现IO问题", e);
        }
        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            throw new RuntimeException("短信接口请求失败，失败原因：" + value);
        }
        logger.info("发送短信接口返回结果：" + value);
    }

}
