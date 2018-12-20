package cn.gz.rd.datacollection.controller;

import cn.gz.rd.datacollection.controller.response.ControllerResponse;
import cn.gz.rd.datacollection.model.ShowUser;
import cn.gz.rd.datacollection.model.SysUser;
import cn.gz.rd.datacollection.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/user")
public class UserCtrl extends BaseCtrl {

    private static final Logger logger = LoggerFactory.getLogger(UserCtrl.class);

    @Autowired
    private IUserService userService;

    @RequestMapping(value = "/countAll/v1", method = RequestMethod.POST)
    @ResponseBody
    public ControllerResponse countAll() {
        logger.info("----UserCtrl-----countAll-----");
        ControllerResponse result = new ControllerResponse();

        try {
            int count = userService.countAll();
            Map<String, Object> data = new HashMap<>();
            data.put("count", count);
            System.out.printf("count:" + count);

            result.setData(data);
            result.success();
        } catch (Exception e) {
            logger.error("Exception", e);
            result.error(e);
        }
        return result;
    }

    @RequestMapping(value = "/insert/v1", method = RequestMethod.POST)
    @ResponseBody
    public ControllerResponse insert(@RequestBody SysUser user){
        logger.info("----UserCtrl-----insertUser-----");
        logger.debug("增加用户：请求参数 " + user.toString());
        ControllerResponse response = new ControllerResponse();
        try {
            if (user == null) {
                response.error();
                response.setMessage("请传递必要的参数！");
                return response;
            }
            Integer userId = user.getUserId();
            if (userId != null) {
                response.error();
                response.setMessage("用户ID由后台自动生成，请不要传递该参数！");
                return response;
            }
            boolean existSameUser = userService.existSameUser(user.getUserName());
            if (existSameUser) {
                response.error();
                response.setMessage("存在相同的用户名称！");
                return response;
            }

            int insertCount = userService.insert(user);
            if (insertCount == 1) {
                response.success();
                return response;
            } else {
                logger.error("用户信息保存失败，请核查！");
                response.error("用户信息保存失败！");
                return response;
            }

        } catch (Exception e) {
            logger.error("Exception", e);
            response.error(e);
        }
        return response;
    }

    @RequestMapping(value = "/update/v1", method = RequestMethod.POST)
    @ResponseBody
    public ControllerResponse update(@RequestBody SysUser user) {
        logger.debug("更新用户：请求参数 " + user.toString());
        ControllerResponse response = new ControllerResponse();
        try {
            boolean existSameUser = userService.existUserById(user.getUserId());
            if (!existSameUser) {
                response.error("用户已经不存在！");
                return response;
            }

            int updateCount = userService.update(user);
            if (updateCount == 1) {
                response.success();
                return response;
            } else {
                logger.error("用户信息更新失败，请核查！");
                response.error("用户信息更新失败！");
                return response;
            }

        } catch (Exception e) {
            logger.error("Exception", e);
            response.error(e);
        }
        return response;
    }

    @RequestMapping(value = "/delete/v1", method = RequestMethod.POST)
    @ResponseBody
    public ControllerResponse delete(Integer userId) {
        logger.info("----UserCtrl-----deleteUser-----");
        ControllerResponse response = new ControllerResponse();
        try {
            if (userId == null) {
                response.error();
                response.setMessage("用户ID不能为空！");
                return response;
            }

            boolean existSameUser = userService.existUserById(userId);
            if (!existSameUser) {
                response.error("用户已经不存在！");
                return response;
            }

            int deleteCount = userService.delete(userId);
            if (deleteCount == 1) {
                response.success();
                return response;
            } else {
                logger.error("用户信息删除失败，userId=" + userId);
                response.error("用户信息删除失败！");
                return response;
            }

        } catch (Exception e) {
            logger.error("Exception", e);
            response.error(e);
        }
        return response;
    }

    @RequestMapping(value = "/query/v1", method = RequestMethod.GET)
    @ResponseBody
    public ControllerResponse query(
            String userName, String nickName,
            Integer pageNum, Integer pageSize) {
        logger.info("----UserCtrl-----query-----");
        ControllerResponse response = new ControllerResponse();
        try {
            List<ShowUser> sysUsers = userService.query(userName, nickName, pageNum, pageSize);
            int count = userService.count(userName, nickName);
            Map<String, Object> data = new HashMap<>();
            data.put("list", sysUsers);
            data.put("totalCount", count);

            response.setData(data);
            response.success();
        } catch (Exception e) {
            logger.error("Exception", e);
            response.error(e);
        }
        return response;
    }

    @RequestMapping(value = "/isExist/v1", method = RequestMethod.GET)
    @ResponseBody
    public ControllerResponse isExist(String userName) {
        logger.info("----UserCtrl-----isExist-----");
        ControllerResponse response = new ControllerResponse();
        try {
            boolean isExist = userService.existSameUser(userName);
            Map<String, Object> data = new HashMap<>();
            data.put("isExist", isExist);
            response.setData(data);
            response.success();
        } catch (Exception e) {
            logger.error("Exception", e);
            response.error(e);
        }
        return response;
    }

    @RequestMapping(value = "/chgPasswd/v1", method = RequestMethod.POST)
    @ResponseBody
    public ControllerResponse chgPasswd(String oldPasswd, String newPasswd) {
        ControllerResponse response = new ControllerResponse();
        if (StringUtils.isBlank(oldPasswd) || StringUtils.isBlank(newPasswd)) {
            response.error("密码不能为空！");
            return response;
        }
        try {
            String userName = getUserCodeFromSession();
            userService.chgPasswd(userName, oldPasswd, newPasswd);
            response.success();
        } catch (Exception e) {
            logger.error("修改密码出现错误", e);
            response.error(e.getMessage(), e);
        }
        return response;
    }
}
