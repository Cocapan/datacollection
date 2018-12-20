package cn.gz.rd.datacollection.controller;

import cn.gz.rd.datacollection.controller.response.ControllerResponse;
import cn.gz.rd.datacollection.model.User;
import cn.gz.rd.datacollection.service.IUserService;
import com.google.common.hash.Hashing;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
@RequestMapping("/api")
public class LoginCtrl extends BaseCtrl {

    private static final Logger logger = LoggerFactory.getLogger(LoginCtrl.class);

    private IUserService iUserService;

    @Autowired
    public LoginCtrl(IUserService userService){
        this.iUserService = userService;
    }

    @RequestMapping("/login")
    @ResponseBody
    public ControllerResponse login(
            HttpServletRequest httpServletRequest,
            @RequestBody User loginMessage) {

        logger.info("-------------LoginCtrl---login-----------before---------------------");
        ControllerResponse result = new ControllerResponse();

        String password = loginMessage.getPassword();
        if (StringUtils.isBlank(password)){
            result.error("请输入密码");
            return result;
        }

        User user = new User();
        user.setAccount(loginMessage.getAccount());
        String codecPasswd = Hashing.md5().hashBytes(password.getBytes()).toString();
        user.setPassword(codecPasswd);

        try {
            User loginUser = iUserService.selectByAccountAndPw(user);
            if (null != loginUser) {
                //登录成功后，将用户信息放到session中
                HttpSession session = httpServletRequest.getSession();
                session.setAttribute("user", loginUser);

                Map<String, Object> resultMap = new HashMap<>();
                logger.info("login userInfo: " + loginUser.toString());
                String departmentId = loginUser.getDepartmentId();
                session.setAttribute("userCode", loginUser.getAccount());
                session.setAttribute("deptCode", departmentId);
                session.setAttribute("workingCommitteeCode", loginUser.getWorkingCommitteeId());
                if (StringUtils.equalsIgnoreCase(departmentId, "BMBM_032")) {//市政府办公厅
                    session.setAttribute("isNDRC", false);
                    resultMap.put("role", 2);

                } else if (StringUtils.equalsIgnoreCase(departmentId, "BMBM_011")) {//发改委
                    session.setAttribute("isNDRC", true);
                    resultMap.put("role", 1);

                } else {//其他政府部门
                    resultMap.put("role", 0);
                }
                resultMap.put("user", loginUser);
                result.setData(resultMap);
                result.success("登陆成功");
            } else {
                result.error("用户或密码错误！");
                return result;
            }
        } catch (Exception e) {
            logger.error("Exception", e);
            result.error(e);
        }
        logger.info("-------------LoginCtrl---login--------------end---------------------");
        return result;
    }

    /**
     * 用户登出
     * @return   ControllerResponse
     */
    @RequestMapping("/loginOut")
    @ResponseBody
    public ControllerResponse loginOut(HttpSession session) {
        logger.info("-------------LoginCtrl---loginOut-----------before---------------------");
        ControllerResponse response = new ControllerResponse();
        try {
            //返回前端注销成功，由前端清空保存在cookie中的token，后面再传值的话，token为空，即无法登陆
            session.removeAttribute("user");
            session.invalidate();
            response.success();
        } catch (Exception e) {
            logger.error("Exception", e);
            response.error();
            response.setMessage(e.getMessage());
        }
        logger.info("-------------LoginCtrl---loginOut--------------end---------------------");
        return response;
    }

}
